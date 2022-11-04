import kotlinx.serialization.Serializable

@Serializable
class Arme(val nom:String, val force:Int) : IListItem {

    override val id = nom.hashCode()

    override fun toDescription(): String = "Arme : $nom, de force :$force"

    companion object {
        const val path = "/armes"
    }
}