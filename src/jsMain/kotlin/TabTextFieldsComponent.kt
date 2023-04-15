import react.FC
import react.Props
import react.useState

external interface TabTextFieldProps : Props {
    var itemList: IListItem
}

val tabTextFieldComponent = FC<TabTextFieldProps> { props ->
    var listParcelableAttribute:List<String>
}