package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Requerimiento
import ar.edu.unq.epers.tactics.service.dto.Atributo

interface FormacionDAO {

    fun crearFormacion(nombreFormacion : String, requerimientos : List<Requerimiento>, stats : List<AtributoDeFormacion>) : Formacion
    fun todasLasFormaciones() : List<Formacion>
    fun atributosQueCorresponden(party : Party) : List<AtributoDeFormacion>
    fun formacionesQuePosee(party: Party): List<Formacion>
}