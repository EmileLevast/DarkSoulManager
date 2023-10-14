import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Monster(
    override val nom: String= "inconnu",
    val vie: Int=0,
    val force:Map<Int,Int> = mapOf(),
    val defense:Map<EffectType,String> = mapOf(),
    val intelligence:Int = 0,
    val energie:Int = 0,
    val listDrops:Map<String,Int> = mapOf(),
    val ames:Int=0,
    val capaciteSpeciale:String=""
) : ApiableItem(){

    override val id = nom.hashCode()
    override var isAttached = false

    private fun constructForceSeuils():String{
        val textFinalForce =StringBuilder()
        val textForceSeuilsTemp = StringBuilder()

        force.forEach {
            for(i in 0 until it.key){
                textForceSeuilsTemp.append("\uD83C\uDFB2")
            }
            textForceSeuilsTemp.append(":${it.value} ")

            textFinalForce.append(textForceSeuilsTemp)
            textForceSeuilsTemp.clear()
        }
        return textFinalForce.toString()
    }

    private fun constructListDropsString():String{
        val listDroptext = StringBuilder()
        listDrops.forEach {
            listDroptext.append("  ${it.key} ${if(it.value == 0 ){"∅"}else{"/ ${it.value}+"}}\n")
        }

        return listDroptext.toString()
    }

    override fun getStatsAsStrings(): String {
        val textForceSeuils = constructForceSeuils()
        return "Vie : $vie\n"+
                "Force : $textForceSeuils\n"+
                "Defense:" + convertEffectTypeStatsToString(defense) +"\n"+
                "Intelligence:" + intelligence +"\n"+
                "Energie : $energie\n" +
                "Drops: \n" +
                constructListDropsString() +
                "Ames : $ames\n"+
                "${strSimplify(capaciteSpeciale,false)}\n"
    }

    override fun getStatsSimplifiedAsStrings(): String {
        val textForceSeuils = constructForceSeuils()
        return "Vie : $vie\n"+
                "Force : $textForceSeuils\n"+
                "Defense:" + convertEffectTypeStatsToString(defense) +"\n"+
                "Intelligence:" + intelligence +"\n"+
                "Energie : $energie\n" +
                "Drops: \n" +
                constructListDropsString() +
                "Ames : $ames\n"+
                "${strSimplify(capaciteSpeciale,true)}\n"
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem {
        return Monster(
            listCSVElement[0].cleanupForDB(),
            listCSVElement[1].toInt(),
            parseSeuilsForce(listCSVElement[2]),
            parseDefense(listCSVElement[3]),
            listCSVElement[4].toInt(),
            listCSVElement[5].toInt(),
            parseDrops(listCSVElement[6]),
            listCSVElement[7].toInt(),
            listCSVElement[8]
        )
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: Chaine de caractères",
            "Vie: Entier",
            "Force : Format = Int:Int|Int:Int... ",
            "Defense : Format = EffectType:Int|EffectType:Int... (EffectType = Po/Ph/F/Ma)",
            "Intelligence : Int",
            "Energie : Int",
            "Drops : Format = String:Int|String:Int... ",
            "Ames : Int",
            "Capacite speciale : String"
            )
    }

    override fun getDeparsedAttributes(): List<String> {
        return listOf(
            nom,
            vie.toString(),
            deparseForce(force),
            deparseDefense(defense),
            intelligence.toString(),
            energie.toString(),
            deparseListDrops(listDrops),
            ames.toString(),
            capaciteSpeciale
        )
    }
}