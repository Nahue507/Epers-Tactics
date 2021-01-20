package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Pelea() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var fecha: LocalDateTime? = null

    @OneToOne
    private var party: Party? = null

    private var partyEnemiga: String? = null


    @OneToMany(mappedBy = "pelea",cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    var habilidades: MutableList<Habilidad> = mutableListOf()

    constructor(party: Party, partyEnemiga: String): this() {
        this.party = party
        this.partyEnemiga = partyEnemiga
        this.fecha = LocalDateTime.now()
    }

    fun getParty(): Party {
        return this.party!!
    }

    fun getNombrePartyEnemiga(): String {
        return this.partyEnemiga!!
    }

    fun agregarHabilidadEjecutada(habilidad: Habilidad) {
        habilidades.add(habilidad)
        habilidad.pelea = this
    }

    fun agregarHabilidadRecibida(habilidad: Habilidad) {
        habilidad.pelea = this
        habilidad.ejecutada = true
        if (!habilidades.contains(habilidad)) {
            habilidades.add(habilidad)
        }

    }
}