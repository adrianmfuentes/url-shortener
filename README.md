# 🔗 URL Shortener API
Esta es una aplicación para el acortamiento de URLs. 

Permite a los usuarios transformar enlaces extensos en códigos únicos y rastreables.

## 🚀 Funcionalidades Clave

    - Acortamiento de URLs: Generación de códigos alfanuméricos únicos.

    - Rate Limiting: Control de tasa basado en IP (máximo 5 peticiones diarias).

    - Validación Robusta: Verificación de formato y existencia de URLs antes del proceso de persistencia.

    - Seguridad Garantizada: Mitigación de vulnerabilidades críticas (CVE-2024-25710, entre otros) mediante gestión de dependencias transitivas.

## 🛠️ Stack Tecnológico

    - Core: Java con Spring Boot 3.5.7.

    - Persistencia: MongoDB.

    - Frontend: Thymeleaf + Tailwind CSS + FontAwesome.

    - Testing: JUnit 5, Mockito, AssertJ y Flapdoodle.

    - DevOps: GitHub Actions (CI/CD) y Dependabot.

## 🏗️ Arquitectura del Sistema

    - Capa de Control: Gestiona las entradas del usuario y las respuestas HTML/API.

    - Capa de Negocio: Implementa las reglas de validación, lógica de acortamiento y control de límites.

    - Capa de Persistencia: Abstracción de acceso a datos sobre MongoDB.

    - Validadores: Componentes especializados para asegurar la integridad de los datos de entrada.

## Requisitos

    - JDK 17 o superior.

    - Maven 3.8+.

    - Docker (opcional, para MongoDB real).


## Ejecución

### Clonar el repositorio
    git clone [https://github.com/tu-usuario/url-shortener.git](https://github.com/tu-usuario/url-shortener.git)

### Compilar e instalar dependencias
    mvn clean install

### Ejecutar la aplicación
    mvn spring-boot:run


## 🧪 Estrategia de Testing

    El proyecto incluye un conjunto de pruebas automatizadas para garantizar la estabilidad:

    - Tests de Integración: Prueban el ciclo de vida completo en un entorno Spring real.

    - Tests de Rendimiento: Prueban la capacidad de respuesta bajo peticiones concurrentes.

    # Ejecutar todos los tests
    mvn test

    # Ejecutar solo tests de rendimiento
    mvn test -Dtest=UrlPerformanceTest


## 🤖 CI/CD Pipeline

    El flujo de GitHub Actions (maven.yml) está configurado para:

    - Iniciar un servicio de MongoDB 6.0 en un contenedor Docker con healthcheck.

    - Configurar el entorno con Java 24.

    - Ejecutar los tests.

    - Generar el artefacto .jar final tras una validación exitosa.



