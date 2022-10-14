import kotlinx.serialization.Serializable

@Serializable
data class Monster(val name: String, var life: Int) {
    val id: Int = name.hashCode()

    companion object {
        const val path = "/monster"
    }
}