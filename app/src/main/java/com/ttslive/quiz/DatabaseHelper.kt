package com.ttslive.quiz

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Question(
    val id: Int,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String,
    val category: String
)

data class Score(
    val playerName: String,
    val score: Int,
    val timestamp: Long
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "quiz_live.db"
        private const val DB_VERSION = 1
        private const val TABLE_QUESTIONS = "questions"
        private const val TABLE_SCORES = "scores"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_QUESTIONS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                question TEXT NOT NULL,
                option_a TEXT NOT NULL,
                option_b TEXT NOT NULL,
                option_c TEXT NOT NULL,
                option_d TEXT NOT NULL,
                correct_answer TEXT NOT NULL,
                category TEXT NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_SCORES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_name TEXT NOT NULL,
                score INTEGER NOT NULL,
                timestamp INTEGER NOT NULL
            )
        """)

        // Seed questions
        val questions = listOf(
            listOf("Ibu kota Indonesia?", "Jakarta", "Bandung", "Surabaya", "Medan", "A", "Umum"),
            listOf("2 + 2 = ?", "3", "4", "5", "6", "B", "Matematika"),
            listOf("Warna bendera Indonesia?", "Merah Putih", "Biru Putih", "Hijau Kuning", "Hitam Merah", "A", "Umum"),
            listOf("Planet terdekat matahari?", "Venus", "Merkurius", "Bumi", "Mars", "B", "Sains"),
            listOf("Bahasa pemrograman Google?", "Java", "Python", "Go", "Ruby", "C", "Teknologi"),
            listOf("Penemu lampu?", "Tesla", "Edison", "Einstein", "Newton", "B", "Sejarah"),
            listOf("7 x 8 = ?", "54", "56", "64", "72", "B", "Matematika"),
            listOf("Pulau terbesar Indonesia?", "Jawa", "Sumatera", "Kalimantan", "Papua", "D", "Geografi"),
            listOf("Nama laut di Jakarta?", "Jawa", "Banda", "Arafuru", "Flores", "A", "Geografi"),
            listOf("Mount Everest ada di?", "Indonesia", "Nepal", "India", "China", "B", "Geografi")
        )

        questions.forEach { q ->
            val cv = ContentValues().apply {
                put("question", q[0])
                put("option_a", q[1])
                put("option_b", q[2])
                put("option_c", q[3])
                put("option_d", q[4])
                put("correct_answer", q[5])
                put("category", q[6])
            }
            db.insert(TABLE_QUESTIONS, null, cv)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SCORES")
        onCreate(db)
    }

    fun getRandomQuestions(limit: Int = 5): List<Question> {
        val list = mutableListOf<Question>()
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_QUESTIONS ORDER BY RANDOM() LIMIT ?",
            arrayOf(limit.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                list.add(Question(
                    id = cursor.getInt(0),
                    question = cursor.getString(1),
                    optionA = cursor.getString(2),
                    optionB = cursor.getString(3),
                    optionC = cursor.getString(4),
                    optionD = cursor.getString(5),
                    correctAnswer = cursor.getString(6),
                    category = cursor.getString(7)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun saveScore(playerName: String, score: Int) {
        val cv = ContentValues().apply {
            put("player_name", playerName)
            put("score", score)
            put("timestamp", System.currentTimeMillis())
        }
        writableDatabase.insert(TABLE_SCORES, null, cv)
    }

    fun getTopScores(limit: Int = 10): List<Score> {
        val list = mutableListOf<Score>()
        val cursor = readableDatabase.rawQuery(
            "SELECT player_name, score, timestamp FROM $TABLE_SCORES ORDER BY score DESC, timestamp ASC LIMIT ?",
            arrayOf(limit.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                list.add(Score(
                    playerName = cursor.getString(0),
                    score = cursor.getInt(1),
                    timestamp = cursor.getLong(2)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
