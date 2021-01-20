package test.hibernate.dto

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.modelo.habilidades.*
import ar.edu.unq.epers.tactics.service.dto.*
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HabilidadDTOTest {

    lateinit var ataqueDTO: AtaqueDTO
    lateinit var ataque: Ataque
    lateinit var defensaDTO: DefensaDTO
    lateinit var defender: Defender
    lateinit var curarDTO: CurarDTO
    lateinit var curar: Curar
    lateinit var ataqueMagicoDTO: AtaqueMagicoDTO
    lateinit var ataqueMagico: AtaqueMagico
    lateinit var meditarDTO: MeditarDTO
    lateinit var meditar: Meditar


    @BeforeEach
    fun beforeEach(){

        //val aventurero = Aventurero(Party("Party"), "Aventurero", 10, 10, 10, 10)
        val aventurero = Aventurero("Aventurero", "URL", 10, 10, 10, 10)
        val aventureroDTO = AventureroDTO.desdeModelo(aventurero)

        ataqueDTO = AtaqueDTO("Attack",10.0, 10.0, aventureroDTO)
        defensaDTO = DefensaDTO("Defend", aventureroDTO, aventureroDTO)
        curarDTO = CurarDTO("Heal", 10.0, aventureroDTO)
        ataqueMagicoDTO = AtaqueMagicoDTO("MagickAttack", 10.0, 10, aventureroDTO)
        meditarDTO = MeditarDTO(aventureroDTO)

        ataque = Ataque(aventurero, 20, 20)
        defender = Defender(aventurero, aventurero)
        curar = Curar(aventurero,20.0)
        ataqueMagico = AtaqueMagico(aventurero, 20.0, 20)
        meditar = Meditar(aventurero)

    }

    @Test
    fun ataqueDTOaModeloTest(){
        val modeloAtaque : Ataque = ataqueDTO.aModelo() as Ataque
        Assert.assertEquals(modeloAtaque.danio, ataqueDTO.daño.toInt())
        Assert.assertEquals(modeloAtaque.precisionFisica, ataqueDTO.prisicionFisica.toInt())
        Assert.assertEquals(modeloAtaque.receptor.nombre, ataqueDTO.objetivo.aModelo().nombre)
    }

    @Test
    fun defensaDTOaModeloTest(){
        val modeloDefensa : Defender = defensaDTO.aModelo() as Defender
        Assert.assertEquals(modeloDefensa.fuente.nombre, defensaDTO.source.aModelo().nombre)
        Assert.assertEquals(modeloDefensa.receptor.nombre, defensaDTO.objetivo.aModelo().nombre)
    }

    @Test
    fun curarDTOaModeloTest(){
        val modeloCurar : Curar = curarDTO.aModelo() as Curar
        Assert.assertEquals(modeloCurar.poderMagico.toInt(), curarDTO.poderMagico.toInt())
        Assert.assertEquals(modeloCurar.receptor.nombre, curarDTO.objetivo.nombre)

    }

    @Test
    fun ataqueMagicoDTOaModeloTest(){
        val modeloAtaqueMagico : AtaqueMagico = ataqueMagicoDTO.aModelo() as AtaqueMagico
        Assert.assertEquals(modeloAtaqueMagico.level, ataqueMagicoDTO.sourceLevel)
        Assert.assertEquals(modeloAtaqueMagico.poderMagico.toInt(), ataqueMagicoDTO.poderMagico.toInt())
        Assert.assertEquals(modeloAtaqueMagico.receptor.nombre, ataqueMagicoDTO.objetivo.nombre)

    }

    @Test
    fun meditarDTOaModeloTest(){
        val modeloMeditar : Meditar = meditarDTO.aModelo() as Meditar
        Assert.assertEquals(modeloMeditar.receptor.nombre, meditarDTO.objetivo.nombre)
    }

    @Test
    fun atacarDesdeModeloTest(){
        val newAtaqueDTO : AtaqueDTO = HabilidadDTO.desdeModelo(ataque) as AtaqueDTO
        Assert.assertEquals(newAtaqueDTO.daño.toInt(), ataque.danio)
        Assert.assertEquals(newAtaqueDTO.prisicionFisica.toInt(), ataque.precisionFisica)
        Assert.assertEquals(newAtaqueDTO.objetivo.nombre, ataque.receptor.nombre)
    }

    @Test
    fun defenderDesdeModeloTest(){
        val newDefensaDTO : DefensaDTO = HabilidadDTO.desdeModelo(defender) as DefensaDTO
        Assert.assertEquals(newDefensaDTO.source.nombre, defender.fuente.nombre)
        Assert.assertEquals(newDefensaDTO.objetivo.nombre, defender.receptor.nombre)
    }

    @Test
    fun curarDesdeModeloTest(){
        val newCurarDTO : CurarDTO = HabilidadDTO.desdeModelo(curar) as CurarDTO
        Assert.assertEquals(newCurarDTO.poderMagico.toInt(), curar.poderMagico.toInt())
        Assert.assertEquals(newCurarDTO.objetivo.nombre, curar.receptor.nombre)
    }

    @Test
    fun ataqueMagicoDesdeModeloTest(){
        val newAtaqueMagicoDTO : AtaqueMagicoDTO = HabilidadDTO.desdeModelo(ataqueMagico) as AtaqueMagicoDTO
        Assert.assertEquals(newAtaqueMagicoDTO.poderMagico.toInt(), ataqueMagico.poderMagico.toInt())
        Assert.assertEquals(newAtaqueMagicoDTO.sourceLevel, ataqueMagico.level)
        Assert.assertEquals(newAtaqueMagicoDTO.objetivo.nombre, ataqueMagico.receptor.nombre)
    }

    @Test
    fun meditarDesdeModeloTest(){
        val newMeditarDTO : MeditarDTO = HabilidadDTO.desdeModelo(meditar) as MeditarDTO
        Assert.assertEquals(newMeditarDTO.objetivo.nombre, meditar.receptor.nombre)
    }
}