import kotlinx.serialization.Serializable

@Serializable
class Armure(
    override val nom: String,
    val defense:Map<EffectType,String> = mapOf(),
    val contraintes:String="Aucune contraintes",
    val poids:Int=0,
    val capaciteSpeciale:String=""
)
    :IListItem {
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

    companion object {
        const val path = "/armures"
        const val pathToUpdate ="/updateArmures"
    }
}

enum class EffectType(val shortname:String){
    FIRE("F"),
    MAGIC("Ma"),
    POISON("Po"),
    PHYSICAL("Ph")
}