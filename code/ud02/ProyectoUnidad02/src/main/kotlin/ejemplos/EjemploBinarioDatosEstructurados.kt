package org.example.ejemplos

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
fun main() {
    val ruta = Path.of("multimedia/bin/binario.dat")
    Files.createDirectories(ruta.parent)
    // Escritura binaria
    val fos = FileOutputStream(ruta.toFile())
    val out = DataOutputStream(fos)
    out.writeInt(42) // int (4 bytes)
    out.writeDouble(3.1416) // double (8 bytes)
    out.writeUTF("K") // char (2 bytes)
    out.close()
    fos.close()
    println ("Fichero binario escrito con DataOutputStream.")
    // Lectura binaria
    val fis = FileInputStream(ruta.toFile())
    val input = DataInputStream(fis)
    val entero = input.readInt()
    val decimal = input.readDouble()
    val caracter = input.readUTF()
    input.close()
    fis.close()
    println ("Contenido leído:")
    println ("  Int: $entero")
    println ("  Double: $decimal")
    println ("  Char: $caracter")
}