package com.rommellaranjo.letsmultiply.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.rommellaranjo.letsmultiply.R
import com.rommellaranjo.letsmultiply.adapters.PlayersAdapter
import com.rommellaranjo.letsmultiply.database.DatabaseHandler
import com.rommellaranjo.letsmultiply.models.PlayerModel
import com.rommellaranjo.letsmultiply.utils.Validator
import kotlinx.android.synthetic.main.layout_rank_list.*
import kotlinx.android.synthetic.main.popup_dialog.*

class PlayersListActivity : AppCompatActivity() {
    // TODO: Add Ads 
    private var dbHandler: DatabaseHandler? = null
    private var playersList: ArrayList<PlayerModel>? = null
    private val validator: Validator = Validator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players_list)

        dbHandler = DatabaseHandler(this)
        playersList = dbHandler!!.getPlayerList()

        if (playersList != null && playersList!!.size != 0) {
            setupPlayerRecyclerView(playersList!!)
        } else {
            Toast.makeText(this, "No one has played this game in your phone yet.", Toast.LENGTH_LONG).show()
            finish()
        }

    }

    /**
     * This will display the players in the Recycler View
     */
    private fun setupPlayerRecyclerView(players: ArrayList<PlayerModel>) {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

        val playersAdapter = PlayersAdapter(this, players)
        recycler_view.adapter = playersAdapter

        playersAdapter.setOnClickListener(object: PlayersAdapter.OnClickListener {
            override fun onClick(position: Int, model: PlayerModel) {
                val playerID = model.id
                val intent = Intent(this@PlayersListActivity, SelectLevelActivity::class.java)
                intent.putExtra(MainActivity.PLAYER_ID, playerID)
                startActivity(intent)
                finish()
            }
        })
    }

    /**
     * Called when the Edit Icon is clicked
     */
    fun updatePlayerDialog(player: PlayerModel) {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.popup_dialog)
        dialog.et_update_name.setText(player.name)

        // handle the update button click
        dialog.btn_dialog_update.setOnClickListener(View.OnClickListener {
            val name = dialog.et_update_name.text.toString().trim()

            if (!validator.isAlphaNumeric(name)) {
                Toast.makeText(applicationContext, "Please use only letters[a-z or A-Z] and numbers[0-9].", Toast.LENGTH_LONG).show()
            } else if (!validator.isCorrectLength(name, 25)) {
                Toast.makeText(applicationContext, "Name should not be greater than 25 characters.", Toast.LENGTH_LONG).show()
            } else {
                val status = dbHandler!!.updatePlayer(player.copy(name = name))
                if (status > 0) {
                    // get updated players
                    playersList = dbHandler!!.getPlayerList()

                    // display updated list of players
                    setupPlayerRecyclerView(playersList!!)
                    dialog.dismiss() // Dialog will be dismissed
                } else {
                    // Possibly, the Player Name was already taken
                    Toast.makeText(applicationContext, "Please make sure your name is not taken yet.", Toast.LENGTH_LONG).show()
                }
            }
        })

        // handle the cancel button click
        dialog.btn_dialog_cancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss() // Dialog will be dismissed
        })

        // Pop-up this dialog
        dialog.show()
    }

    fun deletePlayerDialog(player: PlayerModel) {
        val builder = AlertDialog.Builder(this)
        // set title
        builder.setTitle("Delete Player")
        // set message
        builder.setMessage("Are you sure you want to delete ${player.name}?")
        //builder.setIcon(android.R.drawable.ic_dialog_alert)

        // perform Cancel action
        builder.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }

        // perform Yes action
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dbHandler!!.deletePlayer(player.id)
            // get updated players
            playersList = dbHandler!!.getPlayerList()

            // display updated list of players
            setupPlayerRecyclerView(playersList!!)
            dialogInterface.dismiss() // Dialog will be dismissed
        }


        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()

        // set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(applicationContext, R.color.colorButton))
//        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorButton))

//        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).layout(10, 10, 10, 10)
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(applicationContext, R.color.colorButton))
//        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorButton))
    }

}