# ABP Módulo 3 - Desarrollo Conducido por Pruebas (TDD)

**Desarrollado por:** Roberto Rivas L.  
**Institución:** Alkemy - Programa ABP  
**Repositorio:** https://github.com/RobertoRivasL/ABP-Modulo-3

## 🏆 Resultados Destacados

- ✅ **81% cobertura de código** (supera el 80% requerido)
- ✅ **35+ tests ejecutados** - 0 fallos, 0 errores
- ✅ **100% cobertura** en paquetes críticos (service, model, exception)
- ✅ **Arquitectura multi-entorno** con GitHub Copilot
- ✅ **14+ ciclos TDD** documentados
- ✅ **Mejora continua:** 69% → 81% cobertura (+12 puntos)

## Descripción del Proyecto

Este proyecto implementa un **módulo CRUD completo** para gestión de usuarios, desarrollado siguiendo metodología **Test Driven Development (TDD)** estricta. La implementación fue optimizada con **GitHub Copilot**, resultando en código de nivel empresarial.

### Funcionalidades Implementadas
- ✅ **Crear Usuario** - Con validaciones completas
- ✅ **Obtener Usuario** - Por ID y listado completo  
- ✅ **Actualizar Usuario** - Con verificaciones de integridad
- ✅ **Eliminar Usuario** - Con validaciones de existencia
- ✅ **Listar Usuarios** - Ordenado por fecha de creación

## Arquitectura del Proyecto

```
src/
├── main/java/com/alkemy/tdd/
│   ├── model/Usuario.java              # Entidad principal
│   ├── dao/
│   │   ├── UsuarioDAO.java             # Interfaz DAO
│   │   └── UsuarioDAOImpl.java         # Implementación JDBC
│   ├── service/
│   │   ├── UsuarioService.java         # Interfaz de servicio
│   │   └── UsuarioServiceImpl.java     # Lógica de negocio
│   ├── config/DatabaseConfig.java      # Configuración multi-entorno
│   └── exception/UsuarioException.java # Excepciones personalizadas
└── test/java/com/alkemy/tdd/
    ├── dao/UsuarioDAOIntegrationTest.java  # Tests de integración
    ├── service/UsuarioServiceTest.java     # Tests unitarios + Mockito
    ├── model/UsuarioTest.java              # Tests para equals, hashCode, toString
    └── exception/UsuarioExceptionTest.java # Tests para excepciones
```

## Tecnologías Utilizadas

- **Java 11** - Lenguaje principal
- **Maven 3.6+** - Gestión de dependencias
- **JUnit 5.9.2** - Framework de testing
- **Mockito 4.11.0** - Mocking de dependencias
- **Jacoco 0.8.8** - Cobertura de código
- **SQLite + JDBC** - Persistencia de datos
- **GitHub Copilot** - Asistente de código IA

## Métricas de Cobertura

### Evolución de Cobertura Jacoco

**Reporte Inicial (69%)**
```
com.alkemy.tdd.dao      78%
com.alkemy.tdd.config   55% 
com.alkemy.tdd.service  68%
com.alkemy.tdd.model    54%
com.alkemy.tdd.exception 44%
Total: 69%
```

**Reporte Final (81%)**
```
com.alkemy.tdd.dao      71%
com.alkemy.tdd.config   74%
com.alkemy.tdd.service  100% 🎯
com.alkemy.tdd.model    100% 🎯
com.alkemy.tdd.exception 100% 🎯
Total: 81% ✅
```

**Mejora lograda:** +12 puntos porcentuales

## Ejecutar el Proyecto

### Prerrequisitos
```bash
# Verificar Java 11+
java -version

# Verificar Maven 3.6+
mvn -version
```

### Comandos de Ejecución
```bash
# Clonar repositorio
git clone https://github.com/RobertoRivasL/ABP-Modulo-3.git
cd ABP-Modulo-3

# Compilar proyecto
mvn clean compile

# Ejecutar todos los tests
mvn test

# Generar reporte de cobertura
mvn jacoco:report

# Ver reporte HTML
open target/site/jacoco/index.html

# Verificar cobertura mínima (80%)
mvn jacoco:check
```

