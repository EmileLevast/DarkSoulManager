import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Joueur(
    override val nom:String="inconnu",
    @Transient
    var listEquipement:MutableList<String> = mutableListOf()
    ) : ApiableItem() {

    override val id = nom.hashCode()
    override var isAttached = false

    var chaineEquipementSerialisee=""

    fun serializeEquipementToString(){
        chaineEquipementSerialisee = listEquipement.joinToString{";"}
    }

    fun deserializeStringToEquipment(){
        listEquipement = chaineEquipementSerialisee.split(";").toMutableList()
    }

    override fun getStatsAsStrings():String{
        return listEquipement.map { it }.joinToString {"\n"}
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