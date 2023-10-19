import kotlinx.serialization.Serializable

@Serializable
class Carac(
    var vie: Int=0,
    var force: Int=0,
    var defense: Map<EffectType, String> = mapOf(),
    var intelligence: Int=0,
    var energie: Int=0
) {
    fun toCSV():String{
        return "$vie/$force/${deparseDefense(defense)}/$intelligence/$energie"
    }

    fun showWithComparisonOriginCarac(originCarac:Carac):String{
        return "Vie (${originCarac.vie}): $vie\n" +
                "Force (${originCarac.force}): $force\n" +
                "Defense : ${convertEffectTypeStatsToString(defense)}\n" +
                "Intelligence (${originCarac.intelligence}): $intelligence\n" +
                "Energie (${originCarac.energie}): $energie\n"
    }

    companion object {
        fun fromCSV(csvStr:String):Carac{
            val listCarac = csvStr.split("/")
            return Carac(
                listCarac[0].getIntOrZero(),
                listCarac[1].getIntOrZero(),
                parseDefense(listCarac[2]),
                listCarac[3].getIntOrZero(),
                listCarac[4].getIntOrZero(),
            )
        }
    }
}