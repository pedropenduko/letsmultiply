package com.rommellaranjo.letsmultiply.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.rommellaranjo.letsmultiply.R
import com.rommellaranjo.letsmultiply.adapters.LevelsAdapter
import com.rommellaranjo.letsmultiply.database.DatabaseHandler
import com.rommellaranjo.letsmultiply.models.Level
import com.rommellaranjo.letsmultiply.models.PlayerModel
import kotlinx.android.synthetic.main.layout_rank_list.*

class SelectLevelActivity : AppCompatActivity() {

    private var playerID : Long = 0
    private var playerDetails: PlayerModel? = null
    private var dbHandler: DatabaseHandler? = null
    private var allLevels: ArrayList<Level>? = null
    private lateinit var levelAdapter: LevelsAdapter

    companion object {
        const val RANK_CLICKED = "rank_clicked"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_rank_list)

        // get the player ID
        if (intent.hasExtra(MainActivity.PLAYER_ID)) {
            playerID = intent.getLongExtra(MainActivity.PLAYER_ID, 0)
        }
        if (playerID > 0) {
            setSupportActionBar(tb_rank_list)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

            tb_rank_list.setNavigationOnClickListener {
                onBackPressed()
            }

            dbHandler = DatabaseHandler(this)
            allLevels = dbHandler!!.getLevels()
            playerDetails = dbHandler!!.getPlayer(playerID)

            setupLevelRecyclerView(allLevels!!, playerDetails!!.levelId)
        } else {
            Log.e("Error: ", "No Player ID passed.")
        }


    }

    private fun setupLevelRecyclerView(levels: ArrayList<Level>, playerLevel: Long) {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

        val levelsAdapter = LevelsAdapter(this, levels, playerLevel)
        recycler_view.adapter = levelsAdapter

        levelsAdapter.setOnClickListener(object: LevelsAdapter.OnClickListener {
            override fun onClick(position: Int, model: Level) {
                // Toast.makeText(this@SelectLevelActivity, "Rank clicked.", Toast.LENGTH_SHORT).show()
                //Log.e("Levels", "Item clicked.")
                // call another intent for the actual game
                val intent = Intent(this@SelectLevelActivity, GameQuestionsActivity::class.java)
                intent.putExtra(MainActivity.PLAYER_ID, playerID)
                intent.putExtra(RANK_CLICKED, model.id)
                startActivity(intent)
                finish()
            }
        })
    }

    /**
     * Show the Settings Icon
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Set Action when the Settings Icon is clicked
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_settings -> {
                Toast.makeText(this, "Show Settings UI...", Toast.LENGTH_SHORT).show()
                true
            } else -> super.onOptionsItemSelected(item)
        }

    }

}