package com.example.datalift.model

import com.google.firebase.Timestamp
import java.util.Date

data class Mpost(
    val docID: String = "",
    val date: Timestamp = Timestamp.now(),
    val workout: Mworkout? = null,
    //var goal: Mugoal? = null,
    val poster: Muser? = Muser(),
    var title: String = "",
    var body: String = "",
    //var pictures: List<Picture> = emptyList(),
    //Picture is a filler cuz idk the datatype
    //Be sure to update Muser.kt with the same datatype
    var likes: List<String> = emptyList(),
    //var draft: Boolean = false
){
    fun getLikesCount(): Int {
        return likes.size
    }
}

fun testPost() : Mpost{
    return Mpost(
        docID = "1",
        date = Timestamp.now(),
        workout = Mworkout(
            name = "",
            date = Timestamp(Date(1743459788216)),
            muscleGroup = "",
            docID = "",
            exercises = listOf(
                Mexercise(
                    id = "1",
                    name = "Lift McDonald's",
                    sets = listOf(
                        Mset(
                            rep = 5,
                            weight = 30.0
                        ),
                        Mset(
                            rep = 8,
                            weight = 30.0
                        )
                    )
                ),
                Mexercise(
                    id = "2",
                    name = "Lift Broke People",
                    sets = listOf(
                        Mset(
                            rep = 5,
                            weight = 110.0
                        ),
                    )
                ),
            )
        ),
        //goal = ,
        poster =  Muser("VlcMPI0J6JZA3LIUqNxgUyp4nY63", "dcmith", "dcsmith0396@gmail.com",
            "Male", "Dylan", 70.0, 225.0, privacy = false, imperial = true ),
        title = "Broke McDonald's Workout",
        body = "I had a great day at the gym. My homeless broke besties all joined up with me " +
                "to go to McDonald's and get a bunch of Big Macs. We had to preacher curl our " +
                "Big Macs to our mouths cuz we were double fisting the burgers. I also had to " +
                "do wall-sits because we're too poor to afford chairs.",
        //pictures = ,
//            likes = 1000000,
//            draft = true
    )
}


fun testPostList() : List<Mpost> {
    return listOf(
        Mpost(
            docID = "1",
            //date = ,
            workout = Mworkout(
                name = "",
                date = Timestamp(Date(1743459788216)),
                muscleGroup = "",
                docID = "",
                exercises = listOf(
                    Mexercise(
                        id = "1",
                        name = "Lift McDonald's",
                        sets = listOf(
                            Mset(
                                rep = 5,
                                weight = 30.0
                            ),
                            Mset(
                                rep = 8,
                                weight = 30.0
                            )
                        )
                    ),
                    Mexercise(
                        id = "2",
                        name = "Lift Broke People",
                        sets = listOf(
                            Mset(
                                rep = 5,
                                weight = 110.0
                            ),
                        )
                    ),
                )
            ),
            //goal = ,
            poster = Muser("VlcMPI0J6JZA3LIUqNxgUyp4nY63", "dcmith", "dcsmith0396@gmail.com",
                "Male", "Dylan", 70.0, 225.0, privacy = false, imperial = true ),
            title = "Broke McDonald's Workout",
            body = "I had a great day at the gym. My homeless broke besties all joined up with me " +
                    "to go to McDonald's and get a bunch of Big Macs. We had to preacher curl our " +
                    "Big Macs to our mouths cuz we were double fisting the burgers. I also had to " +
                    "do wall-sits because we're too poor to afford chairs.",
            //pictures = ,
//            likes = 1000000,
//            draft = true
        ),
        Mpost(
            docID = "2",
            //date = ,
            workout = Mworkout(
                name = "",
                date = Timestamp(Date(1743459788216)),
                muscleGroup = "",
                docID = "",
                exercises = listOf(
                    Mexercise(
                        id = "3",
                        name = "Lift Burger King",
                        sets = listOf(
                            Mset(
                                rep = 5,
                                weight = 30.0
                            ),
                            Mset(
                                rep = 8,
                                weight = 30.0
                            )
                        )
                    ),
                    Mexercise(
                        id = "4",
                        name = "Lift Homeless People",
                        sets = listOf(
                            Mset(
                                rep = 5,
                                weight = 110.0
                            ),
                        )
                    ),
                )
            ),
            //goal = ,
            poster = Muser("VlcMPI0J6JZA3LIUqNxgUyp4nY63", "dcmith", "dcsmith0396@gmail.com",
                "Male", "Dylan", 70.0, 225.0, privacy = false, imperial = true ),
            title = "Homeless Burger King Workout",
            body = "For my next exercise, I took my homeless homies to a Burger King, where I got " +
                    "absolutely plastered by eating so many Whopper Jrs and Vanilla Shakes. One of" +
                    " my homeless homies got some chicken fries, but that's he can't even afford a" +
                    " Whopper. In the end, I had to lift my homeless homies back to da crib and " +
                    "eat a lot of Burger King",
            //pictures = ,
//            likes = 500000,
//            draft = true
        )
    )
}
