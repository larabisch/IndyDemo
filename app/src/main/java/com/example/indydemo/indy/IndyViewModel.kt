package com.example.indydemo.indy

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.indydemo.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class IndyViewModel(val database: CredentialDao, context: Context, application: Application)
    : AndroidViewModel(application)  {


    val allCredentials = database.getAllCredentials()
    var environmentPath : String = context.getExternalFilesDir(null)!!.absolutePath
    val indy = Indy()


    init {
        viewModelScope.launch { deleteAll() }
    }



    private val _addButtonIsClicked = MutableLiveData<Boolean>().apply { value = true }
    val addButtonIsClicked
        get() = _addButtonIsClicked
    fun closeAnimationDone() { _addButtonIsClicked.value = true }
    fun openAnimationDone() { _addButtonIsClicked.value = false }


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



    private fun setVariables() {
        _initialisationDone.value = false
        _identificationDone.value = false
        _proofRequestDegreeDone.value = false
        _degreeCredentialDone.value = false
        _proofRequestJobDone.value = false
        _jobCredentialDone.value = false
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
            insert(Credential(
                    null, IdentityCredential.document, IdentityCredential.issuer,
                    IdentityCredential.attribute1_name, IdentityCredential.attribute1_value,
                    IdentityCredential.attribute2_name, IdentityCredential.attribute2_value,
                    IdentityCredential.attribute3_name, IdentityCredential.attribute3_value,
                    IdentityCredential.attribute4_name, IdentityCredential.attribute4_value,
                    IdentityCredential.attribute5_name, IdentityCredential.attribute5_value,
            ))
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
            insert(Credential(
                    null, DegreeCredential.document, DegreeCredential.issuer,
                    DegreeCredential.attribute1_name, DegreeCredential.attribute1_value,
                    DegreeCredential.attribute2_name, DegreeCredential.attribute2_value,
                    DegreeCredential.attribute3_name, DegreeCredential.attribute3_value,
                    DegreeCredential.attribute4_name, DegreeCredential.attribute4_value,
                    DegreeCredential.attribute5_name, DegreeCredential.attribute5_value,
            ))
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
            insert(Credential(
                    null, JobCredential.document, JobCredential.issuer,
                    JobCredential.attribute1_name, JobCredential.attribute1_value,
                    JobCredential.attribute2_name, JobCredential.attribute2_value,
                    JobCredential.attribute3_name, JobCredential.attribute3_value,
                    JobCredential.attribute4_name, JobCredential.attribute4_value,
                    JobCredential.attribute5_name, JobCredential.attribute5_value,
            ))
            jobCredentialSuccess()
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
        }
    }

    private suspend fun getIdentityCertificate() {
        withContext(Dispatchers.Default) {
            indy.requestIdentityCertificate()
        }
    }

    private suspend fun requestDegree() {
        withContext(Dispatchers.Default) {
            indy.proofDegreeCertificate()
        }
    }

    private suspend fun getDegreeCertificate() {
        withContext(Dispatchers.Default) {
            indy.requestDegreeCertificate()
        }
    }

    private suspend fun requestJob() {
        withContext(Dispatchers.Default) {
            indy.proofApplicationJob()
        }
    }

    private suspend fun getJobCertificate() {
        withContext(Dispatchers.Default) {
            indy.requestJobCertificate()
        }
    }


    private suspend fun insert(cred: Credential) {
        withContext(Dispatchers.IO) {
            database.insert(cred)
        }
    }

    private suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            database.deleteAll()
        }
    }

}

