package com.example.indydemo.ident


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.indydemo.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class SuccessFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(com.twigbit.identsdk.R.layout.fragment_success,
                container, false)

        val buttonFinish = view.findViewById<View>(R.id.buttonFinish) as Button

        val identityViewModel: IdentityViewModel by activityViewModels()

        buttonFinish.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Your identity is valid!")
            builder.setMessage("The Identity-Credential will be added to your Wallet.")
            builder.setNeutralButton("Thank's!") { _, _ ->
                this.findNavController().navigate(R.id.action_successFragment_to_indyFragment)
                identityViewModel.identificationSuccess()
                identityViewModel.setIdentityButtonInvisible()
            }
            builder.show()
        }

        return view
    }


}
