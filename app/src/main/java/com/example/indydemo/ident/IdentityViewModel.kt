package com.example.indydemo.ident

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class IdentityViewModel: ViewModel() {

    private val _initialisationVisible = MutableLiveData<Boolean>(). apply { value = false }
    val initialisationVisible
        get() = _initialisationVisible
    fun setInitializeButtonVisible() { _initialisationVisible.value = true }
    fun setInitializeButtonInvisible() { _initialisationVisible.value = false }


    private val _identificationDone = MutableLiveData<Boolean>(). apply { value = false }
    val identificationDone
        get() = _identificationDone
    fun identificationSuccess() { _identificationDone.value = true }

}