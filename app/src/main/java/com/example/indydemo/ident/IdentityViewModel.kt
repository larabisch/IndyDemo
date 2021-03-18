package com.example.indydemo.ident

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class IdentityViewModel: ViewModel() {

    private val _identificationVisible = MutableLiveData<Boolean>(). apply { value = false }
    val identificationVisible
        get() = _identificationVisible
    fun setIdentityButtonVisible() { _identificationVisible.value = true }
    fun setIdentityButtonInvisible() { _identificationVisible.value = false }

    private val _identificationDone = MutableLiveData<Boolean>(). apply { value = false }
    val identificationDone
        get() = _identificationDone
    fun identificationSuccess() { _identificationDone.value = true }

}