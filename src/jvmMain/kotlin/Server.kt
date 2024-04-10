import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.mongodb.MongoBulkWriteException
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import org.bson.conversions.Bson
import org.litote.kmongo.eq
import org.litote.kmongo.setTo
import org.litote.kmongo.setValue
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException


fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 9090

    val logger = KtorSimpleLogger("logger")

    (LoggerFactory.getILoggerFactory() as LoggerContext).getLogger("org.mongodb.driver").level = Level.WARN


    createCollectionTables()

    embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            json()
        }
        install(CallLogging) {
            level = org.slf4j.event.Level.INFO
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
                        val nom = call.parameters["nom"] ?: ""
                        val itemsFound = getCollectionElements(itapiable,nom)
                        call.respond(itemsFound.ifEmpty { HttpStatusCode.NoContent })
                    }
                    get("/$ENDPOINT_RECHERCHE_STRICTE/{nom}") {
                        val nom = call.parameters["nom"] ?: ""
                        val itemsFound = getCollectionElements(itapiable,nom,true)
                        call.respond(itemsFound.ifEmpty { HttpStatusCode.NoContent })
                    }
                    get("/"+ itapiable.uploadFileForApi) {
                        //retrieve the data from csv file

                        val parsedData = try {
                            itapiable.decomposeCSV(File("src/jvmMain/resources/${itapiable.nameForApi}.csv").readLines()
                                .asSequence()) as List<Nothing>
                        } catch (e: FileNotFoundException) {
                            //si le fichier existe pas on retourne une liste vide
                            logger.error(e.stackTraceToString())
                            listOf()
                        }
                        //send data to database
                        try {
                            collectionsApiableItem[itapiable.nameForApi]!!.insertMany(parsedData)
                        } catch (e: MongoBulkWriteException) {
                            logger.error(e.stackTraceToString())
                        }
                        call.respond(parsedData)
                    }
                    post("/"+ itapiable.updateForApi) {
                        logger.debug("post en cours")
                        val elementToUpdate:ApiableItem = getApiableElementAccordingToType(call, itapiable)

                        val resInsert = collectionsApiableItem[itapiable.nameForApi]!!.updateOneById(elementToUpdate._id,elementToUpdate)

                        if(resInsert.wasAcknowledged()){
                            call.respond(HttpStatusCode.OK)
                        }else{
                            call.respond(HttpStatusCode.ExpectationFailed)
                        }
                    }
                    post("/"+ itapiable.insertForApi) {
                        logger.debug("insert en cours")
                        val elementToInsert:ApiableItem = getApiableElementAccordingToType(call, itapiable)

                        //S'il y'a déjà un élément avec cet identifiant là, on insère pas, faut supprimer avant
                        val resInsert =  if(collectionsApiableItem[itapiable.nameForApi]!!.countDocuments(ApiableItem::_id eq elementToInsert._id) < 1){
                            collectionsApiableItem[itapiable.nameForApi]!!.insertMany(listOf(elementToInsert) as List<Nothing>)
                        } else  { null }

                        if(resInsert?.wasAcknowledged() == true){
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
                    get("/"+itapiable.downloadForApi) {
                        val itemsFound = getCollectionElements(itapiable,".*")
                        val stringFileCSV = itemsFound.first().getParsingRulesAttributesAsList()
                            .joinToString(";") { it.split(":").first() } + "\n"+
                                itemsFound.map { it.getDeparsedAttributes().joinToString(";") }.joinToString ("\n")
                        val filename = "${itapiable.nameForApi}.csv"
                        val file = File(filename)
                        file.writeText(stringFileCSV)
                        call.response.header(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "${filename}")
                                .toString()
                        )
                        call.respondFile(file)
                    }
                    if(itapiable is Joueur){
                        get("/$ENDPOINT_MAJ_CARACS_JOUEUR"){
                            val joueurToUpdateCaracs:Joueur = getApiableElementAccordingToType(call, itapiable) as Joueur

                            val resInsert = database.getCollection<Joueur>().updateOne(filter = Joueur::_id eq joueurToUpdateCaracs._id, update = setValue(Joueur::caracActuel, joueurToUpdateCaracs.caracActuel))

                            if(resInsert.wasAcknowledged()){
                                call.respond(HttpStatusCode.OK)
                            }else{
                                call.respond(HttpStatusCode.ExpectationFailed)
                            }
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}

/**
 * This function return the object deducing his type
 */
private suspend fun getApiableElementAccordingToType(
    call: ApplicationCall,
    itapiable: ApiableItem
) = when (itapiable) {
    //TODO ajouter une ligne dans le when quand on ajoute un table dans la bdd

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

    is Sort -> {
        call.receive<Sort>()
    }

    is Special -> {
        call.receive<Special>()
    }

    is Joueur -> {
        call.receive<Joueur>()
    }

    is Equipe -> {
        call.receive<Equipe>()
    }

    else -> {
        call.receive<Armure>()
    }
}

