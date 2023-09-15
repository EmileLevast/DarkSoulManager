import csstype.*
import mui.material.*
import csstype.Display.Companion.inlineBlock
import react.dom.html.ReactHTML.button

import kotlinx.css.button
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.useState

external interface IListItemComponent:Props{
    var editMode:Boolean
    var simpleRulesOn:Boolean
    var itemList: IListItem
    var navigateToEditTablistener:(IListItem)->Unit

}

inline var GridProps.xs: Int
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().xs = value
    }

inline var TabsProps.ariaLabel: String
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic()["aria-label"] = value
    }

val itemListComponent = FC<IListItemComponent>{ props ->

    var isCardAttached:Boolean by useState(props.itemList.isAttached)
    var simplifiedRulesActive:Boolean by useState(props.simpleRulesOn)
    val editionListener:(IListItem)->Unit = props.navigateToEditTablistener

  Grid {

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
              if(props.editMode){
                  button{
                      onClick = {
                          props.itemList.isAttached = !props.itemList.isAttached
                          isCardAttached = props.itemList.isAttached
                      }
                      + "Pin"
                  }
              }

              Typography{
                  variant = TypographyVariant.h4
                  if((props.itemList as? Sort)?.type ==  SpellType.ARACHNOMANCIE){

                  }
                  +props.itemList.nom
                  if(isCardAttached&&props.editMode){
                      +"📌"
                  }
              }

              val statsArme = if(simplifiedRulesActive){
                  try {
                      props.itemList.getStatsSimplifiedAsStrings()
                  } catch (e: Exception) {
                    "Erreur parsing ${e.stackTraceToString()}"
                  }
              }else{
                  props.itemList.getStatsAsStrings()
              }
              statsArme.split("\n").mapIndexed { index: Int, s: String ->
                  Typography {
                      variant = TypographyVariant.h6
                        +s
                    }
                }

              if(props.editMode)
              {
                  button{
                      onClick = {
                          editionListener(props.itemList)
                      }
                      + "Edit"
                  }
              }
            }
      }
  }
}