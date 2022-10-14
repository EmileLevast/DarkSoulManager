import react.*
import kotlinx.coroutines.*
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ul

private val scope = MainScope()

val App = FC<Props> {
    var monsterList:List<Monster> by useState(emptyList<Monster>())

    useEffectOnce {
        scope.launch {
            monsterList = getMonsterList()
        }
    }

    h1 {
        +"Monster List"
    }
    ul {
        monsterList.forEach { item ->
            li {
                key = item.toString()
                onClick = {
                    scope.launch {
                        deleteMonsterListItem(item)
                        monsterList = getMonsterList()
                    }
                }
                +"[${item.life}] ${item.name} "
            }
        }
    }
    inputComponent {
        onSubmit = { input ->
            val cartItem = Monster(input.replace("!", ""), input.count { it == '!' })
            scope.launch {
                addMonsterListItem(cartItem)
                monsterList = getMonsterList()
            }
        }
    }
}