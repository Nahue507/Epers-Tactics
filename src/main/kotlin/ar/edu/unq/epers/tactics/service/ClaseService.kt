package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.modelo.propiedades.Atributos
import ar.edu.unq.epers.tactics.service.dto.Atributo

interface ClaseService {

    fun crearClase(nombreDeClase: String)
    fun crearMejora(nombreDeClase1: String, nombreDeClase2: String, atributos: List<Atributo>, cantidadDeAtributos: Int)
    fun requerir(nombreDeClase1: String, nombreDeClase2: String)
    fun puedeMejorar(aventureroId: Long, mejora: Mejora): Boolean
    fun ganarProficiencia(aventureroId: Long, nombreDeClase1: String, nombreDeClase2: String): Aventurero
    fun posiblesMejoras(aventureroId: Long): Set<Mejora>
    fun caminoMasRentable(puntosDeExperiencia: Int, aventureroId: Long, atributo: Atributo): List<Mejora>

}
