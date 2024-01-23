import csstype.Color
import csstype.Display
import csstype.NamedColor
import csstype.px
import kotlinx.coroutines.launch
import mui.material.Card
import mui.material.CardContent
import mui.material.Checkbox
import mui.material.Grid
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML

external interface GridSelectProps : Props {
    var itemsList: List<ApiableItem>
    var onCheckAction : (ApiableItem, Boolean)->Unit
    var isDefaultCheck : (IListItem)->Boolean
}

val gridSelectComponent = FC<GridSelectProps> { props ->

    val listToShow = props.itemsList

    Grid{
        listToShow.forEach {

            Grid{
                sx{
                    display = Display.inlineBlock
                    margin = 10.px
                }
                Card{
                    sx{
                        backgroundColor = listToShow.firstOrNull()?.let{convertClassToColor(it)}?:NamedColor.white
                        height=100.px
                        width=100.px
                    }
                    CardContent{
                        Checkbox{
                            onChange = { _,checkedRes ->

                                scope.launch {
                                    props.onCheckAction(it,checkedRes)
                                    updateItem(it)
                                }
                            }
                            defaultChecked = props.isDefaultCheck(it)
                        }
                        ReactHTML.h6{
                            +it.nom
                        }
                    }
                }
            }

        }


    }
}