package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.service.dto.Accion
import javax.persistence.Entity

@Entity
class AtaqueMagico(receptor: Aventurero, val poderMagico: Double, val level: Int): Habilidad(receptor, Accion.ATAQUE_MAGICO.name) {

    fun ataqueMagicoExitoso(aventurero : Aventurero) : Boolean{
        return randomizador.random() + level >= aventurero.getEstadisticaVelocidad() / 2
    }

    override fun resolver(aventurero : Aventurero){
        if(ataqueMagicoExitoso(aventurero)){
            aventurero.recibirAtaque(poderMagico.toInt())
        } else {
            acerto = false
        }
    }
}