import io.ktor.util.logging.*

//Update this list whenever you want to add a specific item to the database don't change it during execution
//TODO ajouter ici un element instancie dans cette liste a chaque creation d'une nouvelle classe
val unmutableListApiItemDefinition = listOf<ApiableItem>(Arme(),Armure(),Monster(),Bouclier(),Sort(),Special(),Joueur(),Equipe())

const val CHAR_SEP_EQUIPEMENT = "|"
const val ENDPOINT_RECHERCHE_STRICTE = "precis"
const val ENDPOINT_RECHERCHE_TOUT = "all"
const val ENDPOINT_MAJ_CARACS_JOUEUR = "maj_caracs_joueur"
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

fun String.getIntOrZero(): Int = if (isNotBlank()) toInt() else { 0 }


enum class SpellType(val shortname:String, val symbol:String){
    AME("ame","✨"),
    PYROMANCIE("pyromancie","\uD83D\uDD25"),
    PSIONIQUE("psionique","\uD83E\uDD2F"),
    MIRACLE("miracle","\uD83D\uDC50"),
    NECROMANCIE("necromancie","\uD83D\uDC80"),
    ARACHNOMANCIE("arachnomancie","\uD83D\uDD77");
}

enum class SpecialItemType{
    ANNEAU,
    TALISMAN,
    AMBRE,
    BRAISE,
    TECHNIQUE,
    OUTIL;
}

val logger = KtorSimpleLogger("logger")

fun deparseDefense(defense: Map<EffectType, String>): String {
    var res = ""
    defense.forEach {
        res += it.key.shortname + ":" + it.value + "|"
    }
    return res.removeSuffix("|")
}

fun parseDefense(inputElement: String): MutableMap<EffectType, String> {

    val mapDefenseType = mutableMapOf<EffectType, String>()

    if (inputElement.isNotEmpty()) {
        inputElement.split("|").forEach { currentDefense ->
            currentDefense.split(":").let { currentEffectType ->
                //on check si le type correspond bien a un vrai type
                mapDefenseType[EffectType.entries
                    .find { enumEffectType -> enumEffectType.shortname == currentEffectType.first() }!!] =
                    currentEffectType.last()
            }
        }
    }

    return mapDefenseType
}
