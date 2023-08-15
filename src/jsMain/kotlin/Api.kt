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
        val res = when(it){
            //TODO ajouter le cast ici quand on cree une table
            is Arme -> searchArme(nomSearched)
            is Armure -> searchArmure(nomSearched)//TODO ajouter la methode de recherche
            is Monster -> searchMonster(nomSearched)
            is Bouclier -> searchBouclier(nomSearched)
            is Sort -> searchSort(nomSearched)
            is Special -> searchSpecial(nomSearched)
            is Joueur -> searchJoueur(nomSearched)
            else -> null
        }
        if (res != null){
            listResultsItems.addAll(res)
        }
    }
    return listResultsItems
}

suspend fun searchArme(nomSearched: String) :List<Arme>?{
    jsonClient.get(endpoint +"/"+ Arme().nameForApi + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Arme>>() else null
    }
}

suspend fun searchArmure(nomSearched: String) :List<Armure>?{
    jsonClient.get(endpoint +"/"+ Armure().nameForApi + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Armure>>() else null
    }
}

suspend fun searchBouclier(nomSearched: String) :List<Bouclier>?{
    jsonClient.get(endpoint +"/"+ Bouclier().nameForApi + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Bouclier>>() else null
    }
}

suspend fun searchSort(nomSearched: String) :List<Sort>?{
    jsonClient.get(endpoint +"/"+ Sort().nameForApi + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Sort>>() else null
    }
}

suspend fun searchSpecial(nomSearched: String) :List<Special>?{
    jsonClient.get(endpoint +"/"+ Special().nameForApi + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Special>>() else null
    }
}

suspend fun searchMonster(nomSearched: String) :List<Monster>?{
    jsonClient.get(endpoint +"/"+ Monster().nameForApi + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Monster>>() else null
    }
}

suspend fun searchJoueur(nomSearched: String) :List<Joueur>?{
    jsonClient.get(endpoint +"/"+ Joueur().nameForApi + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Joueur>>() else null
    }
}

suspend fun updateItem(itemSelected:ApiableItem):Boolean{
    jsonClient.post(endpoint +"/"+ itemSelected.nameForApi+"/${itemSelected.updateForApi}"){
        contentType(ContentType.Application.Json)
        setBody(itemSelected)
    }.let{
        return it.status== HttpStatusCode.OK
    }
}

suspend fun deleteItem(itemSelected:ApiableItem):Boolean{
    jsonClient.post(endpoint +"/"+ itemSelected.nameForApi+"/${itemSelected.deleteForApi}/${itemSelected.nom}"){
    }.let{
        return it.status== HttpStatusCode.OK
    }
}