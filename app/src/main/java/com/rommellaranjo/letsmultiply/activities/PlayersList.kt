package com.rommellaranjo.letsmultiply.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.rommellaranjo.letsmultiply.R
import com.rommellaranjo.letsmultiply.adapters.PlayersAdapter
import com.rommellaranjo.letsmultiply.database.DatabaseHandler
import com.rommellaranjo.letsmultiply.models.Level
import com.rommellaranjo.letsmultiply.models.PlayerModel
import kotlinx.android.synthetic.main.layout_rank_list.*

class PlayersList : AppCompatActivity() {

    private var dbHandler: DatabaseHandler? = null
    private var playersList: ArrayList<PlayerModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players_list)

        dbHandler = DatabaseHandler(this)
        playersList = dbHandler!!.getPlayerList()

        if (playersList != null && playersList!!.size != 0) {
            setupPlayerRecyclerView(playersList!!)
        } else {
            Toast.makeText(this, "No one has played this game yet in your phone.", Toast.LENGTH_LONG).show()
            val intent = Intent(this@PlayersList, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setupPlayerRecyclerView(players: ArrayList<PlayerModel>) {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

        val playersAdapter = PlayersAdapter(this, players)
        recycler_view.adapter = playersAdapter

        playersAdapter.setOnClickListener(object: PlayersAdapter.OnClickListener {
            override fun onClick(position: Int, model: PlayerModel) {
                val playerID = model.id
                val intent = Intent(this@PlayersList, SelectLevelActivity::class.java)
                intent.putExtra(MainActivity.PLAYER_ID, playerID)
                startActivity(intent)
                finish()
            }
        })
    }
}