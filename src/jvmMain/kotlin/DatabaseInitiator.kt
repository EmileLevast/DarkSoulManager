import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import io.ktor.server.application.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.regex
import kotlin.reflect.full.createInstance

//val monsterList = mutableListOf<Monster>(
//    Monster("Carcasse",11),
//    Monster("Chauve-soiries",7),
//    Monster("Rat",8)
//)

val client = KMongo.createClient().coroutine
val database = client.getDatabase("MonsterTest")
//val collectionMonster = database.getCollection<Monster>()
//val collectionArmes = database.getCollection<Arme>()
//val collectionArmures = database.getCollection<Armure>()

val collectionsApiableItem:MutableMap<String,CoroutineCollection<out ApiableItem>> = mutableMapOf()

fun createCollectionTables(){
    unmutableListApiItemDefinition.forEach {
        collectionsApiableItem[it.nameForApi!!] = database.getCollection(it.nameForApi)
    }
}

//TODO ajouter ici une ligne dans le when a chaque fois qu'eun nouvelle collection dans la bdd est cree
/**
 * strict est un booleen quand il est à true c'est à dire qu'on cherche exactement l'element qui matche le nom
 */
suspend fun getCollectionElements(instanceOfCollectionItemDefinition:ApiableItem, nameOfItemSearched:String, strict:Boolean =false):List<ApiableItem>{
    return when(instanceOfCollectionItemDefinition){
        is Arme -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Armure -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Monster -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Bouclier -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Sort -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Special -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        is Joueur -> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition,strict)
        else-> getElementAccordingToType(nameOfItemSearched, instanceOfCollectionItemDefinition as Monster,strict)
    }
}

suspend inline fun <reified T:ApiableItem> getElementAccordingToType(nameOfItemWanted:String, instanceOfCollectionItemDefinition:T, strict:Boolean,):List<T>{
    return if(strict){
        database.getCollection<T>(T::class.simpleName!!).find(ApiableItem::nom eq nameOfItemWanted).toList()
    }else
    {
        val regexp = if(nameOfItemWanted.contains('*')) nameOfItemWanted else ".*$nameOfItemWanted.*"
        database.getCollection<T>(T::class.simpleName!!).find(ApiableItem::nom regex regexp).toList()
    }
}
