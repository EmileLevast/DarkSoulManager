import kotlinx.serialization.Serializable

@Serializable
class Armure(
    override val nom: String ="inconnu",
    val defense:Map<EffectType,String> = mapOf(),
    val contraintes:String="Aucune contraintes",
    val poids:Int=0,
    val capaciteSpeciale:String=""
)
    :ApiableItem(){

    override val id: Int = nom.hashCode()
    override var isAttached: Boolean = false

    override fun getStatsAsStrings(): String {
        var textDefenseByType = ""
        defense.forEach { textDefenseByType+= it.key.shortname+":"+it.value+" | " }
        return textDefenseByType+"\n" +
                contraintes+"\n" +
                "Poids:$poids"+"\n"+
                capaciteSpeciale
    }

    override fun parseFromCSV(sequenceLinesFile : Sequence<String>): List<ApiableItem> {
        val listArmure = mutableListOf<Armure>()
        var lineFiltered = sequenceLinesFile.drop(1)

        lineFiltered = lineFiltered.filter{ it.isNotBlank() }

        lineFiltered.forEach {
            val listCSV = it.split(";")


            //If the line is empty we pass it
            if(listCSV.first().isBlank()){
                return@forEach
            }

            //DefenseType
            val listDefenseTypeCSV = listCSV[1]
            val mapDefenseType = mutableMapOf<EffectType,String>()

            if(listDefenseTypeCSV.isNotEmpty()){
                listDefenseTypeCSV.split("|").forEach { currentDefense ->
                    currentDefense.split(":").let{ currentEffectType ->
                        //on check si le type correspond bien a un vrai type
                        mapDefenseType[EffectType.values().find { enumEffectType ->enumEffectType.shortname == currentEffectType.first() }!!] =
                            currentEffectType.last()
                    }
                }
            }

            listArmure.add(Armure(
                listCSV[0].cleanupForDB(),
                mapDefenseType,
                listCSV[2],
                listCSV[3].run{ if(isNotBlank()) toInt() else{0} },
                listCSV[4]
            ))

        }


        return listArmure
    }
}

enum class EffectType(val shortname:String){
    FIRE("F"),
    MAGIC("Ma"),
    POISON("Po"),
    PHYSICAL("Ph")
}