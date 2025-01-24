package com.example.datalift.navigation

enum class Screens {
    Account,
    Analysis,
    Challenges,
    Feed,
    Friends,
    GroupAnalysis,
    LogIn,
    SignUp,
    Workout,
    WorkoutDetails;

    companion object {
        fun fromRoute(route: String?): Screens = when (route?.substringBefore("/")) {
            Account.name -> Account
            Analysis.name -> Analysis
            Challenges.name -> Challenges
            Feed.name -> Feed
            Friends.name -> Friends
            GroupAnalysis.name -> GroupAnalysis
            LogIn.name -> LogIn
            SignUp.name -> SignUp
            Workout.name -> Workout
            WorkoutDetails.name -> WorkoutDetails
            null -> LogIn
            else -> throw IllegalArgumentException("Route $route is not recognized")

        }

    }
}