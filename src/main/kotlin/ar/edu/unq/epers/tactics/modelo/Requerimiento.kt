package ar.edu.unq.epers.tactics.modelo

class Requerimiento {

    var cantidad : Int? = null
    var clase : String? = null

    protected constructor(){}
    constructor(cant: Int, _clase : String){
        cantidad = cant
        clase = _clase
    }
}