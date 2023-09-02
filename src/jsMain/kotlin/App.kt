import io.ktor.util.logging.*
import react.*
import kotlinx.coroutines.*
import mui.lab.TabContext
import mui.lab.TabList
import mui.lab.TabPanel
import mui.material.*

import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.p

val scope = MainScope()

val App = FC<Props> {

    var currentSelectedItem: IListItem by useState(Monster())

    var bddList: List<IListItem> by useState(emptyList<IListItem>())

    var joueurNom by useState("Test")

    var activeTab by useState("1")

    var (openSnack, setOpenSnack) = useState(false)
    var (textSnack, setTextSnack) = useState("Nothing")

    var (simpleRules, setSimpleRules) = useState(false)


    fun navigateToEditTab(selectedItem: IListItem){
        currentSelectedItem = selectedItem
        activeTab="2"
    }

    fun saveEditedItem(listAttributesAsString: List<String>, isDeleting:Boolean=false){
        scope.launch {
            try {
                val itemParsed = (currentSelectedItem as ApiableItem).parseFromCSV(listAttributesAsString)

                if(isDeleting){
                    if(deleteItem(itemParsed)){
                        setTextSnack("${itemParsed.nom} Supprimé")
                    }else{
                        setTextSnack("Erreur - ${itemParsed.nom} suppression impossible")
                    }
                }else{
                    if(updateItem(itemParsed)){
                        setTextSnack("${itemParsed.nom} mis à jour")
                    }else{
                        setTextSnack("Erreur - ${itemParsed.nom} mise à jour impossible")
                    }
                }
            } catch (e: Exception) {
                logger.debug("Erreur parsing ${e.stackTraceToString()}")
                setTextSnack(e.message.toString())
            }
            setOpenSnack(true)
        }
    }

    useEffectOnce {
        scope.launch {
            setSimpleRules(true)
        }
    }
    Box {
        TabContext {
            value = activeTab
            Box {
                Tabs {
                    onChange = { _, newValue -> activeTab = newValue }
                    ariaLabel = "wrapped label tabs example"

                    Tab {
                        label = ReactNode("Recherche")
                        value = "1"
                    }
                    Tab {
                        label = ReactNode("Edition")
                        value = "2"
                    }
                    Tab {
                        label = ReactNode("Joueur")
                        value = "3"
                    }
                }
            }
            TabPanel {
                value = "1"
                h1 {
                    +"Dark Soul Recherche"
                }
                Checkbox{
                    onChange = {_ ,checkedres-> setSimpleRules(checkedres)}
                }
                inputComponent {
                    onSubmit = { input ->
                        //Ici on va chercher les items dans la bdd
                        scope.launch {
                            val itemsFound: List<IListItem> = searchAnything(input.cleanupForDB())
                            logger.debug("Cherche un element $input : ${itemsFound.joinToString { it.nom }}")
                            if (itemsFound.isNotEmpty())
                            {
                                mutableListOf<IListItem>().let{
                                    it.addAll(bddList)
                                    it.addAll(itemsFound)
                                    bddList = it
                                }
                            }
                        }
                    }
                }
                button{
                    onClick = {
                        bddList = bddList.filter{it.isAttached}
                    }
                    + "Clear"
                }
                Grid {
                    bddList.forEach {
                        itemListComponent{
                            editMode = true
                            simpleRulesOn = simpleRules
                            itemList = it
                            navigateToEditTablistener=::navigateToEditTab
                        }
                    }
                }
            }
            TabPanel {
                value = "2"
                h1 {
                    +"${currentSelectedItem.nom}"
                }
                tabTextFieldComponent{
                    itemList = currentSelectedItem
                    saveModifiedItem = ::saveEditedItem
                }
            }
            TabPanel {
                value = "3"
                h1 {
                    +"Joueur"
                }
                joueurStuffComponent{
                    editMode = false
                    simpleRulesOn = simpleRules
                }
            }
        }
        Snackbar{
            open = openSnack
            onClose = {_,_->setOpenSnack(false)}
            message = ReactNode(textSnack)
            autoHideDuration = 5000
        }
    }



}