import csstype.*
import mui.material.*
import csstype.Display.Companion.inlineBlock
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.useState

external interface IListItemComponent:Props{
    var itemList: IListItem

}

inline var GridProps.xs: Int
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().xs = value
    }

val itemListComponent = FC<IListItemComponent>{ props ->

    var isCardAttached:Boolean by useState(props.itemList.isAttached)

  Grid {
      onClick = {
          props.itemList.isAttached = !props.itemList.isAttached
          isCardAttached = props.itemList.isAttached
      }
      sx{
          display = inlineBlock
          margin = 10.px
      }
      Card {
          sx{
              backgroundColor = convertClassToColor(props.itemList)
              width=400.px
          }
          CardContent {
              Typography{
                  variant = TypographyVariant.h4
                  +props.itemList.nom
                  if(isCardAttached){
                      +"📌"
                  }
              }

              props.itemList.getStatsAsStrings().split("\n").mapIndexed { index: Int, s: String ->
                  Typography {
                      variant = TypographyVariant.h6
                        +s
                    }
                }
            }
      }
  }
}