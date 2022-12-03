
interface IListItem {
    val id:Int
    val nom:String
    var isAttached:Boolean
    fun toDescription():String
    fun getStatsAsStrings():String

}