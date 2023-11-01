import kotlinx.serialization.Serializable

@Serializable
class Equipe(
    override val nom:String="inconnu",
    var chaineJoueurSerialisee: String ="",
) : ApiableItem() {

    override val id = nom.hashCode()
    override var isAttached = false

    override fun getStatsAsStrings():String{
        return "Equipe : \n"+chaineJoueurSerialisee.replace(CHAR_SEP_EQUIPEMENT+CHAR_SEP_EQUIPEMENT,"\n")
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem{
        return Equipe(
            listCSVElement[0].cleanupForDB(),
            listCSVElement[1],
        )
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "membres : ${CHAR_SEP_EQUIPEMENT}String$CHAR_SEP_EQUIPEMENT${CHAR_SEP_EQUIPEMENT}String${CHAR_SEP_EQUIPEMENT}"
        )
    }

    override fun getDeparsedAttributes(): List<String> {
        return listOf(
            nom,
            chaineJoueurSerialisee,
        )
    }
}