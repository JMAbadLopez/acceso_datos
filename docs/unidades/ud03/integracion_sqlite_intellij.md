# Integrar y Visualizar tu Base de Datos SQLite en IntelliJ IDEA

## Introducción

Para facilitar el desarrollo y la depuración de nuestra aplicación, es muy útil poder ver y manipular la base de datos directamente desde nuestro entorno de desarrollo (IDE). IntelliJ IDEA incluye una potente herramienta que nos permite conectar con nuestro fichero `plantas.sqlite`, ver sus tablas, ejecutar consultas SQL y mucho más, sin tener que salir del editor de código.

Esta guía te mostrará cómo configurar esta conexión paso a paso.

**Requisitos:**

* Tener tu proyecto de Kotlin abierto en IntelliJ IDEA.
* Haber creado el fichero `.sqlite` (ej: `plantas.sqlite`) y tenerlo ubicado dentro de la carpeta `datos` de tu proyecto.

---

### Paso 1: Abrir la Herramienta de Base de Datos

En el lateral derecho de la ventana de IntelliJ, busca y haz clic en la pestaña vertical **Database**. Si no la encuentras, puedes abrirla desde el menú superior: `View > Tool Windows > Database`.

### Paso 2: Añadir una Nueva Conexión (Data Source)

Dentro de la ventana "Database", haz clic en el icono del signo más (`+`) y en el menú desplegable selecciona `Data Source > SQLite`.

### Paso 3: Configurar la Conexión al Fichero

Se abrirá una ventana de configuración llamada "Data Sources and Drivers". Aquí debemos indicar a IntelliJ dónde se encuentra nuestro fichero de base de datos.

1. **Nombre (Name):** Asígnale un nombre descriptivo a tu conexión, por ejemplo: `BD_Plantas_Proyecto`.
2. **Fichero (File):** Este es el paso clave. Haz clic en el botón con los tres puntos (`...`) para abrir el explorador de archivos. Navega hasta la carpeta raíz de tu proyecto, entra en el directorio `datos` y selecciona tu fichero (`plantas.sqlite`).
3. **Descargar Drivers:** Si es la primera vez que usas esta función, IntelliJ te notificará que faltan los drivers necesarios para comunicarse con SQLite. Verás un texto de advertencia con un enlace azul: `Download missing driver files`. Haz clic en él. IntelliJ los descargará e instalará automáticamente en segundo plano.

La ventana de configuración debería tener un aspecto similar a este:

![Configuración de la conexión SQLite en IntelliJ](https://resources.jetbrains.com/help/img/idea/2023.3/db_sqlite_settings.png)

### Paso 4: Probar la Conexión y Finalizar

Antes de guardar, es fundamental verificar que la configuración es correcta.

1. Haz clic en el botón **Test Connection** en la parte inferior de la ventana.
2. Si todo está bien configurado, verás un mensaje de éxito con un tick verde: `Succeeded`.
3. Una vez confirmada la conexión, haz clic en **Apply** y luego en **OK** para cerrar la ventana.

### Paso 5: ¡Explora tu Base de Datos!

¡Enhorabuena! Ya has conectado tu base de datos al IDE. En la pestaña "Database" ahora verás tu nueva conexión. Si la despliegas (`>`):

* Podrás ver los esquemas de la base de datos (en SQLite, normalmente solo `main`).
* Dentro del esquema, encontrarás tu tabla `plantas` (o la que hayas creado).
* Si despliegas la tabla, verás todas sus columnas con sus tipos de datos.

Si haces **doble clic sobre el nombre de la tabla** (`plantas`), se abrirá un visor de datos interactivo. Desde aquí podrás ver los registros, ordenarlos, e incluso añadir, modificar o eliminar filas directamente como si fuera una hoja de cálculo.

![Visor de datos de la tabla en IntelliJ](https://resources.jetbrains.com/help/img/idea/2023.3/db_data_editor.png)

### ¿Qué más puedes hacer ahora?

* **Ejecutar Consultas SQL:** Haz clic derecho sobre el nombre de tu conexión y selecciona `New > Query Console`. Se abrirá un editor donde podrás escribir y lanzar sentencias SQL (`SELECT`, `INSERT`, etc.) y ver los resultados al instante.
* **Visualizar el Esquema:** Haz clic derecho sobre tu tabla y selecciona `Diagrams > Show Visualization...` para ver un diagrama entidad-relación de tu base de datos.

Esta integración es una herramienta muy potente para comprobar en tiempo real que las operaciones de tu DAO en Kotlin están funcionando como esperas.
