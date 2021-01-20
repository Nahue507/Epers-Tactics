package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.service.dto.Accion
import javax.persistence.Entity

@Entity
class Curar(receptor: Aventurero, val poderMagico: Double): Habilidad(receptor, Accion.CURAR.name) {

    var vidaCurada = 0

    override fun resolver(aventurero : Aventurero) {
        vidaCurada = if(aventurero.getEstadisticaVida() < aventurero.getVidaActual() + poderMagico){
            aventurero.restaurarVida()
            aventurero.getEstadisticaVida() - aventurero.getVidaActual()
        }
        else{
            aventurero.curarVida(poderMagico.toInt())
            poderMagico.toInt()
        }
    }

}