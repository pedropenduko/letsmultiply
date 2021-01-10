package com.rommellaranjo.letsmultiply.activities

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
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
//import kotlin.concurrent.schedule
import kotlin.collections.ArrayList

//import org.w3c.dom.Text
//import kotlin.properties.Delegates

class GameQuestionsActivity : AppCompatActivity(), View.OnClickListener {

    private var playerID : Long = 0
    private var levelID : Long = 0
    private var gameReputation: Int = 0
    private var withSoundFx: Boolean = true

    private var currentQuestion : Int = 1
    private var currentQuestionAnswer : Int? = null
    private lateinit var allQuestions: ArrayList<QuestionWithOptions>
    private var dbHandler: DatabaseHandler? = null
    private var playerDetails: PlayerModel? = null
    private lateinit var allReputations: ArrayList<Reputation>
    private var score : Int = 0
    private var waiting: Boolean = false

    private var questionDuration: Long = 1000
    private var timer: CountDownTimer? = null
    private var timerExpired: Boolean = false

    private var applauseSoundFx: MediaPlayer? = null
    private var correctSoundFx: MediaPlayer? = null
    private var errorSoundFx: MediaPlayer? = null

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
        } else {
            withSoundFx = playerDetails!!.soundFx == 1
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
                    // no randomize questions
                    // no timer
                    gameReputation = 0
                }
                allReputations[1].id -> { // Sage
                    gameReputation = 1
                    allQuestions.shuffle() // randomize the questions
                }
                allReputations[2].id -> { // Hacker
                    gameReputation = 2
                    allQuestions.shuffle() // randomize the questions
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
     * This function will play the sound effects
     */
    private fun playSoundFx(effect: Int) {
        if (withSoundFx) {
            when (effect) {
                1 -> {
                    // applause
                    try {
                        if (applauseSoundFx == null) {
                            applauseSoundFx = MediaPlayer.create(applicationContext, R.raw.applause)
                            applauseSoundFx!!.isLooping = false
                        }
                        if (applauseSoundFx!!.isPlaying) {
                            applauseSoundFx!!.stop()
                            applauseSoundFx!!.prepare()
                            applauseSoundFx!!.start()
                        } else {
                            applauseSoundFx!!.start()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                2 -> {
                    // correct
                    try {
                        if (correctSoundFx == null) {
                            correctSoundFx = MediaPlayer.create(applicationContext, R.raw.correct)
                            correctSoundFx!!.isLooping = false
                        }
                        if (correctSoundFx!!.isPlaying) {
                            Log.i("Correct:", "Stop - Start sound.")
                            correctSoundFx!!.stop()
                            correctSoundFx!!.prepare()
                            correctSoundFx!!.start()
                        } else {
                            Log.i("Correct:", "Playing a sound.")
                            correctSoundFx!!.start()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                3 -> {
                    // error
                    try {
                        if (errorSoundFx == null) {
                            errorSoundFx = MediaPlayer.create(applicationContext, R.raw.error)
                            errorSoundFx!!.isLooping = false
                        }
                        if (errorSoundFx!!.isPlaying) {
                            Log.i("Wrong:", "Stop-Start sound.")
                            errorSoundFx!!.stop()
                            errorSoundFx!!.prepare()
                            errorSoundFx!!.start()
                        } else {
                            Log.i("Wrong:", "Playing a sound.")
                            errorSoundFx!!.start()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    /**
     * This function will display the question and the options/choices in the screen
     */
    private fun displayQuestion() {
        timerExpired = false
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

        // start timer only when reputation is hacker
        if (gameReputation == 2) {
            startTimer()
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
        playSoundFx(3)
        val cvOfOptions : ArrayList<CardView> = arrayListOf<CardView>(cv_choice_a,
            cv_choice_b, cv_choice_c, cv_choice_d)
        cvOfOptions.forEach {
            it.setCardBackgroundColor(Color.parseColor("#CA0101"))
        }
    }


    override fun onClick(v: View?) {
        if (!timerExpired) {
            resetTimer()
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
                playSoundFx(2)
                cv.setCardBackgroundColor(Color.parseColor("#4CAF50"))
                score++
            }
            else -> {
                // Answer is wrong, set background to Red
                playSoundFx(3)
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

    private fun startTimer() {
        timer = object: CountDownTimer(questionDuration, 500) {
            override fun onTick(millisUntilFinished: Long) {
                /*pauseOffset = timerDuration - millisUntilFinished
                binding.tvTime.text = (millisUntilFinished / 1000).toString()*/
            }

            override fun onFinish() {
                timerExpired = true
                setRedCardViewBackground()
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    waiting = false
                    currentQuestion++
                    if (currentQuestion <= allQuestions.size){
                        displayQuestion()
                    } else {
                        val intent = Intent(this@GameQuestionsActivity, ShowResultActivity::class.java)

                        intent.putExtra(SCORE, score)
                        intent.putExtra(TOTAL_QUESTIONS, allQuestions.size)
                        intent.putExtra(LEVEL_ID, levelID)
                        intent.putExtra(MainActivity.PLAYER_ID, playerID)
                        startActivity(intent)

                        finish()

                    }
                },1000)
                //Toast.makeText(this@GameQuestionsActivity, "Timer is finished.", Toast.LENGTH_SHORT).show()
            }

        }.start()
    }

    private fun resetTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    public override fun onDestroy() {
        if (timer != null) {
            timer!!.cancel()
        }
        if (applauseSoundFx != null) {
            applauseSoundFx!!.stop()
        }
        if (correctSoundFx != null) {
            correctSoundFx!!.stop()
        }
        if (errorSoundFx != null) {
            errorSoundFx!!.stop()
        }
        super.onDestroy()
    }

}