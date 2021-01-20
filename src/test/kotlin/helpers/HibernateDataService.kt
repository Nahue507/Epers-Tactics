package helpers

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.dto.Criterio
import ar.edu.unq.epers.tactics.service.dto.TipoDeEstadistica
import ar.edu.unq.epers.tactics.service.dto.TipoDeReceptor
import ar.edu.unq.epers.tactics.service.impl.hibernate.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class HibernateDataService(var partyService: PartyService) : DataService {

    override fun eliminarTodo() {
        runTrx {
            val session = HibernateTransactionRunner.currentSession
            val nombreDeTablas = session.createNativeQuery("show tables").resultList
            session.createNativeQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate()
            nombreDeTablas.forEach { result ->
                var tabla = ""
                when (result) {
                    is String -> tabla = result
                    is Array<*> -> tabla = result[0].toString()
                }
                session.createNativeQuery("truncate table $tabla").executeUpdate()
            }
            session.createNativeQuery("SET FOREIGN_KEY_CHECKS=1;").executeUpdate()
        }
    }

    override fun crearSetDeDatosIniciales() {
            var nombres = listOf("Death Squadron", "Unicorns of Love", "Global Guardians", "Space Alliance")
            var urls = listOf("https://steamuserimages-a.akamaihd.net/ugc/264960339433988628/78705B679E846F09F78B6F5C7AA54AA702B1B019/?imw=1024&imh=1024&ima=fit&impolicy=Letterbox&imcolor=%23000000&letterbox=true", "https://liquipedia.net/commons/images/1/18/Unicorns_Of_Love_Logo.png", "https://vignette.wikia.nocookie.net/marvel_dc/images/0/0f/Global_Guardians_001.jpg/revision/latest/window-crop/width/200/x-offset/0/y-offset/0/window-width/730/window-height/730?cb=20091018060634", "https://media-exp1.licdn.com/dms/image/C4E0BAQFMcRuP6f5qKg/company-logo_200_200/0?e=2159024400&v=beta&t=F77ebBfBsCS8GIwcS2bAw6Cipe_ee08_P4S0_dkJtL0")
            (0..3).forEach {
                var party = partyService.crear(Party(nombres[it], urls[it]))
                var aventurero1 = Aventurero("Mage from ${nombres[it]}", "https://i.pinimg.com/236x/49/e6/19/49e6195bb7b74257c644a3995450a38d.jpg", 20, 36, 49, 60)
                var aventurero2 = Aventurero("Archer from ${nombres[it]}", "https://vignette.wikia.nocookie.net/wiki-random-2/images/3/39/Shrek.jpg/revision/latest/scale-to-width-down/340?cb=20200204132055&path-prefix=es", 40, 42, 30, 20)
                var aventurero3 = Aventurero("Assassin from ${nombres[it]}", "https://pbs.twimg.com/profile_images/1188507013233479681/WuNwaQ8R_400x400.jpg", 57, 45, 25, 18)

                var aventureros = listOf(aventurero1, aventurero2, aventurero3)
                aventureros.forEach { aventurero ->
                    var tacticaMagica = Tactica(aventurero, 1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.DAÑO_MAGICO, Criterio.MAYOR_QUE, 40, if (aventurero.mayorPoderFisicoQueMagico()) Accion.ATAQUE_FISICO else Accion.ATAQUE_MAGICO)
                    var tacticaFisica = Tactica(aventurero, 2, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.DAÑO_FISICO, Criterio.MAYOR_QUE, 40, if (aventurero.mayorPoderFisicoQueMagico()) Accion.ATAQUE_FISICO else Accion.ATAQUE_MAGICO)
                    var tacticaDefensiva = Tactica(aventurero, 3, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 25, Accion.DEFENDER)
                    var tacticaDefault = Tactica(aventurero, 4, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, if (aventurero.mayorPoderFisicoQueMagico()) Accion.ATAQUE_FISICO else Accion.ATAQUE_MAGICO)
                    aventurero.agregarTactica(tacticaMagica)
                    aventurero.agregarTactica(tacticaFisica)
                    aventurero.agregarTactica(tacticaDefault)
                    aventurero.agregarTactica(tacticaDefensiva)

                    partyService.agregarAventureroAParty(party.id!!, aventurero)
                }
            }
    }

}