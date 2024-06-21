package com.sk.basicmusic

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sk.basicmusic.songs
import kotlinx.coroutines.handleCoroutineException
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private lateinit var sp:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    private lateinit var mediaView:mainMediaVIew

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    private lateinit var play: FloatingActionButton
    private lateinit var pause: FloatingActionButton
    private var rIndicator:Boolean = false

    private lateinit var seekbar: SeekBar
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var playerText:TextView

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
        val stop  = findViewById<FloatingActionButton>(R.id.fabStop)
        val next  = findViewById<FloatingActionButton>(R.id.fabForward)
        val back  = findViewById<FloatingActionButton>(R.id.fabBack)
        val repeat = findViewById<FloatingActionButton>(R.id.fabRepeat)

        playerText = findViewById<TextView>(R.id.tvPlayer)
        mediaView.playerText.observe(this,{playerText.text = it.toString()})

        var songTitle = findViewById<TextView>(R.id.songName)
        mediaView.songName.observe(this,{songTitle.text = it})

        play.setOnClickListener()
        {
            Logmsg("Play")
            play.visibility = INVISIBLE
            pause.visibility = VISIBLE
            if (mediaPlayer == null) {
                var st:String = resources.getResourceName(songs.song[mediaView.count])
                mediaView.songName.value = st.split("/")[1]
                Logmsg(mediaView.songName.value.toString())
                mediaPlayer = MediaPlayer.create(this, songs.song[mediaView.count])
                intializeSeekbar()
            }
            mediaPlayer?.start()
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
            stop()
        }

    }

    private fun intializeSeekbar()
    {
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser)
                {
                    mediaPlayer?.seekTo(progress)
                    Logmsg("User Seeked To: ${seekbar.progress/1000}")
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        var due = findViewById<TextView>(R.id.tvDue)
        seekbar.max = mediaPlayer!!.duration
        due.text = (seekbar.max/1000).toString()

        runnable = Runnable {

            if(seekbar.progress == mediaPlayer!!.duration)
            {
                nextMeida()
            }
            else
            {
                mediaView.playerText.value = (mediaPlayer!!.currentPosition/1000).toString().toInt()
                seekbar.progress = mediaPlayer!!.currentPosition
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
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(runnable)
        seekbar.progress = 0

        Logmsg("Memory Released")
        if (mediaPlayer == null) {
            var st:String = resources.getResourceName(songs.song[mediaView.count])
            mediaView.songName.value = st.split("/")[1]
            Logmsg(mediaView.songName.value.toString())
            mediaPlayer = MediaPlayer.create(this, songs.song[mediaView.count])
            intializeSeekbar()
        }
        mediaPlayer?.start()
    }

    private fun  stop()
    {
        Logmsg("Stop")
        play.visibility = VISIBLE
        pause.visibility = INVISIBLE

        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(runnable)
        seekbar.progress = 0
        mediaView.songName.value = ""
    }
    private fun pauseMedia()
    {
        play.visibility = VISIBLE
        pause.visibility = INVISIBLE
        Logmsg("Paused")
        mediaPlayer?.pause()
    }

    private fun nextMeida()
    {
        mediaView.count++
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
        mediaView.count--
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
}