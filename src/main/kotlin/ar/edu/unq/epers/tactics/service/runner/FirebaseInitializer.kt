package ar.edu.unq.epers.tactics.service.runner

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream

object FirebaseInitializer {
    private val serviceAccount = FileInputStream("src/main/resources/epers-tatics-firebase-adminsdk.json")

    private val options: FirebaseOptions = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://epers-tatics.firebaseio.com")
            .build()

    fun initialize(){
        if(FirebaseApp.getApps().isEmpty()){
            FirebaseApp.initializeApp(options)
        }

    }
}