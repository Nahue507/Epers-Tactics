package test.hibernate.services

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.modelo.habilidades.*
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.*
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoDBFormacionDAO
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.impl.hibernate.AventureroLeaderboardServiceImpl
import ar.edu.unq.epers.tactics.service.impl.hibernate.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx
import helpers.DataService
import helpers.FIrebaseDataService
import helpers.HibernateDataService
import javassist.NotFoundException
import org.junit.Assert
import org.junit.jupiter.api.*
import javax.persistence.NoResultException

class HibernateAventureroLeaderboardServiceTest {

    private val aventureroDAO = HibernateAventureroDAO()
    private val partyDAO = HibernatePartyDAO()
    private val partyService: PartyServiceImpl = PartyServiceImpl(partyDAO, FirebaseLeaderBoardDAO(), MongoDBFormacionDAO())
    private val peleaDAO = HibernatePeleaDAO()
    private var firebaseDataService : FIrebaseDataService = FIrebaseDataService()
    private var dataService: DataService = HibernateDataService(partyService)
    private val aventureroLeaderboardService = AventureroLeaderboardServiceImpl(aventureroDAO)

    lateinit var pelea : Pelea
    lateinit var aventurero1: Aventurero
    lateinit var aventurero2: Aventurero
    lateinit var party: Party

    @BeforeEach
    fun beforeEach() {
        party = Party("Fiesta de Prueba", "URL")
        aventurero1 = Aventurero("Aventurero1", "https://imagen.url/img.jpg", 10, 10, 10, 10)
        aventurero2 = Aventurero("Aventurero2", "https://imagen.url/img.jpg", 20, 20, 20, 20)

        party = partyService.crear(party)
        runTrx{
            pelea = peleaDAO.crear(Pelea(party,"Enemigos"))
        }

    }

    @Test
    fun budaTestException(){
        Assertions.assertThrows(NoResultException::class.java) {
            aventureroLeaderboardService.buda()
        }
    }
    @Test
    fun mejorCuranderoTestException(){
        Assertions.assertThrows(NoResultException::class.java) {
            aventureroLeaderboardService.mejorCurandero()
        }
    }
    @Test
    fun mejorMagoTestException(){
        Assertions.assertThrows(NoResultException::class.java) {
            aventureroLeaderboardService.mejorMago()
        }
    }
    @Test
    fun mejorGuerreroTestException(){
        Assertions.assertThrows(NoResultException::class.java) {
            aventureroLeaderboardService.mejorGuerrero()
        }
    }

    @Test
    fun budaTest(){

        val meditar1 = Meditar(aventurero1)
        meditar1.emisor = aventurero1
        val meditar2 = Meditar(aventurero1)
        meditar2.emisor = aventurero1
        val meditar3 = Meditar(aventurero2)
        meditar3.emisor = aventurero2
        val meditar4 = Meditar(aventurero2)
        meditar4.emisor = aventurero2
        val meditar5 = Meditar(aventurero1)
        meditar5.emisor = aventurero1
        val meditar6 = Meditar(aventurero1)
        meditar6.emisor = aventurero1

        partyService.agregarAventureroAParty(party.id!!, aventurero1)
        partyService.agregarAventureroAParty(party.id!!, aventurero2)

        pelea.agregarHabilidadRecibida(meditar1)
        pelea.agregarHabilidadEjecutada(meditar2)
        pelea.agregarHabilidadRecibida(meditar3)
        pelea.agregarHabilidadRecibida(meditar4)

        pelea = runTrx {
            peleaDAO.actualizar(pelea)
            peleaDAO.recuperar(pelea.id!!)
        }

        Assert.assertEquals(aventureroLeaderboardService.buda().id, aventurero2.id)

        pelea.agregarHabilidadRecibida(meditar5)
        pelea.agregarHabilidadRecibida(meditar6)


        pelea = runTrx {
            peleaDAO.actualizar(pelea)
            peleaDAO.recuperar(pelea.id!!)
        }

        Assert.assertEquals(aventureroLeaderboardService.buda().id, aventurero1.id)

    }

    @Test
    fun curanderoTest(){

        val curar1 = Curar(aventurero2, aventurero1.getEstadisticaPoderMagico().toDouble())
        curar1.emisor = aventurero1
        val curar2 = Curar(aventurero2, aventurero1.getEstadisticaPoderMagico().toDouble())
        curar2.emisor = aventurero1
        val curar3 = Curar(aventurero1, aventurero2.getEstadisticaPoderMagico().toDouble())
        curar3.emisor = aventurero2

        partyService.agregarAventureroAParty(party.id!!, aventurero1)
        partyService.agregarAventureroAParty(party.id!!, aventurero2)

        pelea.agregarHabilidadRecibida(curar1)
        pelea.agregarHabilidadRecibida(curar3)

        pelea = runTrx {
            peleaDAO.actualizar(pelea)
            peleaDAO.recuperar(pelea.id!!)
        }

        Assert.assertEquals(aventureroLeaderboardService.mejorCurandero().id, aventurero2.id)

        pelea.agregarHabilidadRecibida(curar2)

        pelea = runTrx {
            peleaDAO.actualizar(pelea)
            peleaDAO.recuperar(pelea.id!!)
        }

        Assert.assertEquals(aventureroLeaderboardService.mejorCurandero().id, aventurero1.id)


    }

