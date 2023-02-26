import csstype.NamedColor

//TODO add color here when creating new item
fun convertClassToColor(item:IListItem):NamedColor{
    return when(item){
        is Armure -> NamedColor.lightblue
        is Arme -> NamedColor.lightgray
        is Monster -> NamedColor.red
        else -> NamedColor.white
    }
}