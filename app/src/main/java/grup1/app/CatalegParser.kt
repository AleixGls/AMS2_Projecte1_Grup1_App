package grup1.app

import org.json.JSONObject

object CatalegParser {

    fun llegirJSON(text: String): Cataleg {
        val arrel = JSONObject(text)

        val recintes = mutableListOf<Recinte>()
        val arrayRecintes = arrel.getJSONArray("recintes")
        for (i in 0 until arrayRecintes.length()) {
            val objRecinte = arrayRecintes.getJSONObject(i)

            val zones = mutableListOf<Zona>()
            val arrayZones = objRecinte.getJSONArray("zones")
            for (j in 0 until arrayZones.length()) {
                val objZona = arrayZones.getJSONObject(j)
                zones.add(Zona(
                    objZona.getString("id"),
                    objZona.getString("nom"),
                    objZona.getString("tipus"),
                    objZona.getInt("aforament")
                ))
            }

            recintes.add(Recinte(objRecinte.getString("id"), objRecinte.getString("nom"), zones))
        }

        val esdeveniments = mutableListOf<Esdeveniment>()
        val arrayEsdeveniments = arrel.getJSONArray("esdeveniments")
        for (i in 0 until arrayEsdeveniments.length()) {
            val objEsd = arrayEsdeveniments.getJSONObject(i)

            val tipus = mutableListOf<TipusEntrada>()
            val arrayTipus = objEsd.getJSONArray("tipusEntrades")
            for (j in 0 until arrayTipus.length()) {
                val objTipus = arrayTipus.getJSONObject(j)
                tipus.add(TipusEntrada(
                    objTipus.getString("id"),
                    objTipus.getString("nom"),
                    objTipus.getString("zona"),
                    objTipus.getDouble("preu"),
                    objTipus.getInt("quota")
                ))
            }

            esdeveniments.add(Esdeveniment(
                objEsd.getString("id"),
                objEsd.getString("recinte"),
                objEsd.getString("nom"),
                objEsd.getString("dataHora"),
                objEsd.getString("categoria"),
                objEsd.getString("descripcio"),
                tipus
            ))
        }

        return Cataleg(recintes, esdeveniments)
    }
}