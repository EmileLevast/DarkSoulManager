import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

//val monsterList = mutableListOf<Monster>(
//    Monster("Carcasse",11),
//    Monster("Chauve-soiries",7),
//    Monster("Rat",8)
//)

val client = KMongo.createClient().coroutine
val database = client.getDatabase("MonsterTest")
val collectionMonster = database.getCollection<Monster>()
val collectionArmes = database.getCollection<Arme>()