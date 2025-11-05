# Evolución a SGBD Cliente-Servidor

Gracias a la buena arquitectura, migrar de SQLite a PostgreSQL, MySQL o cualquier **Sistema Gestor de Bases de Datos** (SGBD) es sencillo. Solo requiere modificar el gestor de conexión.

## 🎯 **Práctica para Aplicar (Opcional): Migración a Mysql o PostgreSQL**

1. Instala MySQL o PostgreSQL, preferiblemente mediante un contenedor Docker.
2. Crea la base de datos y la tabla en el nuevo SGBD con la misma estructura.
3. Modifica `ConexionBD.kt` con los nuevos parámetros de conexión (URL, usuario, contraseña).
4. Ejecuta la aplicación. Debería funcionar sin alterar el DAO o la lógica principal.