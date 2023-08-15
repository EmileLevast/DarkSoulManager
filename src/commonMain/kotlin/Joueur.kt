import kotlinx.serialization.Serializable

@Serializable
class Joueur(
    override val nom:String="inconnu",
    val listEquipement:MutableList<ApiableItem> = mutableListOf()
    ) : ApiableItem() {

    override val id = nom.hashCode()
    override var isAttached = false

    override fun getStatsAsStrings():String{
        return listEquipement.map { it.nom }.joinToString {"\n"}
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem{
        return Joueur(
            listCSVElement[0].cleanupForDB()
        )//TODO il manque le deparsing des joueurs mais pas necessaires en soi
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String"
        )
    }

    override fun getDeparsedAttributes(): List<String> {
        return listOf<String>(
            nom
        )
    }
}