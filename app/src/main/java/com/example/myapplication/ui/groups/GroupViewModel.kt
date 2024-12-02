package com.example.myapplication.ui.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GroupViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Groups Fragment"
    }
    val text: LiveData<String> = _text
}