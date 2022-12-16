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

suspend fun searchAnything(nomSearched: String) : List<IListItem> {
    val listResultsItems = mutableListOf<IListItem>()
    unmutableListApiItemDefinition.forEach {
        searchSomething(nomSearched, it)?.let { elementFound ->
            listResultsItems.addAll(elementFound)
        }
    }
    return listResultsItems
}

suspend fun <T:ApiableItem> searchSomething(nomSearched: String, objectDefinitionSearched:T) :List<T>?{
    jsonClient.get(endpoint +"/"+ objectDefinitionSearched.path + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<T>>() else null
    }
}
