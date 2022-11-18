fun String.cleanupForDB():String{
    return this.lowercase().replace("'"," ").replace("é","e").replace("è","e")
}