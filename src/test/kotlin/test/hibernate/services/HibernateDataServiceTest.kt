package test.hibernate.services

import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoDBFormacionDAO
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.impl.hibernate.PartyServiceImpl
import helpers.DataService
import helpers.FIrebaseDataService
import helpers.HibernateDataService
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

class HibernateDataServiceTest {
    private var partyService: PartyService  = PartyServiceImpl(HibernatePartyDAO(), FirebaseLeaderBoardDAO(), MongoDBFormacionDAO())
    private var dataService: DataService= HibernateDataService(partyService)
    private var firebaseDataService : FIrebaseDataService = FIrebaseDataService()

    @Test
    fun seCreanSetDeDatosIniciales() {
        dataService.crearSetDeDatosIniciales()
        var parties = partyService.recuperarTodas()
        parties = parties.filter { it.nombre == "Death Squadron" || it.nombre == "Unicorns of Love"
                                || it.nombre == "Global Guardians" || it.nombre == "Space Alliance" }
        Assert.assertEquals(parties.size, 4)
        (0..3).forEach{
            Assert.assertEquals(parties[it].aventureros.size, 3)
            Assert.assertEquals(parties[it].aventureros[0].nombre, "Mage from ${parties[it].nombre}")
            Assert.assertEquals(parties[it].aventureros[0].tacticas.size, 4)
            Assert.assertEquals(parties[it].aventureros[1].nombre, "Archer from ${parties[it].nombre}")
            Assert.assertEquals(parties[it].aventureros[1].tacticas.size, 4)
            Assert.assertEquals(parties[it].aventureros[2].nombre, "Assassin from ${parties[it].nombre}")
            Assert.assertEquals(parties[it].aventureros[2].tacticas.size, 4)
        }

        dataService.eliminarTodo()
        firebaseDataService.eliminarTodo()
        parties = partyService.recuperarTodas()
        Assert.assertEquals(parties.size, 0)
    }

    @Test
    fun seCreanDatosYLuegoSeEliminaTodo() {
        dataService.crearSetDeDatosIniciales()
        var parties = partyService.recuperarTodas()
        Assert.assertEquals(parties.size, 4)

        dataService.eliminarTodo()
        firebaseDataService.eliminarTodo()
        parties = partyService.recuperarTodas()
        Assert.assertEquals(parties.size, 0)
    }

}