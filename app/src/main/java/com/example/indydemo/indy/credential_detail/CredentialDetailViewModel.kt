package com.example.indydemo.indy.credential_detail


import androidx.lifecycle.*
import com.example.indydemo.database.Credential
import com.example.indydemo.database.CredentialDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CredentialDetailViewModel(credentialKey: Int = 0, dataSource: CredentialDao) : ViewModel() {

    val database = dataSource

    private val credential = MediatorLiveData<Credential>()

    fun getCredential() = credential

    init {
        credential.addSource(database.getCredentialWithId(credentialKey), credential::setValue)
    }

    private val _navigateToCredentialList = MutableLiveData<Boolean?>()
    val navigateToCredentialList: LiveData<Boolean?>
        get() = _navigateToCredentialList
    fun doneNavigating() { _navigateToCredentialList.value = null }
    fun navigate() { _navigateToCredentialList.value = true }


    fun deleteCredential(credential: Credential) {
        viewModelScope.launch {
            delete(credential.credentialId!!)
            navigate()
        }
    }

    private suspend fun delete(id: Int) {
        withContext(Dispatchers.IO) {
            database.deleteCredential(id)
        }
    }

}



class CredentialDetailViewModelFactory(
        private val sleepNightKey: Int,
        private val dataSource: CredentialDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CredentialDetailViewModel::class.java)) {
            return CredentialDetailViewModel(sleepNightKey, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}