package ar.edu.unq.epers.tactics.service.impl.mongodb

import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Requerimiento
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.FormacionService
import ar.edu.unq.epers.tactics.service.dto.Atributo
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class FormacionServiceImpl(val formacionDAO : FormacionDAO, val partyDAO : PartyDAO) : FormacionService{

    override fun crearFormacion(nombreFormacion: String, requerimientos: List<Requerimiento>, stats: List<AtributoDeFormacion>): Formacion {
        return formacionDAO.crearFormacion(nombreFormacion, requerimientos, stats)
    }

    override fun todasLasFormaciones(): List<Formacion> {
        return formacionDAO.todasLasFormaciones()
    }

    override fun atributosQueCorresponden(partyId: Long): List<AtributoDeFormacion> {
        val party = runTrx{
            partyDAO.recuperar(partyId)
        }
        return formacionDAO.atributosQueCorresponden(party)
    }

    override fun formacionesQuePosee(partyId: Long): List<Formacion> {
        val party = runTrx{
            partyDAO.recuperar(partyId)
        }
        return formacionDAO.formacionesQuePosee(party)
    }
}