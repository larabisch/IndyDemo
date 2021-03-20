package com.example.indydemo.indy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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

    private val fromButton: Animation by lazy { AnimationUtils.loadAnimation(activity, R.anim.from_button_anim) }
    private val toButton: Animation by lazy { AnimationUtils.loadAnimation(activity, R.anim.to_button_anim) }
    private val openAnim: Animation by lazy { AnimationUtils.loadAnimation(activity, R.anim.rotate_open_anim) }
    private val closeAnim: Animation by lazy { AnimationUtils.loadAnimation(activity, R.anim.rotate_close_anim) }

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



        binding.startIdentification.setOnClickListener {
            if (identityViewModel.identificationDone.value == true) {
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Are you sure?")
                builder.setMessage("When you start the identification again everything will be started from the beginning.")
                builder.setPositiveButton("Yes") { _, _ ->
                    this.findNavController().navigate(R.id.action_indyFragment_to_introFragment)
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.show()
            } else {
                this.findNavController().navigate(R.id.action_indyFragment_to_introFragment)
            }
        }

        binding.initializeDemoButton.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Initialize the Demo?")
            builder.setMessage("Everything will be prepared for the demo. This could take a while.")
            builder.setPositiveButton("Yes") { _, _ ->
                loadingDialog.startLoadingDialog()
                indyViewModel.initialise()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }


        indyViewModel.initialisationDone.observe(viewLifecycleOwner, {
            if (it == true) {
                loadingDialog.dismissDialog()
                identityViewModel.setInitializeButtonInvisible()
            }
        })


        binding.requestIdentityButton.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("We found your valid Identity!")
            builder.setMessage("Do you want to Request your Identity-Credential?")
            builder.setPositiveButton("Yes") { _, _ ->
                loadingDialog.startLoadingDialog()
                indyViewModel.identityCertificate()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }


        indyViewModel.identificationDone.observe(viewLifecycleOwner, {
            if (it == true) {
                loadingDialog.dismissDialog()
                indyViewModel.identificationFinished()

                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Congratulation!")
                builder.setMessage("The Identity-Credential was added to your Wallet.")
                builder.setNeutralButton("Thank's!") { _, _ -> }
                builder.show()
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
                builder.setMessage("\nThe following Claims will be used:\n\n" +
                        " - Family name \n - Given name \n - Address \n - Date of birth \n - Nationality")
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

                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Congratulation!")
                builder.setMessage("The Degree-Credential was added to your Wallet.")
                builder.setNeutralButton("Thank's!") { _, _ -> }
                builder.show()
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
                builder.setMessage("\nThe following Claims will be used:\n\n" +
                        " - Family name \n - Given name \n - Specialization \n - Degree \n " +
                        "- Status \n - ZKP-Proof that your average is below 3.0")
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
                builder.setTitle("You were accepted!")
                builder.setMessage("Request your Job-Credential?")
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

                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Congratulation!")
                builder.setMessage("The Job-Credential was added to your Wallet.")
                builder.setNeutralButton("Thank's!") { _, _ -> }
                builder.show()
            }
        })

        binding.requestLoanButton.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Create Proof-Request for Loan-Application?")
            builder.setMessage("\nThe following Claims will be used:\n\n" +
                    " - Family name \n - Given name \n - Employee status \n" +
                    " - ZKP-Proof that you are over 18 \n - ZKP-Proof that your salary is over 2000")
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
                builder.setMessage("With this the demo is finished.")
                builder.setNeutralButton("Thank's!") { _, _ -> }
                builder.show()
            }
        })




        // bind the adapter of the recycler view
        val adapter = WalletAdapter()
        binding.credentialList.adapter = adapter

        indyViewModel.allCredentials.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })



        // setting up animation
        binding.addButton.setOnClickListener {
            if (indyViewModel.addButtonIsClicked.value == true){
                binding.initializeDemoButton.visibility = View.VISIBLE
                binding.initializeDemoButton.startAnimation(fromButton)

                binding.requestIdentityButton.visibility = View.VISIBLE
                binding.requestIdentityButton.startAnimation(fromButton)

                binding.requestDegreeButton.visibility = View.VISIBLE
                binding.requestDegreeButton.startAnimation(fromButton)

                binding.applyJobButton.visibility = View.VISIBLE
                binding.applyJobButton.startAnimation(fromButton)

                binding.requestLoanButton.visibility = View.VISIBLE
                binding.requestLoanButton.startAnimation(fromButton)

                binding.addButton.startAnimation(openAnim)
                indyViewModel.openAnimationDone()
            } else {
                binding.initializeDemoButton.visibility = View.INVISIBLE
                binding.initializeDemoButton.startAnimation(toButton)

                binding.requestIdentityButton.visibility = View.INVISIBLE
                binding.requestIdentityButton.startAnimation(toButton)

                binding.requestDegreeButton.visibility = View.INVISIBLE
                binding.requestDegreeButton.startAnimation(toButton)

                binding.applyJobButton.visibility = View.INVISIBLE
                binding.applyJobButton.startAnimation(toButton)

                binding.requestLoanButton.visibility = View.INVISIBLE
                binding.requestLoanButton.startAnimation(toButton)

                binding.addButton.startAnimation(closeAnim)
                indyViewModel.closeAnimationDone()
            }
        }



        return binding.root
    }


}