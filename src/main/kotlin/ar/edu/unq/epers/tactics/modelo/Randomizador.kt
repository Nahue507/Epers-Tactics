package ar.edu.unq.epers.tactics.modelo

import java.util.*

class Randomizador : Random() {

    var randomNumber : Int? = null

    fun random() : Int {
        return randomNumber ?: Random().nextInt(21)
    }
}