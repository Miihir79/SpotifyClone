package com.mihir.spotifyclone.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.mihir.spotifyclone.entities.Song
import com.mihir.spotifyclone.other.Constants.SONG_COLLECTION

import kotlinx.coroutines.tasks.await

class MusicDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)

    suspend fun getAllSongs(): List<Song> {
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch(e: Exception) {
            emptyList()
        }
    }
}