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
    private var allReputations: ArrayList<Reputation>? = null

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

        // TODO: Check if higher level was already unlocked by the Player
        val nextLevel = dbHandler!!.getNextLevel(levelID)

        if (nextLevel != null) {
            tv_score.text = "Your score is $score/$total_questions!"
            tv_result_feedback.text = "You are now promoted to " + nextLevel.level + "!"

            // Promote the player to the next level
            when (playerDetails!!.reputationId) {
                allReputations!![0].id -> {
                    playerDetails!!.copy(levelNewbieId = nextLevel.id)
                }
                allReputations!![1].id -> {
                    playerDetails!!.copy(levelSageId = nextLevel.id)
                }
                allReputations!![2].id -> {
                    playerDetails!!.copy(levelHackerId = nextLevel.id)
                }

            }
            dbHandler!!.updatePlayer(playerDetails!!)
        } else {
            // TODO: Check if levelID is the last level
        }
    }

    private fun appreciate() {
        tv_congratulations.text = "Good job " + playerDetails!!.name + "!"
        iv_result_image.setImageResource(R.drawable.ic_balloons)

        // TODO: Check if higher level was already unlocked by the Player
        val nextLevel = dbHandler!!.getNextLevel(levelID)

        if (nextLevel != null) {
            tv_score.text = "Your score is $score/$total_questions!"

            val morePoints = total_questions - score
            if (morePoints > 1) {
                tv_result_feedback.text =
                    "You need $morePoints more points to be promoted to " + nextLevel.level + "."
            } else {
                tv_result_feedback.text =
                    "You need $morePoints more point to be promoted to " + nextLevel.level + "."
            }
        } else {
            //TODO: Check if levelID is the last level
        }

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