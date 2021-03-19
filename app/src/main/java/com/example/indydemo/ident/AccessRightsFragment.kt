package com.example.indydemo.ident

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.example.indydemo.R
import com.example.indydemo.database.IdentityCredential
import com.example.indydemo.database.ServiceProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.ArrayList


class AccessRightsFragment : Fragment() {


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(com.twigbit.identsdk.R.layout.fragment_access_rights,
                container, false)

        val buttonAccept = view.findViewById<View>(R.id.buttonAccept) as Button
        val buttonDeny = view.findViewById<View>(R.id.buttonDeny) as Button
        val cardServiceProvider = view.findViewById<View>(R.id.cardServiceProvider) as CardView
        val recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        val textServiceProvider = view.findViewById<View>(R.id.textServiceProvider) as TextView
        val textPurpose = view.findViewById<View>(R.id.textPurpose) as TextView


        buttonAccept.setOnClickListener {
            this.findNavController().navigate(R.id.action_accessRightsFragment_to_successFragment)
        }
        cardServiceProvider.setOnClickListener {
            this.findNavController().navigate(R.id.action_accessRightsFragment_to_certificateFragment)
        }
        buttonDeny.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Are you sure?")
            builder.setMessage("The Identity-Credential cant't be added to your Wallet.")
            builder.setPositiveButton("Yes") { _, _ ->
                this.findNavController().navigate(R.id.action_accessRightsFragment_to_indyFragment)
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }

        val accessRights = ArrayList<String>()
        accessRights.add("Document type")
        accessRights.add(IdentityCredential.attribute1_name)
        accessRights.add(IdentityCredential.attribute2_name)
        accessRights.add(IdentityCredential.attribute3_name)
        accessRights.add(IdentityCredential.attribute4_name)
        accessRights.add(IdentityCredential.attribute5_name)

        recyclerView.adapter = MyAdapter(accessRights)

        textServiceProvider.text = ServiceProvider.serviceProvider
        textPurpose.text = ServiceProvider.purpose

        return view
    }
}


class MyAdapter(var data: List<String>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_access_right, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.accessRight.text = item
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val accessRight: TextView = itemView.findViewById(R.id.text)
    }
}