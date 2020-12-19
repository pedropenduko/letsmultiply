package com.rommellaranjo.letsmultiply.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rommellaranjo.letsmultiply.R
import com.rommellaranjo.letsmultiply.models.PlayerModel
import kotlinx.android.synthetic.main.layout_player_item.view.*

open class PlayersAdapter(private val context: Context,
                          private val players: ArrayList<PlayerModel>
                          ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlayerViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_player_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = players[position]

        if (holder is PlayerViewHolder) {
            holder.itemView.tv_player_name.text = model.name
            holder.itemView.setOnClickListener{
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return players.size
    }

    fun setOnClickListener(onClickAction: OnClickListener) {
        this.onClickListener = onClickAction
    }

    interface OnClickListener {
        fun onClick(position: Int, model: PlayerModel)
    }
}