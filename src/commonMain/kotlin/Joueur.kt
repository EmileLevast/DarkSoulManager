import kotlinx.serialization.Serializable

@Serializable
class Joueur(
    override val nom:String="inconnu",
    var chaineEquipementSerialisee: String ="",
    var details: String ="",
    var caracOrigin: Carac = Carac(),
    var caracActuel: Carac = Carac(),
) : ApiableItem() {

    override val id = nom.hashCode()
    override var isAttached = false

    override fun getStatsAsStrings():String{
        return chaineEquipementSerialisee.replace(CHAR_SEP_EQUIPEMENT+CHAR_SEP_EQUIPEMENT,"\n") +
                "\n"+details
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem{
        return Joueur(
            listCSVElement[0].cleanupForDB(),
            listCSVElement[1],
            listCSVElement[2],
            Carac.fromCSV(listCSVElement[3]),
            Carac.fromCSV(listCSVElement[4])
        )
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "equipement : ${CHAR_SEP_EQUIPEMENT}String$CHAR_SEP_EQUIPEMENT${CHAR_SEP_EQUIPEMENT}String${CHAR_SEP_EQUIPEMENT}",
            "details : String",
            "caracOrigin : vie/force/EffectType:Int|Effect:Int.../intelligence/energie",
            "caracActuel : vie/force/EffectType:Int|Effect:Int.../intelligence/energie"
        )
    }

    override fun getDeparsedAttributes(): List<String> {
        return listOf<String>(
            nom,
            chaineEquipementSerialisee,
            details,
            caracOrigin.toCSV(),
            caracActuel.toCSV(),
        )
    }
}