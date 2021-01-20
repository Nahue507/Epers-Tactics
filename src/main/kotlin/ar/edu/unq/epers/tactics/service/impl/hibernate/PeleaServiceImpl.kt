package ar.edu.unq.epers.tactics.service.impl.hibernate

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.habilidades.Ataque
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.LeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PeleaService
import ar.edu.unq.epers.tactics.service.PeleasPaginadas
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class PeleaServiceImpl(val peleaDAO: PeleaDAO, val partyDAO: PartyDAO, val aventureroDAO: AventureroDAO, val leaderBoardDAO: LeaderBoardDAO): PeleaService {

    override fun iniciarPelea(partyId: Long, partyEnemiga: String): Pelea {
        return runTrx {
            val party = partyDAO.recuperar(partyId)
            var pelea = peleaDAO.crear(Pelea(party, partyEnemiga))
            party.pelea = pelea
            party.setEstaPeleando(true)
            partyDAO.actualizar(party)
            pelea
        }
    }

    override fun estaEnPelea(partyId: Long): Boolean {
        return runTrx {
            val party = partyDAO.recuperar(partyId)
            party.getEstaPeleando() && party.pelea != null
        }
    }

    override fun resolverTurno(peleaId: Long, aventureroId: Long, enemigos: List<Aventurero>): Habilidad {
        return runTrx {
            val aventurero = aventureroDAO.recuperar(aventureroId)
            val tactica = aventurero.tacticas.sortedBy { it.prioridad }.find { it.canHandle(enemigos) }
            val pelea = peleaDAO.recuperar(peleaId)

            aventurero.restarTurno()
            val habilidad = if (tactica != null){
                tactica.handle(enemigos)!!

            } else {
                Ataque(enemigos.first(), aventurero.getEstadisticaDamageFisico(), aventurero.getEstadisticaPrecisionFisica())
            }
            habilidad.setearEmisor(aventurero)
            aventureroDAO.actualizar(aventurero)
            pelea.agregarHabilidadEjecutada(habilidad)
            peleaDAO.actualizar(pelea)
            habilidad
        }
    }

    override fun recibirHabilidad(idPelea : Long, aventureroId: Long, habilidadId: Habilidad): Aventurero {

        return runTrx {
            val pelea = peleaDAO.recuperar(idPelea)
            val aventurero = aventureroDAO.recuperar(aventureroId)
            habilidadId.resolver(aventurero)
            habilidadId.ejecutada = true
            habilidadId.emisor = aventurero
            pelea.agregarHabilidadRecibida(habilidadId)
            leaderBoardDAO.actualizarAventureros(habilidadId)
            aventureroDAO.actualizar(aventurero)
            peleaDAO.actualizar(pelea)
            aventurero
        }
    }

    override fun terminarPelea(idDeLaPelea: Long): Pelea {
        return runTrx {
            var pelea = peleaDAO.recuperar(idDeLaPelea)
            var party = partyDAO.recuperar(pelea.getParty().id!!)
            party.sumarResultado(party.aventureros.sumBy {it.getVidaActual()} > 0)
            party.reestablecer()
            partyDAO.actualizar(party)
            leaderBoardDAO.actualizarVictorias(party)
            peleaDAO.actualizar(pelea)
            pelea
        }
    }

    override fun recuperarOrdenadas(partyId: Long, pagina: Int?): PeleasPaginadas {
        return runTrx {
            peleaDAO.recuperarOrdenadas(partyId,pagina)
        }
    }
}