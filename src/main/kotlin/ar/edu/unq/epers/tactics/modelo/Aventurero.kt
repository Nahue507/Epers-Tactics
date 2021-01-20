package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.excepciones.PuntosDeExperienciaInsuficientesException
import ar.edu.unq.epers.tactics.modelo.propiedades.Atributos
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.persistence.*

@Entity
class Aventurero() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne
    var party: Party? = null

    @Column(nullable = false, length = 50 , unique = true)
    var nombre: String? = null
    var nivel = 1
    var imagenUrl: String? = null

    @OneToOne(cascade = [CascadeType.ALL])
    var atributos: Atributos = Atributos()

    @OneToOne(cascade = [CascadeType.ALL])
    var atributoDeFormacion : Atributos = Atributos()

    @OneToMany(mappedBy = "aventurero", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    var tacticas: List<Tactica> = listOf()

    @Transient
    var defensor : Aventurero? = null

    var turnos : Int = 0
    var defendiendo : Boolean = turnos == 3 //Define si es el primer turno que defiende

    var clases: String = "Aventurero"

    private var vidaActual: Int = 1
    private var manaActual: Int = 1

    var poder: Int? = null

    var puntosDeExperiencia: Int = 0

    constructor(nombre: String?, imagenURL: String?, fuerza: Int, destreza: Int, constitucion: Int, inteligencia: Int, nivel : Int = 1) : this() {
        this.nombre = nombre
        this.nivel = nivel
        this.imagenUrl = imagenURL
        this.atributos = Atributos(fuerza, destreza, constitucion, inteligencia)
        this.vidaActual= nivel * 5 + constitucion * 2 + fuerza
        this.manaActual= nivel + inteligencia

        this.poder = getPoder()
    }

    fun recibirAtaque(cantidad : Int){
        if(defensor == null){
            restarVida(cantidad)
        }
        else{
            defensor!!.defender(cantidad)
        }
    }

    private fun defender(cantidad : Int) {
        restarVida(cantidad / if (defendiendo) 2 else 1 )
    }

    fun agregarTactica(tactica: Tactica) {
        var tacticas = this.tacticas.toMutableList()
        tacticas.add(tactica)
        this.tacticas = tacticas
    }

    fun getFuerza(): Int {
        return atributos.getAtributoFuerza() + atributoDeFormacion.getAtributoFuerza()
    }

    fun setFuerza(fuerza: Int) {
        this.atributos.setAtributoFuerza(fuerza)
        actualizarPoder()
    }

    fun getDestreza(): Int {
        return atributos.getAtributoDestreza() + atributoDeFormacion.getAtributoDestreza()
    }

    fun setDestreza(destreza: Int) {
        this.atributos.setAtributoDestreza(destreza)
        actualizarPoder()
    }

    fun getConstitucion(): Int {
        return atributos.getAtributoConstitucion() + atributoDeFormacion.getAtributoConstitucion()
    }

    fun setConstitucion(constitucion: Int) {
        this.atributos.setAtributoConstitucion(constitucion)
        actualizarPoder()
    }

    fun getInteligencia(): Int {
        return atributos.getAtributoInteligencia() + atributoDeFormacion.getAtributoInteligencia()
    }

    fun setInteligencia(inteligencia: Int) {
        this.atributos.setAtributoInteligencia(inteligencia)
        actualizarPoder()
    }

    private fun actualizarPoder() {
        this.poder = getPoder()
    }

    fun reestablecerEstado() {
        restaurarMana()
        restaurarVida()
        defensor = null
        defendiendo = false
        turnos = 0
    }

    fun restarTurno() {
        if (turnos > 0) {
            turnos -= 1
        }
    }

    fun mayorPoderFisicoQueMagico(): Boolean {
        return getEstadisticaDamageFisico() > getEstadisticaPoderMagico()
    }

    fun getEstadisticaVida(): Int {
        return nivel * 5 + atributos.getAtributoConstitucion() * 2 + atributos.getAtributoFuerza()
    }
    fun getEstadisticaArmadura(): Int {
        return nivel + getConstitucion()
    }
    fun getEstadisticaMana(): Int {
        return nivel + atributos.getAtributoInteligencia()
    }
    fun getEstadisticaVelocidad(): Int {
        return nivel + getDestreza()
    }
    fun getEstadisticaDamageFisico(): Int {
        return nivel + getFuerza() + getDestreza() / 2
    }
    fun getEstadisticaPoderMagico(): Int {
        return nivel + getInteligencia()
    }
    fun getEstadisticaPrecisionFisica(): Int {
        return nivel + getFuerza() + getDestreza()
    }

    fun getVidaActual(): Int {
        return this.vidaActual
    }
    fun restarVida(cantidad : Int){
        if (this.vidaActual - cantidad < 0) {
            this.vidaActual = 0
        } else {
            this.vidaActual -= cantidad
        }
    }
    fun curarVida(cantidad : Int){
        if (this.vidaActual + cantidad > getEstadisticaVida()) {
            this.vidaActual = getEstadisticaVida()
        } else {
            this.vidaActual += cantidad
        }
    }
    fun restaurarVida(){
        this.vidaActual = getEstadisticaVida()
    }
    fun getManaActual() : Int{
        return this.manaActual
    }
    fun restarMana(cantidad: Int) {
        if (this.manaActual - cantidad < 0) {
            this.manaActual = 0
        } else {
            this.manaActual -= cantidad
        }
    }
    fun setMana(cantidad : Int){
        if (this.manaActual + cantidad > getEstadisticaMana()) {
            this.manaActual = getEstadisticaMana()
        } else {
            this.manaActual += cantidad
        }
    }
    fun restaurarMana(){
        this.manaActual= getEstadisticaMana()
    }

    fun getPoder(): Int {
        return getEstadisticaDamageFisico() + getEstadisticaPoderMagico() + getEstadisticaPrecisionFisica()
    }

    fun subirDeNivel() {
        this.nivel++
        this.puntosDeExperiencia++
    }

    fun getClases(): List<String> {
        return this.clases.split(";")
    }

    fun addClase(clase: String) {
        this.clases += ";$clase"
    }

    fun deleteClase(clase: String) {
        var clases: MutableList<String> = this.getClases() as MutableList<String>
        clases.remove(clase)
        this.clases = clases.joinToString(";")
    }

    fun ganarProficiencia(mejora: Mejora) {
        if (this.puntosDeExperiencia > 0) {
            this.addClase(mejora.claseFinal)
            mejora.atributos.forEach {
                when (it.toString()) {
                    "fuerza" -> this.setFuerza(this.getFuerza() + mejora.cantidadDeAtributos)
                    "destreza" -> this.setDestreza(this.getDestreza() + mejora.cantidadDeAtributos)
                    "constitucion" -> this.setConstitucion(this.getConstitucion() + mejora.cantidadDeAtributos)
                    "inteligencia" -> this.setInteligencia(this.getInteligencia() + mejora.cantidadDeAtributos)
                }
            }
            this.puntosDeExperiencia--
        } else {
            throw PuntosDeExperienciaInsuficientesException("No tiene el punto de experiencia necesario para ganar proficientia a una nueva clase")
        }
    }

    fun setearAtributosDeFormacion(atributos: Atributos) {
        atributoDeFormacion.setAtributoConstitucion(atributos.getAtributoConstitucion())
        atributoDeFormacion.setAtributoDestreza(atributos.getAtributoDestreza())
        atributoDeFormacion.setAtributoFuerza(atributos.getAtributoFuerza())
        atributoDeFormacion.setAtributoInteligencia(atributos.getAtributoInteligencia())
        actualizarPoder()
    }
}