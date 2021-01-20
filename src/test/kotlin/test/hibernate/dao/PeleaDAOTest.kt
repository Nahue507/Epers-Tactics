package test.hibernate.dao

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoDBFormacionDAO
import ar.edu.unq.epers.tactics.service.impl.hibernate.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx
import helpers.DataService
import helpers.HibernateDataService
import javassist.NotFoundException
import org.junit.Assert
import org.junit.jupiter.api.*

class PeleaDAOTest {

    private val peleaDAO = HibernatePeleaDAO()
    private val partyDAO = HibernatePartyDAO()
    private val dataService : DataService = HibernateDataService(PartyServiceImpl(partyDAO, FirebaseLeaderBoardDAO(), MongoDBFormacionDAO()))

    private val aventurero1 = Aventurero("Aventurero1", "URL", 10, 10, 10, 10)
    private val aventurero2 = Aventurero("Aventurero2", "URL", 10, 10, 10, 10)
    private val aventurero3 = Aventurero("Aventurero3", "URL", 10, 10, 10, 10)

    lateinit var party: Party

    @BeforeEach
    fun beforeEach(){

        runTrx {
            party = partyDAO.crear(Party("nombre_party", "url_imagen"))
            party.agregarAventurero(aventurero1)
            party.agregarAventurero(aventurero2)
            party.agregarAventurero(aventurero3)
            partyDAO.actualizar(party)
        }

    }

    @Test
    fun persistirYRecuperarPeleaConIdValidoTest(){
        val peleaPersistida = runTrx { peleaDAO.crear(Pelea(party, "nombre_party_enemiga")) }
        val peleaRecuperada = runTrx { peleaDAO.recuperar(peleaPersistida.id!!) }

        Assert.assertNotNull(peleaRecuperada)
        Assert.assertEquals(party.id, peleaRecuperada.getParty().id)
        Assert.assertEquals("nombre_party_enemiga", peleaRecuperada.getNombrePartyEnemiga())
        Assert.assertEquals(peleaPersistida.id, peleaRecuperada.id)

    }

    @Test
    fun recuperarPeleaConIdInvalidoExcepcionTest(){
        val peleaPersistida = runTrx { peleaDAO.crear(Pelea(party, "nombre_party_enemiga")) }
        Assertions.assertThrows(NotFoundException::class.java){
            runTrx { peleaDAO.recuperar(peleaPersistida.id!! + 1) }
        }
    }

    @Test
    fun actualizarPeleaEnSesionActualTest(){
        val peleaPersistida = runTrx { peleaDAO.crear(Pelea(party, "nombre")) }
        val peleaRecuperada = runTrx { peleaDAO.recuperar(peleaPersistida.id!!) }

        Assert.assertNotNull(peleaRecuperada.fecha)

        runTrx {
            peleaRecuperada.fecha = null
            peleaDAO.actualizar(peleaRecuperada)
        }

        Assert.assertNull(peleaRecuperada.fecha)
    }

    @Test
    fun recuperarPeleasPaginadasTest(){

        var paginasPelea = runTrx { peleaDAO.recuperarOrdenadas(party.id!!, 0) }
        Assert.assertNotNull(paginasPelea)
        Assert.assertEquals(0, paginasPelea.total)

        val pelea1 = runTrx { peleaDAO.crear(Pelea(party, "nombre_party_enemiga")) }
        val pelea2 = runTrx { peleaDAO.crear(Pelea(party, "nombre_party_enemiga")) }
        val pelea3 = runTrx { peleaDAO.crear(Pelea(party, "nombre_party_enemiga")) }

        paginasPelea = runTrx { peleaDAO.recuperarOrdenadas(party.id!!, 0) }

        Assert.assertEquals(3, paginasPelea.total)
        Assert.assertTrue(paginasPelea.peleas.any { it.id == pelea1.id })
        Assert.assertTrue(paginasPelea.peleas.any { it.id == pelea2.id })
        Assert.assertTrue(paginasPelea.peleas.any { it.id == pelea3.id })

    }

    @Test
    fun recuperarPeleasPaginadasPaginaInexistenteExcepcionTest(){
        Assertions.assertThrows(NotFoundException::class.java){
            runTrx { peleaDAO.recuperarOrdenadas(party.id!!, 100) }
        }
    }

    @AfterEach
    fun afterEach() {
        dataService.eliminarTodo()
    }

}