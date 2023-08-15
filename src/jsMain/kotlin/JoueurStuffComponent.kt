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

    var joueurSelected by useState(Joueur())

    useEffectOnce {
        scope.launch {
            listJoueurs = searchJoueur(".*") ?: listOf<Joueur>()
        }
    }

    Stack {
        Button {
            onClick = {
                scope.launch {
                    listJoueurs = searchJoueur(".*") ?: listOf<Joueur>()
                    tempList.clear()
                    joueurSelected = listJoueurs.first { it.nom == joueurSelected.nom }
                    joueurSelected.chaineEquipementSerialisee.split("$CHAR_SEP_EQUIPEMENT$CHAR_SEP_EQUIPEMENT")
                        .forEach { itemSearched ->
                            tempList.add(searchAnything(itemSearched.replace("|", "")).first())
                        }
                    listItem = tempList.toList()
                }
            }
            +"Refresh"
        }
        Select {
            labelId = "demo-simple-select-label"
            id = "demo-simple-select"
            value = joueurSelected.nom
            label = ReactNode("Nom")
            onChange = { event, _ ->
                joueurSelected = listJoueurs.first { it.nom == event.target.value }
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
