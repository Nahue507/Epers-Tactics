package ar.edu.unq.epers.tactics.persistencia.dao.neo4j

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.modelo.excepciones.MejoraInexistente
import ar.edu.unq.epers.tactics.modelo.excepciones.RequerimientoDuplicadoException
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.service.dto.Atributo
import ar.edu.unq.epers.tactics.service.runner.Neo4jSessionFactory.createSession
import org.neo4j.driver.Record
import org.neo4j.driver.Values

class Neo4JClaseDAO: ClaseDAO {

    override fun crearClase(nombreDeClase: String) {
        createSession().use { session ->
            session.writeTransaction {
                val query = "MERGE (n:Clase {nombre: ${'$'}elNombre })"
                it.run(query, Values.parameters(
                        "elNombre", nombreDeClase
                ))
            }
        }
    }

    override fun crearMejora(nombreDeClase1: String, nombreDeClase2: String, atributos: List<Atributo>, cantidadDeAtributos: Int) {
        createSession().use { session ->
            val query = """
                MATCH (padre:Clase {nombre: ${'$'}nombreDeClase1})
                MATCH (hijo:Clase {nombre: ${'$'}nombreDeClase2})
                MERGE (padre)-[:habilita {claseInicio: ${'$'}nombreDeClase1, claseFinal: ${'$'}nombreDeClase2, atributos: ${'$'}losAtributos, cantidad: ${'$'}laCantidad}]->(hijo)
            """
            var nuevosAtributos: MutableList<String> = mutableListOf()
            atributos.forEach { nuevosAtributos.add(it.toString()) }
            session.run(
                    query, Values.parameters(
                    "nombreDeClase1", nombreDeClase1,
                    "nombreDeClase2", nombreDeClase2,
                    "losAtributos", nuevosAtributos,
                    "laCantidad", cantidadDeAtributos
            )
            )
        }
    }

    override fun requerir(nombreDeClase1: String, nombreDeClase2: String) {
        if (obtenerRequeridos(nombreDeClase1).any { it.toLowerCase() == nombreDeClase2.toLowerCase() }){
            throw RequerimientoDuplicadoException("$nombreDeClase1 ya posee como requerimiento a la clase $nombreDeClase2")
        }
        createSession().use { session ->
            val query = """
                MATCH (padre:Clase {nombre: ${'$'}nombreDeClase1})
                MATCH (hijo:Clase {nombre: ${'$'}nombreDeClase2})
                MERGE (padre)-[:requiere {claseInicio: ${'$'}nombreDeClase1, claseFinal: ${'$'}nombreDeClase2 }]->(hijo)
            """
            session.run(
                    query, Values.parameters(
                    "nombreDeClase1", nombreDeClase1,
                    "nombreDeClase2", nombreDeClase2
            )
            )
        }
    }

    override fun obtenerMejoras(clases: List<String>): Set<Mejora> {
        return createSession().use { session ->
            val query = """
                        MATCH (c)-[r:habilita]->(n)  
                        WHERE r.claseInicio IN ${'$'}clases
                        AND NOT r.claseFinal IN ${'$'}clases 
                        RETURN r
            """
            val result = session.run(query, Values.parameters("clases", clases))
            result.list { record: Record ->
                val mejora = record[0]
                val nombreDeClase1 = mejora["claseInicio"].asString()
                val nombreDeClase2 = mejora["claseFinal"].asString()
                val atributos = mejora["atributos"].asList()
                val cantidadDeAtributos = mejora["cantidad"].asInt()
                var nuevosAtributos: MutableList<Atributo> = mutableListOf()
                atributos.forEach {
                    when (it) {
                        "fuerza" -> nuevosAtributos.add(Atributo.FUERZA)
                        "destreza" -> nuevosAtributos.add(Atributo.DESTREZA)
                        "constitucion" -> nuevosAtributos.add(Atributo.CONSTITUCION)
                        "inteligencia" -> nuevosAtributos.add(Atributo.INTELIGENCIA)
                    }
                }
                Mejora(nombreDeClase1, nombreDeClase2, nuevosAtributos, cantidadDeAtributos)
            }.toSet()
        }
    }

    override fun obtenerRequeridos(nombreDeClase: String): List<String> {
        return createSession().use { session ->
            val query = """MATCH (n:Clase {nombre: ${'$'}nombreDeClase})-[r:requiere]->(c)
                           RETURN r
            """
            val result = session.run(query, Values.parameters("nombreDeClase", nombreDeClase))
            result.list { record: Record ->
                val clases = record[0]
                val claseRequerida = clases["claseFinal"].asString()
                claseRequerida
            }
        }
    }

