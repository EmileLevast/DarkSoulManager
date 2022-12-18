import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class ApiableItem : IListItem{
        @Transient
        val nameForApi = this::class.simpleName
        @Transient
        val updateNameForApi = "update$nameForApi"

        abstract fun parseFromCSV(sequenceLinesFile : Sequence<String>):List<ApiableItem>
}