package com.rommellaranjo.letsmultiply.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.rommellaranjo.letsmultiply.models.*
//import android.database.sqlite.SQLiteQueryBuilder

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "LetsMultiplyDb"
        private const val TABLE_PLAYER = "Player"
        private const val TABLE_QUESTION = "Question"
        private const val TABLE_OPTION = "Option"
        private const val TABLE_RECORD = "Record"
        private const val TABLE_LEVEL = "Level"
        private const val TABLE_REPUTATION = "Reputation"

        // All the column names
        private const val PLAYER_ID = "Id"
        private const val PLAYER_NAME = "Name"
        private const val PLAYER_LEVELNEWBIEID = "LevelNewbieId"
        private const val PLAYER_LEVELSAGEID = "LevelSageId"
        private const val PLAYER_LEVELHACKERID = "LevelHackerId"
        private const val PLAYER_REPUTATIONID = "ReputationId"
        private const val PLAYER_SOUNDFX = "SoundFX"

        private const val QUESTION_ID = "Id"
        private const val QUESTION_VAL1 = "Val1"
        private const val QUESTION_VAL2 = "Val2"
        private const val QUESTION_LEVELID = "LevelId"

        private const val OPTION_ID = "Id"
        private const val OPTION_OPTION = "Option"
        private const val OPTION_QUESTIONID = "QuestionId"
        private const val OPTION_CORRECT = "Correct"

        private const val RECORD_ID = "Id"
        private const val RECORD_PLAYERID = "PlayerId"
        private const val RECORD_LEVELID = "LevelId"
        private const val RECORD_SCORE = "Score"
        private const val RECORD_STATUS = "Status"

        private const val LEVEL_ID = "Id"
        private const val LEVEL_LEVEL = "Level"
        private const val LEVEL_IMAGE = "Image"

        private const val REPUTATION_ID = "Id"
        private const val REPUTATION_NAME = "Name"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // creating tables with fields

        val CREATE_LEVEL_TABLE = ("CREATE TABLE " + TABLE_LEVEL + "("
                + LEVEL_ID + " INTEGER PRIMARY KEY, "
                + LEVEL_LEVEL + " TEXT NOT NULL UNIQUE, "
                + LEVEL_IMAGE + " TEXT)")
        val CREATE_REPUTATION_TABLE = ("CREATE TABLE " + TABLE_REPUTATION + "("
                + REPUTATION_ID + " INTEGER PRIMARY KEY, "
                + REPUTATION_NAME + " TEXT NOT NULL UNIQUE)")
        val CREATE_PLAYER_TABLE = ("CREATE TABLE " + TABLE_PLAYER + "("
                + PLAYER_ID + " INTEGER PRIMARY KEY, "
                + PLAYER_NAME + " TEXT NOT NULL UNIQUE, "
                + PLAYER_LEVELNEWBIEID + " INTEGER, "
                + PLAYER_LEVELSAGEID + " INTEGER, "
                + PLAYER_LEVELHACKERID + " INTEGER, "
                + PLAYER_REPUTATIONID + " INTEGER, "
                + PLAYER_SOUNDFX + " INTEGER, "
                + "FOREIGN KEY(" + PLAYER_LEVELNEWBIEID + ") "
                + "REFERENCES " + TABLE_LEVEL + "(" + LEVEL_ID + "), "
                + "FOREIGN KEY(" + PLAYER_LEVELSAGEID + ") "
                + "REFERENCES " + TABLE_LEVEL + "(" + LEVEL_ID + "), "
                + "FOREIGN KEY(" + PLAYER_LEVELHACKERID + ") "
                + "REFERENCES " + TABLE_LEVEL + "(" + LEVEL_ID + "), "
                + "FOREIGN KEY(" + PLAYER_REPUTATIONID + ") "
                + "REFERENCES " + TABLE_REPUTATION + "(" + REPUTATION_ID + ") "
                +")")
        val CREATE_QUESTION_TABLE = ("CREATE TABLE " + TABLE_QUESTION + "("
                + QUESTION_ID + " INTEGER PRIMARY KEY, "
                + QUESTION_VAL1 + " INTEGER, "
                + QUESTION_VAL2 + " INTEGER, "
                + QUESTION_LEVELID + " INTEGER, "
                + "FOREIGN KEY(" + QUESTION_LEVELID + ") "
                + "REFERENCES " + TABLE_LEVEL + "(" + LEVEL_ID + "))")
        val CREATE_OPTION_TABLE = ("CREATE TABLE " + TABLE_OPTION + "("
                + OPTION_ID + " INTEGER PRIMARY KEY, "
                + OPTION_OPTION + " INTEGER, "
                + OPTION_QUESTIONID + " INTEGER, "
                + OPTION_CORRECT + " INTEGER, "
                + "FOREIGN KEY(" + OPTION_QUESTIONID + ") "
                + "REFERENCES " + TABLE_QUESTION + "(" + QUESTION_ID + "))")
        val CREATE_RECORD_TABLE = ("CREATE TABLE " + TABLE_RECORD + "("
                + RECORD_ID + " INTEGER PRIMARY KEY, "
                + RECORD_PLAYERID + " INTEGER, "
                + RECORD_LEVELID + " INTEGER, "
                + RECORD_SCORE + " INTEGER, "
                + RECORD_STATUS + " INTEGER, "
                + "FOREIGN KEY(" + RECORD_PLAYERID + ") "
                + "REFERENCES " + TABLE_PLAYER + "(" + PLAYER_ID + "), "
                + "FOREIGN KEY(" + RECORD_LEVELID + ") "
                + "REFERENCES " + TABLE_LEVEL + "(" + LEVEL_ID + "))")
        db?.execSQL(CREATE_LEVEL_TABLE)
        db?.execSQL(CREATE_REPUTATION_TABLE)
        db?.execSQL(CREATE_PLAYER_TABLE)
        db?.execSQL(CREATE_QUESTION_TABLE)
        db?.execSQL(CREATE_OPTION_TABLE)
        db?.execSQL(CREATE_RECORD_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_RECORD")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OPTION")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTION")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REPUTATION")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LEVEL")
        onCreate(db)
    }

    /**
     * This function will insert player into Player table
     * @param player    PlayerModel object containing the player info
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    fun addPlayer(player: PlayerModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(PLAYER_NAME, player.name)
        contentValues.put(PLAYER_LEVELNEWBIEID, 1) // this is the default level for newly added player/s
        contentValues.put(PLAYER_LEVELSAGEID, 1) // default
        contentValues.put(PLAYER_LEVELHACKERID, 1) // default
        contentValues.put(PLAYER_REPUTATIONID, 1) // default
        contentValues.put(PLAYER_SOUNDFX, 1) // default "On"

        val result = db.insert(TABLE_PLAYER, null, contentValues)
        db.close()

        return result
    }

    /**
     * This function will get all the players
     * @return an arraylist of Players
     */
    fun getPlayerList(): ArrayList<PlayerModel> {
        val playerList: ArrayList<PlayerModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_PLAYER"
        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val player = PlayerModel(
                        cursor.getLong(cursor.getColumnIndex(PLAYER_ID)),
                        cursor.getString(cursor.getColumnIndex(PLAYER_NAME)),
                        cursor.getLong(cursor.getColumnIndex(PLAYER_LEVELNEWBIEID)),
                        cursor.getLong(cursor.getColumnIndex(PLAYER_LEVELSAGEID)),
                        cursor.getLong(cursor.getColumnIndex(PLAYER_LEVELHACKERID)),
                        cursor.getLong(cursor.getColumnIndex(PLAYER_REPUTATIONID)),
                        cursor.getInt(cursor.getColumnIndex(PLAYER_SOUNDFX))
                    )
                    playerList.add(player)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return playerList
    }

    /**
     * Get the player details of the given player id
     * @param playerID The player id of the player details to be retrieved
     * @return PlayerModel Object containing the player details
     */
    fun getPlayer(playerID: Long) : PlayerModel? {
        var player: PlayerModel? = null
        val columns = arrayOf(PLAYER_ID, PLAYER_NAME, PLAYER_LEVELNEWBIEID, PLAYER_LEVELSAGEID, PLAYER_LEVELHACKERID, PLAYER_REPUTATIONID, PLAYER_SOUNDFX)
        val selection = "$PLAYER_ID=?"
        val selectionArgs = arrayOf(playerID.toString())
        val limit = "1"
        val db = this.readableDatabase

        try {
            val cursor = db.query(TABLE_PLAYER, columns, selection, selectionArgs, null, null, null, limit)
            if (cursor.moveToFirst()) {
                player = PlayerModel(
                    cursor.getLong(cursor.getColumnIndex(PLAYER_ID)),
                    cursor.getString(cursor.getColumnIndex(PLAYER_NAME)),
                    cursor.getLong(cursor.getColumnIndex(PLAYER_LEVELNEWBIEID)),
                    cursor.getLong(cursor.getColumnIndex(PLAYER_LEVELSAGEID)),
                    cursor.getLong(cursor.getColumnIndex(PLAYER_LEVELHACKERID)),
                    cursor.getLong(cursor.getColumnIndex(PLAYER_REPUTATIONID)),
                    cursor.getInt(cursor.getColumnIndex(PLAYER_SOUNDFX))
                )
            }
            cursor.close()
        } catch (e: SQLiteException) {
            return null
        }
        return player
    }

    /**
     * Update player details
     */
    fun updatePlayer(player: PlayerModel) : Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(PLAYER_NAME, player.name)
        contentValues.put(PLAYER_LEVELNEWBIEID, player.levelNewbieId)
        contentValues.put(PLAYER_LEVELSAGEID, player.levelSageId)
        contentValues.put(PLAYER_LEVELHACKERID, player.levelHackerId)
        contentValues.put(PLAYER_REPUTATIONID, player.reputationId)
        contentValues.put(PLAYER_SOUNDFX, player.soundFx)

        val result = db.update(TABLE_PLAYER, contentValues,
        "$PLAYER_ID=?", arrayOf(player.id.toString()))
        db.close()
        return result
    }

    /**
     * This function will insert new reputation into Reputation table
     * @param reputation    Reputation object containing the reputation info
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    fun addReputation(reputation: Reputation): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(REPUTATION_NAME, reputation.name)

        val result = db.insert(TABLE_REPUTATION, null, contentValues)
        db.close()

        return result
    }

    /**
     * This function will get all the reputations
     * @return an arraylist of Reputation
     */
    fun getReputations(): ArrayList<Reputation> {
        val reputations: ArrayList<Reputation> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_REPUTATION ORDER BY $REPUTATION_ID ASC"
        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val reputation = Reputation(
                        cursor.getLong(cursor.getColumnIndex(REPUTATION_ID)),
                        cursor.getString(cursor.getColumnIndex(REPUTATION_NAME))
                    )
                    reputations.add(reputation)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return reputations
    }

    /**
     * Get the next reputation higher than the given reputation id
     */
    fun getNextReputation(reputationID: Long): Reputation? {
        var reputation : Reputation? = null
        val columns = arrayOf(REPUTATION_ID, REPUTATION_NAME)
        val selection = "$REPUTATION_ID>?"
        val selectionArgs = arrayOf(reputationID.toString())
        val limit = "1"
        val orderby = "$REPUTATION_ID ASC"
        val db = this.readableDatabase

        try {
            val cursor = db.query(
                TABLE_REPUTATION, columns, selection, selectionArgs,
                null, null, orderby, limit)
            if (cursor.moveToFirst()) {
                reputation = Reputation(
                    cursor.getLong(cursor.getColumnIndex(REPUTATION_ID)),
                    cursor.getString(cursor.getColumnIndex(REPUTATION_NAME))
                )
            }
            cursor.close()
        } catch (e: SQLiteException) {
            return null
        }
        return reputation
    }

    /**
     * This function will insert new level into Level table
     * @param level    Level object containing the level info
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    fun addLevel(level: Level): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(LEVEL_LEVEL, level.level)
        contentValues.put(LEVEL_IMAGE, level.image)

        val result = db.insert(TABLE_LEVEL, null, contentValues)
        db.close()

        return result
    }

    /**
     * This function will get all the levels
     * @return an arraylist of Levels
     */
    fun getLevels(): ArrayList<Level> {
        val levels: ArrayList<Level> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_LEVEL ORDER BY $LEVEL_ID ASC"
        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val level = Level(
                        cursor.getLong(cursor.getColumnIndex(LEVEL_ID)),
                        cursor.getString(cursor.getColumnIndex(LEVEL_LEVEL)),
                        cursor.getString(cursor.getColumnIndex(LEVEL_IMAGE))
                    )
                    levels.add(level)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return levels
    }

    /**
     * Get the next level higher than the given level id
     */
    fun getNextLevel(levelID: Long): Level? {
        var level : Level? = null
        //val levels: ArrayList<Level> = ArrayList()
        val columns = arrayOf(LEVEL_ID, LEVEL_LEVEL, LEVEL_IMAGE)
        val selection = "$LEVEL_ID>?"
        val selectionArgs = arrayOf(levelID.toString())
        val limit = "1"
        val orderby = "$LEVEL_ID ASC"
        val db = this.readableDatabase

        try {
            val cursor = db.query(TABLE_LEVEL, columns, selection, selectionArgs,
                null, null, orderby, limit)
            if (cursor.moveToFirst()) {
                level = Level(
                    cursor.getLong(cursor.getColumnIndex(LEVEL_ID)),
                    cursor.getString(cursor.getColumnIndex(LEVEL_LEVEL)),
                    cursor.getString(cursor.getColumnIndex(LEVEL_IMAGE))
                )
            }
            cursor.close()
        } catch (e: SQLiteException) {
            return null
        }
        return level
    }

    /**
     * This function will insert new question into question table
     * @param question    Question object containing the question info
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    fun addQuestion(question: Question): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(QUESTION_VAL1, question.val1)
        contentValues.put(QUESTION_VAL2, question.val2)
        contentValues.put(QUESTION_LEVELID, question.levelId)

        val result = db.insert(TABLE_QUESTION, null, contentValues)
        db.close()

        return result
    }

    /**
     * Get all questions and its options of a particular level
     * @param levelId The level id of the questions we wish to retrieve
     * @return ArrayList<QuestionWithOptions> An array list containing all the questions with
     * the corresponding options that belongs to the level
     */
    fun getQuestionsWithOptions(levelId: Long) : ArrayList<QuestionWithOptions> {
        val questions: ArrayList<QuestionWithOptions> = ArrayList()
        val columns = arrayOf(QUESTION_ID, QUESTION_VAL1, QUESTION_VAL2, QUESTION_LEVELID)
        val selection = "$QUESTION_LEVELID=?"
        val selectionArgs = arrayOf(levelId.toString())
        val db = this.readableDatabase

        try {
            val cursor = db.query(
                TABLE_QUESTION,
                columns,
                selection,
                selectionArgs,
                null,null,null,null)
            if (cursor.moveToFirst()) {
                do {
                    val questionWithOptions = QuestionWithOptions(
                        cursor.getLong(cursor.getColumnIndex(QUESTION_ID)),
                        cursor.getInt(cursor.getColumnIndex(QUESTION_VAL1)),
                        cursor.getInt(cursor.getColumnIndex(QUESTION_VAL2)),
                        cursor.getLong(cursor.getColumnIndex(QUESTION_LEVELID)),
                        getOptions(cursor.getLong(cursor.getColumnIndex(QUESTION_ID)))
                    )
                    questions.add(questionWithOptions)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            // if there's an error, return an empty array
            return ArrayList()
        }
        return questions
    }

    /**
     * This function will insert new option into option table
     * @param option    Option object containing the option info
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    fun addOption(option: Option): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(OPTION_OPTION, option.option)
        contentValues.put(OPTION_QUESTIONID, option.questionId)
        contentValues.put(OPTION_CORRECT, option.correct)

        val result = db.insert(TABLE_OPTION, null, contentValues)
        db.close()

        return result
    }

    /**
     * Get all options of the given question id
     * @param questionId The question id where the options belong to
     * @return ArrayList<Option> An Array List containing all the options
     */
    private fun getOptions(questionId: Long) : ArrayList<Option> {
        val options: ArrayList<Option> = ArrayList()
        val columns = arrayOf(OPTION_ID, OPTION_OPTION, OPTION_CORRECT, OPTION_QUESTIONID)
        val selection = "$OPTION_QUESTIONID=?"
        val selectionArgs = arrayOf(questionId.toString())
        val db = this.readableDatabase

        try {
            val cursor = db.query(TABLE_OPTION,
                                    columns,
                                    selection,
                                    selectionArgs,
                            null,null,null,null)
            if (cursor.moveToFirst()) {
                do {
                    val option = Option(
                        cursor.getLong(cursor.getColumnIndex(OPTION_ID)),
                        cursor.getInt(cursor.getColumnIndex(OPTION_OPTION)),
                        cursor.getLong(cursor.getColumnIndex(OPTION_QUESTIONID)),
                        cursor.getInt(cursor.getColumnIndex(OPTION_CORRECT))
                    )
                    options.add(option)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            // if there's an error, return an empty array
            return ArrayList()
        }
        return options
    }
}