import kotlinx.serialization.Serializable

@Serializable
class Sort(

) :ApiableItem(){
    override fun parseFromCSV(listCSVElement: List<String>): ApiableItem {
        TODO("Not yet implemented")
    }

    override val id: Int = nom.hashCode()
    override val nom: String
        get() = TODO("Not yet implemented")
    override var isAttached: Boolean = false



    override fun getStatsAsStrings(): String {
        TODO("Not yet implemented")
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        TODO("Not yet implemented")
    }

    override fun getDeparsedAttributes(): List<String> {
        TODO("Not yet implemented")
    }


}