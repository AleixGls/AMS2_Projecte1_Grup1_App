package grup1.app

import android.os.Bundle
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

        if (Dades.cistella.isEmpty()) {
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

    fun afegirLinia(tipus: TipusEntrada, quantitat: Int) {
        val vista = layoutInflater.inflate(R.layout.item_cistella, layoutLinies, false)

        val textEntrada: TextView = vista.findViewById(R.id.textEntrada)
        val textEsdeveniment: TextView = vista.findViewById(R.id.textEsdeveniment)
        val textQuantitat: TextView = vista.findViewById(R.id.textQuantitat)
        val textSubtotal: TextView = vista.findViewById(R.id.textSubtotal)

        textEntrada.text = tipus.nom

        val esdeveniment = Dades.buscarEsdevenimentDeTipus(tipus)
        if (esdeveniment != null) {
            textEsdeveniment.text = esdeveniment.nom
        }

        textQuantitat.text = "x" + quantitat + "  ·  " + String.format("%.2f €", tipus.preu) + " / u"
        textSubtotal.text = String.format("%.2f €", tipus.preu * quantitat)

        layoutLinies.addView(vista)
    }

    fun ferCompra() {
        if (Dades.cistella.isEmpty()) {
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

        val peticio = JSONObject()
        peticio.put("type", "compra")
        peticio.put("nom", nom)
        peticio.put("correu", correu)
        peticio.put("entrades", entrades)

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
            mostrarCistella()

            val localitzadors = obj.getJSONArray("localitzadors")
            var text = "Localitzadors:\n"
            for (i in 0 until localitzadors.length()) {
                text = text + "\n" + localitzadors.getString(i)
            }

            mostrarDialeg("Compra realitzada", text)
        }

        if (type == "compraError") {
            mostrarDialeg("Error", obj.getString("message"))
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