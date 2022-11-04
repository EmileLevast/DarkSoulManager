import kotlinx.serialization.Serializable

@Serializable
class Arme(
    val nom:String="inconnu",
    val force:Int=0,
    val seuils:Map<String,List<Int>> = mapOf(),//en clé c'est le facteur et en valeur c'est la liste des seuils associés
    val maximumEnergie:Int=0,
    val seuilBlocage:Int=0,
    val valeurBlocage:Int=0,
    val forceMinimum:Int=0,
    val poids:Int=0
    ) : IListItem {

    override val id = nom.hashCode()

    override fun toDescription(): String {
        var textSeuils = ""
        seuils.forEach {
            textSeuils += "|   ${it.value.joinToString("/")} => x${it.key}\n"}
        return "\n" +
        "----Arme: $nom----\n" +
        "| Force :$force\n"+
        "| Seuils:\n" + textSeuils +
        "| Maximum énergie : $maximumEnergie\n" +
        "| Seuil de blocage : $seuilBlocage\n" +
        "| Baleur de blocage : $valeurBlocage\n" +
        "| Force Minimum requise : $forceMinimum\n" +
        "| Poids : $poids\n" +
        "------------------\n"
    }


    companion object {
        const val path = "/armes"
    }
}