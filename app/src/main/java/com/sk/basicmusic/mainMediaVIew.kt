package com.sk.basicmusic

import android.media.MediaPlayer
import android.media.session.MediaController
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout.LengthCounter

class mainMediaVIew:ViewModel() {

    var dueText = MutableLiveData<Int>()
    var playerText = MutableLiveData<Int>()
    var count:Int = 0
    var songName = MutableLiveData<String>()

    var mediaplayer: MediaPlayer? = null

    init {
        playerText.value = 0
        dueText.value = 0
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