package com.rommellaranjo.letsmultiply.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.rommellaranjo.letsmultiply.R
import com.rommellaranjo.letsmultiply.database.DatabaseHandler
import com.rommellaranjo.letsmultiply.models.*
import com.rommellaranjo.letsmultiply.utils.DataSource
import com.rommellaranjo.letsmultiply.utils.Validator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var dbHandler: DatabaseHandler
    private lateinit var allLevels: ArrayList<Level>
    private lateinit var allReputations: ArrayList<Reputation>
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
        // TODO: Create a new theme with Army colors
    }

    /**
     * Get all levels from the database.
     * If it is empty, it is probably our first run, so populate the DB
     */
    private fun getLevelsFromDb() {
        allLevels = dbHandler.getLevels()

        if (allLevels.size == 0) {
            // the Level table in DB is still empty, let us populate the DB
            allLevels = populateLevelInDb()
            // populate also the Reputation table
            allReputations = populateReputaionInDb()
            // populate also the Question table
            populateQuestionInDb()
        } else {
            // DB has been populated already, get all reputations into an array
            allReputations = dbHandler.getReputations()
        }

    }

    /**
     * This function will populate the Reputation Table in DB
     * @return ArrayList<Reputation> An array containing all reputations that was saved in DB
     */
    private fun populateReputaionInDb() : ArrayList<Reputation> {
        val dataSourceReputations = DataSource.getReputations()

        dataSourceReputations.forEach {
            dbHandler.addReputation(Reputation(0, it))
        }
        // return all reputations for later use
        return dbHandler.getReputations()
    }

    /**
     * Populate the Level Table in DB
     * @return ArrayList<Level> An array list containing all the levels
     */
    private fun populateLevelInDb(): ArrayList<Level> {
        val dataSourceLevels = DataSource.getLevels()
        dataSourceLevels.forEach{
            dbHandler.addLevel(it)
        }
        // return the levels with the correct level ids from db
        return dbHandler.getLevels()
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
            questionId = dbHandler.addQuestion(
                Question(
                    0,
                    it.val1,
                    it.val2,
                    allLevels[levelIndex].id
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
            dbHandler.addOption(
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
            // Start Button was clicked
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
                            allLevels[0].id, // default
                            allLevels[0].id, // default
                            allLevels[0].id, // default
                            allReputations[0].id, // default
                            1 // default
                        )

                        // Save the Player Name together with some default values into the DB
                        val playerID = dbHandler.addPlayer(playerModel)

                        if (playerID > 0) {
                            // If successfully saved, open up the SelectLevelActivity
                            val intent = Intent(this, SelectLevelActivity::class.java)
                            intent.putExtra(PLAYER_ID, playerID)
                            startActivity(intent)
                            //finish()
                        } else {
                            // Possibly, the Player Name was already taken
                            Toast.makeText(this, "Please make sure your name is not taken yet. Check \"Continue Playing As..\"", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            // Continue Playing As Button was clicked
            R.id.btn_playas -> {
                // A returning player, open up the PlayersList Activity
                val intent = Intent(this, PlayersList::class.java)
                startActivity(intent)
                //finish()
            }
        }
    }
}