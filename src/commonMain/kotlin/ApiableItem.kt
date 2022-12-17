import kotlinx.serialization.Serializable

@Serializable
abstract class ApiableItem : IListItem{
        val nameForApi = this::class.simpleName
        val updateNameForApi = "update$nameForApi"

        abstract fun parseFromCSV(sequenceLinesFile : Sequence<String>):List<ApiableItem>
}