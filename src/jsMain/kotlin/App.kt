import io.ktor.util.logging.*
import react.*
import kotlinx.coroutines.*
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.ul

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
    ul {
        bddList.forEach { item : IListItem ->
            val text = item.toDescription()
            div {
                text.split("\n").mapIndexed { index: Int, s: String ->
                    div {
                        key = index.toString()
                        +s
                    }
                }
            }
        }
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
            bddList = listOf()
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
}

fun createMonsterFromInput(input:String):Monster{
    return Monster(input.replace("!", ""), input.count { it == '!' },mapOf(Pair<Int,Int>(1,1),Pair<Int,Int>(2,3),Pair<Int,Int>(3,5)),
    listDrops = mapOf(Pair<String,Int>("marteau",4),Pair<String,Int>("jambiere",5)),ames=90)
}

fun createArmeFromInput(input:String):Arme{
    return Arme(input.replace("!", "").replace("*",""), input.count { it == '!' }.toString(),
        mapOf(Pair("0",listOf(1,2)),Pair("1",listOf(3,4)),Pair("2",listOf(5,6))))
}