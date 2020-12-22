package com.rommellaranjo.letsmultiply.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
//import androidx.core.content.ContextCompat
import com.rommellaranjo.letsmultiply.R
import com.rommellaranjo.letsmultiply.database.DatabaseHandler
import com.rommellaranjo.letsmultiply.models.PlayerModel
//import com.rommellaranjo.letsmultiply.models.Question
import com.rommellaranjo.letsmultiply.models.QuestionWithOptions
import com.rommellaranjo.letsmultiply.models.Reputation
import kotlinx.android.synthetic.main.activity_game_questions.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.collections.ArrayList

//import org.w3c.dom.Text
//import kotlin.properties.Delegates

class GameQuestionsActivity : AppCompatActivity(), View.OnClickListener {

    private var playerID : Long = 0
    private var levelID : Long = 0

    private var currentQuestion : Int = 1
    private var currentQuestionAnswer : Int? = null
    private lateinit var allQuestions: ArrayList<QuestionWithOptions>
    private var dbHandler: DatabaseHandler? = null
    private var playerDetails: PlayerModel? = null
    private lateinit var allReputations: ArrayList<Reputation>
    private var score : Int = 0
    private var waiting: Boolean = false

    companion object {
        const val SCORE = "score"
        const val TOTAL_QUESTIONS = "total_questions"
        const val LEVEL_ID = "level_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_questions)

        // get the player ID
        if (intent.hasExtra(MainActivity.PLAYER_ID)) {
            playerID = intent.getLongExtra(MainActivity.PLAYER_ID, 0)
        } else {
            Log.e("Let's Multiply Error:", "No Player ID was passed at the GameQuestionsActivity.")
            finish()
        }
        // get the Rank Clicked, this is the level id
        if (intent.hasExtra(SelectLevelActivity.RANK_CLICKED)) {
            levelID = intent.getLongExtra(SelectLevelActivity.RANK_CLICKED, 0)
        } else {
            Log.e("Let's Multiply Error:", "No Rank/Level ID was passed at the GameQuestionsActivity.")
            finish()
        }

        dbHandler = DatabaseHandler(this)

        playerDetails = dbHandler!!.getPlayer(playerID)
        if (playerDetails == null) {
            Log.e("Let's Multiply Error: ", "Player doesn't exist.")
            finish()
        }

        allReputations = dbHandler!!.getReputations()
        if (allReputations.size == 0) {
            Log.e("Let's Multiply Error:", "There are no reputations in db?")
            finish()
        }
        allQuestions = dbHandler!!.getQuestionsWithOptions(levelID)
        if (allQuestions.size > 0) {
            when (playerDetails!!.reputationId) {
                allReputations[0].id -> { // Newbie
                    // nothing to do here
                }
                allReputations[1].id -> { // Sage
                    allQuestions.shuffle() // randomize the questions
                }
                allReputations[2].id -> { // Hacker
                    // set timer for each question
                }
            }
        } else {
            Log.e("Let's Multiply Error:", "There are no questions yet?")
            finish()
        }


        // now that we have all the questions, let us display them one by one
        displayQuestion()

        // setOnclickListener for every CardView Options
        cv_choice_a.setOnClickListener(this)
        cv_choice_b.setOnClickListener(this)
        cv_choice_c.setOnClickListener(this)
        cv_choice_d.setOnClickListener(this)
    }

    /**
     * This function will display the question and the options/choices in the screen
     */
    private fun displayQuestion() {
        val question = allQuestions.get(currentQuestion - 1)

        // clear the background color
        setWhiteCardViewBackground()

        // set the progress bar
        pb_progress_bar.progress = currentQuestion
        tv_progress.text = "$currentQuestion" + "/" + allQuestions.size

        // display the question
        tv_question.text = question.val1.toString() + " x " + question.val2.toString()

        val options = question.options
        options.shuffle() // randomize the options

        val tvOfOptions : ArrayList<TextView> = arrayListOf<TextView>(tv_choice_a,
            tv_choice_b, tv_choice_c, tv_choice_d)
        // iterate thru options to get the answer
        for (i in 0 until options.size) {
            tvOfOptions[i].text = options[i].option.toString()
            if (options[i].correct == 1) currentQuestionAnswer = i
        }
    }

    /**
     * This will set the Card View background colors to White
     */
    private fun setWhiteCardViewBackground() {
        val cvOfOptions : ArrayList<CardView> = arrayListOf<CardView>(cv_choice_a,
            cv_choice_b, cv_choice_c, cv_choice_d)
        cvOfOptions.forEach {
            it.setCardBackgroundColor(Color.parseColor("#E4E2E2"))
        }
    }

    /**
     * This will set the Card View background colors to Red
     * This will happen or be called when the question timer has expired
     */
    private fun setRedCardViewBackground() {
        val cvOfOptions : ArrayList<CardView> = arrayListOf<CardView>(cv_choice_a,
            cv_choice_b, cv_choice_c, cv_choice_d)
        cvOfOptions.forEach {
            it.setCardBackgroundColor(Color.parseColor("#CA0101"))
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cv_choice_a -> {
                if (!waiting) checkAnswer(cv_choice_a, 0)
            }
            R.id.cv_choice_b -> {
                if (!waiting) checkAnswer(cv_choice_b, 1)
            }
            R.id.cv_choice_c -> {
                if (!waiting) checkAnswer(cv_choice_c, 2)
            }
            R.id.cv_choice_d -> {
                if (!waiting) checkAnswer(cv_choice_d, 3)
            }
        }
    }

    /**
     * This is the function which check the answer if it is correct or wrong.
     * Green if correct, Red if it is wrong.
     * A 1second delay as interval for the next question to be shown
     * If no more question to be displayed, it will load the next activity which is the ShowResultActivity
     */
    private fun checkAnswer(cv: CardView, i: Int) {
        waiting = true
        when (i) {
//            0 -> {
//                setRedCardViewBackground()
//            }
            currentQuestionAnswer -> {
                // Answer is correct, set background to Green
                cv.setCardBackgroundColor(Color.parseColor("#4CAF50"))
                score++
            }
            else -> {
                // Answer is wrong, set background to Red
                cv.setCardBackgroundColor(Color.parseColor("#CA0101"))
            }
        }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            waiting = false
            currentQuestion++
            if (currentQuestion <= allQuestions.size){
                displayQuestion()
            } else {
                val intent = Intent(this, ShowResultActivity::class.java)

                intent.putExtra(SCORE, score)
                intent.putExtra(TOTAL_QUESTIONS, allQuestions.size)
                intent.putExtra(LEVEL_ID, levelID)
                intent.putExtra(MainActivity.PLAYER_ID, playerID)
                startActivity(intent)
                finish()

            }
        },1000)
    }
}