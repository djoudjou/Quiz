package models

import play.api.Logger


case class Parameters (
	logintimeout : long,
  synchrotime : long,
  nbusersthreshold : long,
  questiontimeframe : long,
  nbquestions : long,
  flushusertable : boolean,
  trackeduseridmail : String)

case class Question (
  good_choice : long,
	label : String,
  answer_1 : String,
  answer_2 : String,
  answer_3 : String,
  answer_4 : String) {
	override def toString = s"$label 1)$answer_1 2)$answer_2 3)$answer_3 4)$answer_4 $good_choice"
}



case class Game(questions:List[Question],parameters:Parameters)


object Game {

  val 20_questions = List(
    Question(good_choice = 1, label = "question 1", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 2, label = "question 2", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 3, label = "question 3", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 4, label = "question 4", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 1, label = "question 5", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 2, label = "question 6", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 3, label = "question 7", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 4, label = "question 8", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 1, label = "question 9", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 2, label = "question 10", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 3, label = "question 11", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 4, label = "question 12", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 1, label = "question 13", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 2, label = "question 14", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 3, label = "question 15", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 4, label = "question 16", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 1, label = "question 17", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 2, label = "question 18", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 3, label = "question 19", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    Question(good_choice = 4, label = "question 20", answer_1 ="answer_1", answer_2 ="answer_2", answer_3 ="answer_3", answer_4 ="answer_4"),
    )

  def default = 
}
