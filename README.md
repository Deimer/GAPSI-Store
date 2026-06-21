# GAPSIStore

Aplicación Android de búsqueda de productos para el examen de **GAPSI**, construida con **Clean Architecture** modular y **Jetpack Compose**, integrando la API de Axesso (Walmart Data Service) vía RapidAPI.

## Requisitos del examen

- Buscar productos por palabra clave, con paginación real contra el servicio.
- Mostrar título, precio e imagen (thumbnail) de cada producto.
- Persistir el historial de búsquedas, incluso tras reiniciar la app.
- UI no bloqueante, en Kotlin, para Android 12+ (API 31).

## Stack técnico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation3 |
| Inyección de dependencias | Hilt |
| Red | Retrofit + OkHttp + Gson |
| Persistencia | Room |
| Paginación | Paging 3 |
| Imágenes | Coil |
| Tests | JUnit, Mockito (mockito-kotlin), Coroutines Test, Robolectric, Paging Testing |
| Min SDK / Compile SDK | 31 / 36 |

## Arquitectura

Clean Architecture con 6 módulos Gradle:

```
app ─┬─> domain ─┬─> datasource ─┬─> network
     │           │               └─> database
     └─> design-system
```

| Módulo | Responsabilidad |
|---|---|
| `network` | Cliente Retrofit/OkHttp, DTOs, deserialización custom del JSON de Axesso |
| `database` | Persistencia con Room (caché de productos + historial de búsqueda) |
| `datasource` | Interfaces y fuentes de datos remotas/locales, sin lógica de negocio |
| `domain` | Modelos, mappers, `PagingSource`, repositorios, casos de uso |
| `design-system` | Theme de Compose y componentes UI reutilizables |
| `app` | Features (Splash, Home), navegación, ViewModels, punto de entrada |

### Flujo de datos para la búsqueda de productos

```
ProductDTO (red) → ProductEntity (persistido en Room) → ProductModel (UI)
```

Room actúa como *single source of truth*: ningún dato llega a la UI directamente desde la red sin pasar primero por la base de datos local.

## Decisiones de diseño relevantes

### Paginación

`SearchProductsUseCase` expone `Flow<PagingData<ProductModel>>` mediante un `Pager` configurado con `pageSize = 40` (alineado con el tamaño de página real que devuelve Walmart). `ProductPagingSource` llama directamente a la API por cada página solicitada, persiste el resultado en Room dentro del mismo `load()`, y construye el `ProductModel` final a partir de lo efectivamente guardado — nunca desde el DTO crudo.

### Deserialización del JSON de Axesso

La respuesta de Axesso anida los productos 5+ niveles de profundidad (`item.props.pageProps.initialData.searchResult.itemStacks[0].items[]`) y mezcla productos reales con elementos no relevantes (`AdPlaceholder`). Un `JsonDeserializer` custom (`WalmartSearchResultDeserializer`) navega esa estructura, filtra por `__typename == "Product"`, y resuelve el precio real a partir de `priceInfo.linePriceDisplay` (con fallback al campo `price` truncado).

Gson no respeta de forma confiable un deserializer custom registrado para tipos de colección genéricos (`List<T>`), así que el resultado se envuelve en `WalmartSearchResponseDTO` (un tipo concreto, no colección) antes de aplicar el adapter — esto evita que Gson recurra a su parser por defecto, que esperaría la raíz del JSON como un array.

### Historial de búsqueda con thumbnail

Cada búsqueda se guarda con la imagen del primer resultado, y se refresca si el usuario repite la misma búsqueda más tarde (gracias a un índice `UNIQUE` sobre `keyword` en `SearchHistoryEntity`, combinado con `OnConflictStrategy.REPLACE`).

La captura del thumbnail ocurre **sin disparar ninguna llamada de red adicional**: la UI (`HomeScreenCompose`), al detectar que Paging ya materializó el primer producto real de una búsqueda nueva (vía `LazyPagingItems`), lo reporta al `ViewModel` a través de la acción `onFirstResultLoaded`, que persiste la búsqueda usando `SaveSearchUseCase`. Si la búsqueda no devuelve resultados, no se guarda nada en el historial.

