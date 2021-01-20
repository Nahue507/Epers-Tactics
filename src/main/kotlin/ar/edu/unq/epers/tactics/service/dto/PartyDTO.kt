package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.Party


data class PartyDTO(var id:Long?, var nombre:String, var imagenURL:String, var aventureros:List<AventureroDTO>){

    companion object {

        fun desdeModelo(party: Party):PartyDTO{
            return PartyDTO(party.id, party.nombre!!, party.imagenUrl!!, party.aventureros.map { AventureroDTO.desdeModelo(it) })
        }
    }

    fun aModelo(): Party {
        val party = Party(nombre, imagenURL)
        actualizarModelo(party)
        return party
    }

    fun actualizarModelo(party: Party){
        party.id = id
        party.nombre = nombre
        party.imagenUrl = imagenURL
        party.aventureros = aventureros.map { it.aModelo() }
        party.aventureros.forEach { it.party = party }
    }
}