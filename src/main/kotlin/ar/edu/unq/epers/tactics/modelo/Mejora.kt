package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.service.dto.Atributo

class Mejora() {

    lateinit var claseInicio: String
    lateinit var claseFinal: String
    lateinit var atributos: List<Atributo>
    var cantidadDeAtributos: Int = 0

    constructor(claseInicio: String, claseFinal: String, atributos: List<Atributo>, cantidadDeAtributos: Int) : this(){
        this.claseInicio = claseInicio
        this.claseFinal = claseFinal
        this.atributos = atributos
        this.cantidadDeAtributos = cantidadDeAtributos
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other is Mejora) {
            return claseInicio == other.claseInicio && claseFinal == other.claseFinal
        }
        return false
    }

}
