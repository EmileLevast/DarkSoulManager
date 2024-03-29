

interface IListItem {
    val _id:Int
    val nom:String
    val nomComplet:String
    var isAttached:Boolean
    val imageName:String
        get() = "${nom.cleanupForDB().replace(" ","")}.jpg"

    fun getStatsAsStrings():String
    fun getStatsSimplifiedAsStrings():String
    fun getParsingRulesAttributesAsList():List<String>
    fun getDeparsedAttributes():List<String>

}

