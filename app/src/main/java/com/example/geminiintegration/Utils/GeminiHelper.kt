package com.example.geminiintegration.Utils

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig

object GeminiHelper {
    private const val API_KEY = "AIzaSyCHManL_NhbdMiBYT_vvMVRxnC3HWlce_E"

    // Simple model without restrictive configs - like your chatbot
    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash", // or "gemini-1.5-pro" for better quality
        apiKey = API_KEY
        // NO generationConfig - let it use defaults for unlimited generation
    )

    // Alternative: If you want some control but no limits
    private val modelWithConfig = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY,
        generationConfig = generationConfig {
            temperature = 0.7f
            // Remove topK, topP, and maxOutputTokens completely
            // Let Gemini decide the appropriate length
        }
    )

    suspend fun generateStudyContent(
        standard: String,
        subject: String,
        topic: String,
        difficulty: String = "medium",
        attemptNumber: Int = 1,
        additionalPrompt: String = ""
    ): String {
        val difficultyInstruction = when {
            attemptNumber > 2 || difficulty == "easy" ->
                "Explain in VERY SIMPLE language suitable for beginners. Use everyday examples and break down complex terms."
            difficulty == "hard" ->
                "Provide detailed explanation with advanced concepts."
            else ->
                "Provide clear explanation suitable for ${standard} students."
        }

        val prompt = """
            You are an expert tutor teaching ${subject} to a ${standard} student.
            
            Topic: ${topic}
            Difficulty Level: ${difficulty}
            ${if (additionalPrompt.isNotBlank()) "Additional Instructions: $additionalPrompt" else ""}
            
            ${difficultyInstruction}
            
            Create comprehensive study material that includes:
            1. **Introduction**: Brief overview of the topic
            2. **Key Concepts**: Main ideas explained clearly
            3. **Detailed Explanation**: Step-by-step breakdown
            4. **Examples**: 2-3 practical examples with solutions
            5. **Important Points**: Key takeaways to remember
            6. **Common Mistakes**: What students should avoid
            
            Make it engaging and easy to understand. Reference textbook concepts where applicable.
            Format the response with clear headings and structure.
        """.trimIndent()

        return try {
            // Same approach as your working chatbot - simple and direct
            val response = model.generateContent(prompt)
            response.text ?: "Failed to generate content. Please try again."
        } catch (e: Exception) {
            "Error: ${e.message ?: "Unknown error occurred"}"
        }
    }

    suspend fun generateQuiz(
        standard: String,
        subject: String,
        topic: String,
        studyContent: String
    ): String {
        val prompt = """
            Based on the following study content about ${topic} for ${standard} ${subject} students, 
            generate exactly 5 multiple-choice questions to test understanding.
            
            Study Content:
            ${studyContent.take(2000)}
            
            Return ONLY valid JSON with no markdown formatting:
            {"questions":[{"question":"Question text?","options":["A","B","C","D"],"correctAnswer":0}]}
            
            Rules:
            - correctAnswer is the index (0-3) of the correct option
            - Each question must have exactly 4 options
            - Questions should test understanding
            - Return ONLY the JSON object, no text before or after
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            var jsonText = response.text ?: "{\"questions\": []}"

            // Clean potential markdown formatting
            jsonText = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            jsonText
        } catch (e: Exception) {
            "{\"questions\": []}"
        }
    }
}