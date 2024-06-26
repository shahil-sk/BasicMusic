package com.sk.basicmusic

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class about_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about)

        val info = findViewById<TextView>(R.id.tvInfo)

        info.text = "This is made to understand the most basic workings of the android-studio" +
                " like (MediaPlayer API's, MutableLiveData, ViewModel, Runnables, Handlers etc) " +
                "Main purpose of this project is to develop my skills as beginner in Android App Development"

    }
}