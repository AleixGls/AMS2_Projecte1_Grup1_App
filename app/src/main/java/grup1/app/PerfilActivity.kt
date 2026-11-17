package grup1.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class PerfilActivity : AppCompatActivity() {

    private lateinit var editUrl: EditText
    private lateinit var editNom: EditText
    private lateinit var editCorreu: EditText

    private var urlAntiga = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        editUrl = findViewById(R.id.editUrlPerfil)
        editNom = findViewById(R.id.editNomPerfil)
        editCorreu = findViewById(R.id.editCorreuPerfil)

        carregarDades()

        findViewById<Button>(R.id.btnGuardarPerfil).setOnClickListener {
            guardarDades()
        }

        findViewById<LinearLayout>(R.id.btnMenuCartellera).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        findViewById<LinearLayout>(R.id.btnMenuEntrades).setOnClickListener {
            startActivity(Intent(this, MevesEntradesActivity::class.java))
            finish()
        }
    }

    private fun carregarDades() {
        val fitxer = File(filesDir, "config.xml")
        if (!fitxer.exists()) {
            return
        }

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fitxer)
        urlAntiga = doc.getElementsByTagName("url").item(0).textContent
        editUrl.setText(urlAntiga)
        editNom.setText(doc.getElementsByTagName("nom").item(0).textContent)
        editCorreu.setText(doc.getElementsByTagName("correu").item(0).textContent)
    }

    private fun guardarDades() {
        val url = editUrl.text.toString().trim()
        val nom = editNom.text.toString().trim()

        if (url.isEmpty() || nom.isEmpty()) {
            Toast.makeText(this, "Omple la URL i el nom", Toast.LENGTH_SHORT).show()
            return
        }

        val fitxer = File(filesDir, "config.xml")
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fitxer)
        doc.getElementsByTagName("url").item(0).textContent = url
        doc.getElementsByTagName("nom").item(0).textContent = nom

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(DOMSource(doc), StreamResult(fitxer))

        if (url != urlAntiga) {
            Toast.makeText(this, "URL canviada, torna't a connectar", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ConfigActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            Toast.makeText(this, "Dades guardades", Toast.LENGTH_SHORT).show()
        }
    }
}