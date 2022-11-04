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