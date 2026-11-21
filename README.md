# Automation Exercise con Serenity Screenplay

Proyecto de Calidad de Software 2026-2 para la tienda de práctica [Automation Exercise](https://automationexercise.com/). Incluye 4 features en inglés con 25 escenarios y 26 casos ejecutables. El flujo principal registra un comprador, agrega ropa al carrito, revisa la dirección y el total, paga con datos de prueba y verifica la confirmación del pedido.

## Ejecución en Windows

Abre esta carpeta como proyecto Gradle. Se requiere JDK 17 o 21, Google Chrome e Internet. El proyecto incluye Gradle Wrapper.

| Ejecución | Comando PowerShell |
|---|---|
| Batería completa | `.\gradlew.bat clean test` |
| Las dos rutas de compra completa | `.\gradlew.bat test -Prunner=PurchaseRunner` |
| Caminos felices | `.\gradlew.bat test "-Dcucumber.filter.tags=@happy"` |
| Rutas alternas | `.\gradlew.bat test "-Dcucumber.filter.tags=@alternate"` |
| Caminos excepcionales | `.\gradlew.bat test "-Dcucumber.filter.tags=@negative"` |
| Escenarios límite | `.\gradlew.bat test "-Dcucumber.filter.tags=@boundary"` |
| Solo carrito | `.\gradlew.bat test "-Dcucumber.filter.tags=@cart"` |
| Un criterio de aceptación | `.\gradlew.bat test "-Dcucumber.filter.tags=@AC09"` |
| Compra con pausa para la sustentación | `.\gradlew.bat test -Prunner=PurchaseRunner "-Ddemo.delay=1500"` |
| Validar los pasos de Cucumber sin abrir Chrome | `.\gradlew.bat test "-Dcucumber.execution.dry-run=true"` |

El reporte HTML de Serenity se genera en `target/site/serenity/index.html`. La pausa de la sustentación está desactivada por defecto. También puedes configurar Chrome sin ventana con `"-Dheadless.mode=true"`.

## Criterios de aceptación

| Criterio | Comportamiento verificable |
|---|---|
| AC01 | Se confirma el registro y el encabezado muestra el nombre del comprador. |
| AC02 | El formulario rechaza un correo registrado. |
| AC03 | El inicio de sesión válido muestra el nombre del comprador. |
| AC04 | La contraseña incorrecta produce un error. |
| AC05 | Una búsqueda por nombre incluye Blue Top. |
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

## Estructura

- `src/test/resources/features/automationexercise` contiene los escenarios Gherkin y etiquetas por criterio.
- `src/test/resources/serenity.conf` configura Chrome y las capturas.
- Los paquetes `runners` y `stepdefinitions` enlazan Cucumber con Serenity y el actor Alex.
- Los paquetes `tasks`, `interactions`, `questions` y `userinterfaces` organizan el flujo de negocio, las acciones, las consultas y los localizadores.
- La estructura sigue el plugin [ScreenplayArchitecture](https://github.com/jumosqu12/ScreenplayArchitecture) de la guía.

## FIRST

**Fast** usa esperas de interfaz y deja la pausa solo para la sustentación. **Independent** prepara los datos de cada escenario en una sesión propia. **Repeatable** emplea un correo ficticio único y elimina la cuenta de prueba al finalizar cuando la tienda responde. **Self-validating** comprueba los resultados mediante aserciones. **Timely** vincula el diseño de escenarios a los criterios de aceptación del taller.

La cuenta y la tarjeta usan datos de prueba. Los flujos de referencia están en los [casos oficiales de Automation Exercise](https://automationexercise.com/test_cases).
