import kotlinx.serialization.Serializable

@Serializable
class Armes(val nom:String, val force:Int) : IListItem {

    override val id = nom.hashCode()

    override fun toDescription(): String = "Arme : $nom, de force :$force"
}