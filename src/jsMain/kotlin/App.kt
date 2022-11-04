import io.ktor.util.logging.*
import react.*
import kotlinx.coroutines.*
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ul

private val scope = MainScope()
private val logger = KtorSimpleLogger("logger")

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
            li {
                key = item.toString()
//                onClick = {
//                    scope.launch {
//                        deleteMonsterListItem(item)
//                        monsterList = getMonsterList()
//                    }
//                }
                +item.toDescription()
            }
        }
    }
    inputComponent {
        onSubmit = { input ->
            if(input.length > 0 && input.get(0) != '*')//si on veut creer un monstre on met pas d'etoiles
            {
                val monster = createMonsterFromInput(input)//on cree le monstre
                scope.launch {
                    addMonsterListItem(monster)//on enregistre le monstre dans la bdd
                }
            }else if(input.length > 0 && input.get(0) == '*')//si ça commence par une etoile on ajoute une arme
            {
                val arme = createArmeFromInput(input)//on cree le arme
                scope.launch {
                    addArmesListItem(arme)//on enregistre le arme dans la bdd
                }
            }

        }
    }
    inputComponent {
        onSubmit = { input ->
            //Ici on va chercher les items dans la bdd
            scope.launch {
                val item: IListItem? = searchOneSpecificArme(input) ?: searchOneSpecificMonster(input)
                logger.debug("Cherche un element $input : $item")
                //
                if (item != null)
                {
                    mutableListOf<IListItem>().let{
                        it.addAll(bddList)
                        it.add(item)
                        bddList = it
                    }
                }
            }
        }
    }
}

fun createMonsterFromInput(input:String):Monster{
    return Monster(input.replace("!", ""), input.count { it == '!' })
}

fun createArmeFromInput(input:String):Arme{
    return Arme(input.replace("!", "").replace("*",""), input.count { it == '!' },
        mapOf<String,List<Int>>(Pair("0",listOf(1,2)),Pair("1",listOf(3,4)),Pair("2",listOf(5,6))) as HashMap<String, List<Int>>)
}