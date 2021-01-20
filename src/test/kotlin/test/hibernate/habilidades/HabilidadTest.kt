package test.hibernate.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.habilidades.*
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HabilidadTest {

    lateinit var aventureroEmisor : Aventurero
    lateinit var aventureroReceptor : Aventurero
    lateinit var ataqueMagico: AtaqueMagico
    lateinit var ataque : Ataque
    lateinit var curar: Curar
    lateinit var defender: Defender
    lateinit var meditar: Meditar

    @BeforeEach
    fun beforeEach(){
        aventureroEmisor = Aventurero("Emisor", "url", 10, 10, 10, 10)
        aventureroReceptor = Aventurero("Receptor", "url", 20, 20, 20, 20)
        ataqueMagico = AtaqueMagico(aventureroReceptor, aventureroEmisor.getEstadisticaPoderMagico().toDouble(), aventureroEmisor.nivel)
        ataque = Ataque(aventureroReceptor, aventureroEmisor.getEstadisticaDamageFisico(), aventureroEmisor.getEstadisticaPrecisionFisica())
        curar = Curar(aventureroReceptor, aventureroEmisor.getEstadisticaPoderMagico().toDouble())
        defender = Defender(aventureroReceptor, aventureroEmisor)
        meditar = Meditar(aventureroEmisor)
        ataqueMagico.randomizador.randomNumber = 10
        ataque.randomizador.randomNumber = 10
    }

    @Test
    fun ataqueMagicoExitosoTest(){

        Assert.assertTrue(ataqueMagico.ataqueMagicoExitoso(aventureroReceptor))

        ataqueMagico.randomizador.randomNumber = 1

        Assert.assertFalse(ataqueMagico.ataqueMagicoExitoso(aventureroReceptor))
    }

    @Test
    fun ataqueMagicoResolverTest(){
        val vida = aventureroReceptor.getVidaActual()

        ataqueMagico.randomizador.randomNumber = 1
        ataqueMagico.resolver(aventureroReceptor)
        Assert.assertEquals(vida, aventureroReceptor.getVidaActual())

        ataqueMagico.randomizador.randomNumber = 10
        ataqueMagico.resolver(aventureroReceptor)
        Assert.assertEquals(aventureroReceptor.getVidaActual(), vida - aventureroEmisor.getEstadisticaPoderMagico())
    }

    @Test
    fun ataqueExitosoTest(){
        Assert.assertTrue(ataque.ataqueExitoso(aventureroReceptor))
        ataque.randomizador.randomNumber = 1
        Assert.assertFalse(ataque.ataqueExitoso(aventureroReceptor))
    }

    @Test
    fun ataqueResolverTest(){
        val vida = aventureroReceptor.getVidaActual()

        ataque.randomizador.randomNumber = 1
        ataque.resolver(aventureroReceptor)
        Assert.assertEquals(vida, aventureroReceptor.getVidaActual())

        ataque.randomizador.randomNumber = 10
        ataque.resolver(aventureroReceptor)
        Assert.assertEquals(aventureroReceptor.getVidaActual(), vida - aventureroEmisor.getEstadisticaDamageFisico())

    }

    @Test
    fun curarTest(){
        val vida = aventureroReceptor.getVidaActual()
        curar.resolver(aventureroReceptor)
        Assert.assertEquals(vida, aventureroReceptor.getVidaActual())

        aventureroReceptor.restarVida(30)
        val vidaRestada = aventureroReceptor.getVidaActual()

        curar.resolver(aventureroReceptor)
        Assert.assertEquals(aventureroReceptor.getVidaActual(), vidaRestada + aventureroEmisor.getEstadisticaPoderMagico())
    }

    @Test
    fun defenderTest(){
        defender.resolver(aventureroReceptor)
        Assert.assertEquals(aventureroReceptor.defensor!!.nombre, aventureroEmisor.nombre)
    }

    @Test
    fun meditarTest(){
        val mana = aventureroEmisor.getManaActual()
        meditar.resolver(aventureroEmisor)
        Assert.assertEquals(mana, aventureroEmisor.getManaActual())

        aventureroEmisor.restarMana(30)
        val manaRestado = aventureroEmisor.getManaActual()

        meditar.resolver(aventureroEmisor)
        Assert.assertEquals(aventureroEmisor.getManaActual(), manaRestado + aventureroEmisor.nivel)

    }

}