package grup1.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

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

        recyclerEsdeveniments = findViewById(R.id.recyclerEsdeveniments)
        btnCistella = findViewById(R.id.btnCistella)
        textComptador = findViewById(R.id.textComptador)

        recyclerEsdeveniments.layoutManager = LinearLayoutManager(this)

        btnCistella.setOnClickListener {
            val intent = Intent(this, CistellaActivity::class.java)
            startActivity(intent)
        }

        if (Connexio.estaConnectat()) {
            demanarCataleg()
        } else {
            carregarCatalegLocal()
            mostrarCataleg()
        }
    }

    override fun onResume() {
        super.onResume()
        actualitzarComptador()
    }

    fun demanarCataleg() {
        Connexio.onMissatge = { missatge ->
            val obj = JSONObject(missatge)
            val type = obj.getString("type")
            if (type == "catalog") {
                val cataleg = CatalegParser.llegirJSON(obj.getJSONObject("catalog").toString())
                runOnUiThread {
                    Dades.cataleg = cataleg
                    mostrarCataleg()
                }
            } else if (type == "error") {
                runOnUiThread {
                    Toast.makeText(this, "Error: " + obj.optString("message"), Toast.LENGTH_LONG).show()
                }
            }
        }

        val peticio = JSONObject()
        peticio.put("type", "cataleg")
        Connexio.enviar(peticio.toString())
    }

    fun mostrarCataleg() {
        recyclerEsdeveniments.adapter = EsdevenimentAdapter(Dades.cataleg.esdeveniments) { esdeveniment ->
            val intent = Intent(this, EntradesActivity::class.java)
            intent.putExtra("idEsdeveniment", esdeveniment.id)
            startActivity(intent)
        }
    }

    fun actualitzarComptador() {
        textComptador.text = Dades.cistella.size.toString()
    }

    fun carregarCatalegLocal() {
        val text = assets.open("cataleg.json").bufferedReader().readText()
        Dades.cataleg = CatalegParser.llegirJSON(text)
    }
}