package test.neo4j.dao

import ar.edu.unq.epers.tactics.modelo.excepciones.RequerimientoDuplicadoException
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4j.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.dto.Atributo
import ar.edu.unq.epers.tactics.service.impl.hibernate.PartyServiceImpl
import helpers.DataService
import helpers.FIrebaseDataService
import helpers.Neo4JDataService
import org.junit.Assert
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClaseDAOTest {

    private val dataServiceNeo4J: DataService = Neo4JDataService()
    private val claseDAO: ClaseDAO = Neo4JClaseDAO()
    private var firebaseDataService : FIrebaseDataService = FIrebaseDataService()

    @BeforeAll
    fun beforeAll() {
        dataServiceNeo4J.crearSetDeDatosIniciales()
    }

    @Test
    fun crearClaseYRelacionarlaConOtra() {
        claseDAO.crearClase("Asesino")
        claseDAO.crearClase("Picaro")
        claseDAO.crearClase("Necromancer")
        claseDAO.crearMejora("Asesino", "Picaro", listOf(Atributo.DESTREZA), 4)
        claseDAO.requerir("Asesino", "Necromancer")

        Assert.assertEquals(1, claseDAO.obtenerMejoras(listOf("Asesino")).size)
        Assert.assertEquals(1, claseDAO.obtenerRequeridos("Asesino").size)
    }

    @Test
    fun crearClaseYRelacionarlaConOtraMasDeUnaVezLanzaUnaExcepcion(){
        claseDAO.crearClase("Cliente")
        claseDAO.crearClase("Servidor")
        Assertions.assertThrows(RequerimientoDuplicadoException::class.java){
            claseDAO.requerir("Cliente", "Servidor")
            claseDAO.requerir("Cliente", "Servidor")
        }
    }

    @Test
    fun obtenerMejorasDeClaseAventurero() {
        var mejoras = claseDAO.obtenerMejoras(listOf("Aventurero"))
        Assert.assertEquals(2, mejoras.size)
        var nuevasMejoras = mejoras.toList()
        Assert.assertEquals("Aventurero", nuevasMejoras[0].claseInicio)
        Assert.assertEquals("Aventurero", nuevasMejoras[1].claseInicio)
        Assert.assertEquals("Fisico", nuevasMejoras[0].claseFinal)
        Assert.assertEquals("Magico", nuevasMejoras[1].claseFinal)
        Assert.assertEquals(Atributo.FUERZA, nuevasMejoras[0].atributos[0])
        Assert.assertEquals(Atributo.INTELIGENCIA, nuevasMejoras[1].atributos[0])
        Assert.assertEquals(2, nuevasMejoras[0].cantidadDeAtributos)
        Assert.assertEquals(2, nuevasMejoras[1].cantidadDeAtributos)
    }

    @Test
    fun obtenerMejorasDeClaseMagico() {
        var mejoras = claseDAO.obtenerMejoras(listOf("Magico"))
        Assert.assertEquals(3, mejoras.size)
        var nuevasMejoras = mejoras.toList()
        Assert.assertEquals("Magico", nuevasMejoras[0].claseInicio)
        Assert.assertEquals("Magico", nuevasMejoras[1].claseInicio)
        Assert.assertEquals("Magico", nuevasMejoras[2].claseInicio)
        Assert.assertEquals("Clerigo", nuevasMejoras[0].claseFinal)
        Assert.assertEquals("Mago", nuevasMejoras[1].claseFinal)
        Assert.assertEquals("Fisico", nuevasMejoras[2].claseFinal)
        Assert.assertEquals(2, nuevasMejoras[0].atributos.size)
        Assert.assertEquals(1, nuevasMejoras[1].atributos.size)
        Assert.assertEquals(1, nuevasMejoras[2].atributos.size)
        Assert.assertEquals(Atributo.DESTREZA, nuevasMejoras[0].atributos[0])
        Assert.assertEquals(Atributo.INTELIGENCIA, nuevasMejoras[0].atributos[1])
        Assert.assertEquals(Atributo.INTELIGENCIA, nuevasMejoras[1].atributos[0])
        Assert.assertEquals(Atributo.CONSTITUCION, nuevasMejoras[2].atributos[0])
        Assert.assertEquals(2, nuevasMejoras[0].cantidadDeAtributos)
        Assert.assertEquals(4, nuevasMejoras[1].cantidadDeAtributos)
        Assert.assertEquals(1, nuevasMejoras[2].cantidadDeAtributos)
    }

    @Test
    fun obtenerRequeridosDeVariasClases() {
        var requeridos = claseDAO.obtenerRequeridos("Paladin")
        Assert.assertEquals(2, requeridos.size)
        Assert.assertEquals("Guerrero", requeridos[0])
        Assert.assertEquals("Clerigo", requeridos[1])
        requeridos = claseDAO.obtenerRequeridos("Aventurero")
        Assert.assertEquals(0, requeridos.size)
    }

    @Test
    fun aventureroBasicoPuedeVerSusPosiblesMejoras() {
        var mejoras = claseDAO.posiblesMejoras(listOf("Aventurero"))
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
    fun aventureroSinClaseClerigoNoPuedeObtenerProficienciaAPaladin() {
        var mejoras = claseDAO.posiblesMejoras(listOf("Aventurero", "Fisico", "Guerrero"))
        Assert.assertEquals(3, mejoras.size)
        var nuevasMejoras = mejoras.toList()

        Assert.assertEquals("Guerrero", nuevasMejoras[2].claseInicio)
        Assert.assertEquals("Maestro de armas", nuevasMejoras[2].claseFinal)

        Assert.assertEquals("Aventurero", nuevasMejoras[0].claseInicio)
        Assert.assertEquals("Magico", nuevasMejoras[0].claseFinal)

        Assert.assertEquals("Fisico", nuevasMejoras[1].claseInicio)
        Assert.assertEquals("Clerigo", nuevasMejoras[1].claseFinal)
    }

    @AfterAll
    fun afterAll() {
        dataServiceNeo4J.eliminarTodo()
        firebaseDataService.eliminarTodo()
    }

}