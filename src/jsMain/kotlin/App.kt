import io.ktor.util.logging.*
import react.*
import kotlinx.coroutines.*
import mui.material.Grid
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.h1

private val scope = MainScope()
val logger = KtorSimpleLogger("logger")

val App = FC<Props> {

    var bddList:List<IListItem> by useState(emptyList<IListItem>())

    useEffectOnce {
        scope.launch {
            //monsterList = getMonsterList()
            //bddList = getBddList()
        }
    }

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