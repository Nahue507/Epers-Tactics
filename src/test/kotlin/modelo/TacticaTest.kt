package modelo

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.dto.Criterio
import ar.edu.unq.epers.tactics.service.dto.TipoDeEstadistica
import ar.edu.unq.epers.tactics.service.dto.TipoDeReceptor
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TacticaTest {

    lateinit var emisor: Aventurero
    lateinit var receptor: Aventurero
    lateinit var tactica1: Tactica
    lateinit var tactica2: Tactica


    @BeforeEach
    fun setUp() {

        //TODO: implementar parties y aventureros con mock

        emisor = Aventurero("partyEmisor", "emisor", 10, 10, 10, 10)
        receptor = Aventurero("partyReceptor", "receptor", 10, 10, 10, 10)
        tactica1 = Tactica(emisor, 1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.DAÑO_FISICO, Criterio.IGUAL, 10, Accion.ATAQUE_FISICO)
        tactica2 = Tactica(emisor, 2, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.DAÑO_MAGICO, Criterio.MENOR_QUE, 10, Accion.ATAQUE_FISICO)

    }

    @Test
    fun tacticaVerificaSiPuedeHandlearLaCondicion() {
        Assert.assertTrue(tactica1.canHandle(listOf(receptor)))
        Assert.assertFalse(tactica2.canHandle(listOf(receptor)))
    }

    @Test
    fun tacticaHandlearLaCondicion() {
        Assert.assertNotNull(tactica1.handle(listOf(receptor)))
        Assert.assertNull(tactica2.handle(listOf(receptor)))
    }
}