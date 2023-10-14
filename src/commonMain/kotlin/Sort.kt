import kotlinx.serialization.Serializable

@Serializable
class Sort(
    override val nom:String="inconnu",
    val type:SpellType=SpellType.AME,
    val utilisation:Int=0,
    val cout:String="Aucune",
    val intelligenceMin:Int=0,
    val contraintes:String="Aucune",
    val seuils:List<Seuil> = mutableListOf(),//en cl� c'est le facteur et en valeur c'est la liste des seuils associ�s
    val coupCritiques:String="",
    val iajMax:Int=0,
    val description:String=""
) :ApiableItem(){

    override val id: Int = nom.hashCode()



    override var isAttached: Boolean = false

    override fun getStatsAsStrings(): String {
        return statsAsStringAccordingToSimplifyOrNot(false)
    }

    private fun statsAsStringAccordingToSimplifyOrNot(isSimplify:Boolean): String {
        var textSeuils = ""
        seuils.forEach {
            textSeuils += "|   $it\n"
        }

        val coupCritiquesParsed = strSimplify(coupCritiques, isSimplify)

        return type.symbol + "\n" +
                "Utilisations : $utilisation\n" +
                "Cout : $cout\n" +
                "Intelligence Minimum : $intelligenceMin\n" +
                (if (contraintes.isNotBlank()) "$contraintes\n" else "") +
                "Seuils:\n" + textSeuils +
                (if (coupCritiquesParsed.isNotBlank()) "CC : $coupCritiquesParsed\n" else "") +
                "IAJ Max : $iajMax\n" +
                "${strSimplify(description, isSimplify)}\n"
    }

    override fun getStatsSimplifiedAsStrings(): String {
        return statsAsStringAccordingToSimplifyOrNot(true)
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "Type: SpellType = (ame, necromancie, psionique, pyromancie, miracle) ",
            "Utilisation : Int",
            "Cout : String",
            "Intelligence Min : Int",
            "contraintes : String",
            "Seuils: Format = |Int/Int=Effect:Int|EffectType:Int...\\n|Int/Int=Effect:Int|EffectType:Int  ",
            "Coups critiques :String",
            "IAJ Max : Int",
            "Description : String",
        )
    }

    override fun getDeparsedAttributes(): List<String> {

        var textSeuils = ""
        seuils.forEach {
            textSeuils += "|$it\n"
        }

        return listOf<String>(
            nom,
            type.name,
            utilisation.toString(),
            cout,
            intelligenceMin.toString(),
            contraintes,
            textSeuils,
            coupCritiques,
            iajMax.toString(),
            description
        )
    }

    override fun parseFromCSV(listCSVElement: List<String>): ApiableItem {
        return Sort(
            listCSVElement[0].cleanupForDB(),
            parseSpellType(listCSVElement[1]),
            listCSVElement[2].run{ if(isNotBlank()) toInt() else{0} },
            listCSVElement[3],
            listCSVElement[4].run{ if(isNotBlank()) toInt() else{0} },
            listCSVElement[5],
            parseSeuils(listCSVElement[7]),
            listCSVElement[8],
            listCSVElement[9].run{ if(isNotBlank()) toInt() else{0} },
            listCSVElement[10]
        )
    }

}