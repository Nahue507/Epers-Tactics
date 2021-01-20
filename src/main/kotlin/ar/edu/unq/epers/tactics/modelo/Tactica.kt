package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.habilidades.*
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.dto.Criterio
import ar.edu.unq.epers.tactics.service.dto.TipoDeEstadistica
import ar.edu.unq.epers.tactics.service.dto.TipoDeReceptor
import java.lang.RuntimeException
import javax.persistence.*

@Entity
class Tactica() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne
    var aventurero: Aventurero? = null

    var prioridad: Int? = null
    var receptor: TipoDeReceptor? = null
    var tipoDeEstadistica: TipoDeEstadistica? = null
    var criterio: Criterio? = null
    var valor: Int? = null
    var accion: Accion ? = null

    constructor(aventurero: Aventurero, prioridad: Int, receptor: TipoDeReceptor, tipoDeEstadistica: TipoDeEstadistica, criterio: Criterio, valor: Int, accion: Accion): this() {
        this.aventurero = aventurero
        this.prioridad = prioridad
        this.receptor = receptor
        this.tipoDeEstadistica = tipoDeEstadistica
        this.criterio = criterio
        this.valor = valor
        this.accion = accion
    }

    //PELEA
    fun canHandle(enemigos: List<Aventurero>): Boolean {
        return receptor!!.listaReceptores(aventurero!!, enemigos).any {
            criterio!!.verificar(tipoDeEstadistica!!.estadistica(aventurero!!), tipoDeEstadistica!!.estadistica(it)) }
    }

    fun handle(enemigos: List<Aventurero>): Habilidad? {
        var habilidad: Habilidad? = null
        val receptores = receptor!!.listaReceptores(aventurero!!, enemigos).find {
            criterio!!.verificar(tipoDeEstadistica!!.estadistica(aventurero!!), tipoDeEstadistica!!.estadistica(it)) }

        habilidad = if (receptores == null) {null} else {accion!!.habilidad(aventurero!!, receptores)}

        return habilidad
    }

}