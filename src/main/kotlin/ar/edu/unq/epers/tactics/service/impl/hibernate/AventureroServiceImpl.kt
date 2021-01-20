package ar.edu.unq.epers.tactics.service.impl.hibernate

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.LeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class AventureroServiceImpl(val aventureroDAO: AventureroDAO, val partyDAO: PartyDAO, val leaderBoardDAO: LeaderBoardDAO, val formacionDAO : FormacionDAO): AventureroService {

    override fun actualizar(aventurero: Aventurero): Aventurero {
        return runTrx {
            aventureroDAO.actualizar(aventurero)
            aventurero
        }
    }

    override fun recuperar(idDelAventurero: Long): Aventurero {
        return runTrx { aventureroDAO.recuperar(idDelAventurero) }
    }

    override fun eliminar(aventurero: Aventurero) {
        runTrx {
            val party = aventurero.party!!
            party.removeAventurero(aventurero)
            party.setAtributosDeFormacionAventurero(formacionDAO.atributosQueCorresponden(party))
            leaderBoardDAO.eliminar(aventurero)
            aventureroDAO.eliminar(aventurero)
            partyDAO.actualizar(party)
        }
    }
}