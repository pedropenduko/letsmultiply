package com.rommellaranjo.letsmultiply.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import com.rommellaranjo.letsmultiply.R
import com.rommellaranjo.letsmultiply.database.DatabaseHandler
import com.rommellaranjo.letsmultiply.models.PlayerModel
import com.rommellaranjo.letsmultiply.models.Reputation
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.layout_rank_list.*

class SettingsActivity : AppCompatActivity() {

    private var playerID: Long = 0
    private var playerDetails: PlayerModel? = null
    private var dbHandler: DatabaseHandler? = null
    private lateinit var allReputations: ArrayList<Reputation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // get the player ID
        if (intent.hasExtra(MainActivity.PLAYER_ID)) {
            playerID = intent.getLongExtra(MainActivity.PLAYER_ID, 0)
        }
        if (playerID > 0) {
            dbHandler = DatabaseHandler(this)
            playerDetails = dbHandler!!.getPlayer(playerID)

            // set sound effects value
            cb_soundfx.isChecked = playerDetails!!.soundFx == 1

            // set reputation value
            allReputations = dbHandler!!.getReputations()
            if (allReputations.size == 0) {
                Log.e("Let's Multiply Error:", "There are no reputations in db?")
                finish()
            }
            when (playerDetails!!.reputationId) {
                allReputations[0].id -> { // Newbie
                    rb_newbie.isChecked = true
                }
                allReputations[1].id -> { // Sage
                    rb_sage.isChecked = true
                }
                allReputations[2].id -> { // Hacker
                    rb_hacker.isChecked = true
                }
            }

        } else {
            Log.e("Error: ", "No Player ID passed.")
        }

        btn_done_settings.setOnClickListener {
            val checkedReputationId = rg_reputation.checkedRadioButtonId
            val reputation = findViewById<RadioButton>(checkedReputationId)
            var selectedPlayerReputation: Long = playerDetails!!.reputationId
            lateinit var newPlayerGameSettings: PlayerModel

            when (reputation.text.toString()) {
                "Newbie" -> { selectedPlayerReputation = allReputations[0].id }
                "Sage" -> { selectedPlayerReputation = allReputations[1].id }
                "Hacker" -> { selectedPlayerReputation = allReputations[2].id }
            }
            newPlayerGameSettings = playerDetails!!.copy(
                soundFx = (if(cb_soundfx.isChecked) 1 else 0),
                reputationId =  selectedPlayerReputation
            )
            dbHandler!!.updatePlayer(newPlayerGameSettings)

            val intent = Intent(this, SelectLevelActivity::class.java)
            intent.putExtra(MainActivity.PLAYER_ID, playerID)
            startActivity(intent)
            finish()
        }
    }
}