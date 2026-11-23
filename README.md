# Automation Exercise con Serenity Screenplay

Proyecto de Calidad de Software 2026-2 para la tienda de práctica [Automation Exercise](https://automationexercise.com/). Incluye 4 features en inglés con 29 escenarios (3 `Scenario Outline`) y 33 casos ejecutables (`AC01`–`AC30`), además de pruebas unitarias sin navegador (`unitTest`). El flujo principal registra un comprador, agrega ropa al carrito, revisa la dirección y el total, paga con datos de prueba y verifica la confirmación del pedido.

## Equipo

| Integrante | Rol / Aportes |
|---|---|
| <!-- completar --> | <!-- completar --> |
| <!-- completar --> | <!-- completar --> |
| <!-- completar --> | <!-- completar --> |

## Ejecución en Windows

Abre esta carpeta como proyecto Gradle. Se requiere JDK 17 o 21, Google Chrome e Internet. El proyecto incluye Gradle Wrapper.

| Ejecución | Comando PowerShell |
|---|---|
| Batería completa | `.\gradlew.bat clean test` |
| Pruebas unitarias (sin navegador) | `.\gradlew.bat unitTest` |
| Demo guiada para sustentación (pausa `1500 ms` + reporte) | `.\gradlew.bat demo` |
| Demo guiada con pausa personalizada | `.\gradlew.bat demo "-Ddemo.delay=2000"` |
| Las dos rutas de compra completa sin pausa | `.\gradlew.bat test -Prunner=PurchaseRunner` |
| Caminos felices | `.\gradlew.bat test "-Dcucumber.filter.tags=@happy"` |
| Rutas alternas | `.\gradlew.bat test "-Dcucumber.filter.tags=@alternate"` |
| Caminos excepcionales | `.\gradlew.bat test "-Dcucumber.filter.tags=@negative"` |
| Escenarios límite | `.\gradlew.bat test "-Dcucumber.filter.tags=@boundary"` |
| Solo carrito | `.\gradlew.bat test "-Dcucumber.filter.tags=@cart"` |
| Un criterio de aceptación | `.\gradlew.bat test "-Dcucumber.filter.tags=@AC09"` |
| Validar los pasos de Cucumber sin abrir Chrome | `.\gradlew.bat clean test "-Dcucumber.execution.dry-run=true"` |

Opciones de consola y navegador soportadas por `test` y `demo`:

- `"-Dprogress.actions=false"`: oculta la narración por acción (`. Alex ...`) manteniendo el avance por escenario y paso.
- `"-Dprogress.colors=true"`: activa colores ANSI en consola (desactivados por defecto).
- `"-Ddemo.delay=<ms>"`: pausa después de cada paso en milisegundos (`0` por defecto en `test`, `1500` por defecto en `demo`).
- `"-Dheadless.mode=true"`: ejecuta Chrome sin ventana.

El reporte HTML de Serenity se genera en `target/site/serenity/index.html` con capturas por cada acción (`FOR_EACH_ACTION`). La tarea `demo` ejecuta exclusivamente `PurchaseRunner` (`AC11`/`AC12` y `AC26`) y regenera ese reporte al finalizar.

## Cómo leer la consola

Durante la ejecución, `ExecutionProgressReporter` muestra en tiempo real qué escenario va corriendo (`>>`), cada paso (`i/N ..` / `i/N OK` / `i/N FALLO`), las acciones de UI del actor (`Narrator`, con secretos enmascarados como `"****"`) y el resultado final del escenario (`<<`). Si un paso falla, imprime el diagnóstico de `FailureDiagnosis`:

```text
==== Automation Exercise | ejecucion iniciada 14:20:03 ====
>> [3] Checkout and order confirmation > Pay for a clothing order as a newly registered shopper  @checkout @AC11 @AC12 @e2e @happy
   1/9 .. Given the shopper has an empty cart
      . Alex hace clic en "Cart"
   1/9 OK Given the shopper has an empty cart  (4.8 s)
   4/9 .. When the shopper proceeds to checkout
   4/9 FALLO When the shopper proceeds to checkout  (10.2 s)
         Categoria:      ELEMENTO_NO_DISPONIBLE
         Causa probable: El elemento no aparecio o no se pudo usar dentro del tiempo de espera
         Que revisar:    El paso anterior llego a la pagina correcta? Cambio el localizador? El sitio esta lento?
         Detalle:        Expected condition failed: waiting for Proceed To Checkout to be clickable
         Ubicacion:      checkout.feature:12
   5/9 OMITIDO Then checkout shows the shopper's registered delivery address
<< FALLO [3] Pay for a clothing order as a newly registered shopper (31.0 s)
==== Resumen: 2 pasaron | 1 fallaron | 0 omitidos | 0 min 52 s ====
Reporte: target/site/serenity/index.html
```

Categorías de diagnóstico emitidas por `FailureDiagnosis`:

| Categoría | Cuándo aparece | Qué revisar |
|---|---|---|
| `SITIO_NO_DISPONIBLE` | La tienda muestra la página de saturación `heavy load (queue full)` | Reintentar; sobrecarga temporal del sitio |
| `REGLA_DE_NEGOCIO` | Falla una expectativa de negocio (`ShopExpectationError` / prefijo `AC##:`) | Comparar esperado y obtenido en el reporte Serenity |
| `VALOR_INESPERADO` | Otra aserción de pantalla no coincide con el texto/valor esperado | Esperado/obtenido de Hamcrest en el detalle |
| `ELEMENTO_TAPADO` | Otro control (modal o anuncio) intercepta el clic | Captura del paso y bloqueo de anuncios en `serenity.conf` |
| `ELEMENTO_NO_DISPONIBLE` | Timeout o elemento no encontrado/clickable dentro de la espera explícita | Página previa, localizador o lentitud del sitio |
| `PAGINA_RECARGADA` | El DOM cambió entre la búsqueda del elemento y su uso (`StaleElement`) | Esperar el estado final antes de leer o interactuar |
| `NAVEGADOR` | Error al iniciar sesión de Chrome (`SessionNotCreated`) | Versión de Chrome y Selenium Manager |
| `RED` | Error de conectividad o DNS (`ERR_NAME_NOT_RESOLVED`, `ERR_CONNECTION`, etc.) | Conexión a Internet y disponibilidad de la URL |
| `NO_CLASIFICADO` | Excepción no cubierta por las reglas anteriores | Traza completa en `target/site/serenity/index.html` |

*(Si Cucumber detecta un paso sin definición, el reporter muestra `PASO_INDEFINIDO`; ante una invocación nula en `FailureDiagnosis`, devuelve `SIN_ERROR`.)*

## Criterios de aceptación

| Criterio | Comportamiento verificable |
|---|---|
| AC01 | Se confirma el registro y el encabezado muestra el nombre del comprador. |
| AC02 | El formulario rechaza un correo registrado. |
| AC03 | El inicio de sesión válido muestra el nombre del comprador. |
| AC04 | La contraseña incorrecta produce un error. |
| AC05 | Una búsqueda por nombre incluye Blue Top sin distinguir mayúsculas o minúsculas (`Blue Top` y `blue top`). |
| AC06 | Una búsqueda sin coincidencias muestra cero productos. |
| AC07 | La ficha muestra el nombre Blue Top, el precio Rs. 500 y la disponibilidad. |
| AC08 | El carrito conserva 1 o 4 unidades y muestra los subtotales correspondientes. |
| AC09 | El carrito queda vacío al eliminar su único artículo. |
| AC10 | Al eliminar uno de dos artículos, se conserva el restante y su cantidad. |
| AC11 | El checkout muestra la dirección registrada, la cantidad esperada y el total. |
| AC12 | El pago con datos de prueba muestra el título y el mensaje de confirmación. |
| AC13 | El checkout como invitado muestra el acceso al registro o inicio de sesión. |
| AC14 | El checkout muestra exactamente los dos productos elegidos, sus cantidades y subtotales, y el total del pedido. |
| AC15 | Al cerrar sesión, se vuelve a la página de inicio de sesión y desaparece el nombre del comprador. |
| AC16 | La tienda confirma que eliminó la cuenta y rechaza sus credenciales en un nuevo intento de inicio de sesión. |
| AC17 | La categoría de tops para mujer muestra Blue Top y excluye Men Tshirt. |
| AC18 | Tras iniciar sesión, el carrito conserva el producto, la cantidad, el precio y el subtotal de la sesión de invitado. |
| AC19 | Al registrarse desde el checkout, el comprador puede retomarlo con el mismo producto, cantidad y total, y con las direcciones registradas de entrega y facturación. |
| AC20 | El formulario de inicio de sesión exige la contraseña y mantiene al comprador sin autenticar. |
| AC21 | Una búsqueda por nombre parcial permite abrir la ficha de Blue Top y verificar precio y disponibilidad. |
| AC22 | La cantidad inicial coincide con el mínimo de una unidad y el carrito conserva esa cantidad y su subtotal. |
| AC23 | El enlace del carrito vacío abre el catálogo y permite agregar un producto. |
| AC24 | Tras quitar el último producto, se muestra el aviso de carrito vacío y desaparece el acceso al checkout. |
| AC25 | Al cerrar el aviso de inicio de sesión en el checkout, se conservan el producto, la cantidad y el subtotal. |
| AC26 | Un comprador existente puede iniciar sesión desde el carrito invitado, verificar el pedido y obtener la confirmación. |
| AC27 | El resumen del pedido verifica dinámicamente que cada subtotal sea `precio * cantidad` y el total coincida con su suma (`1/1`, `3/2`, `10/1`). |
| AC28 | El formulario de pago exige el número de tarjeta (`:required:invalid`), permanece visible y no confirma el pedido cuando se omite. |
| AC29 | El formulario de registro exige la contraseña (`:required:invalid`), permanece visible y no crea la cuenta cuando se omite. |
| AC30 | Agregar el mismo producto dos veces (`1` y luego `2`) acumula una sola fila en el carrito con cantidad `3` y subtotal `Rs. 1500`. |

## Estructura

- `src/test/resources/features/automationexercise` contiene los 4 features en inglés declarativo con etiquetas `AC01`–`AC30`.
- `src/test/resources/serenity.conf` configura Chrome, bloqueos de anuncios, URL base (`pages.home`), esperas explícitas (`wait.for.timeout = 10000`, `timeouts.implicitlywait = 0`) y capturas (`FOR_EACH_ACTION`).
- `src/main/java/.../automationexercise/` organiza `tasks`, `interactions`, `questions`, `models`, `exceptions`, `userinterfaces` y `utils`.
- `src/test/java/.../automationexercise/` contiene `runners`, `stepdefinitions` (incluida la pausa de sustentación `DemoDelay`), el reporter en consola y las pruebas unitarias (`CartItemTest`, `FailureDiagnosisTest`, `MoneyTest`).
- [docs/hallazgos.md](docs/hallazgos.md) documenta el diagnóstico de pruebas reales en el sitio, las hipótesis verificadas, la evidencia HTML y el comportamiento previo de `AC27`–`AC30`.
- La estructura sigue el plugin [ScreenplayArchitecture](https://github.com/jumosqu12/ScreenplayArchitecture) de la guía.

## FIRST

**Fast** usa esperas explícitas y deja la pausa solo para `demo`. **Independent** prepara los datos de cada escenario en una sesión propia (`restart.browser.for.each = example`). **Repeatable** emplea un correo ficticio único y elimina la cuenta de prueba al finalizar cuando la tienda responde. **Self-validating** valida cada criterio mediante aserciones tipadas (`ShopExpectationError` + `orComplainWith` con prefijo `AC##:`) y complementa la verificación en consola con el diagnóstico legible de `ExecutionProgressReporter` y `FailureDiagnosis`. **Timely** vincula el diseño de escenarios y la verificación previa en el sitio real a los criterios `AC01`–`AC30`.

La cuenta y la tarjeta (`PaymentCard`) usan datos de prueba. Los flujos de referencia están en los [casos oficiales de Automation Exercise](https://automationexercise.com/test_cases).
