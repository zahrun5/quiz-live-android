package com.ttslive.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var playerNameInput: EditText
    private lateinit var startButton: Button
    private lateinit var leaderboardButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerNameInput = findViewById(R.id.playerNameInput)
        startButton = findViewById(R.id.startButton)
        leaderboardButton = findViewById(R.id.leaderboardButton)

        startButton.setOnClickListener {
            val playerName = playerNameInput.text.toString().trim()
            if (playerName.isEmpty()) {
                Toast.makeText(this, "Masukkan nama dulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("PLAYER_NAME", playerName)
            startActivity(intent)
        }

        leaderboardButton.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }
    }
}
