package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.excepciones.PartyLlenaException
import ar.edu.unq.epers.tactics.modelo.propiedades.Atributos
import ar.edu.unq.epers.tactics.service.dto.Atributo
import javax.persistence.*

@Entity
class Party() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, length = 50, unique = true)
    var nombre: String? = null

    var imagenUrl: String? = null

    @OneToMany(mappedBy = "party", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var aventureros: List<Aventurero> = listOf()

    @OneToOne
    var pelea: Pelea? = null

    private var estaPeleando: Boolean = false

    var peleasGanadas: Int = 0
    var peleasPerdidas: Int = 0

    constructor(nombre: String, url: String) : this() {
        this.nombre = nombre
        this.imagenUrl = url
    }

    fun sumarPoderDeAventureros(): Int {
        return aventureros.sumBy { it.getPoder() }
    }

    fun getEstaPeleando(): Boolean {
        return this.estaPeleando
    }
    fun setEstaPeleando(bool: Boolean) {
        this.estaPeleando = bool
    }

    fun reestablecer() {
        setEstaPeleando(false)
        pelea = null
        aventureros.forEach { it.reestablecerEstado() }
    }

    fun agregarAventurero(aventurero: Aventurero) {
        if (aventureros.size >= 5) {
            throw PartyLlenaException("No se pudo agregar aventurero a la party, esta ya tiene 5 miembros")
        }
        aventurero.party = this
        val newAventureros = aventureros.toMutableList()
        newAventureros.add(aventurero)
        aventureros = newAventureros
    }

    fun sumarResultado(resultado: Boolean) {
        if (resultado) {
            this.peleasGanadas++
            this.subirAventurerosDeNivel()
        } else {
            this.peleasPerdidas++
        }
    }

    fun subirAventurerosDeNivel() {
        (this.aventureros).forEach {
            it.subirDeNivel()
        }
    }

    fun setAtributosDeFormacionAventurero(atributosQueCorresponden: List<AtributoDeFormacion>) {
        val fuerza = getAtributo(Atributo.FUERZA ,atributosQueCorresponden)
        val inteligencia = getAtributo(Atributo.INTELIGENCIA, atributosQueCorresponden)
        val constitucion = getAtributo(Atributo.CONSTITUCION, atributosQueCorresponden)
        val destreza = getAtributo(Atributo.DESTREZA, atributosQueCorresponden)
        var atributos = Atributos(fuerza, destreza, constitucion, inteligencia)

        aventureros.forEach { it.setearAtributosDeFormacion(atributos) }
    }

    private fun getAtributo(atributo: Atributo, atributos: List<AtributoDeFormacion>): Int {
        return atributos.filter { it.atributo!! == atributo.toString() }.sumBy { it.cantidad!! }
    }

    fun removeAventurero(aventurero: Aventurero) {
        val list = aventureros.toMutableList()
        list.remove(aventurero)
        aventureros = list
    }

}