    @Test
    fun magoTest(){

        val ataqueMagico1 = AtaqueMagico(aventurero2, aventurero1.getEstadisticaPoderMagico().toDouble(),aventurero1.nivel)
        ataqueMagico1.emisor = aventurero1
        ataqueMagico1.acerto = true
        val ataqueMagico2 = AtaqueMagico(aventurero2, aventurero1.getEstadisticaPoderMagico().toDouble(),aventurero1.nivel)
        ataqueMagico2.emisor = aventurero1
        ataqueMagico2.acerto = true
        val ataqueMagico3 = AtaqueMagico(aventurero1, aventurero2.getEstadisticaPoderMagico().toDouble(),aventurero2.nivel)
        ataqueMagico3.emisor = aventurero2
        ataqueMagico3.acerto = true
        val ataqueMagico4 = AtaqueMagico(aventurero1, aventurero2.getEstadisticaPoderMagico().toDouble(),aventurero2.nivel)
        ataqueMagico4.emisor = aventurero2
        ataqueMagico4.acerto = false


        Assertions.assertThrows(NoResultException::class.java) {
            aventureroLeaderboardService.mejorMago()
        }

        partyService.agregarAventureroAParty(party.id!!, aventurero1)
        partyService.agregarAventureroAParty(party.id!!, aventurero2)

        pelea.agregarHabilidadRecibida(ataqueMagico1)
        pelea.agregarHabilidadRecibida(ataqueMagico3)

        pelea = runTrx {
            peleaDAO.actualizar(pelea)
            peleaDAO.recuperar(pelea.id!!)
        }

        Assert.assertEquals(aventureroLeaderboardService.mejorMago().id, aventurero2.id)

        pelea.agregarHabilidadRecibida(ataqueMagico2)

        pelea = runTrx {
            peleaDAO.actualizar(pelea)
            peleaDAO.recuperar(pelea.id!!)
        }

        Assert.assertEquals(aventureroLeaderboardService.mejorMago().id, aventurero1.id)

        pelea.agregarHabilidadRecibida(ataqueMagico4)

        pelea = runTrx {
            peleaDAO.actualizar(pelea)
            peleaDAO.recuperar(pelea.id!!)
        }

        Assert.assertEquals(aventureroLeaderboardService.mejorMago().id, aventurero1.id)



    }

    @Test
    fun guerreroTest(){

        val ataque1 = Ataque(aventurero2, aventurero1.getEstadisticaDamageFisico(), aventurero1.getEstadisticaPrecisionFisica())
        ataque1.emisor = aventurero1
        ataque1.acerto = true
        val ataque2 = Ataque(aventurero2, aventurero1.getEstadisticaDamageFisico(), aventurero1.getEstadisticaPrecisionFisica())
        ataque2.emisor = aventurero1
        ataque2.acerto = true
        val ataque3 = Ataque(aventurero1, aventurero2.getEstadisticaDamageFisico(), aventurero2.getEstadisticaPrecisionFisica())
        ataque3.emisor = aventurero2
        ataque3.acerto = true
        val ataque4 = Ataque(aventurero1, aventurero2.getEstadisticaDamageFisico(), aventurero2.getEstadisticaPrecisionFisica())
        ataque4.emisor = aventurero2
        ataque4.acerto = false


        Assertions.assertThrows(NoResultException::class.java) {
            aventureroLeaderboardService.mejorGuerrero()
        }

        partyService.agregarAventureroAParty(party.id!!, aventurero1)
        partyService.agregarAventureroAParty(party.id!!, aventurero2)

        pelea.agregarHabilidadRecibida(ataque1)
        pelea.agregarHabilidadRecibida(ataque3)

        pelea = runTrx {
            peleaDAO.actualizar(pelea)
            peleaDAO.recuperar(pelea.id!!)
        }

        Assert.assertEquals(aventureroLeaderboardService.mejorGuerrero().id, aventurero2.id)

        pelea.agregarHabilidadRecibida(ataque2)

        pelea = runTrx {
            peleaDAO.actualizar(pelea)
            peleaDAO.recuperar(pelea.id!!)
        }

        Assert.assertEquals(aventureroLeaderboardService.mejorGuerrero().id, aventurero1.id)

        pelea.agregarHabilidadRecibida(ataque4)

        pelea = runTrx {
            peleaDAO.actualizar(pelea)
            peleaDAO.recuperar(pelea.id!!)
        }

        Assert.assertEquals(aventureroLeaderboardService.mejorGuerrero().id, aventurero1.id)
    }



    @AfterEach
    fun afterEach() {
        dataService.eliminarTodo()
        firebaseDataService.eliminarTodo()
    }
}