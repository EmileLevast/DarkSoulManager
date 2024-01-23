import kotlinx.coroutines.launch
import mui.material.*
import mui.material.styles.TypographyVariant
import org.w3c.dom.*
import react.*
import react.dom.events.ChangeEventHandler
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.dialog
import react.dom.html.ReactHTML.p

external interface TabTextFieldProps : Props {
    var itemList: IListItem
    var saveModifiedItem:(List<String>, ActionOnDb)->Unit
}

val tabTextFieldComponent = FC<TabTextFieldProps> { props ->
    var listModifiedAttribute = mutableListOf<String>()
    val listDeparsedAttributes = props.itemList.getDeparsedAttributes()

    var isOpeningDialog by useState(false)

    var listJoueurs: List<Joueur> by useState(emptyList())
    var listEquipes: List<Equipe> by useState(emptyList())

    useEffectOnce {
        scope.launch {
            listJoueurs = searchJoueur(".*") ?: listOf()
        }
        scope.launch {
            listEquipes = searchEquipe(".*")?: listOf()
        }
    }

    Stack{
//        Grid{
//            listJoueurs.forEach { it ->
//
//                Grid{
//                    sx{
//                        display = Display.inlineBlock
//                        margin = 10.px
//                    }
//                    Card{
//                        sx{
//                            backgroundColor = convertClassToColor(props.itemList)
//                            height=100.px
//                            width=100.px
//                        }
//                        CardContent{
//                            Checkbox{
//                                onChange = { _,checkedRes ->
//
//                                    scope.launch {
//                                        if(checkedRes){
//                                            it.chaineEquipementSerialisee += CHAR_SEP_EQUIPEMENT+props.itemList.nom+CHAR_SEP_EQUIPEMENT
//                                        }else{
//                                            it.chaineEquipementSerialisee = it.chaineEquipementSerialisee.replace("${CHAR_SEP_EQUIPEMENT}${props.itemList.nom}$CHAR_SEP_EQUIPEMENT","")
//                                        }
//                                        updateItem(it)
//                                    }
//                                }
//                                defaultChecked = it.chaineEquipementSerialisee.contains("${CHAR_SEP_EQUIPEMENT}${props.itemList.nom}$CHAR_SEP_EQUIPEMENT")
//                            }
//                            ReactHTML.h6{
//                                +it.nom
//                            }
//                        }
//                    }
//                }
//
//            }
//
//
//        }

        //Permet d'assigner un equipement à un joueur
        gridSelectComponent{
            itemsList = listJoueurs
            onCheckAction = { itemToUpdate , checkedRes ->
                val joueurToUpdate = itemToUpdate as? Joueur
                if(joueurToUpdate!=null){
                    if (checkedRes) {
                        joueurToUpdate.chaineEquipementSerialisee += CHAR_SEP_EQUIPEMENT + props.itemList.nom + CHAR_SEP_EQUIPEMENT
                    } else {
                        joueurToUpdate.chaineEquipementSerialisee = joueurToUpdate.chaineEquipementSerialisee.replace(
                            "${CHAR_SEP_EQUIPEMENT}${props.itemList.nom}$CHAR_SEP_EQUIPEMENT",
                            ""
                        )
                    }
                }
            }
            isDefaultCheck = {
                val joueurToUpdate = it as? Joueur
                if(joueurToUpdate!=null) {
                    it.chaineEquipementSerialisee.contains("${CHAR_SEP_EQUIPEMENT}${props.itemList.nom}$CHAR_SEP_EQUIPEMENT")
                }else{
                    false
                }
            }
        }

        //permet d'assigner un element à une équipe
        gridSelectComponent{
            itemsList = listEquipes
            onCheckAction = { itemToUpdate , checkedRes ->
                val equipeToUpdate = itemToUpdate as? Equipe
                if(equipeToUpdate!=null){
                    if (checkedRes) {
                        equipeToUpdate.chaineDecouvertSerialisee += CHAR_SEP_EQUIPEMENT + props.itemList.nom + CHAR_SEP_EQUIPEMENT
                    } else {
                        equipeToUpdate.chaineDecouvertSerialisee = equipeToUpdate.chaineDecouvertSerialisee.replace(
                            "${CHAR_SEP_EQUIPEMENT}${props.itemList.nom}$CHAR_SEP_EQUIPEMENT",
                            ""
                        )
                    }
                }
            }
            isDefaultCheck = {
                val equipeToUpdate = it as? Equipe
                if(equipeToUpdate!=null) {
                    it.chaineDecouvertSerialisee.contains("${CHAR_SEP_EQUIPEMENT}${props.itemList.nom}$CHAR_SEP_EQUIPEMENT")
                }else{
                    false
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
                props.saveModifiedItem(listModifiedAttribute,ActionOnDb.UPDATE)
            }
            Typography{
                variant = TypographyVariant.h4
                +"Update"
            }
        }
        button{
            onClick = {
                props.saveModifiedItem(listModifiedAttribute,ActionOnDb.INSERT)
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
                    onClick = { isOpeningDialog = false; props.saveModifiedItem(listModifiedAttribute,ActionOnDb.DELETE) }
                    +"Sure Delete ?"
                }
            }
        }
    }
}