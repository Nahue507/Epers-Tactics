package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.service.PeleasPaginadas

interface PeleaDAO {
    fun crear(pelea:Pelea) : Pelea
    fun actualizar(pelea: Pelea)
    fun recuperar(idDeLaPelea: Long): Pelea
    fun recuperarOrdenadas(partyId: Long, pagina: Int?): PeleasPaginadas
}