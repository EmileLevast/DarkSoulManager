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
import org.litote.kmongo.eq
import java.io.File
import java.io.InputStream

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 9090
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
            route(Monster.path) {

                get {
                    call.respond(collectionMonster.find().toList())
                }
                get("/{nom}") {
                    val nom = call.parameters["nom"] ?: "inconnu"
                    val monsterFound = collectionMonster.findOne(Monster::nom eq nom)
                    call.respond(monsterFound ?: HttpStatusCode.NoContent)
                }
                post {
                    collectionMonster.insertOne(call.receive<Monster>())
                    call.respond(HttpStatusCode.OK)
                }
                delete("/{id}") {
                    val id = call.parameters["id"]?.toInt()
                    collectionMonster.deleteOne(Monster::id eq id)
                    call.respond(HttpStatusCode.OK)
                }
            }
            route(Arme.path) {

                get("/updateArmes"){
                    collectionArmes.insertMany(updateDatabase(call))
                    call.respond(HttpStatusCode.OK)
                }
                get {
                    call.respond(collectionArmes.find().toList())
                }
                get("/{nom}") {
                    val nom = call.parameters["nom"] ?: "inconnu"
                    val armeFound = collectionArmes.findOne(Arme::nom eq nom)
                    call.respond(armeFound ?: HttpStatusCode.NoContent)
                }
                post {
                    collectionArmes.insertOne(call.receive<Arme>())
                    call.respond(HttpStatusCode.OK)
                }
                delete("/{id}") {
                    val id = call.parameters["id"]?.toInt()
                    collectionArmes.deleteOne(Arme::id eq id)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }.start(wait = true)
}

fun updateDatabase(call: ApplicationCall): List<Arme> {
    val lines = File("src/jvmMain/resources/Armes.csv").readLines()

    call.application.environment.log.info("Voici les lignes lues : $lines")

    return uploadArmes(lines.asSequence(),call)
}

fun uploadArmes(sequenceLinesFile : Sequence<String>,call: ApplicationCall):List<Arme>{

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

            listArme.add(
                Arme(
                    listCSV[0],
                    listCSV[1],
                    seuils,
                    listCSV[3],
                    listCSV[4].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[5].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[6].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[7].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[8].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[9].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[10]
                )
            )

        }

    return listArme
}