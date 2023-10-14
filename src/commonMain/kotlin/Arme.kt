import kotlinx.serialization.Serializable

@Serializable
class Arme(
    override val nom: String = "inconnu",
    val seuils: List<Seuil> = mutableListOf(),//en clé c'est le facteur et en valeur c'est la liste des seuils associés
    val coupCritiques: String = "",
    val maximumEnergie: Int = 0,
    val seuilBlocage: Int = 0,
    val valeurBlocage: Int = 0,
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
            textSeuils += "|   ${it.value.joinToString("/")} =>×${it.key}\n"
        }
        return "$degat\n" +
                "Seuils:\n" + textSeuils +
                (if (coupCritiques.isNotBlank()) "CC : ${strSimplify(coupCritiques, false)}\n" else "") +
                "Max énergie : $maximumEnergie\n" +
                "Seuil de blocage : $seuilBlocage\n" +
                "Valeur de blocage : $valeurBlocage\n" +
                "FAJ Max : $fajMax\n" +
                (if (contraintes.isNotBlank()) " ${strSimplify(contraintes, false)}\n" else "") +
                "Poids : $poids\n" +
                "${strSimplify(capaciteSpeciale, false)}\n"
    }

    override fun getStatsSimplifiedAsStrings(): String {
        var (textSeuils, coupcCritiquesCalcules) = simplificationTextesSeuilsEtCc(degat,seuils,coupCritiques)



        return "Seuils:\n" + textSeuils +
                (if (coupCritiques.isNotBlank()) "CC : $coupcCritiquesCalcules\n" else "") +
                "Max énergie : $maximumEnergie\n" +
                "Force Max : $fajMax\n" +
                (if (contraintes.isNotBlank()) "${strSimplify(contraintes, true)}\n" else "") +
                "Poids : $poids\n" +
                "${strSimplify(capaciteSpeciale, true)}\n"
    }

    override fun parseFromCSV(listCSVElement: List<String>): ApiableItem {

        return Arme(
            listCSVElement[0].cleanupForDB(),
            listCSVElement[1],
            parseSeuils(listCSVElement[2]),
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

    fun getDeparsedAttributesOld(): List<String> {
        return listOf<String>(
            nom,
            degat,
            deparseSeuils(seuils),
            coupCritiques,
            maximumEnergie.toString(),
            seuilBlocage.toString(),
            valeurBlocage.toString(),
            contraintes,
            fajMax.toString(),
            poids.toString(),
            capaciteSpeciale
        )
    }

    override fun getDeparsedAttributes(): List<String> {

        var (textSeuils, coupcCritiquesCalcules) = simplificationTextesSeuilsEtCc(degat,seuils,coupCritiques)

        return listOf<String>(
            nom,
            textSeuils,
            coupcCritiquesCalcules,
            maximumEnergie.toString(),
            contraintes,
            fajMax.toString(),
            poids.toString(),
            strSimplify(capaciteSpeciale, true)
        )
    }
}