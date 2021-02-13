package com.example.indydemo.indy.credential_detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.indydemo.R
import com.example.indydemo.database.CredentialDatabase
import com.example.indydemo.databinding.FragmentCredentialDetailBinding


class CredentialDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // building the binding
        val binding: FragmentCredentialDetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_credential_detail, container, false
        )

        // building the ViewModelFactory
        val application = requireNotNull(this.activity).application
        val arguments = arguments?.let { CredentialDetailFragmentArgs.fromBundle(it) }

        val dataSource = CredentialDatabase.getInstance(application).credentialDao
        val viewModelFactory = CredentialDetailViewModelFactory(arguments?.credentialKey!!, dataSource)

        // building the ViewModel
        val credentialViewModel = ViewModelProvider(this, viewModelFactory)
            .get(CredentialDetailViewModel::class.java)

        // binding the ViewModel to the binding in the layout XML
        binding.credentialViewModel = credentialViewModel
        // add this for observing data changes with live data
        binding.lifecycleOwner = this


        credentialViewModel.navigateToCredentialList.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController().navigate(
                    CredentialDetailFragmentDirections.actionCredentialDetailFragmentToIndyFragment())
                credentialViewModel.doneNavigating()
            }
        })

        return binding.root

    }

}