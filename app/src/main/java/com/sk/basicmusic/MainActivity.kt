package com.sk.basicmusic

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var sp:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    private lateinit var mediaView:mainMediaVIew

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    private lateinit var play: FloatingActionButton
    private lateinit var pause: FloatingActionButton
    private lateinit var stop: FloatingActionButton
    private var rIndicator:Boolean = false

    private lateinit var seekbar: SeekBar
    var mediaPlayer: MediaPlayer? = null

    private lateinit var playerText:TextView
    private lateinit var dueText:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        mediaView = ViewModelProvider(this).get(mainMediaVIew::class.java)
        sp = getSharedPreferences("USER", MODE_PRIVATE)
        editor = sp.edit()
        handler = Handler(Looper.getMainLooper())

        seekbar = findViewById(R.id.sbDap)
        play  = findViewById<FloatingActionButton>(R.id.fabPlay)
        pause = findViewById<FloatingActionButton>(R.id.fabPause)
        stop  = findViewById<FloatingActionButton>(R.id.fabStop)
        val next  = findViewById<FloatingActionButton>(R.id.fabForward)
        val back  = findViewById<FloatingActionButton>(R.id.fabBack)
        val repeat = findViewById<FloatingActionButton>(R.id.fabRepeat)

        playerText = findViewById<TextView>(R.id.tvPlayer)
        mediaView.playerText.observe(this,{playerText.text = it.toString()})

        dueText = findViewById<TextView>(R.id.tvDue)
        mediaView.dueText.observe(this,{dueText.text = it.toString()})

        var songTitle = findViewById<TextView>(R.id.songName)
        mediaView.songName.observe(this,{songTitle.text = it})

        play.setOnClickListener()
        {
            Logmsg("Play")
            play.visibility = INVISIBLE
            pause.visibility = VISIBLE
            if (mediaView.mediaplayer == null) {
                var st:String = resources.getResourceName(songs.song[mediaView.count])
                mediaView.songName.value = st.split("/")[1]
                Logmsg(mediaView.songName.value.toString())
                mediaView.mediaplayer = MediaPlayer.create(this, songs.song[mediaView.count])
                intializeSeekbar()
            }
            mediaView.mediaplayer?.start()
            mediaView.dueText.value = mediaView.mediaplayer!!.duration/1000
        }

        pause.setOnClickListener()
        {
            pauseMedia()
        }

        next.setOnClickListener()
        {
            Logmsg("Forwarded")
            nextMeida()
        }

        back.setOnClickListener()
        {
            Logmsg("previous")
            backMeida()
        }

        stop.setOnClickListener()
        {
            Logmsg("Stopped")
            handler.removeCallbacks(runnable)
            stop()
        }

    }

    private fun intializeSeekbar()
    {
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser)
                {
                    mediaView.mediaplayer?.seekTo(progress)
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        //seekbar components
        mediaView.playerText.observe(this,{seekbar.progress = it})
        mediaView.dueText.observe(this,{seekbar.max = it})

        runnable = Runnable {
            if (mediaView.mediaplayer == null)
            {
                mediaView.playerText.value = 0
            }
            else
            {
                mediaView.playerText.value = (mediaView.mediaplayer!!.currentPosition/1000).toString().toInt()
            }

        handler.postDelayed(runnable,1000)
    }
    handler.postDelayed(runnable,1000)

}

    private fun player(counter: Int)
    {
        play.visibility = INVISIBLE
        pause.visibility = VISIBLE
        Log.i("media", "in player function")
        mediaView.mediaplayer?.stop()
        mediaView.mediaplayer?.reset()
        mediaView.mediaplayer?.release()
        mediaView.mediaplayer = null
        handler.removeCallbacks(runnable)
        seekbar.progress = 0

        Logmsg("Memory Released")
        if (mediaView.mediaplayer == null) {
            var st:String = resources.getResourceName(songs.song[mediaView.count])
            mediaView.songName.value = st.split("/")[1]
            Logmsg(mediaView.songName.value.toString())
            mediaView.mediaplayer = MediaPlayer.create(this, songs.song[mediaView.count])
            intializeSeekbar()
        }
        mediaView.mediaplayer?.start()
        mediaView.dueText.value = mediaView.mediaplayer!!.duration/1000
    }

    private fun  stop()
    {
        Logmsg("Stop")
//        play.visibility = VISIBLE
//        pause.visibility = INVISIBLE
        mediaView.mediaplayer?.stop()
        mediaView.mediaplayer?.reset()
        mediaView.mediaplayer?.release()
        mediaView.mediaplayer = null
        handler.removeCallbacks(runnable)
//        seekbar.progress = 0
//        mediaView.songName.value = ""
//        mediaView.playerText.value = 0
//        mediaView.dueText.value = 0
recreate()
    }
    private fun pauseMedia()
    {
        play.visibility = VISIBLE
        pause.visibility = INVISIBLE
        Logmsg("Paused")
        mediaView.mediaplayer?.pause()
    }

    private fun nextMeida()
    {
        mediaView.countUp()
        if(mediaView.count >= songs.songCount)
        {
            mediaView.count = 0
            player(mediaView.count)
        }
        else
        {
            player(mediaView.count)
        }
    }

    private fun backMeida()
    {
        mediaView.countDown()
        if(mediaView.count == -1)
        {
            mediaView.count = songs.songCount - 1
            player(mediaView.count)
        }
        else
        {
            player(mediaView.count)
        }
    }

    fun Logmsg(msg:String)
    {
        Log.i("media",msg)
    }

    fun showToast(msg: String)
    {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        if(mediaView.mediaplayer != null)
        {
            play.visibility = INVISIBLE
            pause.visibility = VISIBLE

            intializeSeekbar()
            handler.removeCallbacks(runnable)
        }
    }
}