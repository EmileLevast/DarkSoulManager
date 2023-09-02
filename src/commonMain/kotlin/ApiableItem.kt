import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class ApiableItem : IListItem{
        @Transient
        val nameForApi = this::class.simpleName
        @Transient
        val uploadFileForApi = "uploadFile$nameForApi"
        @Transient
        val updateForApi = "update$nameForApi"
        @Transient
        val deleteForApi = "delete$nameForApi"
        @Transient
        val downloadForApi = "download$nameForApi"

        fun decomposeCSV(sequenceLinesFile: Sequence<String>):List<ApiableItem> {
                val listApiableItem = mutableListOf<ApiableItem>()
                val numberOfSemiColonByLine = sequenceLinesFile.first().count{it == ';'}
                var lineFiltered = sequenceLinesFile.drop(1).toList()

                lineFiltered = lineFiltered.filter { it.isNotBlank() }

                var i = 0
                while(i<lineFiltered.size){
                        var currentLine = lineFiltered[i]

                        //recontruire la ligne si elle est ecrite sur plusieurs lignes
                        //Soit la ligne actuelle ne compte pas assez de points virgule (y'a un bout de texte qui deborde au milieu de la ligne
                        //Soit la ligne suivante ne compte pas de ; alors c'est la description qui deborde)
                        while((i < lineFiltered.size && currentLine.count{it == ';'}<numberOfSemiColonByLine) ||
                                (i+1<lineFiltered.size && lineFiltered[i+1].count{it==';'} == 0)
                                ){
                                //alors on cumule avec la ligne suivante
                                i++
                                currentLine+="\n"+lineFiltered[i]
                        }

                        val listCSVElementOnLine = currentLine.split(";")

                        //If the line is empty we pass it
                        if(!listCSVElementOnLine.first().isBlank()){
                                listApiableItem.add(parseFromCSV(listCSVElementOnLine))
                        }

                        i++
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

        fun parseSpellType(inputElement:String):SpellType{
                return SpellType.values().find { it.name == inputElement }?: SpellType.AME
        }

        fun parseSpecialItemType(inputElement:String):SpecialItemType{
                return SpecialItemType.values().find { it.name == inputElement }?: SpecialItemType.OUTIL
        }

        fun deparseSeuils(seuils:Map<String,List<Int>>):String{
                var res = ""
                seuils.forEach { seuil ->
                        seuil.value.forEach {
                                res+= "$it/"
                        }
                        res=res.removeSuffix("/")
                        res+="="+seuil.key+"|"
                }

                return res.removeSuffix("|")
        }

        fun deparseDefense(defense:Map<EffectType,String>):String{
                var res = ""
                defense.forEach {
                        res+=it.key.shortname+":"+it.value+"|"
                }
                return res.removeSuffix("|")
        }

        fun deparseForce(defense:Map<Int,Int>):String{
                var res = ""
                defense.forEach {
                        res+=it.key.toString()+":"+it.value+"|"
                }
                return res.removeSuffix("|")
        }

        fun deparseListDrops(drops:Map<String,Int>):String{
                var res = ""
                drops.forEach {
                        res+=it.key+":"+it.value+"|"
                }
                return res.removeSuffix("|")
        }

        abstract fun parseFromCSV(listCSVElement : List<String>):ApiableItem

        override fun getStatsSimplifiedAsStrings(): String {
                return getStatsAsStrings()
        }
}