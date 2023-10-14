import kotlinx.serialization.Serializable

@Serializable
class Bouclier(
    override val nom: String ="inconnu",
    val seuilParade:Int = 0,
    val seuilBlocage:Int = 0,
    val defense:Map<EffectType,String> = mapOf(),
    val contraintes:String="Aucune contraintes",
    val poids:Int=0,
    val capaciteSpeciale:String=""
)
    :ApiableItem(){

    override val id: Int = nom.hashCode()
    override var isAttached: Boolean = false

    override fun getStatsAsStrings(): String {
        return "Defense: ${convertEffectTypeStatsToString(defense)}"+"\n" +
                "Seuil Blocage:$seuilBlocage\n"+
                "Seuil parade:${seuilParade}\n"+
                strSimplify(contraintes,false)+"\n" +
                "Poids:$poids"+"\n"+
                strSimplify(capaciteSpeciale,false)
    }

    override fun getStatsSimplifiedAsStrings(): String {
        return "Defense: ${convertEffectTypeStatsToString(defense)}"+"\n" +
                strSimplify(contraintes,true)+"\n" +
                "Poids:$poids"+"\n"+
                strSimplify(capaciteSpeciale,true)
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem {
           return Bouclier(
               listCSVElement[0].cleanupForDB(),
               listCSVElement[1].run{ if(isNotBlank()) toInt() else{0} },
               listCSVElement[2].run{ if(isNotBlank()) toInt() else{0} },
               parseDefense(listCSVElement[3]),
               listCSVElement[4],
               listCSVElement[5].run{ if(isNotBlank()) toInt() else{0} },
               listCSVElement[6]
            )
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "Seuil parade: Int",
            "Seuil Blocage: Int",
            "Defense : Format = EffectType:Int|EffectType:Int... (EffectType = Po/Ph/F/Ma)",
            "Contraintes : String",
            "Poids : Int",
            "Capacite speciale : String"
        )
    }

    override fun getDeparsedAttributes(): List<String> {
        return listOf(
            nom,
            deparseDefense(defense),
            contraintes,
            poids.toString(),
            capaciteSpeciale
        )
    }
}

