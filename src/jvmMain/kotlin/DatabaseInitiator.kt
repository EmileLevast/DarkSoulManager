import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.regex

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
suspend fun getCollectionElements(instanceOfCollectionItemDefinition:ApiableItem, nameOfItemSearched:String):List<ApiableItem>{
    return when(instanceOfCollectionItemDefinition.nameForApi){
        Arme::class.simpleName -> getElementAccordingToType<Arme>(nameOfItemSearched, instanceOfCollectionItemDefinition)
        Armure::class.simpleName -> getElementAccordingToType<Arme>(nameOfItemSearched, instanceOfCollectionItemDefinition)
        else-> getElementAccordingToType<Arme>(nameOfItemSearched, instanceOfCollectionItemDefinition)
    }
}

suspend inline fun <reified T:ApiableItem> getElementAccordingToType(nameOfItemWanted:String, instanceOfCollectionItemDefinition:ApiableItem):List<T>{
    return database.getCollection<T>(T::class.simpleName!!).find(ApiableItem::nom regex ".*$nameOfItemWanted.*").toList()
}