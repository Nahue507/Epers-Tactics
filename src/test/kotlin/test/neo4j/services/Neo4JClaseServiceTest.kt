package test.neo4j.services

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.excepciones.MejoraInexistente
import ar.edu.unq.epers.tactics.modelo.excepciones.NoPuedeGanarProficienciaException
import ar.edu.unq.epers.tactics.modelo.excepciones.PuntosDeExperienciaInsuficientesException
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoDBFormacionDAO
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.ClaseService
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.dto.Atributo
import ar.edu.unq.epers.tactics.service.impl.hibernate.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.hibernate.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.impl.neo4j.ClaseServiceImpl
import helpers.DataService
import helpers.FIrebaseDataService
import helpers.HibernateDataService
import helpers.Neo4JDataService
import org.junit.Assert
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Neo4JClaseServiceTest {

    private val claseService: ClaseService = ClaseServiceImpl()
    private val dataServiceNeo4J: DataService = Neo4JDataService()
    private var partyService: PartyService = PartyServiceImpl(HibernatePartyDAO(), FirebaseLeaderBoardDAO(), MongoDBFormacionDAO())
    private var aventureroService: AventureroService = AventureroServiceImpl(HibernateAventureroDAO(), HibernatePartyDAO(), FirebaseLeaderBoardDAO(), MongoDBFormacionDAO())
    private var dataServiceHibernate: DataService = HibernateDataService(partyService)
    private var firebaseDataService : FIrebaseDataService = FIrebaseDataService()

    lateinit var aventurero: Aventurero
    lateinit var party: Party


    @BeforeAll
    fun beforeAll() {
        dataServiceNeo4J.crearSetDeDatosIniciales()

    }

    @BeforeEach
    fun beforeEach() {
        party = Party("Party de Prueba", "URL")
        aventurero = Aventurero("Nombre de Aventurero", "https://imagen.url/img.jpg", 20, 20, 20, 20)

        party = partyService.crear(party)
        aventurero = partyService.agregarAventureroAParty(party.id!!, aventurero)
    }

    @Test
    fun aventureroDeClaseAventureroObtieneClaseMagicoYPuedeMejorarATresClases() {
        Assertions.assertThrows(MejoraInexistente::class.java){
            claseService.puedeMejorar(aventurero.id!!, Mejora("Magico", "Aventurero", listOf(Atributo.INTELIGENCIA), 2))
        }
        Assert.assertEquals("Aventurero", aventurero.getClases()[0])
        Assert.assertTrue(claseService.puedeMejorar(aventurero.id!!, Mejora("Aventurero", "Magico", listOf(Atributo.INTELIGENCIA), 2)))
        Assert.assertTrue(claseService.puedeMejorar(aventurero.id!!, Mejora("Aventurero", "Fisico", listOf(Atributo.FUERZA), 2)))

        aventurero.subirDeNivel()
        aventurero.ganarProficiencia(Mejora("Aventurero", "Magico", listOf(Atributo.INTELIGENCIA), 2))
        aventurero = aventureroService.actualizar(aventurero)

        Assertions.assertThrows(MejoraInexistente::class.java){
            claseService.puedeMejorar(aventurero.id!!, Mejora("Magico", "Aventurero", listOf(Atributo.INTELIGENCIA), 2))
        }
        Assert.assertTrue(claseService.puedeMejorar(aventurero.id!!, Mejora("Aventurero", "Fisico", listOf(Atributo.FUERZA), 2)))
        Assert.assertTrue(claseService.puedeMejorar(aventurero.id!!, Mejora("Magico", "Clerigo", listOf(Atributo.DESTREZA, Atributo.INTELIGENCIA), 2)))
        Assert.assertTrue(claseService.puedeMejorar(aventurero.id!!, Mejora("Magico", "Mago", listOf(Atributo.INTELIGENCIA), 4)))
    }

    @Test
    fun aventureroConClaseAventureroPuedeGanarProficienciaAMagico() {
        aventurero.subirDeNivel()
        aventurero = aventureroService.actualizar(aventurero)
        claseService.ganarProficiencia(aventurero.id!!, "Aventurero", "Magico")

        aventurero = aventureroService.recuperar(aventurero.id!!)
        Assert.assertEquals(2, aventurero.getClases().size)
        Assert.assertEquals("Aventurero", aventurero.getClases()[0])
        Assert.assertEquals("Magico", aventurero.getClases()[1])
    }

    @Test
    fun aventureroSinPuntosDeExperienciaNoPuedeSubirDeClase() {
        aventurero = aventureroService.recuperar(aventurero.id!!)
        Assert.assertEquals(0, aventurero.puntosDeExperiencia)
        assertThrows(PuntosDeExperienciaInsuficientesException::class.java){
            aventurero.ganarProficiencia(Mejora("Aventurero", "Magico", listOf(Atributo.INTELIGENCIA), 2))
        }
    }

    @Test
    fun aventureroNoPuedeObtenerUnaClaseSiNoTieneLaClaseQueLaHabilita() {
        aventurero.subirDeNivel()
        aventurero = aventureroService.actualizar(aventurero)
        Assert.assertEquals(1, aventurero.puntosDeExperiencia)
        assertThrows(NoPuedeGanarProficienciaException::class.java){
            claseService.ganarProficiencia(aventurero.id!!, "Fisico", "Clerigo")
        }
    }

    @Test
    fun aventureroNoPuedeTenerUnaMejoraQueNoExiste() {
        aventurero.subirDeNivel()
        aventurero = aventureroService.actualizar(aventurero)
        Assert.assertEquals(1, aventurero.puntosDeExperiencia)
        assertThrows(NoPuedeGanarProficienciaException::class.java){
            claseService.ganarProficiencia(aventurero.id!!, "Aventurero", "Clerigo")
        }
    }

    @Test
    fun aventureroBasicoPuedeVerSusPosiblesMejoras() {
        var mejoras = claseService.posiblesMejoras(aventurero.id!!)
        Assert.assertEquals(2, mejoras.size)
        var nuevasMejoras = mejoras.toList()

        Assert.assertEquals("Aventurero", nuevasMejoras[0].claseInicio)
        Assert.assertEquals("Fisico", nuevasMejoras[0].claseFinal)
        Assert.assertEquals(Atributo.FUERZA, nuevasMejoras[0].atributos[0])
        Assert.assertEquals(2, nuevasMejoras[0].cantidadDeAtributos)

        Assert.assertEquals("Aventurero", nuevasMejoras[1].claseInicio)
        Assert.assertEquals("Magico", nuevasMejoras[1].claseFinal)
        Assert.assertEquals(Atributo.INTELIGENCIA, nuevasMejoras[1].atributos[0])
        Assert.assertEquals(2, nuevasMejoras[1].cantidadDeAtributos)
    }

    @Test
    fun aventureroConVariasClasesPuedeVerSusPosiblesMejoras() {
        var mejoras = claseService.posiblesMejoras(aventurero.id!!)
        mejoras.forEach{
            aventurero.subirDeNivel()
            aventurero.ganarProficiencia(it)
        }
        aventurero = aventureroService.actualizar(aventurero)

        mejoras = claseService.posiblesMejoras(aventurero.id!!)
        Assert.assertEquals(4, mejoras.size)
        val nuevasMejoras = mejoras.toList()

        Assert.assertEquals("Magico", nuevasMejoras[0].claseInicio)
        Assert.assertEquals("Clerigo", nuevasMejoras[0].claseFinal)

        Assert.assertEquals("Magico", nuevasMejoras[1].claseInicio)
        Assert.assertEquals("Mago", nuevasMejoras[1].claseFinal)

        Assert.assertEquals("Fisico", nuevasMejoras[2].claseInicio)
        Assert.assertEquals("Guerrero", nuevasMejoras[2].claseFinal)

        Assert.assertEquals("Fisico", nuevasMejoras[3].claseInicio)
        Assert.assertEquals("Clerigo", nuevasMejoras[3].claseFinal)
    }

    @Test
    fun aventureroConClaseAventureroSubeAClaseMagico() {
        aventurero.subirDeNivel()
        aventurero = aventureroService.actualizar(aventurero)
        Assert.assertEquals(1, aventurero.getClases().size)
        Assert.assertEquals(20, aventurero.getInteligencia())
        Assert.assertEquals(2, aventurero.nivel)
        Assert.assertEquals(1, aventurero.puntosDeExperiencia)

        aventurero = claseService.ganarProficiencia(aventurero.id!!, "Aventurero", "Magico")
        Assert.assertEquals(2, aventurero.getClases().size)
        Assert.assertEquals(22, aventurero.getInteligencia())
        Assert.assertEquals(0, aventurero.puntosDeExperiencia)
    }

    @Test
    fun obtenerCaminoMasRentableDeUnAventureroConPreferenciaAFuerza() {
        var mejorasFuerza = claseService.caminoMasRentable(6, aventurero.id!!, Atributo.FUERZA)
        Assert.assertEquals(4, mejorasFuerza.size)
        Assert.assertEquals("Fisico", mejorasFuerza[0].claseFinal)
        Assert.assertEquals("Guerrero", mejorasFuerza[1].claseFinal)
        Assert.assertEquals("Maestro de armas", mejorasFuerza[2].claseFinal)
        Assert.assertEquals("Caballero", mejorasFuerza[3].claseFinal)
    }

    @Test
    fun obtenerCaminoMasRentableDeUnAventureroConPreferenciaAInteligencia() {
        aventurero = aventureroService.recuperar(aventurero.id!!)
        Assert.assertEquals("Aventurero", aventurero.getClases()[0])
        var mejorasInteligencia = claseService.caminoMasRentable(6, aventurero.id!!, Atributo.INTELIGENCIA)
        Assert.assertEquals(6, mejorasInteligencia.size)
        Assert.assertEquals("Magico", mejorasInteligencia[0].claseFinal)
        Assert.assertEquals("Clerigo", mejorasInteligencia[1].claseFinal)
        Assert.assertEquals("Mago", mejorasInteligencia[2].claseFinal)
        Assert.assertEquals("Brujo", mejorasInteligencia[3].claseFinal)
        Assert.assertEquals("Hechizero", mejorasInteligencia[4].claseFinal)
        Assert.assertEquals("Invocador", mejorasInteligencia[5].claseFinal)
    }

    @Test
    fun obtenerCaminoMasRentableDeUnAventureroConPreferenciaADestreza() {
        aventurero = aventureroService.recuperar(aventurero.id!!)
        Assert.assertEquals("Aventurero", aventurero.getClases()[0])
        var mejorasDestreza = claseService.caminoMasRentable(4, aventurero.id!!, Atributo.DESTREZA)
        Assert.assertEquals(4, mejorasDestreza.size)
        Assert.assertEquals("Fisico", mejorasDestreza[0].claseFinal)
        Assert.assertEquals("Clerigo", mejorasDestreza[1].claseFinal)
        Assert.assertEquals("Brujo", mejorasDestreza[2].claseFinal)
        Assert.assertEquals("Caballero de la muerte", mejorasDestreza[3].claseFinal)
    }

    @Test
    fun obtenerCaminoMasRentableEnFuerzaDeUnAventureroConMuchasClases() {
        aventurero.addClase("Fisico")
        aventurero.addClase("Magico")
        aventurero.addClase("Guerrero")
        aventurero.addClase("Maestro de armas")
        aventurero.addClase("Clerigo")
        aventurero.addClase("Paladin")
        aventurero.addClase("Caballero")
        aventurero.addClase("Brujo")
        aventureroService.actualizar(aventurero)
        var mejorasInteligencia = claseService.caminoMasRentable(8, aventurero.id!!, Atributo.INTELIGENCIA)
        Assert.assertEquals(3, mejorasInteligencia.size)
        Assert.assertEquals("Mago", mejorasInteligencia[0].claseFinal)
        Assert.assertEquals("Hechizero", mejorasInteligencia[1].claseFinal)
        Assert.assertEquals("Invocador", mejorasInteligencia[2].claseFinal)
    }

    @Test
    fun obtenerCaminoMasRentableEnDestrezaDeUnAventureroConMuchasClases() {
        aventurero.addClase("Fisico")
        aventurero.addClase("Guerrero")
        aventurero.addClase("Maestro de armas")
        aventurero.addClase("Clerigo")
        aventurero.addClase("Paladin")
        aventurero.addClase("Caballero")
        aventurero.addClase("Brujo")
        aventureroService.actualizar(aventurero)
        var mejorasDestreza = claseService.caminoMasRentable(8, aventurero.id!!, Atributo.DESTREZA)
        Assert.assertEquals(1, mejorasDestreza.size)
        Assert.assertEquals("Caballero de la muerte", mejorasDestreza[0].claseFinal)
    }

    @AfterEach
    fun afterEach(){
        dataServiceHibernate.eliminarTodo()
        firebaseDataService.eliminarTodo()
    }

    @AfterAll
    fun afterAll() {
        dataServiceNeo4J.eliminarTodo()
    }

}