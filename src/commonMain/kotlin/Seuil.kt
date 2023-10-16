import kotlinx.serialization.Serializable

/**
 * Classe permettant de sauvegarder des seuils et leur degats correspondant
 */
@Serializable

class Seuil {

    var seuils: MutableList<Int> = mutableListOf()
    var degats: MutableList<Pair<EffectType, Int>> = mutableListOf()

    constructor(seuils: List<Int>, degats: List<Pair<EffectType, Int>>) {
        this.seuils.addAll(seuils)
        this.degats.addAll(degats)
    }


    override fun toString(): String {
        return seuils.joinToString("/")+"⇒"+degats.joinToString ("|"){ it.first.shortname+":"+it.second }
    }
}