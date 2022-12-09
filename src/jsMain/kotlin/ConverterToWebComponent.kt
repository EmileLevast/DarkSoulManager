import csstype.NamedColor

fun convertClassToColor(item:IListItem):NamedColor{
    return when(item){
        is Armure -> NamedColor.lightblue
        is Arme -> NamedColor.lightgray
        else -> NamedColor.white
    }
}