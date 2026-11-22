# Hallazgos del diagnóstico — Fase 2

Diagnóstico realizado sobre el commit `2311e3a` (antes de modificar código de producción de pruebas), a partir de **dos corridas completas consecutivas** de `.\gradlew.bat clean test` y la inspección directa de los reportes, capturas PNG y fuentes HTML (`pagesource*.html`) generados por Serenity BDD 4.1.3 en `target/site/serenity/`.

## 1. Resumen de las dos corridas diagnósticas base

| Corrida | Commit base | Pasaron | Fallaron | Omitidos | Escenarios fallidos |
|---|---|---|---|---|---|
| Corrida 1 (fin de Fase 1) | `2311e3a` | 21 | 5 | 0 | `AC11/AC12`, `AC14`, `AC19`, `AC26`, `AC08` (ej. 2, qty 4) |
| Corrida 2 (revisión Fase 1) | `2311e3a` | 20 | 6 | 0 | `AC11/AC12`, `AC14`, `AC19`, `AC26`, `AC08` (ej. 2, qty 4), `AC10` |

Ningún escenario fue eliminado, comentado ni relajado; ningún valor esperado fue modificado.

---

## 2. Escenarios fallidos en las corridas diagnósticas

> **Nota sobre evidencia permanente:** Los archivos bajo `target/site/serenity/` son efímeros y se eliminan en cada `clean`. Por ello, la **evidencia permanente y consultable** de este diagnóstico son los fragmentos HTML literales conservados en la sección 4. Asimismo, Serenity nombra las capturas PNG mediante el hash de su contenido visual; pantallas idénticas en escenarios distintos (por ejemplo `35e3ada…` en `/checkout` o `88c133aa…` en `heavy load`) comparten el mismo nombre de archivo.

