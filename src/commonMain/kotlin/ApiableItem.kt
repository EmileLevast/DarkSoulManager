abstract class ApiableItem : IListItem{
        val path = this::class.simpleName
        val updatePath = "update$path"
}