import kotlinx.serialization.Serializable

@Serializable
class Arme(
    override val nom: String = "inconnu",
    val seuils: List<Seuil> = mutableListOf(),//en clé c'est le facteur et en valeur c'est la liste des seuils associés
    val coupCritiques: String = "",
    val maximumEnergie: Int = 0,
    val contraintes: String = "Aucune",
    val fajMax: Int = 0,
    val poids: Int = 0,
    val capaciteSpeciale: String = "",
) : ApiableItem() {


    override val id = nom.hashCode()
    override var isAttached = false

    override fun getStatsAsStrings(): String {
        var textSeuils = ""
        seuils.forEach {
            textSeuils += "|   $it"
        }
        return "Seuils:\n" + textSeuils +
                (if (coupCritiques.isNotBlank()) "CC : ${strSimplify(coupCritiques, false)}\n" else "") +
                "Max énergie : $maximumEnergie\n" +
                "FAJ Max : $fajMax\n" +
                (if (contraintes.isNotBlank()) " ${strSimplify(contraintes, false)}\n" else "") +
                "Poids : $poids\n" +
                "${strSimplify(capaciteSpeciale, false)}\n"
    }

    override fun getStatsSimplifiedAsStrings(): String {
        var textSeuils = ""
        seuils.forEach {
            textSeuils += "|   $it\n"
        }
        return "Seuils:\n" + textSeuils +
                (if (coupCritiques.isNotBlank()) "CC : ${strSimplify(coupCritiques, false)}\n" else "") +
                "FAJ Max : $fajMax\n" +
                (if (contraintes.isNotBlank()) " ${strSimplify(contraintes, false)}\n" else "") +
                "Poids : $poids\n" +
                "${strSimplify(capaciteSpeciale, false)}\n"
    }

    override fun parseFromCSV(listCSVElement: List<String>): ApiableItem {

        return Arme(
            listCSVElement[0].cleanupForDB(),
            parseSeuils(listCSVElement[1]),
            listCSVElement[3],
            listCSVElement[4].run {
                if (isNotBlank()) toInt() else {
                    0
                }
            },
            listCSVElement[5].run {
                if (isNotBlank()) toInt() else {
                    0
                }
            },
            listCSVElement[6].run {
                if (isNotBlank()) toInt() else {
                    0
                }
            },
            listCSVElement[7],
            listCSVElement[8].run {
                if (isNotBlank()) toInt() else {
                    0
                }
            },
            listCSVElement[9].run {
                if (isNotBlank()) toInt() else {
                    0
                }
            },
            listCSVElement[10]
        )
    }

    override fun getParsingRulesAttributesAsList(): List<String> {
        return listOf(
            "Nom: String",
            "Degats: Format = EffectType:Int|EffectType:Int... (EffectType = Po/Ph/F/Ma)",
            "Seuils: Format = Int/Int=Int|Int/Int=Int... ",
            "Coups critiques :String",
            "Maximum Energie : Int",
            "Seuil Blocage : Int",
            "Valeur Blocage : Int",
            "Contraintes : String",
            "FAJ Max : Int",
            "Poids : Int",
            "Capacite speciale : String"
        )
    }

    override fun getDeparsedAttributes(): List<String> {

        var textSeuils = ""
        seuils.forEach {
            textSeuils += "|$it\n"
        }

        return listOf<String>(
            nom,
            textSeuils,
            coupCritiques,
            maximumEnergie.toString(),
            contraintes,
            fajMax.toString(),
            poids.toString(),
            strSimplify(capaciteSpeciale, true)
        )
    }
}