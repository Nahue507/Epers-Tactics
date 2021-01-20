package test.hibernate.dao

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoDBFormacionDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.service.impl.hibernate.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import helpers.DataService
import helpers.HibernateDataService
import javassist.NotFoundException
import org.junit.Assert
import org.junit.jupiter.api.*
import kotlin.random.Random

class PartyDAOTest {

    private val partyDAO = HibernatePartyDAO()
    private val dataService: DataService = HibernateDataService(PartyServiceImpl(partyDAO, FirebaseLeaderBoardDAO(), MongoDBFormacionDAO()))
    lateinit var party: Party
    lateinit var party2: Party


    @BeforeEach
    fun beforeEach() {
        party = Party("Fiesta de Prueba", "URL")
        party2 = Party("Prueba 2 de Fiesta", "URL")
    }

    @Test
    fun crearPartyYRecuperarla() {
        party = HibernateTransactionRunner.runTrx{ partyDAO.crear(party)}
        HibernateTransactionRunner.runTrx{partyDAO.recuperar(party.id!!)}
        Assert.assertNotEquals(null, party.id)
        Assert.assertEquals("Fiesta de Prueba", party.nombre)
    }

    @Test
    fun actualizarParty() {
        party = HibernateTransactionRunner.runTrx{partyDAO.crear(party)}

        party.nombre = "Fiesta de Prueba actualizada"
        party.imagenUrl = "https://imgur.com/img/party20203.jpg"
        HibernateTransactionRunner.runTrx{partyDAO.actualizar(party)}
        party = HibernateTransactionRunner.runTrx{partyDAO.recuperar(party.id!!)}
        Assert.assertEquals("Fiesta de Prueba actualizada", party.nombre)
        Assert.assertEquals("https://imgur.com/img/party20203.jpg", party.imagenUrl)
    }

    @Test
    fun recuperarAventureroNoExistente(){
        Assertions.assertThrows(NotFoundException::class.java){
            HibernateTransactionRunner.runTrx { partyDAO.recuperar(-1) }
        }
    }

    @Test
    fun traerTodasLasParties() {
        party = HibernateTransactionRunner.runTrx{partyDAO.crear(party)}
        party2 = HibernateTransactionRunner.runTrx{partyDAO.crear(party2)}
        val parties = HibernateTransactionRunner.runTrx{partyDAO.recuperarTodas()}
        Assert.assertEquals(2, parties.size)
    }

    @Test
    fun recuperoOrdenadasDesc(or:Orden ,dir : Direccion, partys : PartyPaginadas):Boolean{
        var cal = true
        var comp1 = 0
        var comp2 = 0
        for((index, element) in partys.parties.withIndex()){
           when (or){
               Orden.PODER -> comp1 = element.sumarPoderDeAventureros();
               Orden.PODER->  comp2 = partys.parties.get(index+1).sumarPoderDeAventureros();
               Orden.VICTORIAS -> comp1 = element.peleasGanadas;
               Orden.VICTORIAS->  comp2 = partys.parties.get(index+1).peleasGanadas;
               Orden.DERROTAS -> comp1 = element.peleasPerdidas
               Orden.DERROTAS->  comp2 = partys.parties.get(index+1).peleasPerdidas;
          }
           when(dir){
            Direccion.ASCENDENTE -> cal = cal && comp1 <= comp2;
            Direccion.DESCENDENTE -> cal = cal && comp1 >= comp2;
           }
        }
        return cal
    }

