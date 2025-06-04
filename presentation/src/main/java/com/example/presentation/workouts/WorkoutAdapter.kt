package com.example.presentation.workouts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.workout.Workout
import com.example.domain.workout.WorkoutType
import com.example.presentation.databinding.ItemWorkoutBinding

class WorkoutAdapter(
    private val onItemClick: (Workout) -> Unit
) : ListAdapter<Workout, WorkoutAdapter.WorkoutViewHolder>(WorkoutDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WorkoutViewHolder(private val binding: ItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workout: Workout) {
            binding.textTitle.text = workout.title
            val typeText = when (workout.type) {
                WorkoutType.TRAINING -> "Training"
                WorkoutType.LIVE     -> "Live"
                WorkoutType.COMPLEX  -> "Complex"
            }
            binding.textType.text = typeText
            binding.textDuration.text = "${workout.duration} мин"
            binding.textDescription.text = workout.description
            binding.root.setOnClickListener { onItemClick(workout) }
        }
    }

    class WorkoutDiffCallback : DiffUtil.ItemCallback<Workout>() {
        override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean =
            oldItem == newItem
    }
}
