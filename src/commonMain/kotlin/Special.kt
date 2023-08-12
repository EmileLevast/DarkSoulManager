import kotlinx.serialization.Serializable

@Serializable
class Special(
    override val nom:String="inconnu",
    val itemType: SpecialItemType=SpecialItemType.OUTIL,
    val capaciteSpeciale:String=""
    ) : ApiableItem() {

    override val id = nom.hashCode()
    override var isAttached = false

    override fun getStatsAsStrings():String{
        return "${itemType.name}\n$capaciteSpeciale\n"
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem{
        return Special(
            listCSVElement[0].cleanupForDB(),
            parseSpecialItemType(listCSVElement[1]),
            listCSVElement[2]
        )
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "Type: SpellType = (ANNEAU, TALISMAN, OUTIL, BRAISE, AMBRE) ",
            "Capacite speciale : String"
        )
    }

    override fun getDeparsedAttributes(): List<String> {
        return listOf<String>(
            nom,
            itemType.name,
            capaciteSpeciale
        )
    }
}