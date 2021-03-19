package com.example.indydemo.indy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.indydemo.R
import com.example.indydemo.databinding.FragmentIndyBinding
import com.example.indydemo.database.CredentialDatabase
import com.example.indydemo.ident.IdentityViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class IndyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val binding: FragmentIndyBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_indy, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = CredentialDatabase.getInstance(application).credentialDao
        val viewModelFactory = IndyViewModelFactory(dataSource, requireContext(), application)

        val indyViewModel = ViewModelProvider(this, viewModelFactory)
            .get(IndyViewModel::class.java)

        val identityViewModel: IdentityViewModel by activityViewModels()

        binding.identityViewModel = identityViewModel
        binding.indyViewModel = indyViewModel
        binding.lifecycleOwner = this

        val loadingDialog = LoadingDialog(requireActivity())


        binding.initializeDemoButton.setOnClickListener {
            loadingDialog.startLoadingDialog()
            indyViewModel.initialise()
        }

        indyViewModel.initialisationDone.observe(viewLifecycleOwner, {
            if (it == true) {
                loadingDialog.dismissDialog()
                identityViewModel.setIdentityButtonVisible()
            }
        })

        binding.requestIdentityButton.setOnClickListener {
            this.findNavController().navigate(R.id.action_indyFragment_to_introFragment)
        }

        identityViewModel.identificationDone.observe(viewLifecycleOwner, {
            if (it == true) {
                loadingDialog.startLoadingDialog()
                indyViewModel.identityCertificate()
            }
        })

        indyViewModel.identificationDone.observe(viewLifecycleOwner, {
            if (it == true) {
                loadingDialog.dismissDialog()
                indyViewModel.identificationFinished()
            }
        })

        binding.requestDegreeButton.setOnClickListener {
            if (indyViewModel.proofRequestDegreeDone.value == true) {
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Request your Degree-Credential?")
                builder.setMessage("You already created a Proof-Request for Degree-Credential!")
                builder.setPositiveButton("Yes") { _, _ ->
                    loadingDialog.startLoadingDialog()
                    indyViewModel.degreeCertificate()
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.show()
            } else {
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Create Proof-Request for Degree-Credential?")
                builder.setMessage("After this you can request your Degree-Credential!")
                builder.setPositiveButton("Yes") { _, _ ->
                    loadingDialog.startLoadingDialog()
                    indyViewModel.degreeRequest()
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.show()
            }
        }

        indyViewModel.proofRequestDegreeDone.observe(viewLifecycleOwner, {
            if (it == true) {
                loadingDialog.dismissDialog()

                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Request your Degree-Credential?")
                builder.setMessage("After this the Degree-Credential will be added to your Wallet!")
                builder.setPositiveButton("Yes") { _, _ ->
                    loadingDialog.startLoadingDialog()
                    indyViewModel.degreeCertificate()
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.show()
            }
        })

        indyViewModel.degreeCredentialDone.observe(viewLifecycleOwner, {
            if (it == true) {
                loadingDialog.dismissDialog()
                indyViewModel.degreeFinished()
            }
        })

        binding.applyJobButton.setOnClickListener {
            if (indyViewModel.proofRequestJobDone.value == true) {
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Request your Job-Credential?")
                builder.setMessage("You already created a Proof-Request for Job-Credential!")
                builder.setPositiveButton("Yes") { _, _ ->
                    loadingDialog.startLoadingDialog()
                    indyViewModel.jobCertificate()
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.show()
            } else {
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Create Proof-Request for Job-Application?")
                builder.setMessage("After this you can request your Job-Credential!")
                builder.setPositiveButton("Yes") { _, _ ->
                    loadingDialog.startLoadingDialog()
                    indyViewModel.jobRequest()
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.show()
            }
        }

        indyViewModel.proofRequestJobDone.observe(viewLifecycleOwner, {
            if (it == true) {
                loadingDialog.dismissDialog()

                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Request your Job-Credential?")
                builder.setMessage("After this the Job-Credential will be added to your Wallet!")
                builder.setPositiveButton("Yes") { _, _ ->
                    loadingDialog.startLoadingDialog()
                    indyViewModel.jobCertificate()
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.show()
            }
        })

        indyViewModel.jobCredentialDone.observe(viewLifecycleOwner, {
            if (it == true) {
                loadingDialog.dismissDialog()
                indyViewModel.jobFinished()
            }
        })

        binding.requestLoanButton.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Create Proof-Request for Loan-Application?")
            builder.setMessage("With this you can apply for Loan.")
            builder.setPositiveButton("Yes") { _, _ ->
                loadingDialog.startLoadingDialog()
                indyViewModel.loanRequest()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }

        indyViewModel.proofRequestLoanDone.observe(viewLifecycleOwner, {
            if (it == true) {
                loadingDialog.dismissDialog()
                indyViewModel.loanFinished()

                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("You were accepted!")
                builder.setMessage("")
                builder.setNeutralButton("Thank's!") { _, _ -> }
                builder.show()
            }
        })




        // bind the adapter of the recycler view and set the ClickListener for clicking on an item
        val adapter = WalletAdapter()
        binding.credentialList.adapter = adapter

        // tell the adapter what data should be adapted
        indyViewModel.allCredentials.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })


        return binding.root
    }


}