package com.ttslive.quiz

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {

    private lateinit var questionText: TextView
    private lateinit var categoryText: TextView
    private lateinit var scoreText: TextView
    private lateinit var timerText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var optionA: Button
    private lateinit var optionB: Button
    private lateinit var optionC: Button
    private lateinit var optionD: Button

    private lateinit var db: DatabaseHelper
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private var score = 0
    private lateinit var playerName: String
    private var timer: CountDownTimer? = null
    private val TIME_PER_QUESTION = 15000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        playerName = intent.getStringExtra("PLAYER_NAME") ?: "Guest"
        
        questionText = findViewById(R.id.questionText)
        categoryText = findViewById(R.id.categoryText)
        scoreText = findViewById(R.id.scoreText)
        timerText = findViewById(R.id.timerText)
        progressBar = findViewById(R.id.progressBar)
        optionA = findViewById(R.id.optionA)
        optionB = findViewById(R.id.optionB)
        optionC = findViewById(R.id.optionC)
        optionD = findViewById(R.id.optionD)

        db = DatabaseHelper(this)
        questions = db.getRandomQuestions(5)

        optionA.setOnClickListener { checkAnswer("A") }
        optionB.setOnClickListener { checkAnswer("B") }
        optionC.setOnClickListener { checkAnswer("C") }
        optionD.setOnClickListener { checkAnswer("D") }

        loadQuestion()
    }

    private fun loadQuestion() {
        if (currentQuestionIndex >= questions.size) {
            finishQuiz()
            return
        }

        val question = questions[currentQuestionIndex]
        questionText.text = question.question
        categoryText.text = "Kategori: ${question.category}"
        scoreText.text = "Skor: $score"
        progressBar.progress = ((currentQuestionIndex + 1) * 100) / questions.size

        optionA.text = "A. ${question.optionA}"
        optionB.text = "B. ${question.optionB}"
        optionC.text = "C. ${question.optionC}"
        optionD.text = "D. ${question.optionD}"

        resetButtonColors()
        enableButtons(true)

        startTimer()
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(TIME_PER_QUESTION, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = "⏱ ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                timerText.text = "⏱ 0s"
                Toast.makeText(this@QuizActivity, "Waktu habis!", Toast.LENGTH_SHORT).show()
                highlightCorrectAnswer()
                enableButtons(false)
                
                android.os.Handler().postDelayed({
                    currentQuestionIndex++
                    loadQuestion()
                }, 2000)
            }
        }.start()
    }

    private fun checkAnswer(answer: String) {
        timer?.cancel()
        enableButtons(false)

        val question = questions[currentQuestionIndex]
        val isCorrect = answer == question.correctAnswer

        if (isCorrect) {
            score += 10
            scoreText.text = "Skor: $score"
            Toast.makeText(this, "✅ Benar! +10", Toast.LENGTH_SHORT).show()
            highlightButton(answer, true)
        } else {
            Toast.makeText(this, "❌ Salah!", Toast.LENGTH_SHORT).show()
            highlightButton(answer, false)
            highlightCorrectAnswer()
        }

        android.os.Handler().postDelayed({
            currentQuestionIndex++
            loadQuestion()
        }, 2000)
    }

    private fun highlightButton(answer: String, correct: Boolean) {
        val button = when (answer) {
            "A" -> optionA
            "B" -> optionB
            "C" -> optionC
            "D" -> optionD
            else -> return
        }
        button.setBackgroundColor(if (correct) Color.parseColor("#4CAF50") else Color.parseColor("#F44336"))
    }

    private fun highlightCorrectAnswer() {
        val correctAnswer = questions[currentQuestionIndex].correctAnswer
        highlightButton(correctAnswer, true)
    }

    private fun resetButtonColors() {
        val defaultColor = Color.parseColor("#2196F3")
        optionA.setBackgroundColor(defaultColor)
        optionB.setBackgroundColor(defaultColor)
        optionC.setBackgroundColor(defaultColor)
        optionD.setBackgroundColor(defaultColor)
    }

    private fun enableButtons(enabled: Boolean) {
        optionA.isEnabled = enabled
        optionB.isEnabled = enabled
        optionC.isEnabled = enabled
        optionD.isEnabled = enabled
    }

    private fun finishQuiz() {
        timer?.cancel()
        db.saveScore(playerName, score)

        AlertDialog.Builder(this)
            .setTitle("🎉 Quiz Selesai!")
            .setMessage("$playerName\nSkor akhir: $score/${questions.size * 10}")
            .setPositiveButton("Lihat Leaderboard") { _, _ ->
                val intent = android.content.Intent(this, LeaderboardActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Main Lagi") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
