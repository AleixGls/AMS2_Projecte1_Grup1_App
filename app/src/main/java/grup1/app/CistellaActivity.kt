package grup1.app

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CistellaActivity : AppCompatActivity() {

    lateinit var layoutLinies: LinearLayout
    lateinit var textTotal: TextView
    lateinit var btnTornar: ImageView

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

        btnTornar.setOnClickListener {
            finish()
        }

        mostrarCistella()
    }

    fun mostrarCistella() {
        if (Dades.cistella.isEmpty()) {
            val text = TextView(this)
            text.text = "La cistella està buida"
            text.setPadding(24, 24, 24, 24)
            layoutLinies.addView(text)
            textTotal.text = "TOTAL: " + String.format("%.2f €", 0.0)
            return
        }

        val idsVistos = mutableListOf<String>()
        var total = 0.0

        for (tipus in Dades.cistella) {
            total = total + tipus.preu

            if (!idsVistos.contains(tipus.id)) {
                idsVistos.add(tipus.id)

                var quantitat = 0
                for (t in Dades.cistella) {
                    if (t.id == tipus.id) {
                        quantitat = quantitat + 1
                    }
                }

                afegirLinia(tipus, quantitat)
            }
        }

        textTotal.text = "TOTAL: " + String.format("%.2f €", total)
    }

    fun afegirLinia(tipus: TipusEntrada, quantitat: Int) {
        val vista = layoutInflater.inflate(R.layout.item_cistella, layoutLinies, false)

        val textEntrada: TextView = vista.findViewById(R.id.textEntrada)
        val textEsdeveniment: TextView = vista.findViewById(R.id.textEsdeveniment)
        val textQuantitat: TextView = vista.findViewById(R.id.textQuantitat)
        val textSubtotal: TextView = vista.findViewById(R.id.textSubtotal)

        val esdeveniment = Dades.buscarEsdevenimentDeTipus(tipus)

        textEntrada.text = tipus.nom
        if (esdeveniment != null) {
            textEsdeveniment.text = esdeveniment.nom
        }
        textQuantitat.text = "x" + quantitat + "  ·  " + String.format("%.2f €", tipus.preu) + " / u"
        textSubtotal.text = String.format("%.2f €", tipus.preu * quantitat)

        layoutLinies.addView(vista)
    }
}