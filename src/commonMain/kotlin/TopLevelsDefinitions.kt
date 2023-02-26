
//Update this list whenever you want to add a specific item to the database don't change it during execution
//TODO ajouter ici un element instancie dans cette liste a chaque creation d'une nouvelle classe
val unmutableListApiItemDefinition = listOf<ApiableItem>(Arme(),Armure(),Monster())

enum class EffectType(val shortname:String){
    FIRE("F"),
    MAGIC("Ma"),
    POISON("Po"),
    PHYSICAL("Ph")
}

fun convertEffectTypeStatsToString(statsToConvert:Map<EffectType,String> ) :String {
    var textDefenseByType = ""
    statsToConvert.forEach { textDefenseByType+= it.key.shortname+":"+it.value+" | " }
    return textDefenseByType
}