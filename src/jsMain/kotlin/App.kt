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
                val itemsFound: List<IListItem>? = searchArmes(input) ////TODO add fnction to call monsters ?: searchOneSpecificMonster(input)
                logger.debug("Cherche un element $input : ${itemsFound?.joinToString { it.nom }}")
                //
                if (itemsFound != null)
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
    button{
        onClick = {
            scope.launch {
//                uploadArmes(blob).forEach {
//                    logger.debug(it.toDescription())
//                }
            }
        }
        + "readFile"
    }
    Grid {
        bddList.forEach {
            itemListComponent{
                listItem = it
            }
        }
    }
}