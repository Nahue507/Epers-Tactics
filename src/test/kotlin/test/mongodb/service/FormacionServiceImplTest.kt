package test.mongodb.service

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.GenericMongoDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoDBFormacionDAO
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.dto.Atributo
import ar.edu.unq.epers.tactics.service.impl.hibernate.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.hibernate.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.impl.mongodb.FormacionServiceImpl
import helpers.FIrebaseDataService
import helpers.HibernateDataService
import org.junit.Assert
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

class FormacionServiceImplTest {

    private val formacionDAO: FormacionDAO = MongoDBFormacionDAO()
    private val genericMongoDAO: GenericMongoDAO<Formacion> = GenericMongoDAO(Formacion::class.java)
    private var partyService: PartyService = PartyServiceImpl(HibernatePartyDAO(), FirebaseLeaderBoardDAO(), MongoDBFormacionDAO())
    private var firebaseDataService : FIrebaseDataService = FIrebaseDataService()
    private val partyDAO = HibernatePartyDAO()
    lateinit var aventurero: Aventurero
    lateinit var aventurero2: Aventurero
    lateinit var aventurero3 : Aventurero
    lateinit var aventurero4 : Aventurero
    lateinit var aventurero5 : Aventurero
    lateinit var aventurero6 : Aventurero
    lateinit var party: Party
    lateinit var party2: Party
    lateinit var party3: Party
    private lateinit var serviceFormacion : FormacionServiceImpl


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
        aventurero.addClase("Mago")
        party = partyService.crear(party)
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
        serviceFormacion = FormacionServiceImpl(formacionDAO,partyDAO)

    }

    @Test
    fun crearFormacion() {
        serviceFormacion.crearFormacion("Magicos", listOf(Requerimiento(2, "Magico")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name)))
        val res = genericMongoDAO.getBy("nombre", "Magicos")

        Assert.assertEquals("Magicos", res?.nombre)
    }

    @Test
    fun todasLasFormaciones(){
        Assert.assertTrue(serviceFormacion.todasLasFormaciones().isEmpty())

        serviceFormacion.crearFormacion("Magicos", listOf(Requerimiento(2, "Magico")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name)))
        serviceFormacion.crearFormacion("Picaros", listOf(Requerimiento(2, "Picaro")), listOf(AtributoDeFormacion(10, Atributo.DESTREZA.name)))
        Assert.assertEquals(2, serviceFormacion.todasLasFormaciones().size)
    }

    @Test
    fun formacionesQuePosee(){
        partyService.recuperar(party3.id!!)
        serviceFormacion.crearFormacion("Aventureros", listOf(Requerimiento(3, "Aventurero")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name)))
        serviceFormacion.crearFormacion("Aventureross", listOf(Requerimiento(1, "Aventurero"), Requerimiento(1, "Aventurero")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name)))
        serviceFormacion.crearFormacion("Aventureros Magicos", listOf(Requerimiento(2, "Aventurero"), Requerimiento(1, "Mago")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name),AtributoDeFormacion(5, Atributo.CONSTITUCION.name)))
        val formacionesQuePosee = serviceFormacion.formacionesQuePosee(party.id!!)
        //Cumple solamente con aventureross
        assertEquals(formacionesQuePosee.size, 1)
        assertEquals(formacionesQuePosee[0].nombre, "Aventureross")
        //No cumple con ninguna
        assertEquals(serviceFormacion.formacionesQuePosee(party2.id!!).size, 0)
        //Cumple con todas
        assertEquals(serviceFormacion.formacionesQuePosee(party3.id!!).size, 3)

    }

    @Test
    fun atributosQueCorresponden(){
        serviceFormacion.crearFormacion("Magos", listOf(Requerimiento(1, "Mago")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name)))
        serviceFormacion.crearFormacion("Aventureros", listOf(Requerimiento(1, "Aventurero")), listOf(AtributoDeFormacion(10, Atributo.FUERZA.name)))
        serviceFormacion.crearFormacion("Aventurerosss", listOf(Requerimiento(1, "Aventurero")), listOf(AtributoDeFormacion(10, Atributo.INTELIGENCIA.name), AtributoDeFormacion(10, Atributo.FUERZA.name)))

        //  cumple con todas

        var atributos = serviceFormacion.atributosQueCorresponden(party.id!!)

        assertEquals(2, atributos.size)
        assertEquals(Atributo.FUERZA.name, atributos[0].atributo)
        assertEquals(20, atributos[0].cantidad)
        assertEquals(Atributo.INTELIGENCIA.name, atributos[1].atributo)
        assertEquals(20, atributos[1].cantidad)

        //  cumple con una

        atributos = serviceFormacion.atributosQueCorresponden(party2.id!!)

        assertEquals(1, atributos.size)
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