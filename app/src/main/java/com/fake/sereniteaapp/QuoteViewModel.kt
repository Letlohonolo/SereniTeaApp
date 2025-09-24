package com.fake.sereniteaapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.fake.sereniteaapp.RetrofitClient


class QuoteViewModel : ViewModel() {
    private val _quote = MutableLiveData<Quote>()
    val quote: LiveData<Quote> get() = _quote

}