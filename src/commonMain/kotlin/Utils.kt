fun String.cleanupForDB():String{
    return this.lowercase().replace("'"," ").replace("é","e").replace("è","e").replace("î","i").replace("ï","i").replace("ä","a").replace("'"," ")
}