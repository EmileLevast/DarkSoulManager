import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

import kotlinx.browser.window
import kotlinx.js.import.meta.url

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

    jsonClient.get(endpoint +"/$ENDPOINT_RECHERCHE_TOUT"+ "/${nomSearched}"){
        url {
            parameters.append(ENDPOINT_RECHERCHE_STRICTE, strict.toString())
        }
    }.let{
        return if (it.status != HttpStatusCode.NoContent) it.body<List<IListItem>>() else listOf()
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