package com.wofh.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wofh.R
import com.wofh.entity.relation.UserAction

class WorkoutAdapter(private val listWorkout: List<UserAction>) : RecyclerView.Adapter<WorkoutAdapter.ListViewHolder>() {
    private lateinit var onCardClickCallback: OnCardClickCallback

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var workoutCard: CardView = itemView.findViewById(R.id.card)
        var workoutImage: ImageView = itemView.findViewById(R.id.image)
        var workoutName: TextView = itemView.findViewById(R.id.name)
        var workoutDescription: TextView = itemView.findViewById(R.id.description)
        var workoutStatus: ImageView = itemView.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder = ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_activity, parent, false))

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.workoutCard.setCardBackgroundColor(
            Color.parseColor(
                when (listWorkout[position].workout.type) {
                    "bulk" -> "#F8C9B9"
                    "cut" -> "#CFD2E2"
                    "diet" -> "#BFF2D1"
                    "recomp" -> "#F9B9B9"
                    else -> "#FFFFFF"
                }
            )
        )
        Glide.with(holder.workoutImage.context)
            .load(listWorkout[position].workout.image)
            .into(holder.workoutImage)
        holder.workoutName.text = listWorkout[position].workout.name
        holder.workoutDescription.text = listWorkout[position].workout.description
        Glide.with(holder.workoutStatus.context)
            .load(
                if (listWorkout[position].detailAction.isCleared)
                    R.drawable.activity_checked
                else
                    R.drawable.activity_uncheck
            )
            .into(holder.workoutStatus)
        holder.itemView.setOnClickListener { onCardClickCallback.onCardClicked(listWorkout[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = listWorkout.size

    interface OnCardClickCallback {
        fun onCardClicked(data: UserAction)
    }

    fun setOnCardClickCallback(onCardClickCallback: OnCardClickCallback) {
        this.onCardClickCallback = onCardClickCallback
    }
}