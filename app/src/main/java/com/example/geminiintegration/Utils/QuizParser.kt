package com.example.geminiintegration.Utils



import com.example.geminiintegration.DataModels.Quiz
import com.example.geminiintegration.DataModels.QuizQuestion
import org.json.JSONObject

object QuizParser {
    fun parseQuizJson(jsonString: String): Quiz? {
        return try {
            // Clean the JSON string (remove markdown if present)
            val cleanJson = jsonString
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val jsonObject = JSONObject(cleanJson)
            val questionsArray = jsonObject.getJSONArray("questions")

            val questions = mutableListOf<QuizQuestion>()

            for (i in 0 until questionsArray.length()) {
                val questionObj = questionsArray.getJSONObject(i)
                val optionsArray = questionObj.getJSONArray("options")

                val options = mutableListOf<String>()
                for (j in 0 until optionsArray.length()) {
                    options.add(optionsArray.getString(j))
                }

                questions.add(
                    QuizQuestion(
                        questionText = questionObj.getString("question"),
                        options = options,
                        correctAnswerIndex = questionObj.getInt("correctAnswer")
                    )
                )
            }

            Quiz(questions)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}