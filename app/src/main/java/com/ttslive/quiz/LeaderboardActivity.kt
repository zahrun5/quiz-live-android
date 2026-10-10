package com.ttslive.quiz

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var titleText: TextView
    private lateinit var leaderboardList: ListView
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        titleText = findViewById(R.id.titleText)
        leaderboardList = findViewById(R.id.leaderboardList)

        db = DatabaseHelper(this)
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        val scores = db.getTopScores(20)
        
        if (scores.isEmpty()) {
            val emptyList = listOf("Belum ada skor. Main dulu yuk!")
            leaderboardList.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emptyList)
            return
        }

        val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        val displayList = scores.mapIndexed { index, score ->
            val medal = when (index) {
                0 -> "🥇"
                1 -> "🥈"
                2 -> "🥉"
                else -> "${index + 1}."
            }
            val time = dateFormat.format(Date(score.timestamp))
            "$medal ${score.playerName} - ${score.score} poin ($time)"
        }

        leaderboardList.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
    }
}
