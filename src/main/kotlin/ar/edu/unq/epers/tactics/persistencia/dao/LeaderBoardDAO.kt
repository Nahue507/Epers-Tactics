package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad

interface LeaderBoardDAO {

    fun crearParty(party: Party)
    fun crearAventurero(aventurero: Aventurero)
    fun actualizarVictorias(party: Party)
    fun actualizarAventureros(habilidade: Habilidad)
    fun eliminar(aventurero: Aventurero)

}