import kotlinx.coroutines.launch
import mui.material.*
import react.FC
import react.Props
import react.ReactNode
import react.useState

external interface JoueurStuffComponentProps:Props{
    var editMode:Boolean
}

val joueurStuffComponent = FC<JoueurStuffComponentProps> { props ->

    var listJoueurs: List<Joueur> by useState(emptyList<Joueur>())

    scope.launch {
        listJoueurs = searchJoueur(".*") ?: listOf<Joueur>()
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
            }

            listJoueurs.forEach {
                MenuItem {
                    value = it.nom
                    +it.nom
                }
            }
        }

        Grid {
            joueurSelected.listEquipement.forEach {
                itemListComponent {
                    itemList = it
                    navigateToEditTablistener = { _->Unit }
                }
            }
        }

    }
}