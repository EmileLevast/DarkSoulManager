import mui.material.Box
import mui.material.LabelPlacement
import mui.material.Stack
import mui.material.TextField
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.ReactNode
import react.dom.events.ChangeEventHandler
import react.dom.events.FormEventHandler
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.h4
import react.dom.onChange
import react.useState

external interface TabTextFieldProps : Props {
    var itemList: IListItem
}

val tabTextFieldComponent = FC<TabTextFieldProps> { props ->
    var listModifiedAttribute = mutableListOf<String>()




    Stack{
        props.itemList.getParsingRulesAttributesAsList().forEach {

            val (text, setText) = useState("")
            val changeHandler: FormEventHandler<HTMLDivElement> = {
                setText(((it.target) as HTMLInputElement).value)
            }

            TextField{
                helperText = ReactNode(it)
                onChange = changeHandler
            }

            listModifiedAttribute.add(text)
        }

        button{
            onClick = {
                listModifiedAttribute.forEach {
                    logger.debug(">$it")
                }
            }
            + "Lecture"
        }
    }
}