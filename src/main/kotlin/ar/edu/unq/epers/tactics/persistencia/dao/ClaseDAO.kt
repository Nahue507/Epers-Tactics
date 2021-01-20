package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.service.dto.Atributo

interface ClaseDAO {

    fun crearClase(nombreDeClase: String)
    fun crearMejora(nombreDeClase1: String, nombreDeClase2: String, atributos: List<Atributo>, cantidadDeAtributos: Int)
    fun requerir(nombreDeClase1: String, nombreDeClase2: String)
    fun obtenerMejoras(clases: List<String>): Set<Mejora>
    fun obtenerRequeridos(nombreDeClase: String): List<String>
    fun posiblesMejoras(clases: List<String>): Set<Mejora>
    fun clear()
    fun puedeMejorar(aventurero: Aventurero, mejora: Mejora): Boolean
    fun caminoMasRentable(aventurero: Aventurero, atributo: Atributo, puntosDeExperiencia: Int): List<Mejora>

}
