package grup1.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EsdevenimentAdapter(
    val esdeveniments: List<Esdeveniment>,
    val onClick: (Esdeveniment) -> Unit
) : RecyclerView.Adapter<EsdevenimentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textNom: TextView = view.findViewById(R.id.textNom)
        val textDataHora: TextView = view.findViewById(R.id.textDataHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_esdeveniment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val esdeveniment = esdeveniments[position]
        holder.textNom.text = esdeveniment.nom
        holder.textDataHora.text = esdeveniment.dataHora.replace("T", "  ")
        holder.itemView.setOnClickListener {
            onClick(esdeveniment)
        }
    }

    override fun getItemCount(): Int {
        return esdeveniments.size
    }
}