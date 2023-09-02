import io.ktor.util.logging.*
import kotlinx.serialization.Serializable

@Serializable
class Arme(
    override val nom:String="inconnu",
    val degat:String="Ph:0",
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
            textSeuils += "|   ${it.value.joinToString("/")} =>×${it.key}\n"
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

    override fun getStatsSimplifiedAsStrings(): String {
        var textSeuils = ""

        val temp = parseDefense(degat.replace("P:","Ph:"))

        var parseDegats =temp.mapValues {
            try {
                it.value.toInt()
            } catch (e: Exception) {
                -1
            }
        }


        var facteur = 0
        var degatFinaux = mapOf<EffectType,Int>()
        seuils.forEach {
            try {
                facteur = it.key.toInt()
            } catch (e: Exception) {
                facteur = -1
            }


            degatFinaux = parseDegats.mapValues { degatSeuil -> degatSeuil.value * facteur }

            val listDegatFinaux = degatFinaux.map{type->type.key.shortname+":"+type.value}

            textSeuils += "|   ${it.value.joinToString("/")} =>${listDegatFinaux.joinToString ("|" )}\n"

        }

        var coupcCritiquesCalcules=""
        if(coupCritiques.isNotBlank() && coupCritiques.first().isDigit()){
            val ccSplit = coupCritiques.split("=>×")
            degatFinaux = parseDegats.mapValues { degatSeuil -> degatSeuil.value * try {
                ccSplit.last().toInt()
            } catch (e: Exception) {
                -1
            }
            }
            coupcCritiquesCalcules = ccSplit.first() + "=>" + degatFinaux.map{type->type.key.shortname+":"+type.value}.joinToString ("|" )
        }else{
            coupcCritiquesCalcules = coupCritiques
        }

        return  "Seuils:\n" + textSeuils +
                (if(coupCritiques.isNotBlank())"CC : $coupcCritiquesCalcules\n" else "") +
                "Max énergie : $maximumEnergie\n" +
                "FAJ Max : $fajMax\n" +
                (if(contraintes.isNotBlank())"$contraintes\n" else "") +
                "Poids : $poids\n" +
                "$capaciteSpeciale\n"
    }

    override fun parseFromCSV(listCSVElement : List<String>):ApiableItem{

        return Arme(
            listCSVElement[0].cleanupForDB(),
            listCSVElement[1],
            parseSeuils(listCSVElement[2]),
            listCSVElement[3],
            listCSVElement[4].run{ if(isNotBlank()) toInt() else{0} },
            listCSVElement[5].run{ if(isNotBlank()) toInt() else{0} },
            listCSVElement[6].run{ if(isNotBlank()) toInt() else{0} },
            listCSVElement[7],
            listCSVElement[8].run{ if(isNotBlank()) toInt() else{0} },
            listCSVElement[9].run{ if(isNotBlank()) toInt() else{0} },
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
}