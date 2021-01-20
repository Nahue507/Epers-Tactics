package test.hibernate.dto

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.service.dto.PeleaDTO
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PeleaDTOTest {

    lateinit var pelea: Pelea

    @BeforeEach
    fun beforeEach(){
        pelea = Pelea(Party("Party", "URL"), "Party Enemiga")
    }

    @Test
    fun desdeModeloTest(){
        val peleaDTO : PeleaDTO = PeleaDTO.desdeModelo(pelea)

        Assert.assertEquals(peleaDTO.partyId, pelea.getParty().id)
        Assert.assertEquals(peleaDTO.date, pelea.fecha)
        Assert.assertEquals(peleaDTO.peleaId, pelea.id)
        Assert.assertEquals(peleaDTO.partyEnemiga, pelea.getNombrePartyEnemiga())
    }
}