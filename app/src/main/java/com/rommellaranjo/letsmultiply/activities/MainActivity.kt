package com.rommellaranjo.letsmultiply.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.rommellaranjo.letsmultiply.R
import com.rommellaranjo.letsmultiply.database.DatabaseHandler
import com.rommellaranjo.letsmultiply.models.Level
import com.rommellaranjo.letsmultiply.models.Option
import com.rommellaranjo.letsmultiply.models.PlayerModel
import com.rommellaranjo.letsmultiply.models.Question
import com.rommellaranjo.letsmultiply.utils.DataSource
import com.rommellaranjo.letsmultiply.utils.Validator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var dbHandler: DatabaseHandler? = null
    private var allLevels: ArrayList<Level>? = null
    private val validator: Validator = Validator()

    companion object {
        const val PLAYER_ID = "player_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHandler = DatabaseHandler(this)
        getLevelsFromDb()

        btn_start.setOnClickListener(this)
        btn_playas.setOnClickListener(this)
    }

    /**
     * Get all levels from the database.
     * If it is empty, it is probably our first run, so populate the DB
     */
    private fun getLevelsFromDb() {
        allLevels = dbHandler!!.getLevels()

        if (allLevels!!.size == 0) {
            // Toast.makeText(this, "This is our first run... populating db!", Toast.LENGTH_LONG).show()
            allLevels = populateLevelInDb()
            populateQuestionInDb()
        }
    }

    /**
     * Populate the Level Table in DB
     * @return ArrayList<Level> An array list containing all the levels
     */
    private fun populateLevelInDb(): ArrayList<Level> {
        val dataSourceLevels = DataSource.getLevels()
        dataSourceLevels.forEach{
            dbHandler!!.addLevel(it)
        }
        // return the levels with the correct level ids from db
        return dbHandler!!.getLevels()
    }

    /**
     * Populate the Question Table in DB
     */
    private fun populateQuestionInDb() {
        val dataSourceQuestions = DataSource.questionsData()
        var questionId: Long
        var levelIndex: Int

        dataSourceQuestions.forEach{
            levelIndex = it.levelId.toInt() - 1
            questionId = dbHandler!!.addQuestion(
                Question(
                    0,
                    it.val1,
                    it.val2,
                    allLevels!![levelIndex].id
                )
            )
            val qOptions = DataSource.generateRandomChoices(it.val1, it.val2, 4)
            populateOptionInDb(questionId, it.val1, it.val2, qOptions)
        }
    }

    /**
     * Populate question options/choices in DB
     */
    private fun populateOptionInDb(qId: Long, val1: Int, val2: Int, qOptions: ArrayList<Int>) {
        val answer = val1 * val2
        for (option in qOptions) {
            dbHandler!!.addOption(
                Option(
                    0,
                    option,
                    qId,
                    if (answer == option) 1 else 0
                )
            )
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_start -> {
                when {
                    et_name.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please type a name.", Toast.LENGTH_SHORT).show()
                    }
                    !validator.isAlphaNumeric(et_name.text.toString()) -> {
                        Toast.makeText(this, "Please use only letters[a-z or A-Z] and numbers[0-9].", Toast.LENGTH_LONG).show()
                    } else -> {
                        val playerModel = PlayerModel(
                            0,
                            et_name.text.toString().trim(),
                            allLevels!![0].id
                        )

                        val dbHandler = DatabaseHandler(this)
                        val playerID = dbHandler.addPlayer(playerModel)
                        if (playerID > 0) {
                            // Toast.makeText(this, "Player name successfully saved.", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, SelectLevelActivity::class.java)
                            intent.putExtra(PLAYER_ID, playerID)
                            startActivity(intent)
                            //finish()
                        } else {
                            Toast.makeText(this, "Please make sure your name is not taken yet. Check \"Continue Playing As..\"", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            R.id.btn_playas -> {
                //Toast.makeText(this, "Continue playing as...", Toast.LENGTH_LONG).show()
                val intent = Intent(this, PlayersList::class.java)
                startActivity(intent)
                //finish()
            }
        }
    }
}