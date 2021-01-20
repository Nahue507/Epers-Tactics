package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.service.dto.Accion
import javax.persistence.Entity

@Entity
class Defender (receptor: Aventurero, @Transient val fuente: Aventurero) : Habilidad(receptor, Accion.DEFENDER.name) {

    override fun resolver(aventurero : Aventurero) {
        // Falta pasarlo a null ceceptouando pasen 3 turnos
        aventurero.defensor = fuente
        fuente.turnos = 3;

    }
}