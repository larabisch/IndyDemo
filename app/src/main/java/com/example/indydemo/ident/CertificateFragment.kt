package com.example.indydemo.ident


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.fragment.findNavController
import com.example.indydemo.R
import com.example.indydemo.database.ServiceProvider


class CertificateFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(com.twigbit.identsdk.R.layout.fragment_certificate,
                container, false)

        val buttonBack = view.findViewById<View>(R.id.buttonBack) as AppCompatImageButton
        val textIssuerName = view.findViewById<View>(R.id.textIssuerName) as TextView
        val textServiceProviderName = view.findViewById<View>(R.id.textServiceProviderName) as TextView
        val textPurpose = view.findViewById<View>(R.id.textPurpose) as TextView
        val textServiceProviderInfo = view.findViewById<View>(R.id.textServiceProviderInfo) as TextView
        val textValidity = view.findViewById<View>(R.id.textValidity) as TextView

        textIssuerName.text = ServiceProvider.issuer
        textServiceProviderName.text = ServiceProvider.serviceProvider
        textPurpose.text = ServiceProvider.purpose
        textServiceProviderInfo.text = ServiceProvider.serviceProviderInfo
        textValidity.text = ServiceProvider.validity

        buttonBack.setOnClickListener {
            this.findNavController().navigate(R.id.action_certificateFragment_to_accessRightsFragment)
        }

        return view
    }


}
