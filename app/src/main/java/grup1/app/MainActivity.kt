package grup1.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    lateinit var recyclerEsdeveniments: RecyclerView
    lateinit var btnCistella: ImageView
    lateinit var textComptador: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        carregarCatalegLocal()

        recyclerEsdeveniments = findViewById(R.id.recyclerEsdeveniments)
        btnCistella = findViewById(R.id.btnCistella)
        textComptador = findViewById(R.id.textComptador)

        recyclerEsdeveniments.layoutManager = LinearLayoutManager(this)
        recyclerEsdeveniments.adapter = EsdevenimentAdapter(Dades.cataleg.esdeveniments) { esdeveniment ->
            val intent = Intent(this, EntradesActivity::class.java)
            intent.putExtra("idEsdeveniment", esdeveniment.id)
            startActivity(intent)
        }

        btnCistella.setOnClickListener {
            val intent = Intent(this, CistellaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        actualitzarComptador()
    }

    fun actualitzarComptador() {
        textComptador.text = Dades.cistella.size.toString()
    }

    fun carregarCatalegLocal() {
        val text = assets.open("cataleg.json").bufferedReader().readText()
        Dades.cataleg = CatalegParser.llegirJSON(text)
    }
}