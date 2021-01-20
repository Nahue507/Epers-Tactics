package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PeleasPaginadas
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import javassist.NotFoundException
import kotlin.math.ceil

class HibernatePeleaDAO: PeleaDAO {

    override fun crear(pelea: Pelea): Pelea {
        val session = HibernateTransactionRunner.currentSession
        session.save(pelea)
        return pelea
    }

    override fun actualizar(pelea: Pelea) {
        val session = HibernateTransactionRunner.currentSession
        session.update(pelea)
    }

    override fun recuperar(idDeLaPelea: Long): Pelea {
        val session = HibernateTransactionRunner.currentSession
        return session.get(Pelea::class.java, idDeLaPelea)?: throw NotFoundException("La pelea con el $idDeLaPelea no existe")
    }

    override fun recuperarOrdenadas(partyId: Long, pagina: Int?): PeleasPaginadas{
        if (pagina!! < 0){
            throw IllegalArgumentException()
        }
        val session = HibernateTransactionRunner.currentSession
        val hql = "select p from Pelea p where p.party.id =: partyId order by p.fecha desc"
        val query = session.createQuery(hql, Pelea::class.java)
        query.setParameter("partyId", partyId)
        val peleasTotal = query.list() as List<Pelea>
        query.firstResult = pagina * 10
        val ultimaPagina = ceil(((peleasTotal.size / 10).toDouble())).toInt()
        query.maxResults = 10
        val peleasEnPaginaBuscada = query.list() as List<Pelea>
        if (pagina in 0..ultimaPagina) {
            return PeleasPaginadas(peleasEnPaginaBuscada, peleasTotal.size)
        } else {
            throw NotFoundException("El número de página $pagina no existe")
        }
    }
}