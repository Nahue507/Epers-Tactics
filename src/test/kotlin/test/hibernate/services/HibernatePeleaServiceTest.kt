package test.hibernate.services

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.modelo.habilidades.Ataque
import ar.edu.unq.epers.tactics.modelo.habilidades.AtaqueMagico
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoDBFormacionDAO
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.PeleaService
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.dto.Criterio
import ar.edu.unq.epers.tactics.service.dto.TipoDeEstadistica
import ar.edu.unq.epers.tactics.service.dto.TipoDeReceptor
import ar.edu.unq.epers.tactics.service.impl.hibernate.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.impl.hibernate.PeleaServiceImpl
import helpers.DataService
import helpers.FIrebaseDataService
import helpers.HibernateDataService
import javassist.NotFoundException
import org.junit.Assert
import org.junit.jupiter.api.*

class HibernatePeleaServiceTest {
    private var peleaService: PeleaService = PeleaServiceImpl(HibernatePeleaDAO(), HibernatePartyDAO(), HibernateAventureroDAO(), FirebaseLeaderBoardDAO())
    private var partyService: PartyService = PartyServiceImpl(HibernatePartyDAO(), FirebaseLeaderBoardDAO(), MongoDBFormacionDAO())
    private var firebaseDataService : FIrebaseDataService = FIrebaseDataService()
    private var dataService: DataService = HibernateDataService(partyService)
    private lateinit var party1 : Party
    private lateinit var party2 : Party

    private val aventurero1 : Aventurero = Aventurero("aventurero1", "URL", 10, 10, 10,10)
    private var aventurero2 : Aventurero = Aventurero("aventurero2", "URL", 1, 1, 1 ,1, 2)
    lateinit var pelea: Pelea


    @BeforeEach
    fun beforeEach() {
        party1 = partyService.crear(Party("Party", "URL"))
        party2 = partyService.crear(Party("Fiesta", "URL"))
        aventurero1.tacticas = listOf(Tactica(aventurero1, 1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 10 ,Accion.ATAQUE_MAGICO), Tactica(aventurero1,2, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 10, Accion.CURAR))
        partyService.agregarAventureroAParty(party1.id!!, aventurero1)
        partyService.agregarAventureroAParty(party2.id!!, aventurero2)
        pelea = peleaService.iniciarPelea(party1.id!!, "Nombre")
    }

    @Test
    fun seCreaUnaPeleaYLuegoCorroboramosSiEstaActiva() {
        var pelea = peleaService.iniciarPelea(party1.id!!, "Nombre")
        var party = partyService.recuperar(party1.id!!)

        Assert.assertEquals(pelea.id, party.pelea?.id)
        Assert.assertEquals(pelea.getParty().id, party.id)
        Assert.assertTrue(party.getEstaPeleando())
        Assert.assertTrue(peleaService.estaEnPelea(party.id!!))
    }

    @Test
    fun iniciarPeleaPartyInexistente(){
        Assertions.assertThrows(NotFoundException::class.java){
            peleaService.iniciarPelea(-1, "Enemigos")
        }
    }

    @Test
    fun estaPeleandoPartyInexistente(){
        Assertions.assertThrows(NotFoundException::class.java){
            peleaService.estaEnPelea(-1)
        }
    }

    @Test
    fun despuesDeTerminarUnaPeleaYaNoEstaActiva() {
        pelea = peleaService.terminarPelea(pelea.id!!)
        var party = partyService.recuperar(party1.id!!)

        Assert.assertFalse(peleaService.estaEnPelea(party.id!!))
        Assert.assertEquals(null, party.pelea)
        Assert.assertFalse(party.getEstaPeleando())
    }

    @Test
    fun terminarPeleaInexistente(){
        Assertions.assertThrows(NotFoundException::class.java){
            peleaService.terminarPelea(-1)
        }
    }

    @Test
    fun resolverTurnoTest(){
        val habilidad = peleaService.resolverTurno(pelea.id!!, aventurero1.id!!, listOf(aventurero2))

        Assert.assertTrue(habilidad is AtaqueMagico)

        habilidad as AtaqueMagico

        Assert.assertEquals(habilidad.level, aventurero1.nivel)
        Assert.assertEquals(habilidad.poderMagico.toInt(), aventurero1.getEstadisticaPoderMagico())

    }

