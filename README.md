# HostHunter

HostHunter es una aplicación que permite escanear redes en busca de información sobre los sistemas conectados. La aplicación puede identificar el nombre del equipo, la dirección IP, el sistema operativo, así como los puertos abiertos y los servicios que se están ejecutando.

## Descripción General

HostHunter proporciona las siguientes funcionalidades:

- **Escaneo de Redes**:
  - Obtiene el nombre del equipo.
  - Obtiene la dirección IP.
  - Identifica el sistema operativo.

- **Escaneo de Puertos**:
  - Detecta los puertos abiertos.
  - Identifica los servicios asociados a los puertos bien conocidos.

## Despliegue del Proyecto

Para desplegar y ejecutar HostHunter en tu entorno local, sigue estos pasos:

### Prerrequisitos

- JDK 8 o superior

### Instrucciones

1. **Clona el repositorio:**

   ```bash
   git clone [https://github.com/HostHunter.git](https://github.com/aldodetena/HostHunter)
   ```

2. **Navega al directorio del proyecto**

   ```bash
   cd HostHunter
   ```

3. **Compila el proyecto**

   ```bash
   javac -d bin HostHunter/*.java
   ```

4. **Ejecuta la aplicación**

   ```bash
   java -cp bin HostHunter.mainApp
   ```

## Funcionalidades

### Escaneo de Redes

HostHunter permite escanear la red para obtener información básica de los sistemas conectados. Esto incluye:

- **Nombre del Equipo**: Identifica el nombre del host de cada sistema.
- **IP**: Obtiene la dirección IP de cada sistema.
- **Sistema Operativo**: Identifica el sistema operativo de cada sistema.

### Escaneo de Puertos

Además de la información básica del sistema, HostHunter también puede escanear los puertos abiertos y los servicios asociados a ellos:

- **Puertos Abiertos**: Detecta qué puertos están abiertos en cada sistema.
- **Servicios**: Identifica los servicios que están corriendo en los puertos abiertos.

## Generar Javadocs

Para generar la documentación Javadoc del proyecto, utiliza el siguiente comando:

**Ejecuta la aplicación**

   ```bash
   javadoc -d docs ruta/al/proyecto/*.java
   ```

Esto generará la documentación en el directorio docs.
