package helpers

import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient

class FIrebaseDataService {

    fun eliminarTodo(){
        var db : Firestore = FirestoreClient.getFirestore()

        db.collections.forEach { it ->
            it.listDocuments().forEach {
                it.delete().get()
            }
        }
    }
}