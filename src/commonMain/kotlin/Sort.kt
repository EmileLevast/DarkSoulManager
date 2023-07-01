import kotlinx.serialization.Serializable

@Serializable
class Sort(
    override val nom:String="inconnu",
    val type:SpellType=SpellType.AME,
    val utilisation:Int=0,
    val cout:String="Aucune",
    val intelligenceMin:Int=0,
    val contraintes:String="Aucune",
    val degats:Map<EffectType,String> = mapOf(),
    val seuils:Map<String,List<Int>> = mapOf(),//en clé c'est le facteur et en valeur c'est la liste des seuils associés
    val coupCritiques:String="",
    val iajMax:Int=0,
    val description:String=""
) :ApiableItem(){

    override val id: Int = nom.hashCode()

    override fun parseFromCSV(listCSVElement: List<String>): ApiableItem {
        return Sort(
            listCSVElement[0].cleanupForDB(),
            parseSpellType(listCSVElement[1]),
            listCSVElement[2].run{ if(isNotBlank()) toInt() else{0} },
            listCSVElement[3],
            listCSVElement[4].run{ if(isNotBlank()) toInt() else{0} },
            listCSVElement[5],
            parseDefense(listCSVElement[6]),
            parseSeuils(listCSVElement[7]),
            listCSVElement[8],
            listCSVElement[9].run{ if(isNotBlank()) toInt() else{0} },
            listCSVElement[10]
            )
    }

    override var isAttached: Boolean = false

    override fun getStatsAsStrings(): String {
        var textSeuils = ""
        seuils.forEach {
            textSeuils += "|   ${it.value.joinToString("/")} =>×${it.key}\n"
        }
        return  type.symbol+"\n"+
                "Utilisations : $utilisation\n" +
                "Cout : $cout\n" +
                "Intelligence Minimum : $intelligenceMin\n" +
                (if(contraintes.isNotBlank())"$contraintes\n" else "") +
                convertEffectTypeStatsToString(degats)+
                "Seuils:\n" + textSeuils +
                (if(coupCritiques.isNotBlank())"CC : $coupCritiques\n" else "") +
                "IAJ Max : $iajMax\n" +
                "$description\n"    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "Type: SpellType = (ame, necromancie, psionique, pyromancie, miracle) ",
            "Utilisation : Int",
            "Cout : String",
            "Intelligence Min : Int",
            "contraintes : String",
            "Degats: Format = EffectType:Int|EffectType:Int... (EffectType = Po/Ph/F/Ma)",
            "Seuils: Format = Int/Int=Int|Int/Int=Int... ",
            "Coups critiques :String",
            "IAJ Max : Int",
            "Description : String",
        )
    }

    override fun getDeparsedAttributes(): List<String> {
        return listOf<String>(
            nom,
            type.shortname,
            utilisation.toString(),
            cout,
            intelligenceMin.toString(),
            contraintes,
            deparseDefense(degats),
            deparseSeuils(seuils),
            coupCritiques,
            iajMax.toString(),
            description
        )
    }


}