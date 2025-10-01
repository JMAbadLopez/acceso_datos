package ejemplos

import java.nio.file.Path
fun main() {
    // Path relativo al directorio del proyecto
    val rutaRelativa: Path = Path.of("documentos", "ejemplo.txt")
    // Path absoluto en Windows
    val rutaAbsolutaWin: Path = Path.of("C:", "Users", "Pol", "Documentos")
    // Path absoluto en Linux/macOS
    val rutaAbsolutaNix: Path = Path.of("/home/pol/documentos")
    println ("Ruta relativa: " + rutaRelativa)
    println ("Ruta absoluta: " + rutaRelativa.toAbsolutePath())
    println ("Ruta absoluta: " + rutaAbsolutaWin)
    println ("Ruta absoluta: " + rutaAbsolutaNix)
}