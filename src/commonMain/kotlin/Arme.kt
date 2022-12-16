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
}