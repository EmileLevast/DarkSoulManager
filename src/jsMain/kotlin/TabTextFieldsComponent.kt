import kotlinx.html.TEXTAREA
import mui.material.*
import mui.material.styles.TypographyVariant
import org.w3c.dom.HTMLAreaElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import react.FC
import react.Props
import react.ReactNode
import react.dom.events.ChangeEventHandler
import react.dom.events.FormEventHandler
import react.dom.html.InputType
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.textarea
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.dialog
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.p
import react.dom.onChange
import react.useState

external interface TabTextFieldProps : Props {
    var itemList: IListItem
    var saveModifiedItem:(List<String>,Boolean)->Unit
}

val tabTextFieldComponent = FC<TabTextFieldProps> { props ->
    var listModifiedAttribute = mutableListOf<String>()
    val listDeparsedAttributes = props.itemList.getDeparsedAttributes()

    var isOpeningDialog by useState(false)

    Stack{
        props.itemList.getParsingRulesAttributesAsList().forEachIndexed { index, formatRule ->
            val (text, setText) = useState(listDeparsedAttributes[index])
            val changeHandler: ChangeEventHandler<HTMLTextAreaElement> = {
                setText((it.target as HTMLTextAreaElement).value)
            }

            p{
                +formatRule
            }
            ReactHTML.textarea{
                defaultValue = listDeparsedAttributes[index]
                onChange = changeHandler
            }

            listModifiedAttribute.add(text)
        }

        button{
            onClick = {
                props.saveModifiedItem(listModifiedAttribute,false)
            }
            Typography{
                variant = TypographyVariant.h4
                +"Save"
            }
        }
        button{
            onClick = {
               isOpeningDialog=true
            }
            +"Delete"
        }
    }

    dialog {
        open = isOpeningDialog
        onClose = { _-> isOpeningDialog = false }

        DialogTitle {
            +"Delete"
        }
        DialogContent {
            DialogContentText {
                +"Do you really want to delete ${listModifiedAttribute.first()}"
            }
            DialogActions {
                Button {
                    onClick = { isOpeningDialog = false }
                    +"Cancel"
                }
                Button {
                    onClick = { isOpeningDialog = false; props.saveModifiedItem(listModifiedAttribute,true) }
                    +"Sure Delete ?"
                }
            }
        }
    }
}