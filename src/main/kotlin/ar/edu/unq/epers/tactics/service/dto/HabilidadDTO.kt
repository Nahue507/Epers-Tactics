package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.habilidades.*
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.lang.RuntimeException

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        JsonSubTypes.Type(value = AtaqueDTO::class, name = "Attack"),
        JsonSubTypes.Type(value = DefensaDTO::class, name = "Defend"),
        JsonSubTypes.Type(value = CurarDTO::class, name = "Heal"),
        JsonSubTypes.Type(value = AtaqueMagicoDTO::class, name = "MagicAttack"),
        JsonSubTypes.Type(value = MeditarDTO::class, name = "Meditate")
)
abstract class HabilidadDTO(){
    companion object {

        fun desdeModelo(habilidad: Habilidad):HabilidadDTO{

            val receptorDTO = AventureroDTO.desdeModelo(habilidad.receptor)

            return when (habilidad){
                is Ataque -> AtaqueDTO("Attack", habilidad.danio.toDouble(), habilidad.precisionFisica.toDouble(), receptorDTO)
                is Defender -> DefensaDTO("Defend", AventureroDTO.desdeModelo(habilidad.fuente), receptorDTO)
                is Curar -> CurarDTO("Heal", habilidad.poderMagico, receptorDTO)
                is AtaqueMagico -> AtaqueMagicoDTO("MagicAttack", habilidad.poderMagico, habilidad.level, receptorDTO)
                else -> MeditarDTO(receptorDTO)
            }
        }
    }

    fun aModelo(): Habilidad {
        return when(this){
            is AtaqueDTO -> Ataque(objetivo.aModelo(),daño.toInt(), prisicionFisica.toInt())
            is DefensaDTO -> Defender(objetivo.aModelo(), source.aModelo())
            is CurarDTO -> Curar(objetivo.aModelo(), poderMagico)
            is AtaqueMagicoDTO -> AtaqueMagico(objetivo.aModelo(), poderMagico, sourceLevel)
            is MeditarDTO-> Meditar(objetivo.aModelo())
            else -> throw RuntimeException("Invalid HabilidadDTO")
        }
    }
}

data class AtaqueDTO(val tipo:String, val daño: Double, val prisicionFisica: Double, val objetivo: AventureroDTO): HabilidadDTO()
class DefensaDTO(val tipo:String, val source: AventureroDTO, val objetivo: AventureroDTO): HabilidadDTO()
data class CurarDTO(val tipo:String, val poderMagico: Double, val objetivo: AventureroDTO): HabilidadDTO()
data class AtaqueMagicoDTO(val tipo:String, val poderMagico: Double, val sourceLevel: Int, val objetivo: AventureroDTO): HabilidadDTO()
class MeditarDTO(val objetivo: AventureroDTO): HabilidadDTO()