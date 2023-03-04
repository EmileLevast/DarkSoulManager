
//Update this list whenever you want to add a specific item to the database don't change it during execution
//TODO ajouter ici un element instancie dans cette liste a chaque creation d'une nouvelle classe
val unmutableListApiItemDefinition = listOf<ApiableItem>(Arme(),Armure(),Monster())

enum class EffectType(val shortname:String, val symbol:String){
    FIRE("F","🔥"),
    MAGIC("Ma","✨"),
    POISON("Po","\uD83E\uDDEA"),
    PHYSICAL("Ph","⚔");
}

fun convertEffectTypeStatsToString(statsToConvert:Map<EffectType,String> ) :String {
    val listMapDefense = StringBuilder()
    statsToConvert.forEach {
        listMapDefense.append("${it.key.symbol}:${it.value}")
    }

    return listMapDefense.toString()
}