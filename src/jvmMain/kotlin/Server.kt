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
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.regex
import java.io.File
import java.io.FileNotFoundException

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 9090

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
                    get("/"+ itapiable.updateNameForApi) {
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
                    post("/"+ "Nothing") {
                        val postedElement = when(itapiable){
                            is Arme -> call.receive<Arme>()
                            is Monster -> call.receive<Monster>()
                            else -> call.receive<Armure>()
                        }
                        var option = UpdateOptions().upsert(true)
                        collectionsApiableItem[itapiable.nameForApi]!!.updateOne(Monster::nom eq postedElement.nom, postedElement as Nothing,option!! , false)
                    }

                }
            }
        }
    }.start(wait = true)
}

