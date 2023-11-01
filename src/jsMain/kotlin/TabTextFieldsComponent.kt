import csstype.Display
import csstype.NamedColor
import csstype.px
import kotlinx.coroutines.launch
import kotlinx.css.Color
import kotlinx.css.Color.Companion.aliceBlue
import kotlinx.html.TEXTAREA
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import org.w3c.dom.*
import react.*
import react.dom.events.ChangeEventHandler
import react.dom.events.FormEventHandler
import react.dom.html.InputType
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.textarea
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.dialog
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.h6
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.p
import react.dom.onChange

external interface TabTextFieldProps : Props {
    var itemList: IListItem
    var saveModifiedItem:(List<String>,Boolean)->Unit
}

val tabTextFieldComponent = FC<TabTextFieldProps> { props ->
    var listModifiedAttribute = mutableListOf<String>()
    val listDeparsedAttributes = props.itemList.getDeparsedAttributes()

    var isOpeningDialog by useState(false)

    var listJoueurs: List<Joueur> by useState(emptyList<Joueur>())

    useEffectOnce {
        scope.launch {
            listJoueurs = searchJoueur(".*") ?: listOf<Joueur>(Joueur())
        }
    }

    Stack{
        Grid{
            listJoueurs.forEach { it ->

                Grid{
                    sx{
                        display = Display.inlineBlock
                        margin = 10.px
                    }
                    Card{
                        sx{
                            backgroundColor = convertClassToColor(props.itemList)
                            height=100.px
                            width=100.px
                        }
                        CardContent{
                            Checkbox{
                                onChange = { _,checkedRes ->

                                    scope.launch {
                                        if(checkedRes){
                                            it.chaineEquipementSerialisee += CHAR_SEP_EQUIPEMENT+props.itemList.nom+CHAR_SEP_EQUIPEMENT
                                        }else{
                                            it.chaineEquipementSerialisee = it.chaineEquipementSerialisee.replace("${CHAR_SEP_EQUIPEMENT}${props.itemList.nom}$CHAR_SEP_EQUIPEMENT","")
                                        }
                                        updateItem(it)
                                    }
                                }
                                defaultChecked = it.chaineEquipementSerialisee.contains("${CHAR_SEP_EQUIPEMENT}${props.itemList.nom}$CHAR_SEP_EQUIPEMENT")
                            }
                            ReactHTML.h6{
                                +it.nom
                            }
                        }
                    }
                }

            }


        }

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