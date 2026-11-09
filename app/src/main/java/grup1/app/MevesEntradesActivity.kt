package grup1.app

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class MevesEntradesActivity : AppCompatActivity() {

    lateinit var layoutEntrades: LinearLayout
    lateinit var btnMenuCartellera: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meves_entrades)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layoutEntrades = findViewById(R.id.layoutEntrades)
        btnMenuCartellera = findViewById(R.id.btnMenuCartellera)

        btnMenuCartellera.setOnClickListener {
            finish()
        }

        demanarEntrades()
    }

    fun demanarEntrades() {
        if (!Connexio.estaConnectat()) {
            mostrarMissatge("No hi ha connexió amb el servidor")
            return
        }

        Connexio.onMissatge = { missatge ->
            runOnUiThread {
                rebreResposta(missatge)
            }
        }

        val peticio = JSONObject()
        peticio.put("type", "lesMevesEntrades")
        peticio.put("correu", llegirCorreu())
        Connexio.enviar(peticio.toString())
    }

    fun rebreResposta(missatge: String) {
        val obj = JSONObject(missatge)
        val type = obj.getString("type")

        if (type == "lesMevesEntrades") {
            Dades.mevesEntrades = CatalegParser.llegirEntrades(missatge)
            mostrarEntrades(Dades.mevesEntrades)
        }

        if (type == "error") {
            mostrarMissatge("No s'han pogut carregar les entrades")
        }
    }

    fun mostrarEntrades(entrades: List<EntradaComprada>) {
        layoutEntrades.removeAllViews()

        if (entrades.isEmpty()) {
            mostrarMissatge("Encara no tens entrades")
            return
        }

        for (entrada in entrades) {
            afegirTarjeta(entrada)
        }
    }

    fun afegirTarjeta(entrada: EntradaComprada) {
        val vista = layoutInflater.inflate(R.layout.item_entrada_comprada, layoutEntrades, false)

        val textEsdeveniment: TextView = vista.findViewById(R.id.textEsdeveniment)
        val textDataHora: TextView = vista.findViewById(R.id.textDataHora)
        val textTipus: TextView = vista.findViewById(R.id.textTipus)
        val textLocalitzador: TextView = vista.findViewById(R.id.textLocalitzador)

        textLocalitzador.text = entrada.localitzador

        val esdeveniment = Dades.buscarEsdeveniment(entrada.idEsdeveniment)
        if (esdeveniment != null) {
            textEsdeveniment.text = esdeveniment.nom
            textDataHora.text = esdeveniment.dataHora.replace("T", "  ")

            for (tipus in esdeveniment.tipusEntrades) {
                if (tipus.id == entrada.idTipus) {
                    textTipus.text = tipus.nom + " · " + String.format("%.2f €", tipus.preu)
                }
            }
        }

        layoutEntrades.addView(vista)
    }

    fun mostrarMissatge(missatge: String) {
        layoutEntrades.removeAllViews()
        val text = TextView(this)
        text.text = missatge
        text.setPadding(24, 24, 24, 24)
        layoutEntrades.addView(text)
    }

    fun llegirCorreu(): String {
        val fitxer = File(filesDir, "config.xml")
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(fitxer)
        return doc.getElementsByTagName("correu").item(0).textContent
    }
}