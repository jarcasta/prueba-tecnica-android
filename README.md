# PokeSearch

[![CI](https://github.com/jarcasta/prueba-tecnica-android/actions/workflows/ci.yml/badge.svg)](https://github.com/jarcasta/prueba-tecnica-android/actions/workflows/ci.yml)

App Android nativa en Kotlin y Jetpack Compose (Material 3) para explorar Pokémon, categorías de items, regiones y generaciones de la [PokeAPI](https://pokeapi.co/) y guardarlos como favoritos. Funciona sin conexión con los datos ya sincronizados y admite varios usuarios locales en el mismo dispositivo.

## Funcionalidades

- **Login multiusuario sin contraseña.**
  - El nombre de usuario tiene de 3 a 20 letras o números y se normaliza a minúsculas.
  - Si el nombre ya existe, se restaura su sesión con sus favoritos; si no, se crea el usuario.
  - "Continuar como" lista los usuarios conocidos por último acceso, con sus iniciales, su cantidad de favoritos y el tiempo desde el último acceso.
- **Sesión.** Al abrir la app se va directo al Dashboard si hay sesión activa.
  - Cerrar sesión no borra al usuario ni sus favoritos.
  - "Restablecer mis datos" borra solo los favoritos del usuario activo.
  - Las dos acciones piden confirmación.
- **Dashboard** con dos tabs, Explorar y Favoritos. Cada una conserva su scroll, su categoría y su búsqueda al cambiar de tab.
- **Explorar.**
  - Cuatro categorías y scroll infinito: páginas de 50 que se piden cuando faltan 5 elementos para el final.
  - Contador de resultados cuando la categoría terminó de cargar, y botón "volver arriba".
  - Estados de carga (skeleton), error con "Reintentar", vacío, error al paginar y sin conexión.
- **Favoritos.**
  - Son de cada usuario. Se marcan desde Explorar y Detalle, se desmarcan también desde Favoritos, y el cambio se ve en todas las pantallas.
  - Búsqueda local por nombre y filtro por categoría.
  - Al quitar uno aparece un Snackbar con "Deshacer", que lo restaura en su misma posición.
- **Detalle por categoría.**
  - **Pokémon**: artwork, tipos, altura en metros, peso en kg, habilidades y estadísticas base.
  - **Categoría de items**: su pocket y sus items, con sprite.
  - **Región**: generación principal, cantidad de locaciones, pokédexes y grupos de versiones.
  - **Generación**: región principal, cantidad de especies nuevas, grupos de versiones y las primeras 12 especies; tocar una abre su Detalle.
- **Sin conexión.**
  - Con datos guardados, Explorar y Detalle muestran un banner.
  - Al volver la red (después de 2 s estable), se reintenta automáticamente lo que había fallado.
- **Tema** claro y oscuro según el sistema, con dynamic color en Android 12 o superior.

## Búsqueda

La PokeAPI no tiene búsqueda por texto en sus listados: solo pagina con `offset` y `limit`. Por eso el requisito de búsqueda (local o remota) se cubre así:

| Pantalla  | Cómo se busca                                              |
|-----------|------------------------------------------------------------|
| Explorar  | Navegación por categoría con scroll infinito               |
| Favoritos | Búsqueda local por nombre, con debounce de 200 ms, sin red |
| Favoritos | Filtro por categoría, con la opción "Todas"                |


## Arquitectura

Arquitectura hexagonal con vertical slicing, en un único módulo Gradle. 
La UI sigue MVVM con flujo de datos unidireccional (patrón Route / Screen).

```
com.alejandro.pokesearch
├── app            # Composition root: Application, MainActivity, AppDatabase, AppNavHost
├── core
│   ├── domain     # Compartido: PokeCategory, EntryKey, DomainError, Outcome, Clock, NetworkMonitor
│   ├── network    # Retrofit/OkHttp, mapeo de errores, URLs de sprites, ConnectivityNetworkMonitor
│   ├── designsystem # Tema M3, colores de tipo y componentes compartidos
│   ├── coroutines # CoroutineScope de aplicación
│   └── time       # Reloj del sistema
└── feature
    ├── auth       # Usuarios y sesión
    ├── dashboard  # Barra superior, menú de usuario, tabs
    ├── catalog    # Explorar
    ├── favorites  # Favoritos
    └── detail     # Detalle
```

Cada slice de feature se divide en:
- domain (modelos y puertos, Kotlin puro)
- application (casos de uso)
- adapter/in/ui (Compose y ViewModels)
- adapter/out (Room y Retrofit)
- di (Hilt).


### Offline first

Room es la única fuente de verdad: la UI siempre lee de la base local y la red solo sincroniza.

- **Explorar**: Paging 3 con `RemoteMediator`. La red escribe páginas en Room y la lista pagina desde el `PagingSource` de Room, con placeholders para que el scroll no salte. Si el refresh falla, los datos guardados se conservan. Una categoría sincronizada hace menos de 24 h no se vuelve a pedir al abrirla.
- **Detalle**: se guarda el JSON del DTO recortado en `detail_cache`. Si no hay caché o tiene más de 24 h, se refresca. Si el refresco falla y hay caché, se muestra la caché con aviso; si no hay caché, se muestra el error.
- **Favoritos**: guardan un snapshot (nombre e imagen), así que no necesitan la red.
- **Conectividad**: el puerto `NetworkMonitor` (`Flow<Boolean>`) lo implementa `ConnectivityNetworkMonitor` con `registerDefaultNetworkCallback`. Es un solo callback compartido para toda la app.

### Base de datos (Room)

| Tabla | Contenido |
|---|---|
| `users` | Usuarios locales (`username` único) |
| `session` | Sesión activa (una sola fila) |
| `favorites` | Favoritos por usuario con su snapshot; se borran en cascada con el usuario |
| `catalog_entries` | Entradas listadas por categoría, en orden de la API |
| `sync_state` | Estado de paginación por categoría (`nextOffset`, `endReached`, `lastSyncedAt`) |
| `detail_cache` | JSON del detalle por entrada y fecha de descarga |

Las migraciones son automáticas y el esquema se exporta en `app/schemas/`.

## Decisiones técnicas

| Decisión | Resumen |
|---|---|
| Hexagonal + vertical slices en un módulo | Un solo `:app` organizado por feature (`domain`, `application`, `adapter`, `di`). |
| Room como fuente de verdad | Toda la UI lee de Room; Explorar usa `RemoteMediator`, que escribe las páginas de la red en Room y pagina desde su `PagingSource`. El offset sale de `sync_state`, no del conteo de filas. |
| `cachedIn` antes del `combine` | `flatMapLatest → cachedIn → combine(favoritos)`: marcar un favorito vuelve a mapear las páginas en caché sin reiniciar la paginación. Un test falla si se invierte el orden. |
| Placeholders en el catálogo | `enablePlaceholders = true` mantiene estables los índices cuando Room invalida el `PagingSource`, y el scroll ya no salta hacia atrás. |
| Caché del detalle como JSON | `detail_cache` guarda el JSON del DTO recortado y lo mapea con el mismo mapper que la red: una tabla para cuatro formas de detalle. |
| `PagingData` en el puerto | `CatalogRepository` expone `Flow<PagingData>` como única excepción de framework en el dominio (`paging-common` no depende de Android), limitada a `CatalogRepository` y `ObserveCatalogUseCase`. |
| Lenguaje publicado entre slices | Un slice usa la `application` de otro y solo los tipos de dominio publicados (`UserId`, `FavoriteCountReader`, `FavoriteSnapshot`). |
| `NetworkMonitor` con reintento | Puerto en `core/domain` con un único callback compartido (`shareIn`); al volver la red y mantenerse 2 s, se reintenta lo que falló (el DNS puede fallar justo tras la validación). |
| Tabs como slots | `DashboardRoute` recibe Explorar y Favoritos como `@Composable` desde `AppNavHost`, así el dashboard no importa la UI de otros slices y cada tab conserva su estado. |

## Trade-offs

| Decisión | Lo que se gana | Lo que se cede |
|---|---|---|
| Un solo módulo Gradle | Sin build logic multi-módulo; refactors y navegación entre slices simples | El aislamiento entre slices es por convención, no lo garantiza el compilador; sin builds incrementales por módulo |
| `PagingData` (androidx) en el puerto del dominio | Se reutilizan `cachedIn`, `map` y `collectAsLazyPagingItems` sin envolver ni desenvolver | El dominio de catalog queda acoplado a `paging-common` |
| Caché del detalle como JSON | Una sola tabla y sin migraciones para cuatro formas de datos de solo lectura | No se puede consultar por campos con SQL; si el DTO cambia, la caché vieja se descarta y se vuelve a pedir |
| Login sin contraseña | Cambio de usuario inmediato, sin backend ni gestión de credenciales | Cualquiera con acceso al dispositivo puede entrar como otro usuario; los perfiles no son privados |
| Sin filtros por atributo | Esquema, sincronización y estados offline más simples | No se puede filtrar Explorar por tipo ni por pocket |
| Snapshot en el favorito | Favoritos funcionan sin red y sin depender de catalog o detail | Si la API cambiara un nombre o una imagen, el favorito conservaría el valor guardado |
| ViewModels que observan la red con `Eagerly` | El reintento ve la reconexión aunque la app esté en segundo plano | El callback de red sigue registrado mientras viven los ViewModels |
| Vigencia de 24 h (catálogo y detalle) | Menos peticiones y el scroll restaurado no se rompe al reabrir | Un cambio en la API puede tardar hasta 24 h en verse |

## Qué no se hizo y por qué

| Pendiente | Motivo                                                                                                                                                                                                              |
|---|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Filtros por tipo y pocket | La API no da conteos de miembros y sincronizar las opciones y membresías costaba ~20 llamadas iniciales, más tablas y casos offline. La arquitectura permite agregarlos como otro puerto y tablas en el slice catalog. |
| Búsqueda remota | La PokeAPI no ofrece búsqueda por texto en los listados.                                                                                                                                  |
| Compose Multiplatform | Room, Hilt y Paging se usan en sus variantes Android.                                                                                                                   |
| SplashScreen API | Habría sumado `core-splashscreen`; el parpadeo del arranque se evita sin montar el NavHost hasta conocer la sesión (`MainViewModel`).                                                                               |
| Contador de resultados durante la carga | El `count` de la API no se guarda, así que el contador aparece solo cuando la categoría terminó de cargar.                                                                                                          |
| Imágenes de categorías de items, regiones y generaciones | La API no tiene imagen para ellas en el listado; se muestran con un monograma.                                                                                                                                      |

## Bonus implementados

| Bonus | Dónde se ve |
|---|---|
| Paging 3 con Compose  | `CatalogRemoteMediator`, `OfflineFirstCatalogRepository`, `collectAsLazyPagingItems()` en `ExploreRoute` |
| Tests instrumentados con Compose Testing | `LoginScreenTest`, `ExploreScreenTest`, `FavoritesScreenTest`, `DetailScreenTest` (`app/src/androidTest`) |
| Animaciones nativas de Compose | Pop y `Crossfade` en `FavoriteButton`; barras animadas en `StatBar`; skeleton con `rememberInfiniteTransition`, `OfflineBanner` con `AnimatedVisibility` y `PagingFooter` con `Crossfade` (`StatusStates.kt`); `animateItem()` en `FavoritesScreen`; botón "volver arriba" en `ExploreScreen`; `AnimatedContent` en el botón de `LoginScreen` |
| Modo oscuro + dynamic color | `PokeSearchTheme` (`core/designsystem/theme/Theme.kt`), con colores de tipo en `TypeColors.kt` |
| Coil | `AsyncImage` en `ResultListItem`, `DetailScreen` y `CategoryDetailContents`, con `coil-network-okhttp` |
| CI con GitHub Actions | [`.github/workflows/ci.yml`](.github/workflows/ci.yml) |

## Cómo ejecutarlo

**Requisitos**: Android SDK con la plataforma 37. El daemon de Gradle usa JDK 25 (`gradle/gradle-daemon-jvm.properties`) y Gradle lo descarga automáticamente si no está instalado. No hace falta configurar una API key: la PokeAPI es pública.

```bash
./gradlew assembleDebug                # compilar
./gradlew installDebug                 # instalar en un dispositivo o emulador
./gradlew testDebugUnitTest            # tests unitarios
./gradlew connectedDebugAndroidTest    # tests instrumentados (requiere dispositivo o emulador)
./gradlew lintDebug                    # lint
```

Para analizar recomposiciones, `./gradlew :app:compileReleaseKotlin -PcomposeCompilerReports=true` genera los reportes de estabilidad del compilador de Compose en `app/build/compose_compiler/`. El build normal no los genera.

## Integración continua

El workflow [`.github/workflows/ci.yml`](.github/workflows/ci.yml) se ejecuta en cada push a `master` y `develop`, y en cada pull request hacia esas ramas:

- Instala JDK 25 (Temurin) y prepara Gradle con caché (`gradle/actions/setup-gradle`).
- Ejecuta `assembleDebug testDebugUnitTest lintDebug`.
- Sube los reportes de tests y de lint como artefacto (`reports`), también cuando el build falla.

Los tests instrumentados no corren en CI porque necesitan un emulador; se ejecutan en local con `connectedDebugAndroidTest`.
