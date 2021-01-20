package ar.edu.unq.epers.tactics.service.impl.hibernate

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.habilidades.*
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.service.AventureroLeaderboardService
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx
import javassist.NotFoundException
import kotlin.math.max
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.ValueParameterData

class AventureroLeaderboardServiceImpl(val aventureroDAO: AventureroDAO) : AventureroLeaderboardService {

    override fun mejorGuerrero(): Aventurero {
        return runTrx {
            aventureroDAO.mejorGuerrero()
        }
    }

    override fun mejorMago(): Aventurero {
        return runTrx {
            aventureroDAO.mejorMago()
        }
    }

    override fun mejorCurandero(): Aventurero {
        return runTrx {
            aventureroDAO.mejorCurandero()
        }
    }

    override fun buda(): Aventurero {
        return runTrx {
            aventureroDAO.buda()
        }
    }

}