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

private val scope = MainScope()
val logger = KtorSimpleLogger("logger")

val App = FC<Props> {

    var currentSelectedItem: IListItem by useState(Monster())

    var bddList: List<IListItem> by useState(emptyList<IListItem>())

    var activeTab by useState("1")

    fun navigateToEditTab(selectedItem: IListItem){
        currentSelectedItem = selectedItem
        activeTab="2"
    }

    useEffectOnce {
        scope.launch {
            //monsterList = getMonsterList()
            //bddList = getBddList()
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
                }
            }
            TabPanel {
                value = "1"
                h1 {
                    +"Dark Soul Recherche"
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
                }
            }
        }
    }



}