import kotlinx.serialization.Serializable

@Serializable
class Bouclier(
    override val nom: String ="inconnu",
    val seuilParade:Int = 0,
    val energie:Int = 0,
    val seuilBlocage:Int = 0,
    val blocage:Map<EffectType,String> = mapOf(),
    val contraintes:String="Aucune contraintes",
    val poids:Int=0,
    val capaciteSpeciale:String=""
)
    :ApiableItem(){

    override val id: Int = nom.hashCode()
    override var isAttached: Boolean = false

    override fun getStatsAsStrings(): String {
        return "Seuil parade:${seuilParade}\n"+
                "Energie:$energie\n"+
                "Seuil Blocage:$seuilBlocage\n"+
                "Blocage: ${convertEffectTypeStatsToString(blocage)}"+"\n" +
                contraintes+"\n" +
                "Poids:$poids"+"\n"+
                capaciteSpeciale
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem {
           return Bouclier(
               listCSVElement[0].cleanupForDB(),
               listCSVElement[1].run{ if(isNotBlank()) toInt() else{0} },
               listCSVElement[2].run{ if(isNotBlank()) toInt() else{0} },
               listCSVElement[3].run{ if(isNotBlank()) toInt() else{0} },
               parseDefense(listCSVElement[4]),
               listCSVElement[5],
               listCSVElement[6].run{ if(isNotBlank()) toInt() else{0} },
               listCSVElement[7]
            )
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "Seuil parade: Int",
            "Energie: Int",
            "Seuil Blocage: Int",
            "Blocage : Format = EffectType:Int|EffectType:Int... (EffectType = Po/Ph/F/Ma)",
            "Contraintes : String",
            "Poids : Int",
            "Capacite speciale : String"
        )
    }

    override fun getDeparsedAttributes(): List<String> {
        return listOf(
            nom,
            seuilParade.toString(),
            energie.toString(),
            seuilBlocage.toString(),
            deparseDefense(blocage),
            contraintes,
            poids.toString(),
            capaciteSpeciale
        )
    }
}

