package com.rommellaranjo.letsmultiply.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
//import androidx.core.content.ContextCompat
import com.rommellaranjo.letsmultiply.R
import com.rommellaranjo.letsmultiply.database.DatabaseHandler
//import com.rommellaranjo.letsmultiply.models.Question
import com.rommellaranjo.letsmultiply.models.QuestionWithOptions
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
    private var allQuestions: ArrayList<QuestionWithOptions>? = null
    private var dbHandler: DatabaseHandler? = null
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
        }
        // get the Rank Clicked, this is the level id
        if (intent.hasExtra(SelectLevelActivity.RANK_CLICKED)) {
            levelID = intent.getLongExtra(SelectLevelActivity.RANK_CLICKED, 0)
        }
        // TODO: Make a trap if no playerID and rankClicked was passed. Do something.
        //Toast.makeText(this, "Rank Clicked: " + levelID.toString(), Toast.LENGTH_SHORT).show()
        dbHandler = DatabaseHandler(this)
        allQuestions = dbHandler!!.getQuestionsWithOptions(levelID)

        // TODO: Make a trap if allQuestions is empty
        allQuestions!!.shuffle() // randomize the questions

        // now that we have all the questions, let us display them one by one
        displayQuestion()

        // setOnclickListener for every CardView Options
        cv_choice_a.setOnClickListener(this)
        cv_choice_b.setOnClickListener(this)
        cv_choice_c.setOnClickListener(this)
        cv_choice_d.setOnClickListener(this)
    }

    private fun displayQuestion() {
        val question = allQuestions!!.get(currentQuestion - 1)

        // clear the background color
        setWhiteCardViewBackground()

        // set the progress bar
        pb_progress_bar.progress = currentQuestion
        tv_progress.text = "$currentQuestion" + "/" + allQuestions?.size

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

    private fun checkAnswer(cv: CardView, i: Int) {
        waiting = true
        if (i == currentQuestionAnswer) {
            cv.setCardBackgroundColor(Color.parseColor("#4CAF50"))
            score++
        } else {
            cv.setCardBackgroundColor(Color.parseColor("#CA0101"))
        }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            waiting = false
            currentQuestion++
            if (currentQuestion <= allQuestions!!.size){
                displayQuestion()
            } else {
                //Toast.makeText(this, "Current level is $levelID...", Toast.LENGTH_LONG).show()
                val intent = Intent(this, ShowResultActivity::class.java)

                intent.putExtra(SCORE, score)
                intent.putExtra(TOTAL_QUESTIONS, allQuestions!!.size)
                intent.putExtra(LEVEL_ID, levelID)
                intent.putExtra(MainActivity.PLAYER_ID, playerID)
                startActivity(intent)
                finish()

            }
        },1000)
//        Timer().schedule(2000) {
//            waiting = false
//            currentQuestion++
//            if (currentQuestion < allQuestions!!.size){
//                displayQuestion()
//            }
//        }
    }
}