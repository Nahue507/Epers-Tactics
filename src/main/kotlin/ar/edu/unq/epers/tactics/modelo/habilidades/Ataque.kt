package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.service.dto.Accion
import javax.persistence.Entity

@Entity
class Ataque(receptor: Aventurero, val danio: Int, val precisionFisica : Int): Habilidad(receptor, Accion.ATAQUE_FISICO.name) {

    override fun resolver(aventurero : Aventurero){
        if(ataqueExitoso(aventurero)){
            aventurero.recibirAtaque(danio)
        } else {
            acerto = false
        }

    }

    fun ataqueExitoso(aventurero: Aventurero): Boolean {
        return randomizador.random() + 1 + precisionFisica >= aventurero.getEstadisticaArmadura() + aventurero.getEstadisticaVelocidad() / 2
    }


}