package test.firebase

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.excepciones.NoHibernateIdentifierException
import ar.edu.unq.epers.tactics.modelo.habilidades.Ataque
import ar.edu.unq.epers.tactics.modelo.habilidades.AtaqueMagico
import ar.edu.unq.epers.tactics.modelo.habilidades.Curar
import ar.edu.unq.epers.tactics.modelo.habilidades.Meditar
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import helpers.FIrebaseDataService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LeaderboardDAOTest {

    private val firebaseDataService = FIrebaseDataService()
    lateinit var party : Party
    lateinit var aventurero : Aventurero
    private var firebaseLeaderBoardDAO : FirebaseLeaderBoardDAO = FirebaseLeaderBoardDAO()

    @BeforeEach
    fun before(){
        party = Party("Party de test", "")
        party.id = 1
        aventurero = Aventurero("Aventurero1", "", 10, 10, 10,10)
        aventurero.id = 1
        aventurero.party = party
        firebaseLeaderBoardDAO.crearParty(party)
        firebaseLeaderBoardDAO.crearAventurero(aventurero)
    }

    @Test
    fun crearPartyTest(){
        val partyNueva = Party()
        partyNueva.nombre = "Party nueva"
        partyNueva.imagenUrl = ""
        partyNueva.id = 2
        firebaseLeaderBoardDAO.crearParty(partyNueva)
        val partyDocument = firebaseLeaderBoardDAO.db.collection("Partys").document(partyNueva.id.toString()).get().get()
        assertEquals(partyDocument["NombreParty"], partyNueva.nombre)
        assertEquals(partyDocument["CantidadVictorias"], partyNueva.peleasGanadas.toLong())
        assertEquals(partyDocument["Imagen"], partyNueva.imagenUrl)

    }

    @Test
    fun identificadorDePartyNuloTest(){
        Assertions.assertThrows(NoHibernateIdentifierException::class.java){
            val partyNueva = Party()
            partyNueva.nombre = "Party nula"
            partyNueva.imagenUrl = ""
            partyNueva.id = null
            firebaseLeaderBoardDAO.crearParty(partyNueva)
        }
    }

    @Test
    fun actualizarPartyTest(){
        party.peleasGanadas = 10
        firebaseLeaderBoardDAO.actualizarVictorias(party)
        val partyDocument = firebaseLeaderBoardDAO.db.collection("Partys").document(party.id.toString()).get().get()
        assertEquals(partyDocument["CantidadVictorias"], party.peleasGanadas.toLong())
    }

    @Test
    fun crearAventureroTest(){
        val nuevoAventurero = Aventurero("Aventurero nuevo", "", 10 ,10 ,10 ,10)
        nuevoAventurero.party = party
        nuevoAventurero.id = 2
        firebaseLeaderBoardDAO.crearAventurero(nuevoAventurero)

        var aventureroDocument = firebaseLeaderBoardDAO.db.collection("Mago").document(nuevoAventurero.id.toString()).get().get()
        assertEquals(aventureroDocument["NombreParty"], nuevoAventurero.party!!.nombre)
        assertEquals(aventureroDocument["Imagen"], nuevoAventurero.imagenUrl)
        assertEquals(aventureroDocument["Nombre"], nuevoAventurero.nombre)
        assertEquals(aventureroDocument["DañoMagico"], 0.toLong())

        aventureroDocument = firebaseLeaderBoardDAO.db.collection("budistas").document(nuevoAventurero.id.toString()).get().get()
        assertEquals(aventureroDocument["NombreParty"], nuevoAventurero.party!!.nombre)
        assertEquals(aventureroDocument["Imagen"], nuevoAventurero.imagenUrl)
        assertEquals(aventureroDocument["Nombre"], nuevoAventurero.nombre)
        assertEquals(aventureroDocument["ManaRegenerado"], 0.toLong())

        aventureroDocument = firebaseLeaderBoardDAO.db.collection("Curanderos").document(nuevoAventurero.id.toString()).get().get()
        assertEquals(aventureroDocument["NombreParty"], nuevoAventurero.party!!.nombre)
        assertEquals(aventureroDocument["Imagen"], nuevoAventurero.imagenUrl)
        assertEquals(aventureroDocument["Nombre"], nuevoAventurero.nombre)
        assertEquals(aventureroDocument["Curo"], 0.toLong())

        aventureroDocument = firebaseLeaderBoardDAO.db.collection("Guerreros").document(nuevoAventurero.id.toString()).get().get()
        assertEquals(aventureroDocument["nombreParty"], nuevoAventurero.party!!.nombre)
        assertEquals(aventureroDocument["Imagen"], nuevoAventurero.imagenUrl)
        assertEquals(aventureroDocument["Nombre"], nuevoAventurero.nombre)
        assertEquals(aventureroDocument["valor"], 0.toLong())
    }

    @Test
    fun identificadorDeAventureroNuloTest(){
        Assertions.assertThrows(NoHibernateIdentifierException::class.java){
            val nuevoAventurero = Aventurero("Aventurero nulo", "", 10 ,10 ,10 ,10)
            nuevoAventurero.party = party
            nuevoAventurero.id = null
            firebaseLeaderBoardDAO.crearAventurero(nuevoAventurero)
        }
    }

    @Test
    fun actualizarBudistasTest(){
        val habilidad = Meditar(aventurero)
        habilidad.ejecutada = true
        habilidad.emisor = aventurero
        habilidad.cantidadManaRegenerado = aventurero.nivel

        firebaseLeaderBoardDAO.actualizarAventureros(habilidad)

        var aventureroDocument = firebaseLeaderBoardDAO.db.collection("budistas").document(aventurero.id.toString()).get().get()
        assertEquals(aventureroDocument["ManaRegenerado"], habilidad.cantidadManaRegenerado.toLong())

        firebaseLeaderBoardDAO.actualizarAventureros(habilidad)

        aventureroDocument = firebaseLeaderBoardDAO.db.collection("budistas").document(aventurero.id.toString()).get().get()
        assertEquals(aventureroDocument["ManaRegenerado"], 2.toLong())
    }
    @Test
    fun actualizarGuerrerosTest(){
        val habilidad = Ataque(aventurero, 10, 10)
        habilidad.ejecutada = true
        habilidad.emisor = aventurero
        habilidad.acerto = true
        firebaseLeaderBoardDAO.actualizarAventureros(habilidad)

        var aventureroDocument = firebaseLeaderBoardDAO.db.collection("Guerreros").document(aventurero.id.toString()).get().get()
        assertEquals(aventureroDocument["valor"], habilidad.danio.toLong())

        firebaseLeaderBoardDAO.actualizarAventureros(habilidad)

        aventureroDocument = firebaseLeaderBoardDAO.db.collection("Guerreros").document(aventurero.id.toString()).get().get()
        assertEquals(aventureroDocument["valor"], 20.toLong())
    }
    @Test
    fun actualizarMagosTest(){
        val habilidad = AtaqueMagico(aventurero, 10.toDouble(), 1)
        habilidad.ejecutada = true
        habilidad.emisor = aventurero
        habilidad.acerto = true
        firebaseLeaderBoardDAO.actualizarAventureros(habilidad)

        var aventureroDocument = firebaseLeaderBoardDAO.db.collection("Mago").document(aventurero.id.toString()).get().get()
        assertEquals(aventureroDocument["DañoMagico"], habilidad.poderMagico.toLong())

        firebaseLeaderBoardDAO.actualizarAventureros(habilidad)

        aventureroDocument = firebaseLeaderBoardDAO.db.collection("Mago").document(aventurero.id.toString()).get().get()
        assertEquals(aventureroDocument["DañoMagico"], 20.toLong())
    }
    @Test
    fun actualizarCuranderosTest(){
        val habilidad = Curar(aventurero, 10.toDouble())
        habilidad.ejecutada = true
        habilidad.emisor = aventurero
        habilidad.vidaCurada = 10
        firebaseLeaderBoardDAO.actualizarAventureros(habilidad)

        var aventureroDocument = firebaseLeaderBoardDAO.db.collection("Curanderos").document(aventurero.id.toString()).get().get()
        assertEquals(aventureroDocument["Curo"], habilidad.poderMagico.toLong())

        firebaseLeaderBoardDAO.actualizarAventureros(habilidad)

        aventureroDocument = firebaseLeaderBoardDAO.db.collection("Curanderos").document(aventurero.id.toString()).get().get()
        assertEquals(aventureroDocument["Curo"], 20.toLong())
    }

    @Test
    fun eliminarTest(){
        firebaseLeaderBoardDAO.eliminar(aventurero)
        assertFalse(firebaseLeaderBoardDAO.db.collection("Mago").listDocuments().any { it.id == aventurero.id.toString() })
    }

    @AfterEach
    fun after(){
        firebaseDataService.eliminarTodo()
    }

}