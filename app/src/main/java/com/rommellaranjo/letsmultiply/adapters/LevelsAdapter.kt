package com.rommellaranjo.letsmultiply.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rommellaranjo.letsmultiply.R
import com.rommellaranjo.letsmultiply.models.Level
import kotlinx.android.synthetic.main.layout_rank_item.view.*

open class LevelsAdapter(
    private val context: Context,
    private val levels: ArrayList<Level>,
    private val playerLevel: Long
):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    /**
     *
     */
    private class LevelViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LevelViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_rank_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = levels[position]

        if (holder is LevelViewHolder) {
            // if the level id is greater than the player's level id, lock the level
            if (model.id > playerLevel) {
                holder.itemView.iv_rank_image.setImageResource(R.drawable.ic_baseline_lock_24)
                holder.itemView.tv_rank_name.text = model.level
                holder.itemView.tv_rank_status.text = "Locked"
                // no OnClickListener yet since it is locked
            } else {
                val drawableResourceId: Int = context.resources
                    .getIdentifier(model.image, "drawable", context.packageName)
                holder.itemView.iv_rank_image.setImageResource(drawableResourceId)
                holder.itemView.tv_rank_name.text = model.level
                holder.itemView.tv_rank_status.text = "Unlocked"

                holder.itemView.setOnClickListener {
                    if (onClickListener != null) {
                        onClickListener!!.onClick(position, model)
                    }
                }
            }
        }
    }

    /**
     * Get the number of levels
     */
    override fun getItemCount(): Int {
        return levels.size
    }

    fun setOnClickListener(onClickAction: OnClickListener) {
        this.onClickListener = onClickAction
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Level)
    }

}