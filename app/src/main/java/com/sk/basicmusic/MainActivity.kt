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
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sk.basicmusic.songs.song
import org.w3c.dom.Text
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {

    private lateinit var sp:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    private lateinit var mediaView:mainMediaVIew

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    private lateinit var play: FloatingActionButton
    private lateinit var pause: FloatingActionButton
    private lateinit var stop: FloatingActionButton

    private lateinit var seekbar: SeekBar

    private lateinit var playerText:TextView
    private lateinit var dueText:TextView
    private lateinit var albumCover: ImageView

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
        val repeatText = findViewById<TextView>(R.id.repeatText)
        albumCover = findViewById(R.id.ivAlbum)

        playerText = findViewById<TextView>(R.id.tvPlayer)
        mediaView.min.observe(this,{playerText.text = it.toString()})

        dueText = findViewById<TextView>(R.id.tvDue)
        mediaView.max.observe(this,{dueText.text = it.toString()})

        var songTitle = findViewById<TextView>(R.id.songName)
        mediaView.songName.observe(this,{songTitle.text = it})

        mediaView.rText.observe(this,{repeatText.text = it})

        play.setOnClickListener()
        {
            Logmsg("Play")
            play.visibility = INVISIBLE
            pause.visibility = VISIBLE
            if (mediaView.mediaplayer == null) {
                var st:String = resources.getResourceName(songs.song[mediaView.count].first)
                mediaView.songName.value = st.split("/")[1]
                albumCover.setImageResource(song[mediaView.count].second)
                Logmsg(mediaView.songName.value.toString())
                mediaView.mediaplayer = MediaPlayer.create(this, songs.song[mediaView.count].first)
                intializeSeekbar()
            }
            mediaView.mediaplayer?.start()
            mediaView.dueText.value = mediaView.mediaplayer!!.duration/1000
            var total_seconds:Int = (mediaView.dueText.value).toString().toInt()
            var minutes = (total_seconds / 60)
            var seconds = (total_seconds % 60) - 1
            mediaView.max.value = "$minutes : $seconds"

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

        repeat.setOnClickListener()
        {
            if (mediaView.rIndicator == false)
            {
                mediaView.rIndicator = true
                mediaView.rText.value = "On"
                Logmsg("Repeat On")
            }
            else
            {
                mediaView.rIndicator = false
                mediaView.rText.value = "Off"
                Logmsg("Repeat Off")
            }
        }

        val about = findViewById<ImageView>(R.id.aboutBtn)
        about.setOnClickListener()
        {
            startActivity(Intent(this@MainActivity,about_page::class.java))
        }

    }

    private fun intializeSeekbar()
    {
        var seek = 0
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {seek = progress*1000
                    mediaView.mediaplayer!!.seekTo(seek)}
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
                if(seekbar.progress == seekbar.max)
                {
                    mediaView.playerText.value = 0
                    mediaView.dueText.value = 0
                    seekbar.progress = 0
                    nextMeida()
                }
                else
                {

                    mediaView.playerText.value = (mediaView.mediaplayer!!.currentPosition/1000).toString().toInt()
                    var total_seconds:Int = (mediaView.playerText.value).toString().toInt()
                    var minutes = (total_seconds / 60)
                    var seconds = (total_seconds % 60)
                    mediaView.min.value = "$minutes : $seconds"
                }
            }

        handler.postDelayed(runnable,1000)
    }
    handler.postDelayed(runnable,1000)

}
//
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
            var st:String = resources.getResourceName(songs.song[mediaView.count].first)
            mediaView.songName.value = st.split("/")[1]
            albumCover.setImageResource(song[mediaView.count].second)

            Logmsg(mediaView.songName.value.toString())
            mediaView.mediaplayer = MediaPlayer.create(this, songs.song[mediaView.count].first)
            intializeSeekbar()
        }
        mediaView.mediaplayer?.start()
        mediaView.dueText.value = mediaView.mediaplayer!!.duration/1000
        var total_seconds:Int = (mediaView.dueText.value).toString().toInt()
        var minutes = (total_seconds / 60)
        var seconds = (total_seconds % 60)
        mediaView.max.value = "$minutes : $seconds"
    }

    private fun  stop()
    {
        Logmsg("Stop")
        play.visibility = VISIBLE
        pause.visibility = INVISIBLE
        mediaView.mediaplayer?.stop()
        mediaView.mediaplayer?.reset()
        mediaView.mediaplayer?.release()
        mediaView.mediaplayer = null
        handler.removeCallbacks(runnable)
        seekbar.progress = 0
        mediaView.playerText.value = 0
        mediaView.dueText.value = 0
        mediaView.min.value = "0 : 0"
        mediaView.max.value = "0 : 0"
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
        if (mediaView.rIndicator != true) mediaView.countUp()

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

    override fun onPause() {
        super.onPause()
         editor.apply()
         {
             putInt("count",mediaView.count)
             commit()
         }
    }
    override fun onResume() {
        super.onResume()
        mediaView.count = sp.getInt("count",0)
        mediaView.songName.value = resources.getResourceName(song[mediaView.count].first).split("/")[1]
        albumCover.setImageResource(song[mediaView.count].second)
        mediaView.min.value = "0 : 0"
        mediaView.max.value = "0 : 0"

        if(mediaView.mediaplayer != null)
        {
            play.visibility = INVISIBLE
            pause.visibility = VISIBLE
            intializeSeekbar()
            handler.removeCallbacks(runnable)
            albumCover.setImageResource(song[mediaView.count].second)
        }
    }
}