    @Test
    fun partyPaginadasTraeLasPartyOrdenadasPorPoderGanadasYPerdidasOrdenadasDeFormaAscendenteYDescendente() {
        (1..25).forEach {
            var party = Party("Nombre$it", "URL")
            party.peleasGanadas = Random.nextInt(0, 15)
            party.peleasPerdidas = Random.nextInt(0, 15)
            party = HibernateTransactionRunner.runTrx{partyDAO.crear(party)}
            val aventurero1 = Aventurero("Mage from Nombre$it", "https://i.pinimg.com/236x/49/e6/19/49e6195bb7b74257c644a3995450a38d.jpg", Random.nextInt(50, 100), Random.nextInt(50, 100), Random.nextInt(50, 100), Random.nextInt(50, 100))
            val aventurero2 = Aventurero("Archer from Nombre$it", "https://vignette.wikia.nocookie.net/wiki-random-2/images/3/39/Shrek.jpg/revision/latest/scale-to-width-down/340?cb=20200204132055&path-prefix=es", Random.nextInt(50, 100), Random.nextInt(50, 100), Random.nextInt(50, 100), Random.nextInt(50, 100))
            party.agregarAventurero(aventurero1)
            party.agregarAventurero(aventurero2)
            HibernateTransactionRunner.runTrx{partyDAO.actualizar(party)}
        }

        val ordenadasn1 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.PODER, Direccion.DESCENDENTE, 0)}
        val ordenadasn2 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.PODER, Direccion.DESCENDENTE, 1)}
        val ordenadasn3 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.PODER, Direccion.DESCENDENTE, 2)}
        Assert.assertTrue(recuperoOrdenadasDesc(Orden.PODER,Direccion.DESCENDENTE,ordenadasn1))
        Assert.assertFalse(recuperoOrdenadasDesc(Orden.PODER,Direccion.ASCENDENTE,ordenadasn1))
        Assert.assertEquals(ordenadasn1.parties.size, 10)
        Assert.assertEquals(ordenadasn1.total, 25)
        Assert.assertEquals(ordenadasn3.parties.size, 5)
        Assert.assertEquals(ordenadasn3.total, 25)
        Assertions.assertThrows(NotFoundException::class.java) {
            HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.PODER, Direccion.DESCENDENTE, 3)}
        }

        Assert.assertTrue(ordenadasn1.parties[0].sumarPoderDeAventureros() > ordenadasn2.parties[0].sumarPoderDeAventureros())
        Assert.assertTrue(ordenadasn1.parties[9].sumarPoderDeAventureros() >= ordenadasn2.parties[0].sumarPoderDeAventureros())
        Assert.assertTrue(ordenadasn1.parties[9].sumarPoderDeAventureros() > ordenadasn2.parties[9].sumarPoderDeAventureros())

        val ordenadasn4 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.PODER, Direccion.ASCENDENTE, 0)}
        val ordenadasn5 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.PODER, Direccion.ASCENDENTE, 1)}
        val ordenadasn6 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.PODER, Direccion.ASCENDENTE, 2)}

        Assert.assertEquals(ordenadasn4.parties.size, 10)
        Assert.assertEquals(ordenadasn4.total, 25)
        Assert.assertEquals(ordenadasn6.parties.size, 5)
        Assert.assertEquals(ordenadasn6.total, 25)

        Assert.assertTrue(ordenadasn4.parties[0].sumarPoderDeAventureros() < ordenadasn5.parties[0].sumarPoderDeAventureros())
        Assert.assertTrue(ordenadasn4.parties[9].sumarPoderDeAventureros() <= ordenadasn5.parties[0].sumarPoderDeAventureros())
        Assert.assertTrue(ordenadasn4.parties[9].sumarPoderDeAventureros() < ordenadasn5.parties[9].sumarPoderDeAventureros())

        val ordenadasv1 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.VICTORIAS, Direccion.DESCENDENTE, 0)}
        val ordenadasv2 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.VICTORIAS, Direccion.DESCENDENTE, 1)}
        val ordenadasv3 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.VICTORIAS, Direccion.DESCENDENTE, 2)}
        Assert.assertEquals(ordenadasv1.parties.size, 10)
        Assert.assertEquals(ordenadasv1.total, 25)
        Assert.assertEquals(ordenadasv3.parties.size, 5)
        Assert.assertEquals(ordenadasv3.total, 25)
        Assertions.assertThrows(NotFoundException::class.java) {
            HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.VICTORIAS, Direccion.DESCENDENTE, 3)}
        }

        Assert.assertTrue(ordenadasv1.parties[0].peleasGanadas > ordenadasv2.parties[0].peleasGanadas)
        Assert.assertTrue(ordenadasv1.parties[9].peleasGanadas >= ordenadasv2.parties[0].peleasGanadas)
        Assert.assertTrue(ordenadasv1.parties[9].peleasGanadas > ordenadasv2.parties[9].peleasGanadas)


        val ordenadasv4 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.VICTORIAS, Direccion.ASCENDENTE, 0)}
        val ordenadasv5 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.VICTORIAS, Direccion.ASCENDENTE, 1)}

        Assert.assertEquals(ordenadasv4.parties.size, 10)
        Assert.assertEquals(ordenadasv4.total, 25)

        Assert.assertTrue(ordenadasv4.parties[0].peleasGanadas < ordenadasv5.parties[0].peleasGanadas)
        Assert.assertTrue(ordenadasv4.parties[9].peleasGanadas <= ordenadasv5.parties[0].peleasGanadas)
        Assert.assertTrue(ordenadasv4.parties[9].peleasGanadas < ordenadasv5.parties[9].peleasGanadas)

        val ordenadasd1 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.DERROTAS, Direccion.DESCENDENTE, 0)}
        val ordenadasd2 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.DERROTAS, Direccion.DESCENDENTE, 1)}

        Assert.assertEquals(ordenadasd1.parties.size, 10)
        Assert.assertEquals(ordenadasd1.total, 25)
        Assertions.assertThrows(NotFoundException::class.java) {
            HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.DERROTAS, Direccion.DESCENDENTE, 3)}
        }

        Assert.assertTrue(ordenadasd1.parties[0].peleasPerdidas > ordenadasd2.parties[0].peleasPerdidas)
        Assert.assertTrue(ordenadasd1.parties[9].peleasPerdidas >= ordenadasd2.parties[0].peleasPerdidas)
        Assert.assertTrue(ordenadasd1.parties[9].peleasPerdidas > ordenadasd2.parties[9].peleasPerdidas)


        val ordenadasp3 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.DERROTAS, Direccion.ASCENDENTE, 0)}
        val ordenadasp4 = HibernateTransactionRunner.runTrx{partyDAO.recuperarOrdenadas(Orden.DERROTAS, Direccion.ASCENDENTE, 1)}

        Assert.assertEquals(ordenadasp3.parties.size, 10)
        Assert.assertEquals(ordenadasp3.total, 25)

        Assert.assertTrue(ordenadasp3.parties[0].peleasPerdidas < ordenadasp4.parties[0].peleasPerdidas)
        Assert.assertTrue(ordenadasp3.parties[9].peleasPerdidas <= ordenadasp4.parties[0].peleasPerdidas)
        Assert.assertTrue(ordenadasp3.parties[9].peleasPerdidas < ordenadasp4.parties[9].peleasPerdidas)
    }

    @AfterEach
    fun afterEach() {
        dataService.eliminarTodo()
    }
}
