import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

import kotlinx.browser.window

val endpoint = window.location.origin // only needed until https://youtrack.jetbrains.com/issue/KTOR-453 is resolved

val jsonClient = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

suspend fun getMonsterList(): List<Monster> {
    return jsonClient.get(endpoint + Monster.path).body()
}

suspend fun addMonsterListItem(monsterItem: Monster) {
    jsonClient.post(endpoint + Monster.path) {
        contentType(ContentType.Application.Json)
        setBody(monsterItem)
    }
}

suspend fun deleteMonsterListItem(monsterItem: Monster) {
    jsonClient.delete(endpoint + Monster.path + "/${monsterItem.id}")
}