## Mejoras de Refactorizacion

### 1. DatabaseConfig Multi-Entorno
```java
public enum DatabaseEnvironment {
    DEVELOPMENT("jdbc:sqlite:database.db"),
    TEST("jdbc:sqlite:test.db"), 
    IN_MEMORY("jdbc:sqlite::memory:");
}
```

### 2. UsuarioDAOImpl Robusto
- ✅ JavaDoc completo en todos los métodos
- ✅ Manejo robusto de excepciones
- ✅ Mapeo flexible de tipos de fecha
- ✅ Validaciones adicionales

### 3. Suite de Tests Exhaustiva
- ✅ Tests unitarios con Mockito
- ✅ Tests de integración con BD real
- ✅ Cobertura de casos edge
- ✅ Tests para equals(), hashCode(), toString()

## Proceso TDD Aplicado

### Metodología Estricta
Para cada funcionalidad se siguió el ciclo:

```
🔴 RED → 🟢 GREEN → 🔵 REFACTOR
```

### Ejemplo: Crear Usuario
```java
// 1. RED - Test que falla
@Test
void deberiaCrearUsuarioValidoCorrectamente() {
    Usuario resultado = usuarioService.crearUsuario(usuario);
    assertNotNull(resultado.getId()); // FALLA
}

// 2. GREEN - Código mínimo
public Usuario crearUsuario(Usuario usuario) {
    return usuarioDAO.guardar(usuario);
}

// 3. REFACTOR - Mejorar código
public Usuario crearUsuario(Usuario usuario) {
    validarUsuario(usuario);
    validarEmailUnico(usuario.getEmail());
    return usuarioDAO.guardar(usuario);
}
```

## Base de Datos

### Schema SQL
```sql
CREATE TABLE usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### Configuración Multi-Entorno
```java
// Desarrollo
DatabaseConfig.configurarEntorno(DatabaseEnvironment.DEVELOPMENT);

// Testing  
DatabaseConfig.configurarEntorno(DatabaseEnvironment.TEST);

// En Memoria
DatabaseConfig.configurarEntorno(DatabaseEnvironment.IN_MEMORY);
```

## Troubleshooting

### Tests Fallan
```bash
mvn clean test
```

### Jacoco No Genera Reporte
```bash
mvn clean compile test jacoco:report
```

### Ver Detalles de Cobertura
```bash
# Abrir reporte HTML
open target/site/jacoco/index.html
```

## Autor

**Roberto Rivas L.**  
🎓 Estudiante ABP - Alkemy  
💻 Especialización: Java, Spring Boot, DevOps  
🛠️ Tecnologías: HTML, Seguridad, Containers, Kubernetes, Jenkins, JMeter, SonarQube, AWS  
📧 GitHub: [RobertoRivasL](https://github.com/RobertoRivasL)

## Logros del Proyecto

### Métricas Excepcionales
- 🎯 **81% cobertura** (supera 80% requerido)
- 🧪 **35+ tests** (vs 8 mínimo requerido)
- 🔄 **14+ ciclos TDD** documentados
- 🏆 **100% cobertura** en paquetes críticos

### Calidad Técnica
- ✅ **Código nivel producción** con Copilot
- ✅ **Arquitectura empresarial** multi-entorno
- ✅ **Documentación completa** JavaDoc
- ✅ **Principios SOLID** aplicados

### Metodología Avanzada
- ✅ **TDD estricto** RED-GREEN-REFACTOR
- ✅ **Mejora continua** demostrada (69%→81%)
- ✅ **Herramientas modernas** (GitHub Copilot)
- ✅ **Testing exhaustivo** con Mockito

---

**⭐ Proyecto desarrollado con excelencia técnica y metodología TDD profesional**
