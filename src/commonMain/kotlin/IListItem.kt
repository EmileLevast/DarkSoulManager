interface IListItem {
    val id:Int
    val nom:String
    var isAttached:Boolean
    fun getStatsAsStrings():String
    fun getParsingRulesAttributesAsList():List<String>
}

