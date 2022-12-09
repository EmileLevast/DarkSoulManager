import kotlinx.serialization.Serializable

@Serializable
data class Monster(
    override val nom: String= "inconnu",
    val vie: Int=0,
    val force:Map<Int,Int> = mapOf(),
    val defense:Int = 0,
    val energie:Int = 0,
    val listDrops:Map<String,Int> = mapOf(),
    val ames:Int,
) : IListItem{

    override val id = nom.hashCode()
    override var isAttached = false


    private fun toDescription(): String {
        val textForceSeuils = constructForceSeuils()
        return "\n" +
                "----Monstre: $nom----\n" +
                "| Vie : $vie\n"+
                "| Force : $textForceSeuils\n"+
                "| Defense:" + defense +"\n"+
                "| Energie : $energie\n" +
                "| Drops: \n" +
                constructListDropsString() +
                "| Ames : $ames\n" +
                "------------------\n"
    }

    private fun constructForceSeuils():String{
        val textFinalForce =StringBuilder()
        val textForceSeuilsTemp = StringBuilder()

        force.forEach {
            for(i in 0 until it.key){
                textForceSeuilsTemp.append("*")
            }
            textForceSeuilsTemp.append(":${it.value} ")

            textFinalForce.append(textForceSeuilsTemp)
            textForceSeuilsTemp.clear()
        }
        return textFinalForce.toString()
    }

    private fun constructListDropsString():String{
        val listDroptext = StringBuilder()
        listDrops.forEach {
            listDroptext.append("|  ${it.key} / ${it.value}+\n")
        }

        return listDroptext.toString()
    }

    override fun getStatsAsStrings(): String {
        return "No stat"
    }

    companion object {
        const val path = "/monster"
    }
}