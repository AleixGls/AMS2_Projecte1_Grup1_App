package grup1.app

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class CistellaActivity : AppCompatActivity() {

    lateinit var layoutLinies: LinearLayout
    lateinit var textTotal: TextView
    lateinit var btnTornar: ImageView
    lateinit var btnComprar: Button

    var nom = ""
    var correu = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cistella)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layoutLinies = findViewById(R.id.layoutLinies)
        textTotal = findViewById(R.id.textTotal)
        btnTornar = findViewById(R.id.btnTornar)
        btnComprar = findViewById(R.id.btnComprar)

        btnTornar.setOnClickListener {
            finish()
        }

        btnComprar.setOnClickListener {
            ferCompra()
        }

        mostrarCistella()
    }

    fun mostrarCistella() {
        layoutLinies.removeAllViews()

        if (Dades.cistella.isEmpty() && Dades.seientsCistella.isEmpty()) {
            val text = TextView(this)
            text.text = "La cistella està buida"
            text.setPadding(24, 24, 24, 24)
            layoutLinies.addView(text)
            textTotal.text = "TOTAL: 0,00 €"
            return
        }

        val idsVistos = mutableListOf<String>()
        var total = 0.0

        for (tipus in Dades.cistella) {
            total = total + tipus.preu

            if (!idsVistos.contains(tipus.id)) {
                idsVistos.add(tipus.id)
                afegirLinia(tipus, comptarQuantitat(tipus))
            }
        }

        for (seient in Dades.seientsCistella) {
            val tipus = buscarTipus(seient.idEsdeveniment, seient.idTipus)
            if (tipus != null) {
                total = total + tipus.preu
                afegirLiniaSeient(seient, tipus)
            }
        }

        textTotal.text = "TOTAL: " + String.format("%.2f €", total)
    }

    fun comptarQuantitat(tipus: TipusEntrada): Int {
        var quantitat = 0
        for (t in Dades.cistella) {
            if (t.id == tipus.id) {
                quantitat = quantitat + 1
            }
        }
        return quantitat
    }

    fun buscarTipus(idEsdeveniment: String, idTipus: String): TipusEntrada? {
        val esdeveniment = Dades.buscarEsdeveniment(idEsdeveniment)
        if (esdeveniment != null) {
            for (tipus in esdeveniment.tipusEntrades) {
                if (tipus.id == idTipus) {
                    return tipus
                }
            }
        }
        return null
    }

    fun afegirLinia(tipus: TipusEntrada, quantitat: Int) {
        val vista = layoutInflater.inflate(R.layout.item_cistella, layoutLinies, false)

        val textEntrada: TextView = vista.findViewById(R.id.textEntrada)
        val textEsdeveniment: TextView = vista.findViewById(R.id.textEsdeveniment)
        val textQuantitat: TextView = vista.findViewById(R.id.textQuantitat)
        val textSubtotal: TextView = vista.findViewById(R.id.textSubtotal)
        val btnMenys: Button = vista.findViewById(R.id.btnMenys)
        val btnMes: Button = vista.findViewById(R.id.btnMes)
        val btnEliminar: Button = vista.findViewById(R.id.btnEliminar)

        textEntrada.text = tipus.nom

        val esdeveniment = Dades.buscarEsdevenimentDeTipus(tipus)
        if (esdeveniment != null) {
            textEsdeveniment.text = esdeveniment.nom
        }

        textQuantitat.text = "x" + quantitat + "  ·  " + String.format("%.2f €", tipus.preu) + " / u"
        textSubtotal.text = String.format("%.2f €", tipus.preu * quantitat)

        btnEliminar.visibility = View.GONE

        btnMes.setOnClickListener {
            Dades.cistella.add(tipus)
            mostrarCistella()
        }

        btnMenys.setOnClickListener {
            if (quantitat == 1) {
                val dialeg = AlertDialog.Builder(this)
                dialeg.setTitle("Treure entrada")
                dialeg.setMessage("Segur que vols treure " + tipus.nom + " de la cistella?")
                dialeg.setPositiveButton("Sí") { _, _ ->
                    Dades.cistella.remove(tipus)
                    mostrarCistella()
                }
                dialeg.setNegativeButton("No", null)
                dialeg.show()
            } else {
                Dades.cistella.remove(tipus)
                mostrarCistella()
            }
        }

        layoutLinies.addView(vista)
    }

    fun afegirLiniaSeient(seient: SeientCistella, tipus: TipusEntrada) {
        val vista = layoutInflater.inflate(R.layout.item_cistella, layoutLinies, false)

        val textEntrada: TextView = vista.findViewById(R.id.textEntrada)
        val textEsdeveniment: TextView = vista.findViewById(R.id.textEsdeveniment)
        val textQuantitat: TextView = vista.findViewById(R.id.textQuantitat)
        val textSubtotal: TextView = vista.findViewById(R.id.textSubtotal)
        val btnMenys: Button = vista.findViewById(R.id.btnMenys)
        val btnMes: Button = vista.findViewById(R.id.btnMes)
        val btnEliminar: Button = vista.findViewById(R.id.btnEliminar)

        textEntrada.text = tipus.nom

        val esdeveniment = Dades.buscarEsdeveniment(seient.idEsdeveniment)
        if (esdeveniment != null) {
            textEsdeveniment.text = esdeveniment.nom
        }

        textQuantitat.text = "Fila " + seient.fila + "  ·  Seient " + seient.columna
        textSubtotal.text = String.format("%.2f €", tipus.preu)

        if (seient.error) {
            textQuantitat.text = textQuantitat.text.toString() + "  ·  NO DISPONIBLE"
            textQuantitat.setTextColor(Color.parseColor("#E53935"))
        }

        btnMenys.visibility = View.GONE
        btnMes.visibility = View.GONE

        btnEliminar.setOnClickListener {
            val dialeg = AlertDialog.Builder(this)
            dialeg.setTitle("Treure butaca")
            dialeg.setMessage("Segur que vols treure la butaca Fila " + seient.fila + " Seient " + seient.columna + "?")
            dialeg.setPositiveButton("Sí") { _, _ ->
                Dades.seientsCistella.remove(seient)
                mostrarCistella()
            }
            dialeg.setNegativeButton("No", null)
            dialeg.show()
        }

        layoutLinies.addView(vista)
    }

    fun ferCompra() {
        if (Dades.cistella.isEmpty() && Dades.seientsCistella.isEmpty()) {
            Toast.makeText(this, "La cistella està buida", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Connexio.estaConnectat()) {
            mostrarDialeg("Error", "No hi ha connexió amb el servidor")
            return
        }

        llegirConfig()

        val entrades = JSONArray()
        val idsVistos = mutableListOf<String>()

        for (tipus in Dades.cistella) {
            if (!idsVistos.contains(tipus.id)) {
                idsVistos.add(tipus.id)

                val esdeveniment = Dades.buscarEsdevenimentDeTipus(tipus)

                val entrada = JSONObject()
                if (esdeveniment != null) {
                    entrada.put("esdeveniment", esdeveniment.id)
                }
                entrada.put("tipus", tipus.id)
                entrada.put("quantitat", comptarQuantitat(tipus))
                entrades.put(entrada)
            }
        }

        val seients = JSONArray()

        for (seient in Dades.seientsCistella) {
            seient.error = false

            val objSeient = JSONObject()
            objSeient.put("esdeveniment", seient.idEsdeveniment)
            objSeient.put("tipus", seient.idTipus)
            objSeient.put("seient", seient.idSeient)
            seients.put(objSeient)
        }

        val peticio = JSONObject()
        peticio.put("type", "compra")
        peticio.put("nom", nom)
        peticio.put("correu", correu)
        peticio.put("entrades", entrades)
        peticio.put("seients", seients)

        Connexio.onMissatge = { missatge ->
            runOnUiThread {
                rebreResposta(missatge)
            }
        }

        btnComprar.isEnabled = false
        Connexio.enviar(peticio.toString())
    }

    fun rebreResposta(missatge: String) {
        val obj = JSONObject(missatge)
        val type = obj.getString("type")

        if (type == "compraOk") {
            Dades.cistella.clear()
            Dades.seientsCistella.clear()
            mostrarCistella()

            val localitzadors = obj.getJSONArray("localitzadors")
            var text = "Localitzadors:\n"
            for (i in 0 until localitzadors.length()) {
                text = text + "\n" + localitzadors.getString(i)
            }

            mostrarDialeg("Compra realitzada", text)
        }

        if (type == "compraError") {
            val fallats = obj.optJSONArray("seientsFallats")

            if (fallats != null && fallats.length() > 0) {
                var text = "Aquestes butaques ja no estan disponibles. No s'ha comprat res.\n"

                for (i in 0 until fallats.length()) {
                    val idFallat = fallats.getString(i)
                    for (seient in Dades.seientsCistella) {
                        if (seient.idSeient == idFallat) {
                            seient.error = true
                            text = text + "\nFila " + seient.fila + " Seient " + seient.columna
                        }
                    }
                }

                mostrarCistella()
                mostrarDialeg("Error", text)
            } else {
                mostrarDialeg("Error", obj.getString("message"))
            }
        }

        btnComprar.isEnabled = true
    }

    fun mostrarDialeg(titol: String, missatge: String) {
        val dialeg = AlertDialog.Builder(this)
        dialeg.setTitle(titol)
        dialeg.setMessage(missatge)
        dialeg.setPositiveButton("D'acord", null)
        dialeg.show()
    }

    fun llegirConfig() {
        val fitxer = File(filesDir, "config.xml")
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(fitxer)

        nom = doc.getElementsByTagName("nom").item(0).textContent
        correu = doc.getElementsByTagName("correu").item(0).textContent
    }
}