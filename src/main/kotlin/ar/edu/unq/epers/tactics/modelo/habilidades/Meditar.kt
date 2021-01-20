package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.service.dto.Accion
import javax.persistence.Entity

@Entity
class Meditar(receptor: Aventurero): Habilidad(receptor, Accion.MEDITAR.name) {

    var cantidadManaRegenerado = 0

    override fun resolver(aventurero: Aventurero) {
        cantidadManaRegenerado = if(aventurero.getEstadisticaMana() < aventurero.nivel + aventurero.getManaActual()){
            aventurero.restaurarMana()
            aventurero.getEstadisticaMana() - aventurero.getManaActual()
        }
        else{
            aventurero.setMana(aventurero.nivel)
            aventurero.nivel
        }
    }

}