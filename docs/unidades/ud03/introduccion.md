# UD3 — Persistencia en Bases de Datos Relacionales

!!! abstract "Resultado de Aprendizaje"
    **RA2** — Desarrolla aplicaciones que gestionan información almacenada en bases de datos relacionales, manteniendo la integridad y consistencia de los datos.

## Objetivos de la Unidad

| # | Objetivo |
| :---: | :--- |
| 1 | Comprender el modelo relacional y los fundamentos del lenguaje SQL (CRUD) |
| 2 | Configurar la conexión a una base de datos desde Kotlin mediante JDBC |
| 3 | Ejecutar operaciones CRUD utilizando `PreparedStatement` de forma segura |
| 4 | Implementar el patrón DAO para separar la lógica de acceso a datos |
| 5 | Gestionar transacciones para garantizar la integridad de los datos |
| 6 | Migrar la aplicación entre distintos SGBD (SQLite → MySQL/PostgreSQL) |

---

## Guía de Uso

Estos apuntes están diseñados para un aprendizaje práctico. A lo largo de la unidad se aplicarán los conceptos teóricos para construir, paso a paso, una aplicación completa de gestión de datos. La temática es de libre elección, pero los pasos serán comunes.

!!! success "🔍 Ejecutar y Analizar"
    Contienen fragmentos de código que deben ser ejecutados y comprendidos en detalle. El objetivo es observar su funcionamiento y salida.

!!! warning "🎯 Práctica para Aplicar"
    Indican la necesidad de programar y aplicar los conceptos aprendidos para avanzar en el desarrollo del proyecto personal.

!!! danger "📁 Entrega"
    Marcan los puntos de entrega del trabajo, que serán revisados y calificados por el profesor.

---

## Fundamentos de las Bases de Datos Relacionaes

En esta primera sección, sentaremos las bases teóricas sobre las bases de datos relacionales y prepararemos el entorno de trabajo inicial para nuestro proyecto.

### Introducción a las Bases de Datos Relacionales

Las bases de datos relacionales son esenciales en el desarrollo de aplicaciones modernas. Su integración con una aplicación requiere realizar una  **conexión** al sistema gestor de base de datos (SGBD) desde el lenguaje de programación.

Este tema se centra en cómo realizar esa conexión, cómo trabajar con datos mediante sentencias SQL y cómo aplicar buenas prácticas, como el cierre de recursos, el uso de transacciones y procedimientos almacenados.

Una **base de datos relacional** es un sistema de almacenamiento de información que **organiza los datos en tablas**. Cada tabla representa una entidad (por ejemplo, clientes, productos, facturas) y está compuestas por filas y columnas, donde cada fila representa un registro único y cada columna contiene un atributo específico de ese registro.

Ejemplo de tabla `clientes`:

| id_cliente | nombre   | ciudad     |
|------------|----------|------------|
| 1          | Pol      | Castellón  |
| 2          | Eli      | Valencia   |

La integridad y las relaciones entre tablas se gestionan mediante claves:

* **Clave Primaria (Primary Key, PK)**: Una o más columnas que identifican de forma única cada registro de una tabla.
* **Clave Foránea (Foreign Key, FK)**: Una columna que referencia la clave primaria de otra tabla, estableciendo así una relación entre ambas.

Ejemplo de tabla `facturas`:

| id_factura | id_cliente | fecha     |
|------------|------------|------------|
| 1          | 1          | 2025-09-18  |
| 2          | 1          | 2025-09-18   |

La interacción con estas bases de datos se realiza a través del lenguaje **SQL (Structured Query Language)**, que permite ejecutar las operaciones **CRUD**:

* **C**reate (Crear): `INSERT`.
* **R**ead (Leer): `SELECT`.
* **U**pdate (Actualizar): `UPDATE`.
* **D**elete (Borrar): `DELETE`.

Un ejemplo sencillo de consulta podría ser:

```sql
    SELECT nombre FROM clientes WHERE ciudad = 'Valencia';
```

Las bases de datos relacionales constituyen un pilar fundamental en el desarrollo de software. Organizan la información en **tablas**, compuestas por filas (registros) y columnas (atributos). Cada tabla representa una entidad (p. ej., `Clientes`, `Productos`).

### Tipos de Gestores de Bases de Datos (SGBD)

Para conectar una aplicación a una base de datos, es esencial conocer el tipo de SGBD, ya que cada uno requiere un conector (driver) específico.

1. **Gestores Embebidos (SQLite, H2, Derby)**:
    * Bases de datos ligeras almacenadas en un único fichero local. No requieren un servicio de servidor independiente.
    * Ideales para aplicaciones de escritorio, móviles, prototipos o pruebas.

2. **Gestores Cliente-Servidor (PostgreSQL, MySQL, Oracle)**:
    * Sistemas robustos que se ejecutan como un servicio en un servidor, permitiendo múltiples conexiones concurrentes.
    * Son el estándar en entornos empresariales y aplicaciones web por su escalabilidad y seguridad.

## Preparación del Entorno

En este proyecto se comenzará usando un **Gestor Embebido** de Bases de Datos, en concreto **SQLite**, por su simplicidad. Antes de comenzar, debes seguir las **instrucciones de Integración de SQLite en IntelliJ**.

### Introducción

