package grup1.app

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EntradesActivity : AppCompatActivity() {

    lateinit var textNom: TextView
    lateinit var textInfo: TextView
    lateinit var textDescripcio: TextView
    lateinit var layoutEntrades: LinearLayout
    lateinit var btnTornar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_entrades)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textNom = findViewById(R.id.textNom)
        textInfo = findViewById(R.id.textInfo)
        textDescripcio = findViewById(R.id.textDescripcio)
        layoutEntrades = findViewById(R.id.layoutEntrades)
        btnTornar = findViewById(R.id.btnTornar)

        btnTornar.setOnClickListener {
            finish()
        }

        val id = intent.getStringExtra("idEsdeveniment")
        val esdeveniment = Dades.buscarEsdeveniment(id ?: "")
        if (esdeveniment == null) {
            finish()
            return
        }

        mostrarEsdeveniment(esdeveniment)
    }

    fun mostrarEsdeveniment(esdeveniment: Esdeveniment) {
        textNom.text = esdeveniment.nom
        textInfo.text = esdeveniment.categoria + "\n" +
                esdeveniment.dataHora.replace("T", "  ") + "\n" +
                Dades.nomRecinte(esdeveniment.recinte)
        textDescripcio.text = esdeveniment.descripcio

        var hiHaGenerals = false
        for (tipus in esdeveniment.tipusEntrades) {
            if (Dades.esGeneral(tipus)) {
                hiHaGenerals = true
                afegirBoto(tipus)
            }
        }

        if (!hiHaGenerals) {
            val text = TextView(this)
            text.text = "No hi ha entrades generals disponibles"
            text.setPadding(0, 24, 0, 0)
            layoutEntrades.addView(text)
        }
    }

    fun afegirBoto(tipus: TipusEntrada) {
        val boto = Button(this)
        boto.text = tipus.nom + " - " + String.format("%.2f €", tipus.preu)
        boto.setTextColor(getColor(android.R.color.white))
        boto.backgroundTintList = ColorStateList.valueOf(getColor(R.color.vermell))
        boto.setOnClickListener {
            Dades.cistella.add(tipus)
            Toast.makeText(this, tipus.nom + " afegida a la cistella (" + Dades.cistella.size + ")", Toast.LENGTH_SHORT).show()
        }
        layoutEntrades.addView(boto)
    }
}