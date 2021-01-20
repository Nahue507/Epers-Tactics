package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import javassist.NotFoundException

class HibernateAventureroDAO: AventureroDAO {

    override fun actualizar(aventurero: Aventurero) {
        val session = HibernateTransactionRunner.currentSession
        session.update(aventurero)
    }

    override fun recuperar(idDelAventurero: Long): Aventurero {
        val session = HibernateTransactionRunner.currentSession
        val aventurero = session.get(Aventurero::class.java, idDelAventurero)
        if(aventurero == null){
            throw NotFoundException("El aventurero con el $idDelAventurero no existe")
        }
        return aventurero
    }

    override fun eliminar(aventurero: Aventurero) {
        val session = HibernateTransactionRunner.currentSession
        session.delete(aventurero)
    }

    override fun recuperarTodas(): List<Aventurero> {
        val session = HibernateTransactionRunner.currentSession
        return session.createQuery("SELECT a FROM Aventurero a", Aventurero::class.java).resultList
    }

    override fun recuperarHabilidades(idDelAventurero: Long): List<Habilidad> {
        val session = HibernateTransactionRunner.currentSession
        val hql = "SELECT h FROM Habilidad h WHERE h.emisor.id = $idDelAventurero and h.ejecutada = TRUE"
        return session.createQuery(hql, Habilidad::class.java).resultList
    }

    override fun buda(): Aventurero {
        val session = HibernateTransactionRunner.currentSession

        val hql ="SELECT * FROM Aventurero WHERE id = " +
                    "(SELECT emisor_id FROM Habilidad h " +
                    "WHERE h.ejecutada = true AND h.acerto = true AND h.type = :type " +
                    "GROUP By h.emisor_id HAVING COUNT(h.emisor_id) = " +
                        "(SELECT MAX(cantMeditaciones) FROM " +
                            "(SELECT count(ha.emisor_id) as cantMeditaciones FROM Habilidad ha " +
                            "WHERE ha.acerto = true and ha.ejecutada = true and ha.type = :type GROUP BY ha.emisor_id) as b) ORDER BY emisor_id DESC Limit 1)"


        return session.createNativeQuery(hql, Aventurero::class.java).setParameter("type", Accion.MEDITAR.name).singleResult as Aventurero
    }

    override fun mejorCurandero(): Aventurero {
        val session = HibernateTransactionRunner.currentSession

        val hql ="SELECT * FROM Aventurero WHERE id = " +
                    "(SELECT emisor_id FROM Curar c JOIN Habilidad h " +
                    "WHERE h.ejecutada = true AND h.acerto = true " +
                    "GROUP By h.emisor_id HAVING SUM(c.poderMagico) = " +
                        "(SELECT MAX(cantCuras) FROM " +
                            "(SELECT SUM(cu.poderMagico) as cantCuras FROM Curar cu JOIN Habilidad ha " +
                            "WHERE ha.acerto = true and ha.ejecutada = true GROUP BY ha.emisor_id) as b) ORDER BY h.emisor_id DESC Limit 1)"

        return session.createNativeQuery(hql, Aventurero::class.java).singleResult as Aventurero
    }

    override fun mejorMago(): Aventurero {
        val session = HibernateTransactionRunner.currentSession

        val hql ="SELECT * FROM Aventurero WHERE id = " +
                "(SELECT emisor_id FROM AtaqueMagico am JOIN Habilidad h " +
                "WHERE h.ejecutada = true AND h.acerto = true " +
                "GROUP By h.emisor_id HAVING SUM(am.poderMagico) = " +
                "(SELECT MAX(cantDaño) FROM " +
                "(SELECT SUM(atqm.poderMagico) as cantDaño FROM AtaqueMagico atqm JOIN Habilidad ha " +
                "WHERE ha.acerto = true and ha.ejecutada = true GROUP BY ha.emisor_id) as b) ORDER BY h.emisor_id DESC Limit 1)"

        return session.createNativeQuery(hql, Aventurero::class.java).singleResult as Aventurero
    }

    override fun mejorGuerrero(): Aventurero {
        val session = HibernateTransactionRunner.currentSession

        val hql ="SELECT * FROM Aventurero WHERE id = " +
                "(SELECT emisor_id FROM Ataque a JOIN Habilidad h " +
                "WHERE h.ejecutada = true AND h.acerto = true " +
                "GROUP By h.emisor_id HAVING SUM(a.danio) = " +
                "(SELECT MAX(cantDaño) FROM " +
                "(SELECT SUM(at.danio) as cantDaño FROM Ataque at JOIN Habilidad ha " +
                "WHERE ha.acerto = true and ha.ejecutada = true GROUP BY ha.emisor_id) as b) ORDER BY h.emisor_id DESC Limit 1)"

        return session.createNativeQuery(hql, Aventurero::class.java).singleResult as Aventurero
    }

}