Para facilitar el desarrollo y la depuración de nuestra aplicación, es muy útil poder ver y manipular la base de datos directamente desde nuestro entorno de desarrollo (IDE). IntelliJ IDEA incluye una potente herramienta que nos permite conectar con nuestro fichero `plantas.sqlite`, ver sus tablas, ejecutar consultas SQL y mucho más, sin tener que salir del editor de código.

Esta guía te mostrará cómo configurar esta conexión paso a paso.

**Requisitos:**

* Tener tu proyecto de Kotlin abierto en IntelliJ IDEA.
* Haber creado el fichero `.sqlite` (ej: `plantas.sqlite`) y tenerlo ubicado dentro de la carpeta `datos` de tu proyecto.

**Descarga tu base de datos:**

* Para trabajar en esta unidad, primero debes descargar el fichero [plantas.sqlite](../../assets/resources/plantas.sqlite)

!!! warning "Requisito: IntelliJ Ultimate"
    Para usar la herramienta de base de datos integrada necesitas la versión **Ultimate de IntelliJ**. Como estudiante puedes activarla gratis — [sigue la guía del student pack](https://www.jetbrains.com/es-es/academy/student-pack/).

---

### Paso 1: Abrir la Herramienta de Base de Datos

En el lateral derecho de la ventana de IntelliJ, busca y haz clic en la pestaña vertical **Database**. Si no la encuentras, puedes abrirla desde el menú superior: `View > Tool Windows > Database`.

![Database IntelliJ](../../assets/images/ud03/ud3_1.png)

### Paso 2: Añadir una Nueva Conexión (Data Source)

Dentro de la ventana "Database", haz clic en el icono del signo más (`+`) y en el menú desplegable selecciona `Data Source > SQLite`.

![Database IntelliJ](../../assets/images/ud03/ud3_2.png)

### Paso 3: Configurar la Conexión al Fichero

Se abrirá una ventana de configuración llamada "Data Sources and Drivers". Aquí debemos indicar a IntelliJ dónde se encuentra nuestro fichero de base de datos.

1. **Nombre (Name):** Asígnale un nombre descriptivo a tu conexión, por ejemplo: `BD_Plantas_Proyecto`.
2. **Fichero (File):** Este es el paso clave. Haz clic en el botón con los tres puntos (`...`) para abrir el explorador de archivos. Navega hasta la carpeta raíz de tu proyecto, entra en el directorio `datos` y selecciona tu fichero (`plantas.sqlite`).
3. **Descargar Drivers:** Si es la primera vez que usas esta función, IntelliJ te notificará que faltan los drivers necesarios para comunicarse con SQLite. Verás un texto de advertencia con un enlace azul: `Download missing driver files`. Haz clic en él. IntelliJ los descargará e instalará automáticamente en segundo plano.

La ventana de configuración debería tener un aspecto similar a este:

![Configuración de la conexión SQLite en IntelliJ](../../assets/images/ud03/ud3_3.png)

### Paso 4: Probar la Conexión y Finalizar

Antes de guardar, es fundamental verificar que la configuración es correcta.

1. Haz clic en el botón **Test Connection** en la parte inferior de la ventana.
2. Si todo está bien configurado, verás un mensaje de éxito con un tick verde: `Succeeded`.
3. Una vez confirmada la conexión, haz clic en **Apply** y luego en **OK** para cerrar la ventana.

### Paso 5: Explora tu Base de Datos

Ya has conectado tu base de datos al IDE. En la pestaña "Database" ahora verás tu nueva conexión. Si la despliegas (`>`):

* Podrás ver los esquemas de la base de datos (en SQLite, normalmente solo `main`).
* Dentro del esquema, encontrarás tu tabla `plantas` (o la que hayas creado).
* Si despliegas la tabla, verás todas sus columnas con sus tipos de datos.

Si haces **doble clic sobre el nombre de la tabla** (`plantas`), se abrirá un visor de datos interactivo. Desde aquí podrás ver los registros, ordenarlos, e incluso añadir, modificar o eliminar filas directamente como si fuera una hoja de cálculo.

![Visor de datos de la tabla en IntelliJ](../../assets/images/ud03/ud3_4.png)

### ¿Qué más puedes hacer ahora?

* **Ejecutar Consultas SQL:** Haz clic derecho sobre el nombre de tu conexión y selecciona `New > Query Console`. Se abrirá un editor donde podrás escribir y lanzar sentencias SQL (`SELECT`, `INSERT`, etc.) y ver los resultados al instante.
* **Visualizar el Esquema:** Haz clic derecho sobre tu tabla y selecciona `Diagrams > Show Visualization...` para ver un diagrama entidad-relación de tu base de datos.

Esta integración es una herramienta muy potente para comprobar en tiempo real que las operaciones de tu DAO en Kotlin están funcionando como esperas.

!!! warning "🎯 Práctica 1: Creación del Proyecto y la Base de Datos"
    1. Crea un nuevo proyecto en Kotlin con Gradle.
    2. Utiliza la herramienta de **SQLite en IntelliJ** para crear un fichero `nombre_de_tu_BD.sqlite`.
    3. Siguiendo los **datos definidos en la Unidad Anterior**, crea una tabla con clave primaria y tipos de datos adecuados.
    4. Crea una carpeta `datos` en la raíz del proyecto y almacena en ella el archivo `.sqlite`.
    5. Realiza operaciones CRUD (`SELECT`, `INSERT`, `UPDATE`, `DELETE`) sobre la tabla para probar su funcionamiento desde la herramienta de IntelliJ.
