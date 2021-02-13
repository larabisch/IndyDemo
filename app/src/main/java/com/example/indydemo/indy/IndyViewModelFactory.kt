package com.example.indydemo.indy

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.indydemo.database.CredentialDao

class IndyViewModelFactory(
    private val dataSource: CredentialDao,
    private val context: Context,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IndyViewModel::class.java)) {
            return IndyViewModel(dataSource, context, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}