### Convención de errores en `domain`

Los flujos reactivos de la capa `domain` exponen `Flow<Result<T>>` (por ejemplo, `SearchHistoryRepository.fetchSearchHistory()`), de modo que la capa de presentación pueda distinguir explícitamente entre éxito y fallo sin depender de excepciones no controladas.

## Configuración del proyecto

### API Key

El proyecto usa el plugin `secrets-gradle-plugin` para mantener la API key de RapidAPI fuera del control de versiones.

1. Crea (o edita) el archivo `local.properties` en la raíz del proyecto.
2. Agrega tu key de RapidAPI para Axesso Walmart Data Service:

   ```properties
   RAPIDAPI_KEY=tu_api_key_aqui
   ```

`local.defaults.properties` (versionado, en la raíz del proyecto) contiene un valor vacío por defecto para que el proyecto compile sin la key real, aunque las búsquedas no funcionarán sin una key válida.

### Compilar y ejecutar

1. Clona el repositorio.
2. Configura `local.properties` como se indica arriba.
3. Sincroniza Gradle.
4. Ejecuta la configuración `app` en un emulador o dispositivo con Android 12 (API 31) o superior.

### Tests

Cada módulo incluye tests unitarios (`src/test`) y, donde aplica, instrumentados (`src/androidTest`, en `database`):

```bash
./gradlew test                 # todos los tests unitarios
./gradlew connectedAndroidTest # tests instrumentados (requiere emulador/dispositivo)
```

## Estructura de paquetes (resumen)

```
com.deymervilla.gapsistore
├── network/                  # módulo :network
│   ├── api/                  # ApiService (Retrofit)
│   ├── constants/            # endpoints, headers, paths del JSON
│   ├── di/                   # ApiModule (Hilt)
│   ├── dto/                  # ProductDTO, WalmartSearchResponseDTO, etc.
│   └── parser/                # WalmartSearchResultDeserializer
├── database/                  # módulo :database
│   ├── constants/             # nombres de tablas y columnas
│   ├── dao/                   # ProductDao, SearchHistoryDao
│   ├── di/                    # RoomModule (Hilt)
│   └── entities/               # ProductEntity, SearchHistoryEntity
├── datasource/                 # módulo :datasource
│   ├── local/                  # ProductLocalDataSource, SearchHistoryLocalDataSource
│   ├── remote/                 # ProductRemoteDataSource
│   └── di/                     # DataSourceModule (Hilt)
├── domain/                     # módulo :domain
│   ├── models/                 # ProductModel, SearchHistoryModel
│   ├── mappers/                 # DTO->Entity, Entity->Model
│   ├── paging/                  # ProductPagingSource
│   ├── repositories/            # ProductRepository, SearchHistoryRepository
│   ├── usecase/                  # casos de uso por feature
│   └── di/                       # RepositoryModule (Hilt)
├── design-system/                 # módulo :design-system
│   ├── theme/                     # Color, Type, Theme
│   └── components/                 # ProductCard, SearchField, etc.
└── app/                             # módulo :app
    ├── features/
    │   ├── splash/
    │   └── home/                    # búsqueda, historial, grid paginado
    ├── navigation/                   # Navigation3 (AppRoutes, AppNavigation)
    ├── di/                            # DispatcherModule
    ├── utils/                         # FlowExtensions (helpers MVI)
    ├── application/                    # GAPSIStoreApplication
    └── main/                            # MainActivity
```

## Alcance actual / próximos pasos

- No hay pantalla de detalle de producto: el listado (título, precio, imagen) cubre el requisito del examen; el tap en una tarjeta queda preparado pero sin navegación.
- La paginación llama directamente a la API por cada página (sin `RemoteMediator`); Room se usa como caché de lo ya descargado, no como fuente única de verdad del paginado en sí — una posible mejora futura.
