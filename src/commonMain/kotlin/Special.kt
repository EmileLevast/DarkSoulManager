import kotlinx.serialization.Serializable

@Serializable
class Special(
    override val nom:String="inconnu",
    val itemType: SpecialItemType=SpecialItemType.OUTIL,
    val capaciteSpeciale:String="",
    override var imageName:String ="logoSpecial.jpg"
    ) : ApiableItem() {

    override val _id = nom.hashCode()
    override var isAttached = false

    override fun getStatsAsStrings():String{
        return "${itemType.name}\n${strSimplify(capaciteSpeciale,false)}\n"
    }

    override fun getStatsSimplifiedAsStrings(): String {
        return "${itemType.name}\n${strSimplify(capaciteSpeciale,true)}\n"
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem{
        return Special(
            listCSVElement[0].cleanupForDB(),
            parseSpecialItemType(listCSVElement[1]),
            listCSVElement[2],
            listCSVElement[3]
        )
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "Type: SpellType = (ANNEAU, TALISMAN, OUTIL, BRAISE, AMBRE, TECHNIQUE) ",
            "Capacite speciale : String",
            "image Name : String"
        )
    }

    override fun getDeparsedAttributes(): List<String> {
        return listOf<String>(
            nom,
            itemType.name,
            capaciteSpeciale,
            imageName
        )
    }
}