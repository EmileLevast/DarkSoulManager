import csstype.NamedColor

//TODO add color here when creating new item
fun convertClassToColor(item:IListItem):NamedColor{
    return when(item){
        is Armure -> NamedColor.lightblue
        is Arme -> NamedColor.lightgray
        is Monster -> NamedColor.red
        is Bouclier -> NamedColor.cornflowerblue
        is Sort -> {
            return when(item.type){
                SpellType.AME -> NamedColor.ghostwhite
                SpellType.MIRACLE -> NamedColor.aliceblue
                SpellType.PYROMANCIE -> NamedColor.orange
                SpellType.PSIONIQUE -> NamedColor.mediumpurple
                else ->  NamedColor.pink //necromancie
            }
        }
        is Special -> {
            return when(item.itemType){
                SpecialItemType.OUTIL-> NamedColor.saddlebrown
                SpecialItemType.AMBRE -> NamedColor.lightpink
                SpecialItemType.ANNEAU -> NamedColor.yellow
                SpecialItemType.BRAISE -> NamedColor.orangered
                SpecialItemType.TALISMAN -> NamedColor.lightgreen
            }
        }
        else -> NamedColor.white
    }
}