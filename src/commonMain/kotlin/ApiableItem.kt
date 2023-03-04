import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class ApiableItem : IListItem{
        @Transient
        val nameForApi = this::class.simpleName
        @Transient
        val updateNameForApi = "update$nameForApi"

        fun decomposeCSV(sequenceLinesFile: Sequence<String>):List<ApiableItem> {
                val listApiableItem = mutableListOf<ApiableItem>()
                var lineFiltered = sequenceLinesFile.drop(1)

                lineFiltered = lineFiltered.filter { it.isNotBlank() }

                lineFiltered.forEach {
                        val listCSVElementOnLine = it.split(";")

                        //If the line is empty we pass it
                        if(listCSVElementOnLine.first().isBlank()){
                                return@forEach
                        }

                        listApiableItem.add(parseFromCSV(listCSVElementOnLine))
                }
                return listApiableItem

        }

        fun parseDefense(inputElement:String): MutableMap<EffectType, String>{

                val mapDefenseType = mutableMapOf<EffectType,String>()

                if(inputElement.isNotEmpty()){
                        inputElement.split("|").forEach { currentDefense ->
                                currentDefense.split(":").let{ currentEffectType ->
                                        //on check si le type correspond bien a un vrai type
                                        mapDefenseType[EffectType.values().find { enumEffectType ->enumEffectType.shortname == currentEffectType.first() }!!] =
                                                currentEffectType.last()
                                }
                        }
                }

                return mapDefenseType
        }

        fun parseSeuilsForce(inputElement:String):MutableMap<Int, Int>{
                val mapSeuilsForce = mutableMapOf<Int,Int>()

                if(inputElement.isNotEmpty()){
                        inputElement.split("|").forEach { currentForceElement ->
                                currentForceElement.split(":").let{ currentSeuilForce ->
                                        //on check si le type correspond bien a un vrai type
                                        mapSeuilsForce[currentSeuilForce.first().toInt()] = currentSeuilForce.last().toInt()
                                }
                        }
                }

                return mapSeuilsForce
        }

        fun parseDrops(inputElement:String):MutableMap<String, Int>{
                val mapDrops = mutableMapOf<String,Int>()

                if(inputElement.isNotEmpty()){
                        inputElement.split("|").forEach { currentDropElement ->
                                currentDropElement.split(":").let{ currentSeuildrop ->
                                        //on check si le type correspond bien a un vrai type
                                        mapDrops[currentSeuildrop.first()] = currentSeuildrop.last().toInt()
                                }
                        }
                }

                return mapDrops
        }

        fun parseSeuils(inputElement:String): Map<String,List<Int>>{
                val listSeuils = mutableListOf<Int>()
                val mapSeuils = HashMap<String,List<Int>>()
                if(inputElement.isNotEmpty()){
                        inputElement.split("|").forEach{
                                val listSeuilsParfFacteur =it.split("=")
                                listSeuilsParfFacteur.first().let{ itInutilise ->
                                        itInutilise.split("/").forEach{itSeuils ->
                                                listSeuils.add(itSeuils.toInt())
                                        }
                                        mapSeuils[listSeuilsParfFacteur.last()] = listSeuils.toList()
                                        listSeuils.clear()
                                }
                        }
                }
                return mapSeuils
        }

        protected abstract fun parseFromCSV(listCSVElement : List<String>):ApiableItem
}