| AC | Escenario | Paso que falla | Categoría (FailureDiagnosis) | ¿Intermitente? | Causa raíz confirmada | Evidencia | Corrección |
|---|---|---|---|---|---|---|---|
| AC11, AC12 | Pay for a clothing order as a newly registered shopper | `checkout.feature:14` (`And the order summary lists only "Blue Top" with a quantity of 2 and a subtotal of "Rs. 1000"`) y posteriormente `checkout.feature:16` (`When the shopper submits the order with test payment details`) | `VALOR_INESPERADO` (H1) / `ELEMENTO_NO_DISPONIBLE` (`PLACE_ORDER`) | No (falló 2/2 en H1; 1/1 tras resolver H1) | **1) H1 confirmada:** en `/checkout` la tabla está dentro de `<div id="cart_info">` **sin** `id="cart_info_table"`, así que `CartPage.PRODUCT_NAMES` devolvió `[]`. **2) Selector ambiguo en `CheckoutPage.PLACE_ORDER`:** al pasar H1, `a[href='/payment']` era compilado por `Selectors.isXPath(...)` de Serenity 4.1.3 como `By.xpath("a[href='/payment']")` en vez de CSS, devolviendo 0 coincidencias pese a estar presente en el HTML. | Captura `target/site/serenity/35e3ada915edf4859e8ba37f306abb5c96e8e351a14260ed8a0db5d1d5f721a9.png`, `pagesource18404122679679757624.html` y `pagesource12987506221858247123.html` (sección 4.1). | Anclar `CartPage.ROW` y `CartPage.PRODUCT_NAMES` a `#cart_info` y `tr[id^='product-']`; prefijar `CheckoutPage.PLACE_ORDER` con `css:a[href='/payment']`. |
| AC14 | Calculate the total for two products with different quantities | `checkout.feature:25` (`Then the order summary lists only "Blue Top" and "Men Tshirt"`) | `VALOR_INESPERADO` | No (falló 2/2) | **H1 confirmada.** Misma causa: `CartPage.PRODUCT_NAMES` busca `#cart_info_table`, inexistente en `/checkout`, y devolvió `[]` (`no item matches: "Blue Top", "Men Tshirt" in []`). | Captura `target/site/serenity/df777055169464c32983d73ba8139589df5cffe62912155f5cf9ba7e35c5ebcd.png` y `pagesource489445336113776824.html` (contiene `Blue Top` y `Men Tshirt` dentro de `#cart_info` sin `id="cart_info_table"`). | Misma corrección en `CartPage.java`. |
| AC19 | Resume checkout after registering from the guest cart | `checkout.feature:45` (`And the order summary lists only "Blue Top" with a quantity of 2 and a subtotal of "Rs. 1000"`) | `VALOR_INESPERADO` | No (falló 2/2) | **H1 confirmada** (H4 descartada). El sitio sí conservó el carrito tras registrarse desde el modal de checkout, pero `CartPage.PRODUCT_NAMES` no encontró `#cart_info_table` en `/checkout`. | Captura `target/site/serenity/35e3ada915edf4859e8ba37f306abb5c96e8e351a14260ed8a0db5d1d5f721a9.png` y `pagesource3923873200062685372.html` (`<tr id="product-1">` con `Blue Top` en `#cart_info`). | Misma corrección en `CartPage.java`. |
| AC26 | Sign in from guest checkout and complete the order | `checkout.feature:73` (`And the order summary lists only "Blue Top" with a quantity of 1 and a subtotal of "Rs. 500"`) y posteriormente `checkout.feature:75` (`When the shopper submits the order with test payment details`) | `VALOR_INESPERADO` (H1) / `ELEMENTO_NO_DISPONIBLE` (`PLACE_ORDER`) | No (falló 2/2 en H1; 1/1 tras resolver H1) | **1) H1 confirmada** (H4 descartada): el sitio conservó el carrito invitado tras iniciar sesión, pero `CartPage.PRODUCT_NAMES` buscaba `#cart_info_table`. **2) Selector ambiguo en `CheckoutPage.PLACE_ORDER`:** tras resolver H1, `Selectors.isXPath("a[href='/payment']")` de Serenity 4.1.3 lo interpretó como XPath (`By.xpath`) en vez de CSS. | Captura `target/site/serenity/8e6ea45ea77b3e78b831af24d7f26f0d39d141d836bf0b807d769d2f1a7a842a.png`, `pagesource18404122679679757624.html` y `pagesource12987506221858247123.html` (sección 4.1). | Misma corrección en `CartPage.java` y prefijar `CheckoutPage.PLACE_ORDER` con `css:a[href='/payment']`. |
| AC08 (ej. 2, qty 4) | Add Blue Top to the cart with quantity 4 | `shopping_cart.feature:7` (`Given the shopper has an empty cart`) | `ELEMENTO_NO_DISPONIBLE` | No (falló 2/2 solo en el 2.º ejemplo del Scenario Outline) | `src/test/resources/serenity.conf` tenía `restart.browser.for.each = scenario`. En Serenity 4.1.3 (`RestartBrowserForEach`), el nivel `SCENARIO` (`order=2`) **no** reinicia el navegador entre ejemplos de un mismo `Scenario Outline` (`EXAMPLE`, `order=3`). El ejemplo 2 reutilizó la sesión del ejemplo 1 con `Blue Top` (qty `1`) ya en el carrito y `#empty_cart` oculto (`style="display: none;"`). | Captura `target/site/serenity/31a517b7bf3ca293f19f3aed0e5d4c2ec942f0c038e356ef67118aeb93a05ebf.png` (muestra el carrito con `Blue Top` qty `1`) y HTML `pagesource805798732521902131.html` (sección 4.2). | Cambiar `restart.browser.for.each = example` en `src/test/resources/serenity.conf` para aislar cada ejemplo de `Scenario Outline`. |
| AC10 | Remove one product from a cart with two different products | `shopping_cart.feature:25` (`heavy load`, corrida base 2) y `shopping_cart.feature:27` (`And the shopper removes "Blue Top" from the cart` en validación intermedia) | `ELEMENTO_NO_DISPONIBLE` | Sí (pasó en corrida base 1; falló en corrida base 2 y validación intermedia) | **1) Inestabilidad puntual del servidor público** en corrida base 2 (`heavy load (queue full)`). **2) Sondeo de 0 s en `WaitUntil` (Serenity 4.1.3):** al validar el borrado AJAX (`ManageCart.remove`), `WaitUntil.the(ROW_FOR, isNotVisible())` sin `.forNoMoreThan(...)` toma el timeout de `timeouts.implicitlywait` (`0`), fallando en el primer chequeo mientras la petición AJAX eliminaba la fila milisegundos después. | Corrida base 2: `88c133aa67a763fdf63c5db6ba008c25a256fbd8b9515e816625f3c47cb294ec.png` y `pagesource1448032984607189993.html` (sección 4.3). Validación intermedia: `d717e976c5f8c08fbf541f076ea67188631a7054f3ca683b09f22bd0b4fd2bc1.png` y `pagesource4658463512244636955.html` (muestra `Men Tshirt` sin `Blue Top` instantes después del fallo en `1.9 s`). | Agregar `.forNoMoreThan(10).seconds()` a los `WaitUntil.the(...)` existentes manteniendo `timeouts.implicitlywait = 0` y `wait.for.timeout = 10000` (y sin debilitar aserciones por inestabilidad del sitio). |

