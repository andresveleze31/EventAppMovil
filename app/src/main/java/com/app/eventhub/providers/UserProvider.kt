package com.app.eventhub.providers

import android.net.Uri
import android.util.Log
import com.app.eventhub.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.io.File
import java.util.HashMap

class UserProvider {

    val db = Firebase.firestore.collection("Users")
    var storage = FirebaseStorage.getInstance().getReference().child("profile")

    fun create(user:User): Task<Void> {
        return db.document(user.id!!).set(user)
    }

    fun uploadImage(id:String, file: File): StorageTask<UploadTask.TaskSnapshot> {
        var fromfile = Uri.fromFile(file)

        val ref = storage.child("${id}.jpg")
        storage = ref
        val uploadTask = ref.putFile(fromfile)

        return uploadTask.addOnFailureListener {
            Log.d("STORAGE", "ERROR")
        }
    }

    fun updateUserInfo(user: User):Task<Void>{
        val map: MutableMap<String, Any> = HashMap()
        map["name"] = user?.name!!
        map["image"] = user?.image!!


        return db.document(user?.id!!).update(map)
    }

    fun getImageUrl():Task<Uri>{
        return storage.downloadUrl
    }

    fun getClient(idDriver:String): Task<DocumentSnapshot> {
        return db.document(idDriver).get()
    }
}