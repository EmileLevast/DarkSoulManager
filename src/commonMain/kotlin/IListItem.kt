

interface IListItem {
    val _id:Int
    val nom:String
    var isAttached:Boolean
    val imageName:String
        get() = nom.cleanupForDB().trim()

    fun getStatsAsStrings():String
    fun getStatsSimplifiedAsStrings():String
    fun getParsingRulesAttributesAsList():List<String>
    fun getDeparsedAttributes():List<String>

}

