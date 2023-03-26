import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
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

                }
            }
        }
    }.start(wait = true)
}

fun parseArmes(sequenceLinesFile : Sequence<String>, call: ApplicationCall):List<Arme>{

    val listArme = mutableListOf<Arme>()

    var lineFiltered = sequenceLinesFile.drop(1)
    lineFiltered = lineFiltered.filter{ it.isNotBlank() }

    lineFiltered.forEach {
            val listCSV = it.split(";")

            call.application.environment.log.info("Voici la ligne lue : $it")

            //If the line is empty we pass it
            if(listCSV.first().isBlank()){
                return@forEach
            }

            //Seuils
            val seuilsCSV = listCSV[2]
            val listSeuils = mutableListOf<Int>()
            val seuils = HashMap<String,List<Int>>()
            if(seuilsCSV.isNotEmpty()){
                seuilsCSV.split("|").forEach{
                    val listSeuilsParfFacteur =it.split("=")
                    listSeuilsParfFacteur.first().let{ itInutilise ->
                        itInutilise.split("/").forEach{itSeuils ->
                            listSeuils.add(itSeuils.toInt())
                        }
                        seuils[listSeuilsParfFacteur.last()] = listSeuils.toList()
                        listSeuils.clear()
                    }
                }
            }

            listArme.add(
                Arme(
                    listCSV[0].cleanupForDB(),
                    listCSV[1],
                    seuils,
                    listCSV[3],
                    listCSV[4].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[5].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[6].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[7],
                    listCSV[8].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[9].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[10]
                )
            )

        }

    return listArme
}

fun parseArmure(sequenceLinesFile : Sequence<String>):List<Armure>{
    val listArmure = mutableListOf<Armure>()
    var lineFiltered = sequenceLinesFile.drop(1)

    lineFiltered = lineFiltered.filter{ it.isNotBlank() }

    lineFiltered.forEach {
        val listCSV = it.split(";")


        //If the line is empty we pass it
        if(listCSV.first().isBlank()){
            return@forEach
        }

        //DefenseType
        val listDefenseTypeCSV = listCSV[1]
        val mapDefenseType = mutableMapOf<EffectType,String>()

        if(listDefenseTypeCSV.isNotEmpty()){
            listDefenseTypeCSV.split("|").forEach { currentDefense ->
                currentDefense.split(":").let{ currentEffectType ->
                    //on check si le type correspond bien a un vrai type
                    mapDefenseType[EffectType.values().find { enumEffectType ->enumEffectType.shortname == currentEffectType.first() }!!] =
                        currentEffectType.last()
                }
            }
        }

        listArmure.add(Armure(
            listCSV[0].cleanupForDB(),
            mapDefenseType,
            listCSV[2],
            listCSV[3].run{ if(isNotBlank()) toInt() else{0} },
            listCSV[4]
        ))

    }


    return listArmure
}