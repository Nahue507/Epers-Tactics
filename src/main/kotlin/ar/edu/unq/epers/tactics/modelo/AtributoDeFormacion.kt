package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.service.dto.Atributo
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

class AtributoDeFormacion {

    var cantidad : Int? = null
    @BsonId
    var atributo : String? = null
    protected constructor(){}

    constructor(cantidad: Int?, atributo: String?) {
        this.cantidad = cantidad
        this.atributo = atributo
    }

}