    @Test
    fun resolverTurnoPeleaInexistente(){
        Assertions.assertThrows(NotFoundException::class.java){
            peleaService.resolverTurno(-1, aventurero1.id!!, listOf())
        }
    }

    @Test
    fun resolverTurnoAventureroInexistente(){
        Assertions.assertThrows(NotFoundException::class.java){
            peleaService.resolverTurno(pelea.id!!, -1, listOf())
        }
    }

    @Test
    fun recibirHabilidadTest(){

        val habilidad = AtaqueMagico(aventurero2, aventurero1.getEstadisticaPoderMagico().toDouble(), aventurero1.nivel)

        habilidad.randomizador.randomNumber = 10

        val vida = aventurero2.getVidaActual()

        aventurero2 = peleaService.recibirHabilidad(pelea.id!!, aventurero2.id!!, habilidad)

        Assert.assertEquals(aventurero2.getVidaActual(), vida - aventurero1.getEstadisticaPoderMagico())
    }

    @Test
    fun recibirHabilidadPeleaInexistente(){
        Assertions.assertThrows(NotFoundException::class.java){
            peleaService.recibirHabilidad(-1, aventurero1.id!!, Ataque(aventurero2, 10, 10))
        }
    }

    @Test
    fun recibirHabilidadAventureroInexistente(){
        Assertions.assertThrows(NotFoundException::class.java){
            peleaService.recibirHabilidad(pelea.id!!, -1, Ataque(aventurero2, 10, 10))
        }
    }


    @Test
    fun recuperarOrdenadasRecibeLasPeleasDeADiezPorOrdenDeFecha() {
        val party = partyService.crear(Party("Nombre de Party", "URL"))
        (1..20).forEach {
            peleaService.iniciarPelea(party.id!!, "Enemigo$it")
        }

        val peleasPaginadas1 = peleaService.recuperarOrdenadas(party.id!!, 0)
        Assert.assertEquals(peleasPaginadas1.peleas.size, 10)
        Assert.assertEquals(peleasPaginadas1.total, 20)
        var cont1 = 20
        (0..9).forEach {
            Assert.assertEquals(peleasPaginadas1.peleas[it].getNombrePartyEnemiga(), "Enemigo${cont1}")
            cont1 --
        }

        val peleasPaginadas2 = peleaService.recuperarOrdenadas(party.id!!, 1)
        Assert.assertEquals(peleasPaginadas2.peleas.size, 10)
        Assert.assertEquals(peleasPaginadas2.total, 20)
        var cont2 = 10
        (0..9).forEach {
            Assert.assertEquals(peleasPaginadas2.peleas[it].getNombrePartyEnemiga(), "Enemigo${cont2}")
            cont2 --
        }

        Assertions.assertThrows(NotFoundException::class.java) {
            peleaService.recuperarOrdenadas(party.id!!, 4)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            peleaService.recuperarOrdenadas(party.id!!, -2)
        }
    }

    @Test
    fun despuesDeGanarUnaPeleaLaPartySumaUnaVictoriaYSusAventurerosSubenDeNivel() {
        pelea = peleaService.terminarPelea(pelea.id!!)
        var party = partyService.recuperar(party1.id!!)

        Assert.assertFalse(peleaService.estaEnPelea(party.id!!))
        Assert.assertEquals(1, party.peleasGanadas)
        Assert.assertEquals(0, party.peleasPerdidas)
        (party.aventureros).forEach {
            Assert.assertEquals(2, it.nivel)
            Assert.assertEquals(1, it.puntosDeExperiencia)
        }
    }

    @Test
    fun despuesDePerderUnaPeleaLaPartySumaUnaDerrota() {
        var party = partyService.recuperar(party1.id!!)
        (party.aventureros).forEach {
            it.restarVida(200)
        }
        partyService.actualizar(party)
        peleaService.terminarPelea(pelea.id!!)
        party = partyService.recuperar(party1.id!!)

        Assert.assertFalse(peleaService.estaEnPelea(party.id!!))
        Assert.assertEquals(1, party.peleasPerdidas)
        Assert.assertEquals(0, party.peleasGanadas)
        (party.aventureros).forEach {
            Assert.assertEquals(1, it.nivel)
            Assert.assertEquals(0, it.puntosDeExperiencia)
        }
    }

    @AfterEach
    fun afterEach() {
        dataService.eliminarTodo()
        firebaseDataService.eliminarTodo()
    }
}