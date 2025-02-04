package com.example.datalift.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


class workoutRepo {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    /**
     * Function to get all workouts of user
     */
    fun getWorkouts(uid: String, callback: (List<Mworkout>) -> Unit) {
        db.collection("Users")
            .document(uid)
            .collection("Workouts")
            .get()
            .addOnSuccessListener { snapShot ->
                val workoutList = mutableListOf<Mworkout>()
                for (document in snapShot.documents) {
                    val workout = document.toObject(Mworkout::class.java)
                    if (workout != null) {
                        workoutList.add(workout)
                    }
                }
                Log.d("Firebase", "Workouts found: ${workoutList.size}")
                callback(workoutList) // Call the callback with the list
            }.addOnFailureListener { e ->
                Log.d("Firebase", "Error getting workouts returning empty list: ${e.message}")
                callback(emptyList()) // Call the callback with an empty list on error
            }
    }

    /**
     * Function to get workout details from database with ID
     */
    fun getWorkout(uid: String, id: String, callback: (Mworkout?) -> Unit){
        db.collection("Users")
            .document(uid)
            .collection("Workouts")
            .document(id)
            .get()
            .addOnSuccessListener {
                Log.d("Firebase", "Workout found: ${it.data}")
                callback(it.toObject(Mworkout::class.java))
            }
            .addOnFailureListener{
                Log.d("Firebase", "Error getting workout: ${it.message}")
                callback(null)
            }
    }

    /**
     * Function to create a new workout object
     */
    fun createNewWorkout(workout: Mworkout, uid: String) {
        db.collection("Users")
            .document(uid)
            .collection("Workouts")
            .add(workout)
            .addOnSuccessListener {documentReference ->
                val documentID = documentReference.id
                val updatedWorkout = workout.copy(docID = documentID)
                db.collection("Users")
                    .document(uid)
                    .collection("Workouts")
                    .document(documentID)
                    .set(updatedWorkout)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Workout docID set: ${updatedWorkout.docID}")
                    }.addOnFailureListener{
                        Log.d("Firebase", "Error setting docID: ${it.message}")
                    }
                Log.d("Firebase", "Workout created: ${workout.name}")
            }.addOnFailureListener{e ->
                Log.d("Firebase", "Error creating workout: ${e.message}")

            }
    }

    /**
     * Function to edit an existing workout
     */
    fun editWorkout(workout: Mworkout, uid: String) {
        db.collection("Users")
            .document(uid)
            .collection("Workouts")
            .document(workout.docID)
            .set(workout)
            .addOnSuccessListener {
                Log.d("Firebase", "Workout updated: ${workout.name}")
            }.addOnFailureListener { e ->
                Log.d("Firebase", "Error updating workout: ${e.message}")
            }
    }

    /**
     * Function to delete an existing workout
     */
    fun deleteWorkout(workout: Mworkout, uid: String){
        db.collection("Users")
            .document(uid)
            .collection("Workouts")
            .document(workout.docID)
            .delete()
            .addOnSuccessListener {
                Log.d("Firebase", "Workout deleted: ${workout.name}")
            }.addOnFailureListener { e ->
                Log.d("Firebase", "Error deleting workout: ${e.message}")
            }
    }

    fun getExercises(query: String = "", callback: (List<ExerciseItem>) -> Unit){
        if (query.isBlank()) {
            // If the query is empty or just whitespace, don't make a Firebase call
            callback(emptyList())
            return
        }

        val exerciseList = mutableListOf<ExerciseItem>()

        db.collection("ExerciseList")
            .whereGreaterThanOrEqualTo("Title", query)
            .whereLessThanOrEqualTo("Title", query + "\uf8ff")
            .limit(10)
            .addSnapshotListener { snapShot, exception ->
                if (exception != null) {
                    // Handle the error (e.g., log or show a message to the user)
                    Log.e("FirestoreError", "Error fetching exercises", exception)
                    callback(emptyList()) // Return an empty list if there's an error
                    return@addSnapshotListener
                }
                snapShot?.documents?.forEach { document ->
                    val exercise = ExerciseItem.fromDocument(document)
                    exerciseList.add(exercise)
                }

                // Return the fetched exercises to the callback
                callback(exerciseList)
            }
    }



}