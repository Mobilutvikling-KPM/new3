package com.example.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

/*
* Hjelper viewmodel med å ta imot argumenter
 */
class ViewModelFactory(private var type: Int): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EventViewModel::class.java)){
            return EventViewModel(type) as T
        }
        else if(modelClass.isAssignableFrom(PersonViewModel::class.java)){
            return PersonViewModel(type) as T
        }
        else if(modelClass.isAssignableFrom(KommentarViewModel::class.java)){
            return KommentarViewModel(type) as T
        }
        throw IllegalArgumentException("ViewModel not found")
    }
}