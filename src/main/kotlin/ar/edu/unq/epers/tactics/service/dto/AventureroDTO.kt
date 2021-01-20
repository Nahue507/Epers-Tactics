package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.modelo.habilidades.*
import ar.edu.unq.epers.tactics.modelo.propiedades.Atributos


data class AventureroDTO(var id:Long?, var nivel:Int, var nombre:String?, var imagenURL:String?, var tacticas: List<TacticaDTO>, var atributos: AtributosDTO){

    companion object {

        fun desdeModelo(aventurero: Aventurero):AventureroDTO{
            return AventureroDTO(aventurero.id, aventurero.nivel, aventurero.nombre, aventurero.imagenUrl, aventurero.tacticas.map { TacticaDTO.desdeModelo(it) }, AtributosDTO.desdeModelo(aventurero.atributos))
        }
    }

    fun aModelo():Aventurero{
        val aventurero =  Aventurero(nombre , imagenURL, atributos.fuerza, atributos.destreza, atributos.constitucion, atributos.inteligencia, nivel)
        aventurero.tacticas = tacticas.map{ it.aModelo(aventurero) }
        aventurero.id = id
        aventurero.atributos = atributos.aModelo()
        return aventurero
    }

    fun actualizarModelo(aventurero: Aventurero){
        val tacticasModelo : List<Tactica> = tacticas.map{ it.aModelo(aventurero) }
        aventurero.atributos = atributos.aModelo()
        aventurero.id = id
        aventurero.nivel = nivel
        aventurero.nombre = nombre
        aventurero.imagenUrl = imagenURL
        aventurero.tacticas = tacticasModelo
    }
}

data class AtributosDTO(var id:Long?, var fuerza:Int, var destreza:Int, var constitucion:Int, var inteligencia:Int){

    companion object{
        fun desdeModelo(atributos: Atributos) : AtributosDTO{
            return AtributosDTO(atributos.id, atributos.getAtributoFuerza(), atributos.getAtributoDestreza(), atributos.getAtributoConstitucion(), atributos.getAtributoInteligencia())
        }
    }

    fun aModelo() : Atributos{
        val atributos = Atributos(fuerza, destreza, constitucion, inteligencia)
        atributos.id = id
        return atributos
    }
}
data class TacticaDTO(var id:Long?, var prioridad:Int, var receptor:TipoDeReceptor, var tipoDeEstadistica:TipoDeEstadistica, var criterio:Criterio, var valor:Int, var accion:Accion){

    companion object{
        fun desdeModelo(tactica : Tactica) : TacticaDTO{
            return TacticaDTO(tactica.id, tactica.prioridad!!, tactica.receptor!!, tactica.tipoDeEstadistica!!, tactica.criterio!!, tactica.valor!!, tactica.accion!!)
        }
    }

    fun aModelo(aventurero : Aventurero) : Tactica{
        val tactica = Tactica(aventurero, prioridad, receptor, tipoDeEstadistica, criterio, valor, accion)
        tactica.id = id
        return tactica
    }
}

enum class TipoDeReceptor {
    ALIADO {
        override fun listaReceptores(emisor: Aventurero, receptores: List<Aventurero>): List<Aventurero> {
            return emisor.party!!.aventureros.filter { it != emisor }
        }
    },
    ENEMIGO {
        override fun listaReceptores(emisor: Aventurero, receptores: List<Aventurero>): List<Aventurero> {
            return receptores
        }
    },
    UNO_MISMO {
        override fun listaReceptores(emisor: Aventurero, receptores: List<Aventurero>): List<Aventurero> {
            return listOf(emisor)
        }
    };

    abstract fun listaReceptores(emisor: Aventurero, receptores: List<Aventurero>): List<Aventurero>

}
enum class TipoDeEstadistica {
    VIDA {
        override fun estadistica(aventurero: Aventurero): Int {
            return aventurero.getVidaActual()
        }
    },
    ARMADURA {
        override fun estadistica(aventurero: Aventurero): Int {
            return aventurero.getEstadisticaArmadura()
        }
    },
    MANA {
        override fun estadistica(aventurero: Aventurero): Int {
            return aventurero.getManaActual()
        }
    },
    VELOCIDAD {
        override fun estadistica(aventurero: Aventurero): Int {
            return aventurero.getEstadisticaVelocidad()
        }
    },
    DAÑO_FISICO {
        override fun estadistica(aventurero: Aventurero): Int {
            return aventurero.getEstadisticaDamageFisico()
        }
    },
    DAÑO_MAGICO {
        override fun estadistica(aventurero: Aventurero): Int {
            return aventurero.getEstadisticaPoderMagico()
        }
    },
    PRECISION_FISICA {
        override fun estadistica(aventurero: Aventurero): Int {
            return aventurero.getEstadisticaPrecisionFisica()
        }
    };

    abstract fun estadistica(aventurero: Aventurero): Int

}

enum class Criterio {
    IGUAL {
        override fun verificar(valor1: Int, valor2: Int): Boolean {
            return valor1 == valor2
        }
    },
    MAYOR_QUE {
        override fun verificar(valor1: Int, valor2: Int): Boolean {
            return valor1 > valor2
        }
    },
    MENOR_QUE {
        override fun verificar(valor1: Int, valor2: Int): Boolean {
            return valor1 < valor2
        }
    };

    abstract fun verificar(valor1: Int, valor2: Int): Boolean

}

enum class Accion{
    ATAQUE_FISICO {
        override fun habilidad(emisor: Aventurero, receptor: Aventurero): Habilidad {
            return Ataque(receptor, emisor.getEstadisticaDamageFisico(), emisor.getEstadisticaPrecisionFisica())
        }
    },
    DEFENDER {
        override fun habilidad(emisor: Aventurero, receptor: Aventurero): Habilidad {
            return Defender(receptor, emisor)
        }
    },
    CURAR {
        override fun habilidad(emisor: Aventurero, receptor: Aventurero): Habilidad {
            emisor.restarMana(5)
            return Curar(receptor, emisor.getEstadisticaPoderMagico().toDouble())
        }
    },
    ATAQUE_MAGICO {
        override fun habilidad(emisor: Aventurero, receptor: Aventurero): Habilidad {
            emisor.restarMana(5)
            return AtaqueMagico(receptor, emisor.getEstadisticaPoderMagico().toDouble(), emisor.nivel)
        }
    },
    MEDITAR {
        override fun habilidad(emisor: Aventurero, receptor: Aventurero): Habilidad {
            return Meditar(emisor)
        }
    };

    abstract fun habilidad(emisor: Aventurero, receptor: Aventurero): Habilidad?

}

enum class Atributo{


    FUERZA {
        override fun toString(): String {
            return "fuerza"
        }
    },
    DESTREZA {
        override fun toString(): String {
            return "destreza"
        }
    },
    CONSTITUCION {
        override fun toString(): String {
            return "constitucion"
        }
    },
    INTELIGENCIA {
        override fun toString(): String {
            return "inteligencia"
        }
    };

}