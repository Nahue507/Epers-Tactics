package test.hibernate.dto

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.modelo.propiedades.Atributos
import ar.edu.unq.epers.tactics.service.dto.*
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AventureroDTOTest {

    lateinit var aventureroDTO : AventureroDTO
    lateinit var aventurero : Aventurero
    lateinit var tacticaDTO: TacticaDTO
    lateinit var tactica : Tactica
    lateinit var atributosDTO: AtributosDTO
    lateinit var atributos : Atributos

    @BeforeEach
    fun beforeEach(){
        //aventurero = Aventurero(Party("Party"),"Caballero", 20, 9, 10, 5)
        aventurero = Aventurero("Caballero", "url",20, 9, 10, 5)
        aventureroDTO = AventureroDTO(1, 3, "Mago", "url", listOf(), AtributosDTO(1, 10, 10, 10, 10))
        tactica = Tactica(aventurero, 1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.ARMADURA, Criterio.IGUAL, 30, Accion.ATAQUE_MAGICO)
        tacticaDTO = TacticaDTO(3, 2, TipoDeReceptor.ALIADO, TipoDeEstadistica.DAÑO_FISICO, Criterio.MAYOR_QUE, 3, Accion.DEFENDER)
        atributos = Atributos(10, 10, 10 ,10)
        atributosDTO = AtributosDTO(1, 20, 20, 20, 20)
    }

    @Test
    fun aventureroDTOaModeloTest(){
        val aventureroModelo = aventureroDTO.aModelo()

        Assert.assertEquals(aventureroModelo.id, aventureroDTO.id)
        Assert.assertEquals(aventureroModelo.nivel, aventureroDTO.nivel)
        Assert.assertEquals(aventureroModelo.nombre, aventureroDTO.nombre)
        Assert.assertEquals(aventureroModelo.imagenUrl, aventureroDTO.imagenURL)

    }

    @Test
    fun aventureroDTODesdeModeloTest(){
        val newAventureroDTO = AventureroDTO.desdeModelo(aventurero)

        Assert.assertEquals(newAventureroDTO.id, aventurero.id)
        Assert.assertEquals(newAventureroDTO.nivel, aventurero.nivel)
        Assert.assertEquals(newAventureroDTO.nombre, aventurero.nombre)
        Assert.assertEquals(newAventureroDTO.imagenURL, aventurero.imagenUrl)
    }

    @Test
    fun aventureroDTOActualizarModeloTest(){
        aventureroDTO.actualizarModelo(aventurero)

        Assert.assertEquals(aventurero.id, aventureroDTO.id)
        Assert.assertEquals(aventurero.nivel, aventureroDTO.nivel)
        Assert.assertEquals(aventurero.nombre, aventureroDTO.nombre)
        Assert.assertEquals(aventurero.imagenUrl, aventureroDTO.imagenURL)

    }

    @Test
    fun atributosDTOaModeloTest(){
        val atributosModelo = atributosDTO.aModelo()

        Assert.assertEquals(atributosModelo.getAtributoConstitucion(), atributosDTO.constitucion)
        Assert.assertEquals(atributosModelo.getAtributoDestreza(), atributosDTO.destreza)
        Assert.assertEquals(atributosModelo.getAtributoFuerza(), atributosDTO.fuerza)
        Assert.assertEquals(atributosModelo.getAtributoInteligencia(), atributosDTO.inteligencia)


    }

    @Test
    fun atributosDTODesdeModeloTest(){
        val newAtributosDTO = AtributosDTO.desdeModelo(atributos)

        Assert.assertEquals(newAtributosDTO.id, atributos.id)
        Assert.assertEquals(newAtributosDTO.constitucion, atributos.getAtributoConstitucion())
        Assert.assertEquals(newAtributosDTO.fuerza, atributos.getAtributoFuerza())
        Assert.assertEquals(newAtributosDTO.inteligencia, atributos.getAtributoInteligencia())
        Assert.assertEquals(newAtributosDTO.destreza, atributos.getAtributoDestreza())

    }

    @Test
    fun tacticaDTOaModeloTest(){
        val tacticaModelo = tacticaDTO.aModelo(aventurero)

        Assert.assertEquals(tacticaModelo.accion, tacticaDTO.accion)
        Assert.assertEquals(tacticaModelo.criterio, tacticaDTO.criterio)
        Assert.assertEquals(tacticaModelo.prioridad, tacticaDTO.prioridad)
        Assert.assertEquals(tacticaModelo.receptor, tacticaDTO.receptor)
        Assert.assertEquals(tacticaModelo.valor, tacticaDTO.valor)
        Assert.assertEquals(tacticaModelo.tipoDeEstadistica, tacticaDTO.tipoDeEstadistica)

    }

    @Test
    fun tacticaDTODesdeModeloTest(){
        val newTacticaDTO = TacticaDTO.desdeModelo(tactica)

        Assert.assertEquals(newTacticaDTO.accion, tactica.accion)
        Assert.assertEquals(newTacticaDTO.criterio, tactica.criterio)
        Assert.assertEquals(newTacticaDTO.prioridad, tactica.prioridad)
        Assert.assertEquals(newTacticaDTO.receptor, tactica.receptor)
        Assert.assertEquals(newTacticaDTO.valor, tactica.valor)
        Assert.assertEquals(newTacticaDTO.tipoDeEstadistica, tactica.tipoDeEstadistica)
    }

}