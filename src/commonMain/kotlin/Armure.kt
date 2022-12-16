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

}

enum class EffectType(val shortname:String){
    FIRE("F"),
    MAGIC("Ma"),
    POISON("Po"),
    PHYSICAL("Ph")
}