    override fun posiblesMejoras(clases: List<String>): Set<Mejora> {
        return createSession().use { session ->
            val query =
                    """
                        MATCH (m:Clase)-[r:requiere]->(b)
                        WHERE NOT r.claseFinal IN ${'$'}clases OR r.claseInicio IN ${'$'}clases
                        WITH collect(r.claseInicio) as excluded
                        MATCH (n:Clase)-[h:habilita]->(c)
                        WHERE NOT h.claseFinal IN excluded AND h.claseInicio IN ${'$'}clases AND NOT h.claseFinal IN ${'$'}clases 
                        return h
                    """
            val result = session.run(query, Values.parameters("clases", clases))
            result.list { record: Record ->
                val mejora = record[0]
                val nombreDeClase1 = mejora["claseInicio"].asString()
                val nombreDeClase2 = mejora["claseFinal"].asString()
                val atributos = mejora["atributos"].asList()
                val cantidadDeAtributos = mejora["cantidad"].asInt()
                var nuevosAtributos: MutableList<Atributo> = mutableListOf()
                atributos.forEach {
                    when (it) {
                        "fuerza" -> nuevosAtributos.add(Atributo.FUERZA)
                        "destreza" -> nuevosAtributos.add(Atributo.DESTREZA)
                        "constitucion" -> nuevosAtributos.add(Atributo.CONSTITUCION)
                        "inteligencia" -> nuevosAtributos.add(Atributo.INTELIGENCIA)
                    }
                }
                Mejora(nombreDeClase1, nombreDeClase2, nuevosAtributos, cantidadDeAtributos)
            }.toSet()
        }
    }

    override fun clear(){
        createSession().use { session ->
            val query = "MATCH (n) " +
                        "DETACH DELETE (n)"
            session.run(query)
        }
    }
    override fun puedeMejorar(aventurero: Aventurero, mejora: Mejora): Boolean {
            return createSession().use { session ->
                val query =
                        """
                        MATCH (n:Clase)-[r:habilita]->(c) 
                        WHERE n.nombre = ${'$'}nombreDeClase1 and c.nombre = ${'$'}nombreDeClase2
                        return r IS NOT NULL AS existe
                    """
                val result = session.run(
                        query, Values.parameters(
                        "nombreDeClase1", mejora.claseInicio,
                        "nombreDeClase2", mejora.claseFinal
                )).list()

                if (result.isEmpty()){
                    throw MejoraInexistente("No existe la mejora")
                }

                val query2 =
                        """
                        MATCH (n:Clase {nombre: ${'$'}nombreDeClase})-[r:requiere]->(c)
                        WITH collect(r) as requerimientos
                        return all(requerimiento IN requerimientos WHERE requerimiento.claseFinal IN ${'$'}clases) as puedeMejorar
                    """

                val result2 = session.run(query2, Values.parameters("nombreDeClase", mejora.claseFinal, "clases", aventurero.getClases()))
                result2.list()[0]["puedeMejorar"].asBoolean() && !aventurero.getClases().contains(mejora.claseFinal)
            }
        }

    override fun caminoMasRentable(aventurero: Aventurero, atributo: Atributo, puntosDeExperiencia: Int): List<Mejora> {
        return createSession().use { session ->
            val query =
                    "CALL { " +
                            "MATCH (n:Clase)-[h:habilita *0.." + puntosDeExperiencia.toString() + " ]->(c) " +
                            "WHERE head(h).claseInicio IN ${'$'}clases AND NOT head(h).claseFinal IN ${'$'}clases " +
                            "AND ANY(subh IN h WHERE ANY(atributo in subh.atributos WHERE atributo = ${'$'}atributoASumar)) " +
                            "AND ALL (relacion IN h WHERE NOT relacion.claseFinal IN ${'$'}clases) " +
                            "CALL { " +
                            "WITH h " +
                            "UNWIND h AS mejora " +
                            "MATCH (m)-[mejora]->(d) " +
                            "WHERE ANY(atributo in mejora.atributos WHERE atributo = ${'$'}atributoASumar) " +
                            "RETURN sum(mejora.cantidad) AS total " +
                            "} " +
                            "RETURN h AS result " +
                            "ORDER BY total DESC LIMIT 1 " +
                            "} " +
                            "UNWIND result as r " +
                            "return r"
            val result = session.run(query, Values.parameters("clases", aventurero.getClases(), "atributoASumar", atributo.toString()))
            result.list { record: Record ->

                val mejora = record[0]
                val nombreDeClase1 = mejora["claseInicio"].asString()
                val nombreDeClase2 = mejora["claseFinal"].asString()
                val atributos = mejora["atributos"].asList()
                val cantidadDeAtributos = mejora["cantidad"].asInt()
                var nuevosAtributos: MutableList<Atributo> = mutableListOf()
                atributos.forEach {
                    when (it) {
                        "fuerza" -> nuevosAtributos.add(Atributo.FUERZA)
                        "destreza" -> nuevosAtributos.add(Atributo.DESTREZA)
                        "constitucion" -> nuevosAtributos.add(Atributo.CONSTITUCION)
                        "inteligencia" -> nuevosAtributos.add(Atributo.INTELIGENCIA)
                    }
                }
                Mejora(nombreDeClase1, nombreDeClase2, nuevosAtributos, cantidadDeAtributos)
            }

        }
    }
}