import kotlinx.coroutines.launch
import mui.material.*
import react.*

external interface JoueurStuffComponentProps : Props {
    var editMode: Boolean
}

val joueurStuffComponent = FC<JoueurStuffComponentProps> { props ->

    var listJoueurs: List<Joueur> by useState(emptyList<Joueur>())
    var listItem: List<IListItem> by useState(emptyList<IListItem>())
    var tempList = mutableListOf<IListItem>()

    useEffectOnce {
        scope.launch {
            listJoueurs = searchJoueur(".*") ?: listOf<Joueur>()
        }
    }

    var joueurSelected by useState(Joueur())

    Stack {
        Select {
            labelId = "demo-simple-select-label"
            id = "demo-simple-select"
            value = joueurSelected.nom
            label = ReactNode("Nom")
            onChange = { event, _ ->
                joueurSelected = listJoueurs.first { it.nom == event.target.value }
                tempList.clear()
                scope.launch {
                    joueurSelected.listEquipement.forEach { itemSearched ->
                        tempList.add(searchAnything(itemSearched).first())
                    }
                    listItem = tempList.toList()
                }

                listJoueurs.forEach {
                    MenuItem {
                        value = it.nom
                        +it.nom
                    }
                }
            }

            Grid {
                listItem.forEach {
                    itemListComponent {
                        itemList = it
                        navigateToEditTablistener = { _ -> Unit }
                    }
                }
            }
        }
    }
}