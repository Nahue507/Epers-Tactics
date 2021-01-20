package test.mongodb.dao

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.GenericMongoDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoDBFormacionDAO
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.dto.Atributo
import ar.edu.unq.epers.tactics.service.impl.hibernate.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.hibernate.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.impl.hibernate.PeleaServiceImpl
import helpers.FIrebaseDataService
import helpers.HibernateDataService
import org.junit.Assert
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

class MongoDBFormacionDAOTest {


    private val genericMongoDAO: GenericMongoDAO<Formacion> = GenericMongoDAO(Formacion::class.java)
    private val firebaseDataService : FIrebaseDataService= FIrebaseDataService()

    lateinit var aventurero: Aventurero
    lateinit var aventurero2: Aventurero
    lateinit var aventurero3 : Aventurero
    lateinit var aventurero4 : Aventurero
    lateinit var aventurero5 : Aventurero
    lateinit var aventurero6 : Aventurero
    lateinit var party: Party
    lateinit var party2: Party
    lateinit var party3: Party
    private val formacionDAO: FormacionDAO = MongoDBFormacionDAO()
    private var partyService: PartyService= PartyServiceImpl(HibernatePartyDAO(), FirebaseLeaderBoardDAO(),formacionDAO)

    @BeforeEach
    fun beforeEach() {
        party = Party("Party de Prueba", "URL")
        party2 = Party("Party de Prueba2", "URl2")
        party3= Party("Party full","www.google.com")
        aventurero = Aventurero("Nombre de Aventurero", "https://imagen.url/img.jpg", 20, 20, 20, 20)
        aventurero2 = Aventurero("Nombre de Aventurero2", "https://imagen.url/img.jpg", 20, 20, 20, 20)
        aventurero3 = Aventurero("Dave el Barbaro", "https://imagen.url/img.jpg", 20, 20, 20, 20)
        aventurero4 = Aventurero("Atila", "https://imagen.url/img.jpg", 20, 20, 20, 20)
        aventurero5 = Aventurero("Tarzan", "https://imagen.url/img.jpg", 20, 20, 20, 20)
        aventurero6 = Aventurero("Jack Sparrow", "https://imagen.url/img.jpg", 20, 20, 20, 20)
        aventurero2.clases="Mago";
        aventurero6.clases="Mago";
        party = partyService.crear(party)
        aventurero.addClase("Mago")
        aventurero = partyService.agregarAventureroAParty(party.id!!, aventurero)
        partyService.actualizar(party)
        party2 = partyService.crear(party2)
        aventurero2 = partyService.agregarAventureroAParty(party2.id!!, aventurero2)
        partyService.actualizar(party2)
        party3 = partyService.crear(party3)
        aventurero3 = partyService.agregarAventureroAParty(party3.id!!,aventurero3)
        aventurero4 = partyService.agregarAventureroAParty(party3.id!!,aventurero4)
        aventurero5 = partyService.agregarAventureroAParty(party3.id!!, aventurero5)
        aventurero6 = partyService.agregarAventureroAParty(party3.id!!, aventurero6)
        partyService.actualizar(party3)

    }

    @Test
    fun crearFormacion() {
        formacionDAO.crearFormacion("Magicos", listOf(Requerimiento(2, "Magico")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name)))
        val res = genericMongoDAO.getBy("nombre", "Magicos")

        Assert.assertEquals("Magicos", res?.nombre)
    }

    @Test
    fun todasLasFormaciones(){
        Assert.assertTrue(formacionDAO.todasLasFormaciones().isEmpty())

        formacionDAO.crearFormacion("Magicos", listOf(Requerimiento(2, "Magico")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name)))
        formacionDAO.crearFormacion("Picaros", listOf(Requerimiento(2, "Picaro")), listOf(AtributoDeFormacion(10, Atributo.DESTREZA.name)))
        Assert.assertEquals(2, formacionDAO.todasLasFormaciones().size)
    }

    @Test
    fun formacionesQuePosee(){
        val partyRecuperada = partyService.recuperar(party.id!!)
        val partyNoCompatible = partyService.recuperar(party2.id!!)
        var partyFull = partyService.recuperar(party3.id!!)
        formacionDAO.crearFormacion("Aventureros", listOf(Requerimiento(3, "Aventurero")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name)))
        formacionDAO.crearFormacion("Aventureross", listOf(Requerimiento(1, "Aventurero"), Requerimiento(1, "Aventurero")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name)))
        formacionDAO.crearFormacion("Aventureros Magicos", listOf(Requerimiento(2, "Aventurero"), Requerimiento(1, "Mago")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name),AtributoDeFormacion(5, Atributo.CONSTITUCION.name)))
        val formacionesQuePosee = formacionDAO.formacionesQuePosee(partyRecuperada)
        //Cumple solamente con aventureross
        assertEquals(formacionesQuePosee.size, 1)
        assertEquals(formacionesQuePosee[0].nombre, "Aventureross")
        //No cumple con ninguna
        assertEquals(formacionDAO.formacionesQuePosee(partyNoCompatible).size, 0)
        //Cumple con todas
        assertEquals(formacionDAO.formacionesQuePosee(partyFull).size, 3)

    }

    @Test
    fun atributosQueCorrespondenTest(){
        val partyRecuperada1 = partyService.recuperar(party.id!!)
        val partyRecuperada2 = partyService.recuperar(party2.id!!)

        formacionDAO.crearFormacion("Magos", listOf(Requerimiento(1, "Mago")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name)))
        formacionDAO.crearFormacion("Aventureros", listOf(Requerimiento(1, "Aventurero")), listOf(AtributoDeFormacion(10, Atributo.FUERZA.name)))
        formacionDAO.crearFormacion("Aventurerosss", listOf(Requerimiento(1, "Aventurero")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name), AtributoDeFormacion(10, Atributo.FUERZA.name)))

        //  cumple con todas

        var atributos = formacionDAO.atributosQueCorresponden(partyRecuperada1)

        assertEquals(atributos.size, 2)
        assertEquals(Atributo.FUERZA.name, atributos[0].atributo)
        assertEquals(20, atributos[0].cantidad)
        assertEquals(20, atributos[1].cantidad)
        assertEquals(Atributo.INTELIGENCIA.name, atributos[1].atributo)

        // cumple con una

        atributos = formacionDAO.atributosQueCorresponden(partyRecuperada2)

        assertEquals(atributos.size, 1)
        assertEquals(10, atributos[0].cantidad)
        assertEquals(Atributo.INTELIGENCIA.name, atributos[0].atributo)
    }

    @AfterEach
    fun limpiarBDD(){
        genericMongoDAO.deleteAll()
        HibernateDataService(partyService).eliminarTodo()
        firebaseDataService.eliminarTodo()
    }

}