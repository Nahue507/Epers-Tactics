package ar.edu.unq.epers.tactics.service.impl.hibernate

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.LeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx
import kotlin.math.ceil


class PartyServiceImpl(var partyDAO: PartyDAO, var leaderboardDAO : LeaderBoardDAO, val formacionDAO : FormacionDAO) : PartyService {
    override fun crear(party: Party): Party {
        return runTrx {
            val partyCreada = partyDAO.crear(party)
            leaderboardDAO.crearParty(partyCreada)
            partyCreada
        }
    }

    override fun actualizar(party: Party): Party {
        return runTrx {
            partyDAO.actualizar(party)
            party
        }
    }

    override fun recuperar(idDeLaParty: Long): Party {
        return runTrx { partyDAO.recuperar(idDeLaParty) }
    }

    override fun recuperarTodas(): List<Party> {
        return runTrx { partyDAO.recuperarTodas() }
    }

    override fun agregarAventureroAParty(idDeLaParty: Long, aventurero: Aventurero): Aventurero {
        val av =  runTrx {
            val party = partyDAO.recuperar(idDeLaParty)
            party.agregarAventurero(aventurero)
            party.setAtributosDeFormacionAventurero(formacionDAO.atributosQueCorresponden(party))
            partyDAO.actualizar(party)
            aventurero
        }
        leaderboardDAO.crearAventurero(av)
        return av
    }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?): PartyPaginadas {
        return runTrx {
            partyDAO.recuperarOrdenadas(orden, direccion, pagina)
        }
    }
}