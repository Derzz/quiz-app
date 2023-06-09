package com.derz.thebigquizapp

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.io.BufferedReader
import java.util.LinkedList
import java.util.Queue
import java.io.InputStreamReader

class QuestionManager() : Parcelable {

    private var questionList: MutableList<Question> = arrayListOf()
    private var questionQueue: Queue<Question> = LinkedList()

    constructor(parcel: Parcel) : this() {
        parcel.readList(questionList, Question::class.java.classLoader)

        val questionArrayList: ArrayList<Question> = ArrayList()
        parcel.readList(questionArrayList, Question::class.java.classLoader)
        questionQueue.addAll(questionArrayList)
    }

    fun fillQuestionList(inputStreamReader: InputStreamReader) {
        // Read TSV file
        val reader = BufferedReader(inputStreamReader)
        var line: String? = ""

        while (reader.readLine().also {line = it} != null) {
            val row: List<String> = line?.split("\t") ?: arrayListOf()
            var answers: MutableList<Answer> = arrayListOf()

            // Add answers to question answer list
            for (i in 1 until 5) {
                var tempAnswer: Answer = if (i == 1) { // Adds the correct answer to the list
                    Answer(row[i], true)
                } else { // Adds the incorrect answers to the list
                    Answer(row[i], false)
                }
                answers.add(tempAnswer)
            }

            // Creates new question and adds it to list of questions
            var tempQuestion = Question(row[0], answers, row[1])
            questionList.add(tempQuestion)
        }
    }

    fun pushQuestionsIntoQueue() {
        val queueSize = 10
        var indexList = IntArray(queueSize)

        // Generate list of distinct indices of length queueSize
        for (i in 0 until queueSize) {
            var randomIndex: Int
            do {
                randomIndex = (0 until this.questionList.size).random()
            } while (indexList.contains(randomIndex))
            indexList[i] = randomIndex
        }

        clearQuestionQueue()
        // Push list of distinct questions into question queue
        for (i in 0 until queueSize) {
            var tempQuestion = this.questionList[indexList[i]]
            tempQuestion.randomizeAnswers()
            this.questionQueue.add(tempQuestion)
        }
    }

    // Clear functions
    fun clearQuestionList() {
        this.questionList.clear()
    }

    fun clearQuestionQueue() {
        questionQueue.clear()
    }

    // Getter functions
    fun getQuestionList(): MutableList<Question> {
        return this.questionList
    }

    fun getQuestionQueue(): Queue<Question> {
        return this.questionQueue
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(questionList)
        parcel.writeList(ArrayList(questionQueue))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionManager> {
        override fun createFromParcel(parcel: Parcel): QuestionManager {
            return QuestionManager(parcel)
        }

        override fun newArray(size: Int): Array<QuestionManager?> {
            return arrayOfNulls(size)
        }
    }
}