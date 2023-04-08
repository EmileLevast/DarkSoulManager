import io.ktor.util.logging.*
import react.*
import kotlinx.coroutines.*
import mui.lab.TabContext
import mui.lab.TabList
import mui.lab.TabPanel
import mui.material.Box

import mui.material.Grid
import mui.material.Tab
import mui.material.Tabs
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.h1

private val scope = MainScope()
val logger = KtorSimpleLogger("logger")

val App = FC<Props> {

    var bddList: List<IListItem> by useState(emptyList<IListItem>())

    var activeTab by useState("1")

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
                        label = ReactNode("1")
                        value = "1"
                    }
                    Tab {
                        label = ReactNode("2")
                        value = "2"
                    }
                }
            }
            TabPanel {
                value = "1"
                h1 {
                    +"Dark Soul List"
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
                        }
                    }
                }
            }
            TabPanel {
                value = "2"
                +"Aurevoir"
            }
        }
    }
//    <Box sx={{ width: "100%", typography: "body1" }}>
//    <TabContext value={value}>
//    <Box sx={{ borderBottom: 1,
//    borderColor: "divider" }}>
//    <TabList
//    onChange={handleChange}
//    aria-label="lab API tabs example"
//    >
//    <Tab label="Tutorial 1" value="1" />
//    <Tab label="Tutorial 2" value="2" />
//    <Tab label="Tutorial 3" value="3" />
//    </TabList>
//    </Box>
//    <TabPanel value="1">Data Structures</TabPanel>
//    <TabPanel value="2">Algorithms</TabPanel>
//    <TabPanel value="3">Web Development</TabPanel>
//    </TabContext>
//    </Box>


}