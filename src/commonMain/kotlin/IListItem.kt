interface IListItem {


    val _id:Int
    val nom:String
    var isAttached:Boolean
    fun getStatsAsStrings():String
    fun getStatsSimplifiedAsStrings():String
    fun getParsingRulesAttributesAsList():List<String>
    fun getDeparsedAttributes():List<String>
}