---

## 3. Verificación de hipótesis H1–H6

| H | Hipótesis | Resultado | Evidencia concreta | Acción tomada |
|---|---|---|---|---|
| H1 | En `/checkout` la tabla **no** tiene `id="cart_info_table"` (en `/view_cart` sí). | **Confirmada** | En los 4 `pagesource*.html` capturados en `/checkout` (`18404122679679757624`, `3923873200062685372`, `4883248913321267095`, `489445336113776824`), la tabla es `<div class="table-responsive cart_info" id="cart_info"><table class="table table-condensed">` sin `id="cart_info_table"`. En `/view_cart` (`pagesource805798732521902131.html`), sí tiene `id="cart_info_table"` dentro de `<div id="cart_info">` y filas `<tr id="product-1">`. | Anclar `CartPage.ROW` a `//div[@id='cart_info']//tr[starts-with(@id,'product-') and ...]` y `CartPage.PRODUCT_NAMES` a `#cart_info tr[id^='product-'] .cart_description h4 a`. |
| H2 | Anuncios o el *interstitial* `#google_vignette` siguen apareciendo porque faltan reglas en `--host-resolver-rules`. | **Descartada** | En las 2 corridas completas (50 resultados / 52 ejecuciones) hubo **0** apariciones de `#google_vignette` y **0** `ElementClickInterceptedException`. En los HTML capturados de `/checkout` y `/view_cart`, el único dominio de anuncios presente es `https://pagead2.googlesyndication.com`, ya bloqueado en `serenity.conf`. | No se agregan reglas innecesarias a `--host-resolver-rules`. |
| H3 | 10 s (`wait.for.timeout`) no alcanzan cuando el sitio está lento. | **Descartada** | Descartada; el problema real en `AC10` era una espera efectiva de `0 s` al omitir `.forNoMoreThan(...)` con `timeouts.implicitlywait = 0` (ver §6), no la falta de tiempo sobre 10 s; `AC08` ej. 2 falló por sesión compartida y `AC10` en corrida base 2 por la página `heavy load (queue full)`. | Se mantiene `wait.for.timeout = 10000`. |
| H4 | El carrito de invitado no se conserva al iniciar sesión o registrarse. | **Descartada** | `AC18` pasó en ambas corridas (2/2) conservando `Blue Top` × 3 tras iniciar sesión. Además, los HTML capturados en `/checkout` de `AC19` (`pagesource3923873200062685372.html`) y `AC26` (`pagesource18404122679679757624.html`) contienen `<tr id="product-1">` con `Blue Top` y su precio/subtotal correctos. | Ningún cambio en los escenarios `AC18`, `AC19` ni `AC26`. |
| H5 | El `@After` (borrar cuenta) falla después de AC16 porque la cuenta sigue en memoria. | **Descartada** | `AccountSteps.java:114-118` ya llama `deletedAccount = forgetRegisteredAccount();` tras ejecutar `DeleteTestAccount`. `AC16` pasó 2/2 sin errores en el hook `@After`. | Ningún cambio requerido. |
| H6 | El modal "Added!" y el enlace "View Cart" (`#cartModal a[href='/view_cart']`) aparecen tarde o no aparecen. | **Descartada** | En todas las corridas, cada escenario que alcanzó el clic en `Add to cart` encontró e hizo clic en `View Cart after adding a product` sin timeout. Los fallos observados en `AC08` ej. 2 y `AC10` ocurrieron antes, en el `Given` inicial. | Ningún cambio en `CatalogPage.VIEW_CART`. |

---

## 4. Extractos HTML reales de evidencia

*(Los archivos de `target/site/serenity/` se eliminan en cada `gradlew clean`; se conservan aquí los fragmentos literales extraídos de las corridas base).*

