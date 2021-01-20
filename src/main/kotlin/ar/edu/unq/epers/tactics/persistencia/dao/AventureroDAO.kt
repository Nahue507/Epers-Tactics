package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad

interface AventureroDAO {
    fun actualizar(aventurero: Aventurero)
    fun recuperar(idDelAventurero: Long): Aventurero
    fun eliminar(aventurero: Aventurero)
    fun recuperarTodas(): List<Aventurero>
    fun recuperarHabilidades(idDelAventurero: Long): List<Habilidad>
    fun buda(): Aventurero
    fun mejorCurandero(): Aventurero
    fun mejorMago(): Aventurero
    fun mejorGuerrero(): Aventurero
}