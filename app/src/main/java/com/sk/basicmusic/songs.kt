package com.sk.basicmusic

import android.util.Pair

object songs {

//    val song = arrayOf(
//        R.raw.ackrite,
//        R.raw.criminal_set,
//        R.raw.bad_intention,
//        R.raw.fein
//    )

    val song = listOf(
        Pair(R.raw.ackrite,R.mipmap.ackrite),
        Pair(R.raw.bad_intention,R.mipmap.bad_intention),
        Pair(R.raw.criminal_set,R.mipmap.criminal_set),
        Pair(R.raw.fein,R.mipmap.fein)
    )

    val songCount = song.size
}