package ar.edu.unq.epers.tactics.persistencia.dao.firebase

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.excepciones.DuplicatedException
import ar.edu.unq.epers.tactics.modelo.excepciones.NoHibernateIdentifierException
import ar.edu.unq.epers.tactics.modelo.habilidades.*
import ar.edu.unq.epers.tactics.persistencia.dao.LeaderBoardDAO
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.runner.FirebaseInitializer
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Transaction
import com.google.firebase.cloud.FirestoreClient
import java.util.*
import kotlin.collections.HashMap


class FirebaseLeaderBoardDAO : LeaderBoardDAO {

    var db : Firestore

    init{
        FirebaseInitializer.initialize()
        db = FirestoreClient.getFirestore()
    }

    override fun crearParty(party: Party) {
        val documentID = party.id ?: throw NoHibernateIdentifierException("La party no ha sido inicializada por Hibernate")
        val docData: MutableMap<String, Any> = HashMap()
        docData["CantidadVictorias"] = 0
        docData["Imagen"] = party.imagenUrl!!
        docData["NombreParty"] = party.nombre!!

        db.collection("Partys").document(documentID.toString()).set(docData).get()
    }

    override fun crearAventurero(aventurero: Aventurero) {
        val documentID = aventurero.id ?: throw NoHibernateIdentifierException("El aventurero no ha sido inicializado por Hibernate")
        val magoDocRef = getDocRef("Mago", documentID)
        val guerreroDocRef = getDocRef("Guerreros", documentID)
        val budistaDocRef = getDocRef("budistas", documentID)
        val curanderoDocRef = getDocRef("Curanderos", documentID)

        db.runTransaction {
            agregarACollecion(it, aventurero, magoDocRef, "DañoMagico")
            agregarACollecion(it, aventurero, guerreroDocRef, "valor")
            agregarACollecion(it, aventurero, budistaDocRef, "ManaRegenerado")
            agregarACollecion(it, aventurero, curanderoDocRef, "Curo")
        }.get()
    }

    private fun getDocRef(collecion: String, id: Long): DocumentReference {
        return db.collection(collecion).document(id.toString())
    }

    private fun agregarACollecion(transaction: Transaction, aventurero: Aventurero, docRef : DocumentReference, campo : String) {
        val docData: MutableMap<String, Any> = basicData(aventurero, campo)
        transaction.set(docRef, docData)
    }

    private fun basicData(aventurero: Aventurero, campo: String): MutableMap<String, Any> {
        val docData: MutableMap<String, Any> = HashMap()
        docData["Imagen"] = aventurero.imagenUrl!!
        if (campo == "valor"){
            docData["nombreParty"] = aventurero.party!!.nombre!!
        }else {
            docData["NombreParty"] = aventurero.party!!.nombre!!
        }
        docData["Nombre"] = aventurero.nombre!!
        docData[campo] = 0
        return docData
    }

    override fun actualizarVictorias(party: Party) {
        val documentID = party.id ?: throw NoHibernateIdentifierException("La party no ha sido inicializada por Hibernate")
        val docRef = getDocRef("Partys", documentID)
        docRef.update("CantidadVictorias", party.peleasGanadas).get()
    }

    override fun actualizarAventureros(habilidade: Habilidad) {
        if(habilidade.ejecutada && habilidade.acerto){
            actualizarLeaderboard(habilidade)
        }
    }

    private fun actualizarLeaderboard(habilidad: Habilidad) {
        val documentID = habilidad.emisor?.id ?: throw NoHibernateIdentifierException("El emisor no ha sido inicializado por Hibernate")
        var collecion = " "
        var campo = " "
        var cantidad = 0
        when(habilidad){
            is Curar -> {
                collecion = "Curanderos"
                campo = "Curo"
                cantidad = habilidad.poderMagico.toInt()
            }
            is Meditar -> {
                collecion = "budistas"
                campo = "ManaRegenerado"
                cantidad = habilidad.cantidadManaRegenerado
            }
            is AtaqueMagico -> {
                collecion = "Mago"
                campo = "DañoMagico"
                cantidad = habilidad.poderMagico.toInt()
            }
            is Ataque -> {
                collecion = "Guerreros"
                campo = "valor"
                cantidad = habilidad.danio
            }
        }
        val aventureroDocRef = getDocRef(collecion, documentID)
        val docData: MutableMap<String, Any> = HashMap()
        db.runTransaction {
            val snapshot = it.get(aventureroDocRef).get()
            docData[campo] = cantidad + snapshot.getLong(campo)!!
            it.update(aventureroDocRef, docData)
        }.get()

    }

    override fun eliminar(aventurero: Aventurero) {
        val documentID = aventurero.id ?: throw NoHibernateIdentifierException("El aventurero no ha sido inicializado por Hibernate")
        val magoDocRef = getDocRef("Mago", documentID)
        val guerreroDocRef = getDocRef("Guerreros", documentID)
        val budistaDocRef = getDocRef("budistas", documentID)
        val curanderoDocRef = getDocRef("Curanderos", documentID)

        db.runTransaction {
            it.delete(magoDocRef)
            it.delete(guerreroDocRef)
            it.delete(budistaDocRef)
            it.delete(curanderoDocRef)
        }.get()
    }

}