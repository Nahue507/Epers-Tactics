package test.hibernate.dto

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.service.dto.PartyDTO
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PartyDTOTest {

    lateinit var partyDTO : PartyDTO
    lateinit var party : Party

    @BeforeEach
    fun beforeEach(){
        party = Party("Party", "URL")
        partyDTO = PartyDTO(3, "Fiesta", "www.facebook.com", listOf())
    }

    @Test
    fun aModeloTest(){
        val partyModelo = partyDTO.aModelo()

        Assert.assertEquals(partyModelo.id, partyDTO.id)
        Assert.assertEquals(partyModelo.nombre, partyDTO.nombre)
        Assert.assertEquals(partyModelo.imagenUrl, partyDTO.imagenURL)

    }

    @Test
    fun desdeModeloTest(){
        val newPartyDTO = PartyDTO.desdeModelo(party)

        Assert.assertEquals(newPartyDTO.id, party.id)
        Assert.assertEquals(newPartyDTO.nombre, party.nombre)
        Assert.assertEquals(newPartyDTO.imagenURL, party.imagenUrl)
    }

    @Test
    fun actualizarModeloTest(){
        partyDTO.actualizarModelo(party)

        Assert.assertEquals(party.id, partyDTO.id)
        Assert.assertEquals(party.nombre, partyDTO.nombre)
        Assert.assertEquals(party.imagenUrl, partyDTO.imagenURL)

    }
}