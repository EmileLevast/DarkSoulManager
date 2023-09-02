import io.ktor.util.logging.*

//Update this list whenever you want to add a specific item to the database don't change it during execution
//TODO ajouter ici un element instancie dans cette liste a chaque creation d'une nouvelle classe
val unmutableListApiItemDefinition = listOf<ApiableItem>(Arme(),Armure(),Monster(),Bouclier(),Sort(),Special(),Joueur())

const val CHAR_SEP_EQUIPEMENT = "|"
const val BALISE_SIMPLE_RULES = "[SIMPLE]"

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

fun strSimplify(str:String, isSimpleRulesOn:Boolean):String{
    return if(str.contains(BALISE_SIMPLE_RULES) && isSimpleRulesOn){
        val indexFin = str.indexOf(BALISE_SIMPLE_RULES)+BALISE_SIMPLE_RULES.length
        if(indexFin == str.length){
            str
        }else{
            str.substring(indexFin)
        }
    }else if(str.contains(BALISE_SIMPLE_RULES)){
        str.substring(0,str.indexOf(BALISE_SIMPLE_RULES))
    }else{
        str
    }
}

enum class SpellType(val shortname:String, val symbol:String){
    AME("ame","✨"),
    PYROMANCIE("pyromancie","\uD83D\uDD25"),
    PSIONIQUE("psionique","\uD83E\uDD2F"),
    MIRACLE("miracle","\uD83D\uDC50"),
    NECROMANCIE("necromancie","\uD83D\uDC80");
}

enum class SpecialItemType{
    ANNEAU,
    TALISMAN,
    AMBRE,
    BRAISE,
    OUTIL;
}

val logger = KtorSimpleLogger("logger")