### 4.1 `/checkout` sin `id="cart_info_table"` y botón `Place Order` (`pagesource18404122679679757624.html` y `pagesource12987506221858247123.html`)

```html
<div class="table-responsive cart_info" id="cart_info">
  <table class="table table-condensed">
    <thead>
      <tr class="cart_menu">
        <td class="image">Item</td>
        <td class="description">Description</td>
        <td class="price">Price</td>
        <td class="quantity">Quantity</td>
        <td class="total">Total</td>
        <td></td>
      </tr>
    </thead>
    <tbody>
      <tr id="product-1">
        <td class="cart_product">...</td>
        <td class="cart_description"><h4><a href="/product_details/1">Blue Top</a></h4><p>Women &gt; Tops</p></td>
        <td class="cart_price"><p>Rs. 500</p></td>
        <td class="cart_quantity"><button class="disabled">1</button></td>
        <td class="cart_total"><p class="cart_total_price">Rs. 500</p></td>
      </tr>
      <tr>
        <td></td><td></td>
        <td colspan="2"><h4><b>Total Amount</b></h4></td>
        <td><p class="cart_total_price">Rs. 500</p></td>
      </tr>
    </tbody>
  </table>
</div>
<div id="ordermsg" class="form-group">
  <label>If you would like to add a comment about your order, please write it in the field below.</label>
  <textarea class="form-control" cols="60" rows="6" name="message"></textarea>
</div>
<div><a href="/payment" class="btn btn-default check_out">Place Order</a></div>
```

### 4.2 `/view_cart` en el 2.º ejemplo de `AC08` con sesión reutilizada (`pagesource805798732521902131.html`)

```html
<div class="table-responsive cart_info" id="cart_info">
  <table class="table table-condensed" id="cart_info_table">
    ...
    <tbody>
      <tr id="product-1">
        <td class="cart_description"><h4><a href="/product_details/1">Blue Top</a></h4><p>Women &gt; Tops</p></td>
        <td class="cart_price"><p>Rs. 500</p></td>
        <td class="cart_quantity"><button class="disabled">1</button></td>
        <td class="cart_total"><p class="cart_total_price">Rs. 500</p></td>
        <td class="cart_delete"><a class="cart_quantity_delete" style="cursor: pointer;" data-product-id="1">...</a></td>
      </tr>
    </tbody>
  </table>
  <span id="empty_cart" style="display: none;">
    <br><p class="text-center" style="font-size: 18px;"><b>Cart is empty!</b> Click <a href="/products"><u>here</u></a> to buy products.</p><br>
  </span>
</div>
```

### 4.3 Respuesta transitoria de sobrecarga del sitio en `AC10` (`pagesource1448032984607189993.html`)

```html
<body>
  <h2>This website is under heavy load (queue full)</h2>
  <p>We're sorry, too many people are accessing this website at the same time. We're working on this problem. Please try again later.</p>
</body>
```

---

## 5. Defectos encontrados en el sitio

- **Sobrecarga intermitente del sitio (`This website is under heavy load (queue full)`):** El servidor público de `automationexercise.com` devuelve ocasionalmente la página estática de error `<h2>This website is under heavy load (queue full)</h2>` en lugar de la tienda. No corresponde a un fallo de las pruebas; no se suaviza ninguna aserción ni se aumenta `wait.for.timeout`:
  - **Corrida diagnóstica base 2 (`2311e3a`, `AC10`, `shopping_cart.feature:25`):** captura `target/site/serenity/88c133aa67a763fdf63c5db6ba008c25a256fbd8b9515e816625f3c47cb294ec.png` y HTML `pagesource1448032984607189993.html` (extracto en sección 4.3).
  - **Corrida post-corrección 2 (`1d008ee`, `AC03`, `account.feature:21`):** al abrir el sitio para hacer clic en `Signup / Login`, el servidor respondió con `heavy load (queue full)` y tras esperar `10.4 s` con `.forNoMoreThan(10).seconds()` el paso falló en `ELEMENTO_NO_DISPONIBLE`. Evidencia: captura `target/site/serenity/88c133aa67a763fdf63c5db6ba008c25a256fbd8b9515e816625f3c47cb294ec.png` y HTML `pagesource5713637551968922988.html` (`<h2>This website is under heavy load (queue full)</h2>`). `AC03` pasó sin fallos en la corrida post-corrección 1 y en las dos corridas diagnósticas base.

