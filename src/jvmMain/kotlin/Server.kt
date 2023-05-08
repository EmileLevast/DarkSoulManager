import com.mongodb.client.model.UpdateOptions
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import org.litote.kmongo.eq
import java.io.File
import java.io.FileNotFoundException

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 9090

    val logger = KtorSimpleLogger("logger")

    createCollectionTables()

    embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        routing {
            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/") {
                resources("")
            }
            unmutableListApiItemDefinition.forEach { itapiable ->
                route("/"+itapiable.nameForApi!!){
                    get("/{nom}") {
                        val nom = call.parameters["nom"] ?: "inconnu"
                        val itemFound = getCollectionElements(itapiable,nom)
                        call.respond(itemFound.ifEmpty { HttpStatusCode.NoContent })
                    }
                    get("/"+ itapiable.uploadFileForApi) {
                        //retrieve the data from csv file
                        val parsedData = try {
                            itapiable.decomposeCSV(File("src/jvmMain/resources/${itapiable.nameForApi}.csv").readLines()
                                .asSequence()) as List<Nothing>
                        } catch (e: FileNotFoundException) {
                            //si le fichier existe pas on retourne une liste vide
                            listOf()
                        }
                        //send data to database
                        collectionsApiableItem[itapiable.nameForApi]!!.insertMany(parsedData)
                        call.respond(parsedData)
                    }
                    post("/"+ itapiable.updateForApi) {
                        logger.debug("post en cours")
                        //TODO ajouter une ligne dans le when quand on ajoute un table dans la bdd
                        val postedElement:ApiableItem = when(itapiable){
                            is Arme -> {
                                call.receive<Arme>()
                            }
                            is Monster -> {
                                call.receive<Monster>()
                            }
                            is Armure -> {
                                call.receive<Armure>()
                            }
                            is Bouclier -> {
                                call.receive<Bouclier>()
                            }
                            else -> {
                                call.receive<Armure>()
                            }
                        }

                        collectionsApiableItem[itapiable.nameForApi]!!.deleteOne(ApiableItem::nom eq postedElement.nom)
                        val resInsert = collectionsApiableItem[itapiable.nameForApi]!!.insertMany(listOf(postedElement) as List<Nothing>)

                        if(resInsert.wasAcknowledged()){
                            call.respond(HttpStatusCode.OK)
                        }else{
                            call.respond(HttpStatusCode.ExpectationFailed)
                        }
                    }
                    post("/"+ itapiable.deleteForApi+"/{nom}"){
                        val nom = call.parameters["nom"] ?: "inconnu"
                        if(collectionsApiableItem[itapiable.nameForApi]!!.deleteOne(ApiableItem::nom eq nom).wasAcknowledged()){
                            call.respond(HttpStatusCode.OK)
                        }else{
                            call.respond(HttpStatusCode.ExpectationFailed)
                        }
                    }

                }
            }
        }
    }.start(wait = true)
}

