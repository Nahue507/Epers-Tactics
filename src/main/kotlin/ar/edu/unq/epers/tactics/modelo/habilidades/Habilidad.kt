package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.Randomizador
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Habilidad(@ManyToOne val receptor : Aventurero, val type : String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne
    var pelea : Pelea? = null

    @ManyToOne
    var emisor : Aventurero? = null


    @Transient
    val randomizador : Randomizador = Randomizador()

    var acerto = true

    var ejecutada = false

    abstract fun resolver(aventurero : Aventurero)
    fun setearEmisor(aventurero: Aventurero){
        emisor = aventurero
    }

}
