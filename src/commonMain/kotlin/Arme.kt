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
    ) : IListItem {


    override val id = nom.hashCode()
    override var isAttached = false

    override fun getStatsAsStrings():String{
        var textSeuils = ""
        seuils.forEach {
            textSeuils += "|   ${it.value.joinToString("/")} => x${it.key}\n"
        }
        return  "$degat\n"+
                "Seuils:\n" + textSeuils +
                "CC : $coupCritiques\n" +
                "Max énergie : $maximumEnergie\n" +
                "Seuil de blocage : $seuilBlocage\n" +
                "Valeur de blocage : $valeurBlocage\n" +
                "FAJ Max : $fajMax\n" +
                "$contraintes\n" +
                "Poids : $poids\n" +
                "$capaciteSpeciale\n"
    }

    companion object {
        const val path = "/armes"
        const val pathToUpdate ="/updateArmes"
    }
}