package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.service.dto.Atributo
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

class Formacion{

    var nombre : String? = null
    var requerimientos : List<Requerimiento> = listOf()
    var atributosDeFormacion : List<AtributoDeFormacion> = listOf()

    @BsonProperty("_id")
    val id: ObjectId? = null

    protected constructor(){}

    constructor(nombre: String?, requerimientos: List<Requerimiento>, atributosDeFormacion: List<AtributoDeFormacion>) {
        this.nombre = nombre
        this.requerimientos = requerimientos
        this.atributosDeFormacion = atributosDeFormacion
    }

}