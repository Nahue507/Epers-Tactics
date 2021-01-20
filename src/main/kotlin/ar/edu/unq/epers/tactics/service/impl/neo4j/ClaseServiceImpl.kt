package ar.edu.unq.epers.tactics.service.impl.neo4j

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.modelo.excepciones.NoPuedeGanarProficienciaException
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoDBFormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4j.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.ClaseService
import ar.edu.unq.epers.tactics.service.dto.Atributo
import ar.edu.unq.epers.tactics.service.impl.hibernate.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.runner.Neo4jSessionFactory.createSession
import org.neo4j.driver.Record
import org.neo4j.driver.Values
import java.lang.NullPointerException

class ClaseServiceImpl(): ClaseService {

    private val claseDAO: ClaseDAO = Neo4JClaseDAO()
    private val aventureroService: AventureroService = AventureroServiceImpl(HibernateAventureroDAO(), HibernatePartyDAO(), FirebaseLeaderBoardDAO(), MongoDBFormacionDAO())

    override fun crearClase(nombreDeClase: String) {
        claseDAO.crearClase(nombreDeClase)
    }

    override fun crearMejora(nombreDeClase1: String, nombreDeClase2: String, atributos: List<Atributo>, cantidadDeAtributos: Int) {
        claseDAO.crearMejora(nombreDeClase1, nombreDeClase2, atributos, cantidadDeAtributos)
    }

    override fun requerir(nombreDeClase1: String, nombreDeClase2: String) {
        claseDAO.requerir(nombreDeClase1, nombreDeClase2)
    }

    override fun puedeMejorar(aventureroId: Long, mejora: Mejora): Boolean {
        val aventurero = aventureroService.recuperar(aventureroId)
        return claseDAO.puedeMejorar(aventurero, mejora)
    }

    override fun ganarProficiencia(aventureroId: Long, nombreDeClase1: String, nombreDeClase2: String): Aventurero {
        val aventurero = aventureroService.recuperar(aventureroId)
        val mejora: Mejora
        try {
            mejora = claseDAO.obtenerMejoras(aventurero.getClases()).first {it.claseFinal == nombreDeClase2}
            if (puedeMejorar(aventurero.id!!, mejora)) {
                aventurero.ganarProficiencia(mejora)
            }
        } catch (e: NoSuchElementException) {
            throw NoPuedeGanarProficienciaException("El aventurero no puede obtener la mejora a clase $nombreDeClase2")
        }
        return aventureroService.actualizar(aventurero)
    }

    override fun posiblesMejoras(aventureroId: Long): Set<Mejora> {
        val aventurero=aventureroService.recuperar(aventureroId)
        return claseDAO.posiblesMejoras(aventurero.getClases())
    }
    override fun caminoMasRentable(puntosDeExperiencia: Int, aventureroId: Long, atributo: Atributo): List<Mejora> {
        val aventurero = aventureroService.recuperar(aventureroId)
        return claseDAO.caminoMasRentable(aventurero, atributo,puntosDeExperiencia)
    }

}
