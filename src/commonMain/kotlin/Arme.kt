import kotlinx.serialization.Serializable

@Serializable
class Arme(
    override val nom:String="inconnu",
    val degat:String="P:0",
    val seuils:Map<String,List<Int>> = mapOf(),//en clé c'est le facteur et en valeur c'est la liste des seuils associés
    val coupCritiques:String="",
    val maximumEnergie:Int=0,
    val seuilBlocage:Int=0,
    val valeurBlocage:Int=0,
    val contraintes:String="Aucune",
    val fajMax:Int=0,
    val poids:Int=0,
    val capaciteSpeciale:String=""
    ) : ApiableItem() {


    override val id = nom.hashCode()
    override var isAttached = false

    override fun getStatsAsStrings():String{
        var textSeuils = ""
        seuils.forEach {
            textSeuils += "|   ${it.value.joinToString("/")} => x${it.key}\n"
        }
        return  "$degat\n"+
                "Seuils:\n" + textSeuils +
                (if(coupCritiques.isNotBlank())"CC : $coupCritiques\n" else "") +
                "Max énergie : $maximumEnergie\n" +
                "Seuil de blocage : $seuilBlocage\n" +
                "Valeur de blocage : $valeurBlocage\n" +
                "FAJ Max : $fajMax\n" +
                (if(contraintes.isNotBlank())"$contraintes\n" else "") +
                "Poids : $poids\n" +
                "$capaciteSpeciale\n"
    }

    override fun parseFromCSV(sequenceLinesFile : Sequence<String>): List<ApiableItem> {

        val listArme = mutableListOf<Arme>()

        var lineFiltered = sequenceLinesFile.drop(1)
        lineFiltered = lineFiltered.filter{ it.isNotBlank() }

        lineFiltered.forEach {
            val listCSV = it.split(";")

            //If the line is empty we pass it
            if(listCSV.first().isBlank()){
                return@forEach
            }

            //Seuils
            val seuilsCSV = listCSV[2]
            val listSeuils = mutableListOf<Int>()
            val seuils = HashMap<String,List<Int>>()
            if(seuilsCSV.isNotEmpty()){
                seuilsCSV.split("|").forEach{
                    val listSeuilsParfFacteur =it.split("=")
                    listSeuilsParfFacteur.first().let{ itInutilise ->
                        itInutilise.split("/").forEach{itSeuils ->
                            listSeuils.add(itSeuils.toInt())
                        }
                        seuils[listSeuilsParfFacteur.last()] = listSeuils.toList()
                        listSeuils.clear()
                    }
                }
            }

            listArme.add(
                Arme(
                    listCSV[0].cleanupForDB(),
                    listCSV[1],
                    seuils,
                    listCSV[3],
                    listCSV[4].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[5].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[6].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[7],
                    listCSV[8].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[9].run{ if(isNotBlank()) toInt() else{0} },
                    listCSV[10]
                )
            )

        }

        return listArme
    }
}