---

## 6. Notas técnicas y desviaciones registradas

- **Comportamiento de `WaitUntil` en Serenity 4.1.3 (Hallazgo #1 de Fase 1):** En Serenity BDD 4.1.3, `WaitUntil.the(target, matcher)` se implementa con `EventualConsequence` y `WebElementQuestion`, por lo que al agotarse el tiempo sin encontrar o habilitar un elemento emite `java.lang.AssertionError` (con mensajes Hamcrest como `Expected: an element that is visible / but: no matching element found by [...]`) en lugar de `TimeoutException` o `NoSuchElementException`. Por esta razón, `FailureDiagnosis.of(Throwable)` evalúa `isElementUnavailableAssertion(...)` para clasificar estos `AssertionError` dentro de `ELEMENTO_NO_DISPONIBLE` y evitar reportarlos erróneamente como `VALOR_INESPERADO`.
- **Timeout por defecto de `WaitUntil.the(...)` cuando `timeouts.implicitlywait = 0`:** En Serenity 4.1.3 (`SystemPropertiesConfiguration.getElementTimeoutInSeconds()`), `EventualConsequence` toma su duración de `webdriver.timeouts.implicitlywait / 1000` (`0 s` por la regla 2) cuando no se indica `.forNoMoreThan(...)`, realizando un único intento sin sondeo ante transiciones AJAX/animaciones (observado en `AC10` al borrar una fila con `isNotVisible()`). Se añadió `.forNoMoreThan(10).seconds()` a los `WaitUntil.the(...)` existentes para respetar los 10 s configurados en `wait.for.timeout` sin usar esperas implícitas ni `Thread.sleep`.
- **Microcorte de red local durante validación intermedia:** En una corrida intermedia sobre `92a88fa`, `AC14` falló al abrir la ficha de `Men Tshirt` por una desconexión puntual de red del equipo (`ERR_INTERNET_DISCONNECTED`, captura `target/site/serenity/a747f21fce9b3da402ed393899c6c8c86d346637c4f7c6b5ddfefec21474263a.png`).
- **Propagación de quejas Screenplay en Serenity 4.1.3 (`ErrorTally`):** `ErrorTally.recordError` registra la excepción de negocio (`ShopExpectationError` o subclase) en el reporte JSON/HTML de Serenity, pero `ErrorTally.throwSummaryExceptionFrom` lanza `new AssertionError(overallErrorMessage)` al propagar el fallo a Cucumber/JUnit. Por eso `FailureDiagnosis` reconoce el prefijo `AC##:` de `orComplainWith` también sobre `AssertionError` plano (sin importar clases de Serenity) para clasificarlo en consola como `REGLA_DE_NEGOCIO`.
- **Clic directo en `ClickWhenReady` frente a `SPEC-01 §3.1`:** `ClickWhenReady.java` usa `target.resolveFor(actor).click()` en lugar de `Click.on(target)` para aislar el reintento único ante `ElementClickInterceptedException` (con `scrollIntoView({block:'center'})`) sin que Serenity registre el primer intento interceptado como un subpaso fallido interno, conservando las capturas por acción.
- **Valores esperados:** No se modificó ningún valor esperado de los criterios `@AC01`–`@AC26` ni de los archivos `.feature`.

---

## 7. Resultados tras las correcciones (2 corridas consecutivas)

Ambas corridas se ejecutaron con `.\gradlew.bat clean test` sobre el código final de Fase 2 (`1d008ee`):

| Corrida | Resultado | Duración | Escenarios corregidos | Observaciones |
|---|---|---|---|---|
| Corrida 1 (`1d008ee`) | **26 pasaron / 0 fallaron / 0 omitidos** | 15 min 30 s | `AC08`×2, `AC10`, `AC11/AC12`, `AC14`, `AC19`, `AC26` en verde | 100 % en verde |
| Corrida 2 (`1d008ee`) | **25 pasaron / 1 fallaron / 0 omitidos** | 14 min 59 s | Todos los escenarios que cargaron la tienda (incluidos `AC08`×2, `AC10`, `AC11/AC12`, `AC14`, `AC19`, `AC26`) en verde | Único fallo en `AC03` por página `This website is under heavy load (queue full)` del sitio (sección 5). |


