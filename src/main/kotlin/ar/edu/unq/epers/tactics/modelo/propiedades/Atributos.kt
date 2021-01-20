package ar.edu.unq.epers.tactics.modelo.propiedades

import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Entity
class Atributos() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Min(0)
    @Max(100)
    private var fuerza: Int = 1

    @Min(0)
    @Max(100)
    private var destreza: Int = 1

    @Min(0)
    @Max(100)
    private var constitucion: Int = 1

    @Min(0)
    @Max(100)
    private var inteligencia: Int = 1

    constructor(fuerza: Int, destreza: Int, constitucion: Int, inteligencia: Int): this() {
        this.fuerza = fuerza
        this.destreza = destreza
        this.constitucion = constitucion
        this.inteligencia = inteligencia
    }

    // Getters y Setters

    fun getAtributoFuerza(): Int {
        return fuerza
    }

    fun setAtributoFuerza(valor: Int) {
        this.fuerza = valor
    }

    fun getAtributoDestreza(): Int {
        return destreza
    }

    fun setAtributoDestreza(valor: Int) {
        this.destreza = valor
    }

    fun getAtributoConstitucion(): Int {
        return constitucion
    }

    fun setAtributoConstitucion(valor: Int) {
        this.constitucion = valor
    }

    fun getAtributoInteligencia(): Int {
        return inteligencia
    }

    fun setAtributoInteligencia(valor: Int) {
        this.inteligencia = valor
    }
}