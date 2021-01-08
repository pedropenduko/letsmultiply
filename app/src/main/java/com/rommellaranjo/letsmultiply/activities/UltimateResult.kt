package com.rommellaranjo.letsmultiply.activities

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rommellaranjo.letsmultiply.R
import com.rommellaranjo.letsmultiply.database.DatabaseHandler
import com.rommellaranjo.letsmultiply.models.PlayerModel
import kotlinx.android.synthetic.main.activity_ultimate_result.*

class UltimateResult : AppCompatActivity() {

    private lateinit var playerName: String
    private var drumSoundFx: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ultimate_result)

        try {
            if (drumSoundFx == null) {
                drumSoundFx = MediaPlayer.create(applicationContext, R.raw.finale_drum_roll)
                drumSoundFx!!.isLooping = false
            }
            drumSoundFx!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // get the player name
        if (intent.hasExtra(ShowResultActivity.PLAYER_NAME)) {
            playerName = intent.getStringExtra(ShowResultActivity.PLAYER_NAME).toString()
        }
        tv_final_message1.text = "${playerName}, you have finally reached the pinnacle of this game!\nCongratulations!!!"

        btn_final_close.setOnClickListener {
            finishAffinity()
        }
    }

    public override fun onDestroy() {
        if (drumSoundFx != null) {
            drumSoundFx!!.stop()
        }

        super.onDestroy()
    }
}