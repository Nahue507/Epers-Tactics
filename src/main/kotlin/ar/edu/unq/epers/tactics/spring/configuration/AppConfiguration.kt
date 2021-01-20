package ar.edu.unq.epers.tactics.spring.configuration


import ar.edu.unq.epers.tactics.persistencia.dao.*
import ar.edu.unq.epers.tactics.persistencia.dao.firebase.FirebaseLeaderBoardDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoDBFormacionDAO
import ar.edu.unq.epers.tactics.service.*
import ar.edu.unq.epers.tactics.service.impl.hibernate.AventureroLeaderboardServiceImpl
import ar.edu.unq.epers.tactics.service.impl.hibernate.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.hibernate.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.impl.hibernate.PeleaServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfiguration {

    @Bean
    fun groupName() : String {
        val groupName :String?  = System.getenv()["GROUP_NAME"]
        return groupName!!
    }


    @Bean
    fun partyDAO() : PartyDAO {
        return HibernatePartyDAO()
    }

    @Bean
    fun adventurerDAO() : AventureroDAO {
        return HibernateAventureroDAO()
    }

    @Bean
    fun fightDAO() : PeleaDAO {
        return HibernatePeleaDAO()
    }

    @Bean
    fun formacionDAO() : FormacionDAO{
        return MongoDBFormacionDAO()
    }

    @Bean
    fun leaderBOardDAO() : LeaderBoardDAO{
        return FirebaseLeaderBoardDAO()
    }

    @Bean
    fun partyService(partyDAO: PartyDAO, leaderBoardDAO: LeaderBoardDAO, formacionDAO: FormacionDAO) : PartyService {
        return PartyServiceImpl(partyDAO, leaderBoardDAO, formacionDAO)
    }

    @Bean
    fun adventurerService(aventureroDAO: AventureroDAO, partyDAO: PartyDAO, leaderBoardDAO: LeaderBoardDAO, formacionDAO: FormacionDAO) : AventureroService {
        return AventureroServiceImpl(aventureroDAO, partyDAO, leaderBoardDAO, formacionDAO)
    }

    @Bean
    fun adventurerLeaderboardService(aventureroDAO: AventureroDAO) : AventureroLeaderboardService {
        return AventureroLeaderboardServiceImpl(aventureroDAO)
    }

    @Bean
    fun fightService(peleaDAO: PeleaDAO, partyDAO: PartyDAO, aventureroDAO: AventureroDAO, leaderBoardDAO: LeaderBoardDAO) : PeleaService {
        return PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO, leaderBoardDAO)
    }

}