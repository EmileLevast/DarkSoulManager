import csstype.*
import mui.material.*
import csstype.Display.Companion.inlineBlock
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.useState

external interface IListItemComponent:Props{
    var listItem: IListItem

}

inline var GridProps.xs: Int
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().xs = value
    }

val itemListComponent = FC<IListItemComponent>{ props ->

    var isCardAttached:Boolean by useState(props.listItem.isAttached)

  Grid {
      onClick = {
          props.listItem.isAttached = !props.listItem.isAttached
          isCardAttached = props.listItem.isAttached
      }
      sx{
          display = inlineBlock
          margin = 10.px
      }
      Card {
          sx{
              backgroundColor = NamedColor.lightgray
              height=600.px
              width=400.px
          }
          CardContent {
              Typography{
                  variant = TypographyVariant.h4
                  +props.listItem.nom
                  if(isCardAttached){
                      +"📌"
                  }
              }

              props.listItem.getStatsAsStrings().split("\n").mapIndexed { index: Int, s: String ->
                  Typography {
                      variant = TypographyVariant.h6
                        +s
                    }
                }
            }
      }
  }
}