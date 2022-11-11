import io.ktor.util.logging.*
import react.*
import kotlinx.coroutines.*
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.p
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
        mapOf<String,List<Int>>(Pair("0",listOf(1,2)),Pair("1",listOf(3,4)),Pair("2",listOf(5,6))))
}


fun uploadArmes(string : String):List<Arme>{
    val listArme = mutableListOf<Arme>()
    string.split("\n").forEach {
        val listCSV = it.split(";")

        logger.debug("voici la ligne :$it")
        //Seuils
        val seuilsCSV = listCSV[2]
        val listSeuils = mutableListOf<Int>()
        val seuils = HashMap<String,List<Int>>()
        logger.debug("ok on est avant seuil")
        seuilsCSV.split("|").forEach{
            logger.debug("un elemeznt de sueil $it")// TODO le it fait reference a la lambda exterieure
            val listSeuilsParfFacteur =it.split("=")
            listSeuilsParfFacteur.first().let{ itInutilise ->
                logger.debug("on a les differents seuils $itInutilise")
                itInutilise.split("/").forEach{itSeuils ->
                    listSeuils.add(itSeuils.toInt())
                }
                seuils[listSeuilsParfFacteur.last()] = listSeuils
                listSeuils.clear()
            }
        }
        logger.debug("ok on est apres seuil")


        listArme.add(
            Arme(
                listCSV[0],
                listCSV[1],
                seuils,
                listCSV[3],
                listCSV[4].toInt(),
                listCSV[5].toInt(),
                listCSV[6].toInt(),
                listCSV[7].toInt(),
                listCSV[8].toInt(),
                listCSV[9].toInt(),
                listCSV[10]
            )
        )
    }

    return listArme
}