import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

import kotlinx.browser.window
import kotlinx.js.import.meta.url
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

val endpoint = window.location.origin // only needed until https://youtrack.jetbrains.com/issue/KTOR-453 is resolved

val jsonClient = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

enum class ActionOnDb{
    INSERT,
    DELETE,
    UPDATE
}

suspend fun searchAnything(nomSearched: String, strict:Boolean = false) : List<IListItem> {
    return  deserializeAnythingItemDTO(searchAnythingStringEncoded(nomSearched, strict))
}

suspend fun searchEverything(searchedNames: List<String>, strict:Boolean = false) : List<IListItem> {
    return  deserializeAnythingItemDTO(searchEverythingStringEncoded(searchedNames))
}

fun deserializeAnythingItemDTO(listAnythingItem : List<AnythingItemDTO>): List<IListItem>{
    val listItemsFound = mutableListOf<IListItem>()
    for(anythingItem in listAnythingItem){

        if(anythingItem.itemContent!= null && anythingItem.typeItem != null){
            // Créer une instance de la classe
            val itemClasseReify: ApiableItem? = unmutableListApiItemDefinition.find { it.nameForApi == anythingItem.typeItem }

            //TODO ajouter ici les nouvelles tables a deserialiser
            listItemsFound.add(when(itemClasseReify){
                is Arme -> Json.decodeFromString<Arme>(anythingItem.itemContent!!)
                is Armure -> Json.decodeFromString<Armure>(anythingItem.itemContent!!)
                is Monster -> Json.decodeFromString<Monster>(anythingItem.itemContent!!)
                is Bouclier -> Json.decodeFromString<Bouclier>(anythingItem.itemContent!!)
                is Sort -> Json.decodeFromString<Sort>(anythingItem.itemContent!!)
                is Special -> Json.decodeFromString<Special>(anythingItem.itemContent!!)
                is Joueur -> Json.decodeFromString<Joueur>(anythingItem.itemContent!!)
                is Equipe -> Json.decodeFromString<Equipe>(anythingItem.itemContent!!)
                else-> throw IllegalArgumentException("Impossible de deserialiser l'objet json reçu, il ne fait pas parti des elements connus")
            })
        }
    }
    return  listItemsFound
}

suspend fun searchEverythingStringEncoded(searchedNames:List<String>) : List<AnythingItemDTO> {

    jsonClient.put("$endpoint/$ENDPOINT_RECHERCHE_TOUT"){
        contentType(ContentType.Application.Json)
        setBody(searchedNames)
    }.let{
        return if (it.status != HttpStatusCode.NoContent) it.body<List<AnythingItemDTO>>() else listOf()
    }
}

suspend fun searchAnythingStringEncoded(nomSearched: String, strict:Boolean) : List<AnythingItemDTO> {

    jsonClient.get(endpoint +"/$ENDPOINT_RECHERCHE_TOUT"+ "/${nomSearched}"){
        url {
            parameters.append(ENDPOINT_RECHERCHE_STRICTE, strict.toString())
        }
    }.let{
        return if (it.status != HttpStatusCode.NoContent) it.body<List<AnythingItemDTO>>() else listOf()
    }
}

suspend fun searchArme(nomSearched: String, strict:Boolean = false) :List<Arme>?{
    jsonClient.get(endpoint +"/"+ if(strict){ Arme().namePrecisForApi} else {Arme().nameForApi} + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Arme>>() else null
    }
}

suspend fun searchArmure(nomSearched: String, strict:Boolean = false) :List<Armure>?{
    jsonClient.get(endpoint +"/"+ if(strict){ Armure().namePrecisForApi} else {Armure().nameForApi} + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Armure>>() else null
    }
}

suspend fun searchBouclier(nomSearched: String, strict:Boolean = false) :List<Bouclier>?{
    jsonClient.get(endpoint +"/"+ if(strict){ Bouclier().namePrecisForApi} else {Bouclier().nameForApi} + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Bouclier>>() else null
    }
}

suspend fun searchSort(nomSearched: String, strict:Boolean = false) :List<Sort>?{
    jsonClient.get(endpoint +"/"+ if(strict){ Sort().namePrecisForApi} else {Sort().nameForApi} + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Sort>>() else null
    }
}

suspend fun searchSpecial(nomSearched: String, strict:Boolean = false) :List<Special>?{
    jsonClient.get(endpoint +"/"+ if(strict) {Special().namePrecisForApi} else {Special().nameForApi} + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Special>>() else null
    }
}

suspend fun searchMonster(nomSearched: String, strict:Boolean = false) :List<Monster>?{
    jsonClient.get(endpoint +"/"+ if(strict){ Monster().namePrecisForApi} else {Monster().nameForApi} + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Monster>>() else null
    }
}

suspend fun searchJoueur(nomSearched: String, strict:Boolean = false) :List<Joueur>?{
    jsonClient.get(endpoint +"/"+ if(strict) {Joueur().namePrecisForApi} else {Joueur().nameForApi} + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Joueur>>() else null
    }
}

suspend fun searchEquipe(nomSearched: String, strict:Boolean = false) :List<Equipe>?{
    jsonClient.get(endpoint +"/"+ if(strict) {Equipe().namePrecisForApi} else {Equipe().nameForApi} + "/${nomSearched}").let {
        return if (it.status != HttpStatusCode.NoContent) it.body<List<Equipe>>() else null
    }
}

suspend fun insertItem(itemSelected:ApiableItem):Boolean{
    jsonClient.post(endpoint +"/"+ itemSelected.nameForApi+"/${itemSelected.insertForApi}"){
        contentType(ContentType.Application.Json)
        setBody(itemSelected)
    }.let{
        return it.status== HttpStatusCode.OK
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