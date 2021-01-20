package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import javassist.NotFoundException
import kotlin.math.ceil

class HibernatePartyDAO: PartyDAO {
    override fun crear(party: Party): Party {
        val session = HibernateTransactionRunner.currentSession
        session.save(party)
        return party
    }

    override fun actualizar(party: Party) {
        val session = HibernateTransactionRunner.currentSession
        session.update(party)
    }

    override fun recuperar(idDeLaParty: Long): Party {
        val session = HibernateTransactionRunner.currentSession
        return session.get(Party::class.java, idDeLaParty)
                ?: throw NotFoundException("La party con el id $idDeLaParty no existe")
    }

    override fun recuperarTodas(): List<Party> {
        val session = HibernateTransactionRunner.currentSession
        return session.createQuery("SELECT p FROM Party p ORDER BY p.nombre ASC", Party::class.java).resultList
    }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?): PartyPaginadas {
        val session = HibernateTransactionRunner.currentSession
        var ordenStr = when (orden) {
            Orden.PODER -> "inner join p.aventureros av group by av.party order by sum(av.poder)"
            Orden.VICTORIAS -> "order by p.peleasGanadas"
            Orden.DERROTAS -> "order by p.peleasPerdidas"
        }
        var direccionStr = when (direccion) {
            Direccion.ASCENDENTE -> "asc"
            Direccion.DESCENDENTE -> "desc"
        }

        val hql = "select p from Party p $ordenStr $direccionStr"
        val query = session.createQuery(hql, Party::class.java)
        val partiesTotal = query.list() as List<Party>
        query.firstResult = pagina!! * 10
        query.maxResults = 10
        val ultimaPagina = ceil((partiesTotal.size / 10).toDouble()).toInt()
        val partiesEnPaginaBuscada = query.list() as List<Party>

        if (pagina in 0..ultimaPagina) {
            return PartyPaginadas(partiesEnPaginaBuscada, partiesTotal.size)
        } else {
            throw NotFoundException("El número de página $pagina no existe")
        }
    }
}