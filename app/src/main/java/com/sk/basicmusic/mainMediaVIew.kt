package com.sk.basicmusic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class mainMediaVIew:ViewModel() {

    var playerText = MutableLiveData<Int>()
    var count:Int = 0
    var songName = MutableLiveData<String>()

    init {
        playerText.value = 0
        songName.value = ""
    }

    fun countUp()
    {
        ++count
    }

    fun countDown()
    {
        --count
    }
}