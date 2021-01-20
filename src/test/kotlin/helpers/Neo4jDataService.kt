package helpers

import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4j.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.ClaseService
import ar.edu.unq.epers.tactics.service.dto.Atributo
import ar.edu.unq.epers.tactics.service.impl.neo4j.ClaseServiceImpl

class Neo4JDataService: DataService {

    private val claseDAO: ClaseDAO = Neo4JClaseDAO()
    private val claseService: ClaseService = ClaseServiceImpl()

    override fun crearSetDeDatosIniciales() {
        claseService.crearClase("Aventurero")
        claseService.crearClase("Magico")
        claseService.crearClase("Fisico")
        claseService.crearClase("Mago")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Guerrero")
        claseService.crearClase("Hechizero")
        claseService.crearClase("Brujo")
        claseService.crearClase("Paladin")
        claseService.crearClase("Maestro de armas")
        claseService.crearClase("Invocador")
        claseService.crearClase("Caballero de la muerte")
        claseService.crearClase("Caballero")

        claseService.crearMejora("Aventurero", "Magico", listOf(Atributo.INTELIGENCIA), 2)
        claseService.crearMejora("Aventurero", "Fisico", listOf(Atributo.FUERZA), 2)
        claseService.crearMejora("Magico", "Fisico", listOf(Atributo.CONSTITUCION), 1)
        claseService.crearMejora("Magico", "Mago", listOf(Atributo.INTELIGENCIA), 4)
        claseService.crearMejora("Magico", "Clerigo", listOf(Atributo.DESTREZA, Atributo.INTELIGENCIA), 2)
        claseService.crearMejora("Fisico", "Clerigo", listOf(Atributo.DESTREZA, Atributo.CONSTITUCION), 2)
        claseService.crearMejora("Fisico", "Guerrero", listOf(Atributo.CONSTITUCION, Atributo.FUERZA), 2)
        claseService.crearMejora("Mago", "Hechizero", listOf(Atributo.INTELIGENCIA), 6)
        claseService.crearMejora("Mago", "Brujo", listOf(Atributo.DESTREZA, Atributo.INTELIGENCIA), 3)
        claseService.crearMejora("Clerigo", "Mago", listOf(Atributo.CONSTITUCION, Atributo.INTELIGENCIA), 2)
        claseService.crearMejora("Clerigo", "Brujo", listOf(Atributo.DESTREZA, Atributo.CONSTITUCION), 3)
        claseService.crearMejora("Clerigo", "Paladin", listOf(Atributo.CONSTITUCION, Atributo.FUERZA), 3)
        claseService.crearMejora("Clerigo", "Guerrero", listOf(Atributo.CONSTITUCION, Atributo.FUERZA), 2)
        claseService.crearMejora("Guerrero", "Paladin", listOf(Atributo.CONSTITUCION), 6)
        claseService.crearMejora("Guerrero", "Maestro de armas", listOf(Atributo.FUERZA), 6)
        claseService.crearMejora("Hechizero", "Invocador", listOf(Atributo.INTELIGENCIA), 10)
        claseService.crearMejora("Brujo", "Invocador", listOf(Atributo.INTELIGENCIA, Atributo.CONSTITUCION, Atributo.DESTREZA), 3)
        claseService.crearMejora("Brujo", "Hechizero", listOf(Atributo.CONSTITUCION, Atributo.INTELIGENCIA), 3)
        claseService.crearMejora("Brujo", "Caballero de la muerte", listOf(Atributo.DESTREZA, Atributo.CONSTITUCION, Atributo.FUERZA), 3)
        claseService.crearMejora("Brujo", "Paladin", listOf(Atributo.FUERZA, Atributo.CONSTITUCION), 2)
        claseService.crearMejora("Paladin", "Caballero de la muerte", listOf(Atributo.CONSTITUCION, Atributo.FUERZA), 5)
        claseService.crearMejora("Paladin", "Caballero", listOf(Atributo.CONSTITUCION, Atributo.FUERZA), 5)
        claseService.crearMejora("Paladin", "Maestro de armas", listOf(Atributo.FUERZA, Atributo.DESTREZA, Atributo.CONSTITUCION), 2)
        claseService.crearMejora("Maestro de armas", "Caballero", listOf(Atributo.FUERZA), 10)

        claseService.requerir("Paladin", "Clerigo")
        claseService.requerir("Paladin", "Guerrero")
        claseService.requerir("Caballero de la muerte", "Brujo")
        claseService.requerir("Caballero de la muerte", "Paladin")
        claseService.requerir("Caballero", "Paladin")
        claseService.requerir("Caballero", "Maestro de armas")
    }

    override fun eliminarTodo() {
        claseDAO.clear()
    }

}
