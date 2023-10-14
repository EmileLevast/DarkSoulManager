import kotlinx.serialization.Serializable

/**
 * Classe permettant de sauvegarder des seuils et leur degats correspondant
 */
@Serializable

class Seuil(var seuils: MutableList<Int>, var degats: MutableList<Pair<EffectType, Int>>) {

}