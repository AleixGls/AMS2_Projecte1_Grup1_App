package grup1.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class ConfigActivity : AppCompatActivity() {

    lateinit var editUrl: EditText
    lateinit var editNom: EditText
    lateinit var editCorreu: EditText
    lateinit var btnConnectar: Button
    lateinit var textEstat: TextView

    var cartelleraOberta = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_config)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editUrl = findViewById(R.id.editUrl)
        editNom = findViewById(R.id.editNom)
        editCorreu = findViewById(R.id.editCorreu)
        btnConnectar = findViewById(R.id.btnConnectar)
        textEstat = findViewById(R.id.textEstat)

        carregarConfig()

        Connexio.onConnectat = {
            runOnUiThread {
                mostrarResultat("Connectat al servidor")
            }
        }
        Connexio.onError = { missatge ->
            runOnUiThread {
                mostrarResultat(missatge)
            }
        }

        btnConnectar.setOnClickListener {
            val url = editUrl.text.toString()
            val nom = editNom.text.toString()
            val correu = editCorreu.text.toString()

            if (url.isEmpty() || nom.isEmpty() || correu.isEmpty()) {
                textEstat.text = "Omple tots els camps"
            } else if (!correu.contains("@")) {
                textEstat.text = "El correu no és vàlid"
            } else {
                guardarConfig(url, nom, correu)
                textEstat.text = "Connectant..."
                try {
                    Connexio.connectar(url)
                } catch (e: Exception) {
                    textEstat.text = "URL no vàlida: " + e.message
                }
            }
        }
    }

    fun mostrarResultat(missatge: String) {
        if (cartelleraOberta) {
            return
        }
        cartelleraOberta = true
        textEstat.text = missatge
        Toast.makeText(this, missatge, Toast.LENGTH_LONG).show()
        obrirCartellera()
    }

    fun obrirCartellera() {
        Connexio.onConnectat = null
        Connexio.onError = null
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun guardarConfig(url: String, nom: String, correu: String) {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.newDocument()

        val config = doc.createElement("config")
        doc.appendChild(config)

        val elUrl = doc.createElement("url")
        elUrl.textContent = url
        config.appendChild(elUrl)

        val elNom = doc.createElement("nom")
        elNom.textContent = nom
        config.appendChild(elNom)

        val elCorreu = doc.createElement("correu")
        elCorreu.textContent = correu
        config.appendChild(elCorreu)

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        val fitxer = File(filesDir, "config.xml")
        transformer.transform(DOMSource(doc), StreamResult(fitxer))
    }

    fun carregarConfig() {
        val fitxer = File(filesDir, "config.xml")
        if (!fitxer.exists()) {
            return
        }

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(fitxer)

        val url = doc.getElementsByTagName("url").item(0).textContent
        val nom = doc.getElementsByTagName("nom").item(0).textContent
        val correu = doc.getElementsByTagName("correu").item(0).textContent

        editUrl.setText(url)
        editNom.setText(nom)
        editCorreu.setText(correu)
    }
}