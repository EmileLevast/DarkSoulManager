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

        protected fun computeCoupCritiqueToStringSimplifie(ccStr:String, parseDegat:Map<EffectType, Int>):String{
                val ccSplit = ccStr.split("=>�")
                var degatFinaux = parseDegat.mapValues { degatSeuil -> degatSeuil.value * try {
                        ccSplit.last().toInt()
                } catch (e: Exception) {
                        -1
                }
                }
                return ccSplit.first() + "=>" + degatFinaux.map{type->type.key.shortname+":"+type.value}.joinToString ("|" )
        }

        /**
         * En sortie Pair<String,String> le premier élément correspond au textes des seuils, le deuxième au texte des coups critiques
         */
        protected fun simplificationTextesSeuilsEtCc(valeurTypeInitial : String, seuilsInput: Map<String, List<Int>>, coupsCritiquesInput: String): Pair<String, String> {
            var textSeuils = ""

            //on recupere la string avec la valeur de defense ou de degats de base selon le type
            val temp = parseDefense(valeurTypeInitial.replace("P:", "Ph:"))
                val pair = simplificationTextesSeuilsEtCCPourMap(temp, seuilsInput, coupsCritiquesInput)

                return Pair(pair.first, pair.second)
        }

        /**
         * En sortie Pair<String,String> le premier élément correspond au textes des seuils, le deuxième au texte des coups critiques
         */
        protected fun simplificationTextesSeuilsEtCCPourMap(
                temp: Map<EffectType, String>,
                seuilsInput: Map<String, List<Int>>,
                coupsCritiquesInput: String
        ): Pair<String, String> {
                //dans cet Map on possède en clé le type et en valeur un entier qui correspond au degat de base
                var textSeuils = ""
                var parseValeurTypeInital = temp.mapValues {
                        try {
                                it.value.toInt()
                        } catch (e: Exception) {
                                -1
                        }
                }


                var facteur = 0
                var degatFinaux = mapOf<EffectType, Int>()
                //ici on va multiplier chaque valeur des seuils par la valeur des types initiaux
                seuilsInput.forEach {
                        try {
                                facteur = it.key.toInt()
                        } catch (e: Exception) {
                                facteur = -1
                        }


                        degatFinaux =
                                parseValeurTypeInital.mapValues { valeurSelonSeuil -> valeurSelonSeuil.value * facteur }

                        //dans cette liste chaque élément correspond à un seuil avec en valeur la multiplication entre le resultat du seuil et les valeurs de bases
                        val listDegatFinaux = degatFinaux.map { type -> type.key.shortname + ":" + type.value }

                        //Ici c'est une string avec tous les seuils de la liste précédente afficher sur plusieurs lignes
                        textSeuils += "|   ${it.value.joinToString("/")} =>${listDegatFinaux.joinToString("|")}\n"

                }

                var coupcCritiquesCalcules = strSimplify(coupsCritiquesInput, true)
                if (coupcCritiquesCalcules.isNotBlank() && coupcCritiquesCalcules.first().isDigit()) {

                        if (coupcCritiquesCalcules.contains("|")) {
                                val tempSplit = coupcCritiquesCalcules.split("|")
                                coupcCritiquesCalcules = ""
                                tempSplit.forEach {
                                        coupcCritiquesCalcules += computeCoupCritiqueToStringSimplifie(
                                                it,
                                                parseValeurTypeInital
                                        ) + "|"
                                }
                        } else {
                                coupcCritiquesCalcules = computeCoupCritiqueToStringSimplifie(
                                        coupcCritiquesCalcules,
                                        parseValeurTypeInital
                                )
                        }
                }
                return Pair(textSeuils, coupcCritiquesCalcules)
        }
}