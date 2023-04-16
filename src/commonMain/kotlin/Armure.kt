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
        return convertEffectTypeStatsToString(defense)+"\n" +
                contraintes+"\n" +
                "Poids:$poids"+"\n"+
                capaciteSpeciale
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem {
           return Armure(
               listCSVElement[0].cleanupForDB(),
                parseDefense(listCSVElement[1]),
               listCSVElement[2],
               listCSVElement[3].run{ if(isNotBlank()) toInt() else{0} },
               listCSVElement[4]
            )
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "Defense : Format = EffectType:Int|EffectType:Int... (EffectType = Po/Ph/F/Ma)",
            "Contraintes : String",
            "Poids : Int",
            "Capacite speciale : String"
        )
    }
}

