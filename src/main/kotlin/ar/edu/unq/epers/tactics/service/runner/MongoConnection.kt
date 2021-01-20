package ar.edu.unq.epers.tactics.service.runner

import com.mongodb.*
import com.mongodb.client.*
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider


/**
 * As per documentation:
 *
 * "The MongoClient instance actually represents a pool of connections to the database;
 * you will only need one instance of class MongoClient even with multiple threads."
 *
 */
class MongoConnection {
    var client:MongoClient
    var dataBase: MongoDatabase


    fun <T> getCollection(name:String, entityType: Class<T> ): MongoCollection<T> {
        try{
            dataBase.createCollection(name)
        } catch (exception: MongoCommandException){
            println("Ya existe la coleccion $name")
        }
        return dataBase.getCollection(name, entityType)
    }

    init {
        val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        )
        val uri = System.getenv().getOrDefault("MONGO_URI", "mongodb://localhost:27017")
        val database = System.getenv().getOrDefault("MONGO_DB", "epersMongo")
        val connectionString = ConnectionString(uri)
        val settings = MongoClientSettings.builder()
            .codecRegistry(codecRegistry)
            .applyConnectionString(connectionString)
            .build()
        client = MongoClients.create(settings)
        dataBase = client.getDatabase(database)
    }
}