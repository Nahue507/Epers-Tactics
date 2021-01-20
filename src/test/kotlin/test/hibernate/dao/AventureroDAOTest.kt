package test.hibernate.dao

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.habilidades.Ataque
import ar.edu.unq.epers.tactics.modelo.habilidades.AtaqueMagico
import ar.edu.unq.epers.tactics.modelo.habilidades.Curar
import ar.edu.unq.epers.tactics.persistencia.dao.LeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
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

class AventureroDAOTest {

    private val aventureroDAO = HibernateAventureroDAO()
    private val partyDAO = HibernatePartyDAO()
    private val dataService = HibernateDataService(PartyServiceImpl(partyDAO, FirebaseLeaderBoardDAO(), MongoDBFormacionDAO()))
    private val peleaDAO = HibernatePeleaDAO()

    lateinit var pelea : Pelea
    lateinit var party : Party
    val aventurero1 = Aventurero("Aventurero1", "URL", 10, 10, 10, 10)
    val aventurero2 = Aventurero("Aventurero2", "URL", 10, 10, 10, 10)
    val aventurero3 = Aventurero("Aventurero3", "URL", 10, 10, 10, 10)


    val ataque = Ataque(aventurero2, aventurero1.getEstadisticaDamageFisico(), aventurero2.getEstadisticaPrecisionFisica())
    val ataqueMagico = AtaqueMagico(aventurero2, aventurero1.getEstadisticaPoderMagico().toDouble(), aventurero1.nivel)
    val curar = Curar(aventurero2,aventurero2.getEstadisticaPoderMagico().toDouble())


    @BeforeEach
    fun beforeEach() {

        ataque.emisor = aventurero1
        ataqueMagico.emisor = aventurero1
        ataque.acerto = true
        curar.emisor = aventurero2
        runTrx {
            party = partyDAO.crear(Party("Party", "Url"))
            party.agregarAventurero(aventurero1)
            party.agregarAventurero(aventurero2)
            party.agregarAventurero(aventurero3)
            partyDAO.actualizar(party)
        }

        runTrx {
            pelea = peleaDAO.crear(Pelea(party, "Enemigos"))
            pelea.agregarHabilidadRecibida(ataque)
            pelea.agregarHabilidadEjecutada(ataqueMagico)
            pelea.agregarHabilidadRecibida(curar)
            peleaDAO.actualizar(pelea)
        }
    }

    @Test
    fun recuperarAventureroTest(){
        val aventurero = runTrx { aventureroDAO.recuperar(aventurero1.id!!) }
        Assert.assertEquals(aventurero.id, aventurero1.id)
        Assert.assertEquals(aventurero.party!!.id, aventurero1.party!!.id)
        Assert.assertEquals(aventurero.nivel, aventurero1.nivel)
        Assert.assertEquals(aventurero.nombre, aventurero1.nombre)

    }

    @Test
    fun actualizarAventureroTest(){
        aventurero1.nombre = "Nuevo nombre"
        aventurero1.nivel = 30

        val aventureroActualizado = runTrx {
            aventureroDAO.actualizar(aventurero1)
            aventureroDAO.recuperar(aventurero1.id!!)
        }

        Assert.assertEquals(aventureroActualizado.nombre, aventurero1.nombre)
        Assert.assertEquals(aventureroActualizado.nivel, aventurero1.nivel)

    }

    @Test
    fun eliminarAventureroTest(){

        runTrx{
            aventureroDAO.eliminar(aventurero3)
        }

        Assertions.assertThrows(NotFoundException::class.java){
            runTrx { aventureroDAO.recuperar(aventurero3.id!!)}
        }
    }


    @Test
    fun recuperarTodosTest(){
        var aventureros = runTrx{
             aventureroDAO.recuperarTodas()
        }

        Assert.assertTrue(aventureros.size == 3)

        dataService.crearSetDeDatosIniciales()

        aventureros = runTrx{
            aventureroDAO.recuperarTodas()
        }

        Assert.assertEquals(aventureros.size, 15)
    }

    @Test
    fun recuperarHabilidadesTest(){

        var habilidades = runTrx {
            aventureroDAO.recuperarHabilidades(aventurero3.id!!)
        }

        Assert.assertTrue(habilidades.isEmpty())

        habilidades = runTrx {
            aventureroDAO.recuperarHabilidades(aventurero1.id!!)
        }

        Assert.assertEquals(habilidades.size, 1)


    }
    @Test
    fun recuperarAventureroNoExistente(){
        Assertions.assertThrows(NotFoundException::class.java){
            runTrx { aventureroDAO.recuperar(-1)}
        }
    }

    @AfterEach
    fun afterEach() {
        dataService.eliminarTodo()
    }
}