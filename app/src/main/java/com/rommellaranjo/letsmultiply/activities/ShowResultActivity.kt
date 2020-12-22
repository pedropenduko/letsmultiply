package com.rommellaranjo.letsmultiply.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.rommellaranjo.letsmultiply.R
import com.rommellaranjo.letsmultiply.database.DatabaseHandler
import com.rommellaranjo.letsmultiply.models.PlayerModel
import com.rommellaranjo.letsmultiply.models.Reputation
import com.rommellaranjo.letsmultiply.utils.DataSource
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_show_result.*
import kotlinx.android.synthetic.main.layout_rank_item.view.*

class ShowResultActivity : AppCompatActivity(), View.OnClickListener {

    private var score: Int = 0
    private var total_questions: Int = 0
    private var levelID: Long = 0
    private var playerID: Long = 0
    private var dbHandler: DatabaseHandler? = null
    private var playerDetails: PlayerModel? = null
    private lateinit var allReputations: ArrayList<Reputation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_result)

        // get score
        if (intent.hasExtra(GameQuestionsActivity.SCORE)) {
            score = intent.getIntExtra(GameQuestionsActivity.SCORE, 0)
        }
        // get total scores
        if (intent.hasExtra(GameQuestionsActivity.TOTAL_QUESTIONS)) {
            total_questions = intent.getIntExtra(GameQuestionsActivity.TOTAL_QUESTIONS, 0)
        }
        // get the player ID
        if (intent.hasExtra(MainActivity.PLAYER_ID)) {
            playerID = intent.getLongExtra(MainActivity.PLAYER_ID, 0)
        }
        // get the level id
        if (intent.hasExtra(GameQuestionsActivity.LEVEL_ID)) {
            levelID = intent.getLongExtra(GameQuestionsActivity.LEVEL_ID, 0)
        }

        dbHandler = DatabaseHandler(this)
        playerDetails = dbHandler!!.getPlayer(playerID)
        allReputations = dbHandler!!.getReputations()

        if (score == total_questions) {
            promote()
        } else {
            appreciate()
        }

        btn_next.setOnClickListener(this)
    }

    private fun promote() {
        tv_congratulations.text = "Congratulations " + playerDetails!!.name + "!"
        iv_result_image.setImageResource(R.drawable.ic_trophy)

        val nextLevel = dbHandler!!.getNextLevel(levelID)
        lateinit var newPlayer: PlayerModel

        tv_score.text = "Your score is $score/$total_questions!"
        if (nextLevel != null) { // when it is null, there is no more next level
            var toUpdate: Boolean = false

            // Promote the player to the next level
            when (playerDetails!!.reputationId) {
                allReputations[0].id -> {
                    if (nextLevel.id > playerDetails!!.levelNewbieId) { // nextLevel is higher than the current level of the player
                        newPlayer = playerDetails!!.copy(levelNewbieId = nextLevel.id)
                        toUpdate = true
                    }
                }
                allReputations[1].id -> {
                    if (nextLevel.id > playerDetails!!.levelSageId) { // nextLevel is higher than the current level of the player
                        newPlayer = playerDetails!!.copy(levelSageId = nextLevel.id)
                        toUpdate = true
                    }
                }
                allReputations[2].id -> {
                    if (nextLevel.id > playerDetails!!.levelHackerId) { // nextLevel is higher than the current level of the player
                        newPlayer = playerDetails!!.copy(levelHackerId = nextLevel.id)
                        toUpdate = true
                    }
                }

            }
            if (toUpdate) {
                tv_result_feedback.text = "You are now promoted to " + nextLevel.level + "!"
                dbHandler!!.updatePlayer(newPlayer)
            } else {
                tv_result_feedback.text = "Great! You did it again!"
            }

        } else {
            // TODO: Check if levelID is the last level
        }
    }

    private fun appreciate() {
        tv_congratulations.text = "Good job " + playerDetails!!.name + "!"
        iv_result_image.setImageResource(R.drawable.ic_balloons)

        // TODO: Check if higher level was already unlocked by the Player
        val nextLevel = dbHandler!!.getNextLevel(levelID)
        tv_score.text = "Your score is $score/$total_questions!"
        tv_result_feedback.text = DataSource.getMotivation()
        btn_next.text = "Try Again"
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_next -> {
                val intent = Intent(this, SelectLevelActivity::class.java)
                intent.putExtra(MainActivity.PLAYER_ID, playerID)
                startActivity(intent)
                finish()
            }
        }
    }
}