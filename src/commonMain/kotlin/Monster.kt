import kotlinx.serialization.Serializable

@Serializable
data class Monster(val nom: String, var vie: Int) : IListItem{

    override val id = nom.hashCode()

    override fun toDescription(): String = "Monstre : $nom, de vie $vie"

    companion object {
        const val path = "/monster"
    }
}