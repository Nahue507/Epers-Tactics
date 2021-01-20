package ar.edu.unq.epers.tactics.persistencia.dao.mongodb

import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Requerimiento
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.service.dto.Atributo
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.client.model.*
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.Projections.fields
import com.mongodb.client.model.Projections.include

class MongoDBFormacionDAO : FormacionDAO, GenericMongoDAO<Formacion>(Formacion::class.java) {

    init {
        collection.createIndex(include("nombre"), IndexOptions().unique(true))
    }

    override fun crearFormacion(nombreFormacion: String, requerimientos: List<Requerimiento>, stats: List<AtributoDeFormacion>): Formacion {
        val formacion = Formacion(nombreFormacion, requerimientos, stats)
        save(formacion)
        return formacion
    }

    override fun todasLasFormaciones(): List<Formacion> {
        return collection.find().into(mutableListOf())
    }

    override fun atributosQueCorresponden(party : Party): List<AtributoDeFormacion> {

        val formaciones = formacionesQuePosee(party)
        val match = Aggregates.match(`in`("nombre", formaciones.map { it.nombre }))
        val project = Aggregates.project(fields(include("atributosDeFormacion")))
        val unwind = Aggregates.unwind("\$atributosDeFormacion")
        val group = Aggregates.group("\$atributosDeFormacion._id", Accumulators.sum("cantidad","\$atributosDeFormacion.cantidad"))
        val sort = Aggregates.sort(Indexes.ascending("_id"))

        return aggregate(listOf(match, project, unwind, group, sort), AtributoDeFormacion::class.java)

    }

    override fun formacionesQuePosee(party : Party): List<Formacion> {

        val mapClases = ObjectMapper().writer().writeValueAsString(party.aventureros.flatMap { it.getClases() }.groupBy { it } .mapValues { it.value.size })
        val cumpleRequisitos = """
            function(){
                const mpClases = $mapClases;
                return this.requerimientos.every(({cantidad, clase}) => mpClases[clase] >= cantidad)
            }
        """
        return collection.find(where(cumpleRequisitos)).into(mutableListOf())

    }
}