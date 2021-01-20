package test.hibernate.services

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoDBFormacionDAO
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.dto.Criterio
import ar.edu.unq.epers.tactics.service.dto.TipoDeEstadistica
import ar.edu.unq.epers.tactics.service.dto.TipoDeReceptor
import ar.edu.unq.epers.tactics.service.impl.hibernate.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.hibernate.PartyServiceImpl
import helpers.DataService
import helpers.FIrebaseDataService
import helpers.HibernateDataService
import javassist.NotFoundException
import org.junit.Assert
import org.junit.jupiter.api.*
import javax.validation.ConstraintViolationException

class HibernateAventureroServiceTest {
    private val partyService: PartyService = PartyServiceImpl(HibernatePartyDAO(), FirebaseLeaderBoardDAO(), MongoDBFormacionDAO())
    private val aventureroService: AventureroService =  AventureroServiceImpl(HibernateAventureroDAO(), HibernatePartyDAO(), FirebaseLeaderBoardDAO(), MongoDBFormacionDAO())
    private val firebaseDataService : FIrebaseDataService = FIrebaseDataService()
    private val dataService: DataService= HibernateDataService(partyService)
    lateinit var aventurero: Aventurero
    lateinit var party: Party


    @BeforeEach
    fun beforeEach() {
        party = Party("Fiesta de Prueba", "URL")
        aventurero = Aventurero("Nombre de Aventurero", "https://imagen.url/img.jpg", 20, 30, 10, 10)

        party = partyService.crear(party)
        aventurero = partyService.agregarAventureroAParty(party.id!!, aventurero)
    }

    @Test
    fun recuperarAventurero() {
        party = partyService.recuperar(party.id!!)
        Assert.assertEquals(party.aventureros.size, 1)
        Assert.assertEquals(party.aventureros[0].id, aventurero.id)
        Assert.assertEquals(party.aventureros[0].nombre, aventurero.nombre)

        aventurero = aventureroService.recuperar(aventurero.id!!)
        Assert.assertEquals(aventurero.nombre, "Nombre de Aventurero")
        Assert.assertEquals(aventurero.party?.id, party.id)
        Assert.assertEquals(aventurero.party?.nombre, party.nombre)
        Assert.assertEquals(aventurero.imagenUrl, "https://imagen.url/img.jpg")

        Assert.assertEquals(aventurero.getFuerza(), 20)
        Assert.assertEquals(aventurero.getDestreza(), 30)
        Assert.assertEquals(aventurero.getConstitucion(), 10)
        Assert.assertEquals(aventurero.getInteligencia(), 10)

        Assert.assertEquals(aventurero.getEstadisticaVida(), 45)
        Assert.assertEquals(aventurero.getEstadisticaArmadura(), 11)
        Assert.assertEquals(aventurero.getEstadisticaDamageFisico(), 36)
        Assert.assertEquals(aventurero.getEstadisticaMana(), 11)
        Assert.assertEquals(aventurero.getEstadisticaPoderMagico(), 11)
        Assert.assertEquals(aventurero.getEstadisticaPrecisionFisica(), 51)
        Assert.assertEquals(aventurero.getEstadisticaVelocidad(), 31)
        Assert.assertEquals(aventurero.getVidaActual(), 45)
        Assert.assertEquals(aventurero.getManaActual(), 11)
    }

    @Test
    fun recuperarAventureroInexistente(){
        Assertions.assertThrows(NotFoundException::class.java){
            aventureroService.recuperar(-1)
        }
    }

    @Test
    fun actualizarAventurero() {
        aventurero = aventureroService.recuperar(aventurero.id!!)
        aventurero.nombre = "Nombre actualizado"
        aventurero.nivel = 5
        aventurero.setFuerza(35)
        aventurero.setDestreza(60)
        aventurero.setConstitucion(40)
        aventurero.setInteligencia(20)

        aventurero = aventureroService.actualizar(aventurero)
        aventurero = aventureroService.recuperar(aventurero.id!!)
        Assert.assertEquals(aventurero.nombre, "Nombre actualizado")
        Assert.assertEquals(aventurero.nivel, 5)
        Assert.assertEquals(aventurero.getFuerza(), 35)
        Assert.assertEquals(aventurero.getDestreza(), 60)
        Assert.assertEquals(aventurero.getConstitucion(), 40)
        Assert.assertEquals(aventurero.getInteligencia(), 20)
    }

    @Test
    fun eliminarAventurero() {
        aventurero = aventureroService.recuperar(aventurero.id!!)
        aventureroService.eliminar(aventurero)

        Assertions.assertThrows(NotFoundException::class.java) {
            aventureroService.recuperar(aventurero.id!!)
        }
    }

    @Test
    fun aventureroNoPuedeRecibirUnAtributoInvalido() {
        aventurero = aventureroService.recuperar(aventurero.id!!)
        aventurero.setFuerza(120)
        aventurero.setDestreza(0)
        aventurero.setConstitucion(111)
        aventurero.setInteligencia(-10)
        Assertions.assertThrows(ConstraintViolationException::class.java) {
            aventureroService.actualizar(aventurero)
        }
        aventurero = aventureroService.recuperar(aventurero.id!!)
        Assert.assertEquals(aventurero.getFuerza(), 20)
        Assert.assertEquals(aventurero.getDestreza(), 30)
        Assert.assertEquals(aventurero.getConstitucion(), 10)
        Assert.assertEquals(aventurero.getInteligencia(), 10)
    }

    @Test
    fun aventureroTieneTacticas() {
        aventurero = aventureroService.recuperar(aventurero.id!!)
        var tacticaAgresiva = Tactica(aventurero, 1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 20, Accion.ATAQUE_FISICO)
        var tacticaMagica = Tactica(aventurero, 2, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.DAÑO_MAGICO, Criterio.MAYOR_QUE, 50, Accion.ATAQUE_MAGICO)
        var tacticaDefensiva = Tactica(aventurero, 3, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 15, Accion.MEDITAR)
        var tacticaCurativa = Tactica(aventurero, 4, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 20, Accion.CURAR)
        aventurero.agregarTactica(tacticaAgresiva)
        aventurero.agregarTactica(tacticaMagica)
        aventurero.agregarTactica(tacticaDefensiva)
        aventurero.agregarTactica(tacticaCurativa)

        aventurero = aventureroService.actualizar(aventurero)

        Assert.assertEquals(aventurero.tacticas.size, 4)
        Assert.assertEquals(aventurero.tacticas[0].prioridad, 1)
        Assert.assertEquals(aventurero.tacticas[0].receptor, TipoDeReceptor.ENEMIGO)
        Assert.assertEquals(aventurero.tacticas[1].tipoDeEstadistica, TipoDeEstadistica.DAÑO_MAGICO)
        Assert.assertEquals(aventurero.tacticas[1].criterio, Criterio.MAYOR_QUE)
        Assert.assertEquals(aventurero.tacticas[2].tipoDeEstadistica, TipoDeEstadistica.VIDA)
        Assert.assertEquals(aventurero.tacticas[2].accion, Accion.MEDITAR)
        Assert.assertEquals(aventurero.tacticas[3].valor, 20)
        Assert.assertEquals(aventurero.tacticas[3].accion, Accion.CURAR)
    }

    @AfterEach
    fun afterEach() {
        dataService.eliminarTodo()
        firebaseDataService.eliminarTodo()
    }
}