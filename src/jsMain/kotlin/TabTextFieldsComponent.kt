import mui.material.Box
import mui.material.Stack
import mui.material.TextField
import react.FC
import react.Props
import react.ReactNode
import react.useState

external interface TabTextFieldProps : Props {
    var itemList: IListItem
}

val tabTextFieldComponent = FC<TabTextFieldProps> { props ->
    var listParcelableAttribute:List<String>

    Stack{
        props.itemList.getParsingRulesAttributesAsList().forEach {
            TextField{
                helperText = ReactNode(it)
            }
        }
    }
}