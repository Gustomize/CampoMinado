package modelo

data class Campo(val linha: Int, val coluna: Int) {

    private val vizinhos = ArrayList<Campo>()
    private val callbacks = ArrayList<(Campo, CampoEvento) -> Unit>()

    var marcado = false
    var aberto = false
    var minado = false

    val desmarcado get() = !marcado
    val fecahdo get() = !aberto
    val seguro get() = !minado
    val objetivoAlcancado get() = seguro && aberto || minado && marcado
    val quantidadeDeVizinhosMinados get() = vizinhos.filter { it.minado }.size
    val vizinhacaSegura get() = vizinhos.map { it.seguro }.reduce { resultado, seguro -> resultado && seguro }

    fun addVizinho(vizinho: Campo) {
        vizinhos.add(vizinho)
    }

    fun onEvento(callback: (Campo, CampoEvento) -> Unit) {
        callbacks.add(callback)
    }

    fun abrir() {
        if (fecahdo) {
            aberto = true
            if (minado) {
                callbacks.forEach { it(this, CampoEvento.EXPLOSAO) }
            } else {
                callbacks.forEach { it(this, CampoEvento.ABERTURA) }
                vizinhos.filter { it.fecahdo && it.seguro && vizinhacaSegura }.forEach { it.abrir() }
            }
        }
    }

    fun alterarMarcacao() {
        if (fecahdo) {
            marcado = !marcado
            val evento = if (marcado) CampoEvento.MARCACAO else CampoEvento.DESMARCACAO
            callbacks.forEach { it(this, evento) }
        }
    }

    fun minar() {
        minado = true
    }

    fun reiniciar() {
        aberto = false
        minado = false
        marcado = false
        callbacks.forEach { it(this, CampoEvento.REINICIALIZACAO) }
    }
}