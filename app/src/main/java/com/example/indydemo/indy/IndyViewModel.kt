package com.example.indydemo.indy

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.indydemo.database.CredentialDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.indydemo.database.Credential


class IndyViewModel (val database: CredentialDao, context: Context, application: Application) : AndroidViewModel(application)  {


    val allCredentials = database.getAllCredentials()
    var environmentPath : String = context.getExternalFilesDir(null)!!.absolutePath
    val indy = Indy()


    private val _navigateToCredentialDetail = MutableLiveData<Int>()
    val navigateToCredentialDetail
        get() = _navigateToCredentialDetail
    fun onItemClicked(credentialId: Int) { _navigateToCredentialDetail.value = credentialId }
    fun onCredentialDetailNavigated() { _navigateToCredentialDetail.value = null }



    private val _initialisationDone = MutableLiveData<Boolean>()
    val initialisationDone
        get() = _initialisationDone
    private fun initialisationSuccess() { _initialisationDone.value = true }

    private val _identificationDone = MutableLiveData<Boolean>()
    val identificationDone
        get() = _identificationDone
    private fun identificationSuccess() { _identificationDone.value = true }
    fun identificationFinished() { _initialisationDone.value = false}

    private val _proofRequestDegreeDone = MutableLiveData<Boolean>()
    val proofRequestDegreeDone
        get() = _proofRequestDegreeDone
    private fun proofRequestDegreeDone() { _proofRequestDegreeDone.value = true }

    private val _degreeCredentialDone = MutableLiveData<Boolean>()
    val degreeCredentialDone
        get() = _degreeCredentialDone
    private fun degreeCredentialSuccess() { _degreeCredentialDone.value = true }
    fun degreeFinished() { _identificationDone.value = false }

    private val _proofRequestJobDone = MutableLiveData<Boolean>()
    val proofRequestJobDone
        get() = _proofRequestJobDone
    private fun proofRequestJobSuccess() { _proofRequestJobDone.value = true }

    private val _jobCredentialDone = MutableLiveData<Boolean>()
    val jobCredentialDone
        get() = _jobCredentialDone
    private fun jobCredentialSuccess() { _jobCredentialDone.value = true }
    fun jobFinished() { _degreeCredentialDone.value = false }

    private val _proofRequestLoanDone = MutableLiveData<Boolean>()
    val proofRequestLoanDone
        get() = _proofRequestLoanDone
    private fun proofRequestLoanSuccess() { _proofRequestLoanDone.value = true }
    fun loanFinished()  { _jobCredentialDone.value = false }



    private fun setVariables() {
        _initialisationDone.value = false
        _identificationDone.value = false
        _proofRequestDegreeDone.value = false
        _degreeCredentialDone.value = false
        _proofRequestJobDone.value = false
        _jobCredentialDone.value = false
        _proofRequestLoanDone.value = false
    }


    fun initialise() {
        viewModelScope.launch {
            setVariables()
            indy.loadLibrary(environmentPath)
            deleteAll()
            initialiseDemo()
            initialisationSuccess()
        }
    }

    fun identityCertificate() {
        viewModelScope.launch {
            getIdentityCertificate()
            insert(Credential(null, "Passport", "Government", 7))
            identificationSuccess()
        }
    }

    fun degreeRequest() {
        viewModelScope.launch {
            requestDegree()
            proofRequestDegreeDone()
        }
    }

    fun degreeCertificate() {
        viewModelScope.launch {
            getDegreeCertificate()
            insert(Credential(null, "Master-Certificate", "University", 5))
            degreeCredentialSuccess()
        }
    }

    fun jobRequest() {
        viewModelScope.launch {
            requestJob()
            proofRequestJobSuccess()
        }
    }

    fun jobCertificate() {
        viewModelScope.launch {
            getJobCertificate()
            insert(Credential(null, "Job-Certificate", "Company", 9))
            jobCredentialSuccess()
        }
    }

    fun loanRequest() {
        viewModelScope.launch {
            requestLoan()
            proofRequestLoanSuccess()
        }
    }


    private fun doAll() {
        viewModelScope.launch {
            all()
        }
    }

    private suspend fun all() {
        withContext(Dispatchers.Default) {
            indy.loadLibrary(environmentPath)
            indy.initializeWallets()
            indy.createIdentityCredOffer()
            indy.createDegreeCredOffer()
            indy.createJobCredOffer()
            indy.createProofRequestDegreeCertificate()
            indy.createProofRequestJobApplication()
            indy.createProofRequestLoanApplication()

            indy.requestIdentityCertificate()
            indy.proofRequestDegreeCertificate()
            indy.requestDegreeCertificate()
            indy.proofRequestApplicationJob()
            indy.requestJobCertificate()
            indy.proofRequestApplicationLoan()
        }
    }


    private suspend fun initialiseDemo() {
        withContext(Dispatchers.Default) {
            indy.initializeWallets()
            indy.createIdentityCredOffer()
            indy.createDegreeCredOffer()
            indy.createJobCredOffer()
            indy.createProofRequestDegreeCertificate()
            indy.createProofRequestJobApplication()
            indy.createProofRequestLoanApplication()
        }

    }


    private suspend fun getIdentityCertificate() {
        withContext(Dispatchers.Default) {
            indy.requestIdentityCertificate()
        }
    }

    private suspend fun requestDegree() {
        withContext(Dispatchers.Default) {
            indy.proofRequestDegreeCertificate()
        }
    }

    private suspend fun getDegreeCertificate() {
        withContext(Dispatchers.Default) {
            indy.requestDegreeCertificate()
        }
    }

    private suspend fun requestJob() {
        withContext(Dispatchers.Default) {
            indy.proofRequestApplicationJob()
        }
    }

    private suspend fun getJobCertificate() {
        withContext(Dispatchers.Default) {
            indy.requestJobCertificate()
        }
    }

    private suspend fun requestLoan() {
        withContext(Dispatchers.Default) {
            indy.proofRequestApplicationLoan()
        }
    }





    private suspend fun insert(cred: Credential) {
        withContext(Dispatchers.IO) { database.insert(cred) }
    }

    private suspend fun deleteAll() {
        withContext(Dispatchers.IO) {database.deleteAll()}
    }

}

