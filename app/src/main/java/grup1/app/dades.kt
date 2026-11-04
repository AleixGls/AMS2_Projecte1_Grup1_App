package grup1.app

data class Zona(val id: String, val nom: String, val tipus: String, val aforament: Int)

data class Recinte(val id: String, val nom: String, val zones: List<Zona>)

data class TipusEntrada(val id: String, val nom: String, val zona: String, val preu: Double, val quota: Int)

data class Esdeveniment(
    val id: String,
    val recinte: String,
    val nom: String,
    val dataHora: String,
    val categoria: String,
    val descripcio: String,
    val tipusEntrades: List<TipusEntrada>
)

data class Cataleg(val recintes: List<Recinte>, val esdeveniments: List<Esdeveniment>)

object Dades {
    var cataleg: Cataleg = Cataleg(listOf(), listOf())
    val cistella: MutableList<TipusEntrada> = mutableListOf()

    fun buscarEsdeveniment(id: String): Esdeveniment? {
        for (esdeveniment in cataleg.esdeveniments) {
            if (esdeveniment.id == id) {
                return esdeveniment
            }
        }
        return null
    }

    fun buscarEsdevenimentDeTipus(tipus: TipusEntrada): Esdeveniment? {
        for (esdeveniment in cataleg.esdeveniments) {
            for (t in esdeveniment.tipusEntrades) {
                if (t.id == tipus.id) {
                    return esdeveniment
                }
            }
        }
        return null
    }

    fun buscarZona(id: String): Zona? {
        for (recinte in cataleg.recintes) {
            for (zona in recinte.zones) {
                if (zona.id == id) {
                    return zona
                }
            }
        }
        return null
    }

    fun nomRecinte(id: String): String {
        for (recinte in cataleg.recintes) {
            if (recinte.id == id) {
                return recinte.nom
            }
        }
        return ""
    }

    fun esGeneral(tipus: TipusEntrada): Boolean {
        val zona = buscarZona(tipus.zona)
        return zona != null && zona.tipus.equals("general", ignoreCase = true)
    }
}