package grup1.app

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray
import org.json.JSONObject

class SeientsActivity : AppCompatActivity() {

    lateinit var textAvis: TextView
    lateinit var scrollSeients: ScrollView
    lateinit var gridSeients: GridLayout
    lateinit var btnAfegirCistella: Button
    lateinit var btnTornar: ImageView

    var idEsdeveniment = ""
    var idTipus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seients)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textAvis = findViewById(R.id.textAvis)
        scrollSeients = findViewById(R.id.scrollSeients)
        gridSeients = findViewById(R.id.gridSeients)
        btnAfegirCistella = findViewById(R.id.btnAfegirCistella)
        btnTornar = findViewById(R.id.btnTornar)

        idEsdeveniment = intent.getStringExtra("idEsdeveniment") ?: ""
        idTipus = intent.getStringExtra("idTipus") ?: ""

        btnTornar.setOnClickListener {
            finish()
        }

        btnAfegirCistella.setOnClickListener {
            afegirACistella()
        }


        if (savedInstanceState == null) {
            Dades.seleccio.clear()
            for (seient in Dades.seientsCistella) {
                if (seient.idEsdeveniment == idEsdeveniment && seient.idTipus == idTipus) {
                    Dades.seleccio.add(seient)
                }
            }
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            textAvis.visibility = View.GONE
            scrollSeients.visibility = View.VISIBLE
            demanarSeients()
        }
    }

    fun demanarSeients() {
        if (!Connexio.estaConnectat()) {
            Toast.makeText(this, "No hi ha connexió amb el servidor", Toast.LENGTH_LONG).show()
            return
        }

        Connexio.onMissatge = { missatge ->
            runOnUiThread {
                rebreResposta(missatge)
            }
        }

        val peticio = JSONObject()
        peticio.put("type", "seients")
        peticio.put("esdeveniment", idEsdeveniment)
        peticio.put("tipus", idTipus)
        Connexio.enviar(peticio.toString())
    }

    fun rebreResposta(missatge: String) {
        val obj = JSONObject(missatge)
        val type = obj.getString("type")

        if (type == "seients") {
            pintarSeients(obj.getJSONArray("seients"))
        }

        if (type == "error") {
            Toast.makeText(this, "No s'han pogut carregar els seients", Toast.LENGTH_LONG).show()
        }
    }

    fun pintarSeients(seients: JSONArray) {
        gridSeients.removeAllViews()

        var maxFila = 0
        var maxColumna = 0
        for (i in 0 until seients.length()) {
            val seient = seients.getJSONObject(i)
            if (seient.getInt("fila") > maxFila) maxFila = seient.getInt("fila")
            if (seient.getInt("columna") > maxColumna) maxColumna = seient.getInt("columna")
        }
        gridSeients.rowCount = maxFila
        gridSeients.columnCount = maxColumna

        val mida = (28 * resources.displayMetrics.density).toInt()
        val marge = (3 * resources.displayMetrics.density).toInt()

        for (i in 0 until seients.length()) {
            val seient = seients.getJSONObject(i)
            val id = seient.getString("id")
            val fila = seient.getInt("fila")
            val columna = seient.getInt("columna")
            val estat = seient.getString("estat")

            val butaca = View(this)
            val params = GridLayout.LayoutParams(GridLayout.spec(fila - 1), GridLayout.spec(columna - 1))
            params.width = mida
            params.height = mida
            params.setMargins(marge, marge, marge, marge)
            butaca.layoutParams = params
            butaca.setBackgroundResource(R.drawable.bg_seient)

            pintarButaca(butaca, id, estat)

            if (estat == "lliure") {
                butaca.setOnClickListener {
                    canviarSeleccio(id, fila, columna)
                    pintarButaca(butaca, id, estat)
                }
            }

            gridSeients.addView(butaca)
        }
    }

    fun pintarButaca(butaca: View, id: String, estat: String) {
        var color = Color.parseColor("#43A047")
        if (estat == "ocupat") {
            color = Color.parseColor("#E53935")
        } else if (estat == "bloquejat") {
            color = Color.parseColor("#9E9E9E")
        } else if (estaSeleccionat(id)) {
            color = Color.parseColor("#1E88E5")
        }
        butaca.backgroundTintList = ColorStateList.valueOf(color)
    }

    fun estaSeleccionat(id: String): Boolean {
        for (seient in Dades.seleccio) {
            if (seient.idSeient == id) {
                return true
            }
        }
        return false
    }

    fun canviarSeleccio(id: String, fila: Int, columna: Int) {

        for (seient in Dades.seleccio) {
            if (seient.idSeient == id) {
                Dades.seleccio.remove(seient)
                return
            }
        }

        Dades.seleccio.add(SeientCistella(idEsdeveniment, idTipus, id, fila, columna))
    }

    fun afegirACistella() {

        val perTreure = mutableListOf<SeientCistella>()
        for (seient in Dades.seientsCistella) {
            if (seient.idEsdeveniment == idEsdeveniment && seient.idTipus == idTipus) {
                perTreure.add(seient)
            }
        }
        Dades.seientsCistella.removeAll(perTreure)
        Dades.seientsCistella.addAll(Dades.seleccio)

        Toast.makeText(this, Dades.seleccio.size.toString() + " seients a la cistella", Toast.LENGTH_SHORT).show()
        finish()
    }
}