package com.rommellaranjo.letsmultiply.utils

import com.rommellaranjo.letsmultiply.models.Level
import com.rommellaranjo.letsmultiply.models.Question
import kotlin.random.Random

class DataSource {

    companion object {
        /**
         * Get all the game levels
         * @return ArrayList<Level>     Return a Level ArrayList containing all the different game levels
         */
        fun getLevels(): ArrayList<Level> {
            val level = ArrayList<Level>()
            level.add(
                Level(
                    1,
                    "Private",
                    "ic_rank_private"
                )
            )
            level.add(
                Level(
                    2,
                    "Corporal",
                    "ic_rank_corporal"
                )
            )
            level.add(
                Level(
                    3,
                    "Sergeant",
                    "ic_rank_sergeant"
                )
            )
            level.add(
                Level(
                    4,
                    "Master Sergeant",
                    "ic_rank_mastersergeant"
                )
            )
            level.add(
                Level(
                    5,
                    "2nd Lieutenant",
                    "ic_rank_2ndlieutenant"
                )
            )
            level.add(
                Level(
                    6,
                    "1st Lieutenant",
                    "ic_rank_1stlieutenant"
                )
            )
            level.add(
                Level(
                    7,
                    "Captain",
                    "ic_rank_captain"
                )
            )
            level.add(
                Level(
                    8,
                    "Major",
                    "ic_rank_major"
                )
            )
            level.add(
                Level(
                    9,
                    "Lieutenant Colonel",
                    "ic_rank_ltcolonel"
                )
            )
            level.add(
                Level(
                    10,
                    "Colonel",
                    "ic_rank_colonel"
                )
            )
            level.add(
                Level(
                    11,
                    "Brigadier General",
                    "ic_rank_bgeneral"
                )
            )
            level.add(
                Level(
                    12,
                    "Major General",
                    "ic_rank_mgeneral"
                )
            )
            level.add(
                Level(
                    13,
                    "Lieutenant General",
                    "ic_rank_ltgeneral"
                )
            )
            level.add(
                Level(
                    14,
                    "General",
                    "ic_rank_general"
                )
            )
            return level
        }

        /**
         * Get all the game questions
         * @return ArrayList<Question>     Return a Question ArrayList containing all the different game questions
         */
        fun questionsData(): ArrayList<Question> {
            val question = ArrayList<Question>()
            var levelId : Long = 1
            var questionId : Long = 1

            // questions from level 1 0x1 up to level 13 12x12
            for (x in 0..12) {
                for (y in 1..12) {
                    question.add(
                        Question(
                            questionId,
                            x,
                            y,
                            levelId
                        )
                    )
                    questionId++
                }
                levelId++
            }

            // for level 14, randomly pick 1 question from each levels 1 to 12
            // do not pick from level 0 questions
            val randomQuestionIndexes = ArrayList<Int>()
            for(i in 12 until question.size) {
                randomQuestionIndexes.add(i)
            }
            randomQuestionIndexes.shuffle()
            for (i in 0..11) {
//                val randomQuestion = question[randomQuestionIndexes[i]].question
//                question.add(
//                    Question(
//                        questionId,
//                        randomQuestion,
//                        levelId
//                    )
//                )
                // this one below is basically the same with above commented code, but shorter
                question.add(
                    question[randomQuestionIndexes[i]].copy(id = questionId, levelId = levelId)
                )
                questionId++
            }
            return question
        }

        /**
         * Randomly generate choices for a given multiplication question
         * @param val1 Int     the x of x * y
         * @param val2 Int     the y of x * y
         * @param numOfChoices Int     the number of random choices to produce including the answer of x * y
         * @return ArrayList<Int>   Returns an Integer ArrayList containing the randomly generated choices
         */
        fun generateRandomChoices(val1: Int, val2: Int, numOfChoices: Int) : ArrayList<Int> {
            val ans = val1 * val2
            val allChoices = ArrayList<Int>()
            val finalChoices = ArrayList<Int>()
            val upperLimit = if (val1 <= 10) 10 else 12
            if (val1 > 1) { // for val1 > 1, choices will range from val1 * 1 to val1 * upperLimit
                for (i in 1..upperLimit) {
                    val choice = val1 * i
                    if (choice != ans) { // do not include the answer
                        allChoices.add(choice)
                    }
                }
            } else { // this is for val1 = 0 or 1 choices will only range from 1 to 10
                for (i in 1..upperLimit) {
                    if (i != ans) {
                        allChoices.add(i)
                    }
                }
            }
            allChoices.shuffle()
            for (i in 0 until numOfChoices - 1) {
                finalChoices.add(allChoices[i])
            }
            finalChoices.add(ans)
            finalChoices.shuffle()

            return finalChoices
        }

    }
}