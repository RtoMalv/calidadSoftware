package com.mycompany.simulacion.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;

public class TestSafari {

    public static WebDriver driver;

    @BeforeMethod
    public void setUp() throws InterruptedException {
        if (driver == null) {
            System.out.println("Iniciando navegador Safari");
            driver = new SafariDriver();
            driver.manage().window().maximize();

            driver.get("https://www.saucedemo.com");
            driver.findElement(By.id("user-name")).sendKeys("standard_user");
            driver.findElement(By.id("password")).sendKeys("secret_sauce");
            driver.findElement(By.id("login-button")).click();
            Thread.sleep(2000);
        }
    }
    // primer test para verificar que el acceso fue exitoso

    @Test
    public void testIngreso() throws InterruptedException {
        System.out.println("Test #1: Verificando que el login redirige a inventory.html");

        try {
            String urlEsperada = "https://www.saucedemo.com/inventory.html";
            String urlActual = driver.getCurrentUrl();

            if (urlActual.equals(urlEsperada)) {
                System.out.println("********Test #1: login exitoso. URL correcta **********" + urlActual);
            } else {
                System.out.println("Test #1 fallido. URL inesperada:");
                System.out.println("Esperada: " + urlEsperada);
                System.out.println("Actual:   " + urlActual);
            }

            assertTrue(urlActual.equals(urlEsperada), "El login no redirigió a inventory.html");

        } catch (Exception e) {
            System.err.println("Test #1 fallido: excepción inesperada al verificar la URL");
            e.printStackTrace();
            throw e;
        }
    }

    // segundo test para verificar que al seleccionar un producto nos lleva al detalle
    @Test(dependsOnMethods = "testIngreso")
    public void testProducto() throws InterruptedException {
        System.out.println("Test #2: Acceder al detalle del producto desde el listado...");

        // XPath correcto del enlace al producto
        By xpathFleeceItem = By.xpath("//*[@id='item_5_title_link']/div");

        WebElement producto = driver.findElement(xpathFleeceItem);

        // Hover y clic
        Actions actions = new Actions(driver);
        actions.moveToElement(producto).perform();
        producto.click();

        Thread.sleep(2000);

        String expectedUrl = "https://www.saucedemo.com/inventory-item.html?id=5";

        String actualUrl = driver.getCurrentUrl();

        boolean redireccion = actualUrl.equals(expectedUrl)
                && driver.getPageSource().contains("Sauce Labs Fleece Jacket");

        assertTrue(redireccion, "No se accedió correctamente a la URL del detalle del producto.\nURL actual: " + actualUrl);
        System.out.println("**********Test #2 acceso a detalle del producto exitoso***********");
    }

// test tres para verificar que se agrega el producto al carrito
    @Test(dependsOnMethods = "testProducto")
    public void testAgregarAlCarrito() throws InterruptedException {
        System.out.println("Test #3: Verificar que se agregue el producto correcto al carrito");

        try {

            driver.get("https://www.saucedemo.com/inventory-item.html?id=5");
            Thread.sleep(2000);
            System.out.println("Página de detalle del producto cargada");

            String nombreEsperado = "", precioEsperado = "";
            try {
                WebElement nombreProducto = driver.findElement(By.className("inventory_details_name"));
                WebElement precioProducto = driver.findElement(By.className("inventory_details_price"));
                nombreEsperado = nombreProducto.getText();
                precioEsperado = precioProducto.getText();
                System.out.println("Producto: " + nombreEsperado + " | Precio: " + precioEsperado);
            } catch (Exception e) {
                System.err.println("No se encontró el nombre o el precio del producto");
                throw e;
            }

            //bloque de código para hacer click en el botón "Add to cart"
            try {
                WebElement botonAgregar = driver.findElement(By.xpath("//*[@id='add-to-cart']"));
                botonAgregar.click();
                Thread.sleep(1000);
                System.out.println("Botón 'Add to cart' clickeado");
            } catch (Exception e) {
                System.err.println("No se pudo hacer clic en el botón 'Add to cart'");
                throw e;
            }

            // Dirige la prueba al carrito de compras 
            try {
                WebElement iconoCarrito = driver.findElement(By.className("shopping_cart_link"));
                iconoCarrito.click();
                Thread.sleep(2000);
                System.out.println("Navegación al carrito realizada");
            } catch (Exception e) {
                System.err.println("No se pudo acceder al carrito");
                throw e;
            }

            // Verifica nombre y precio en el carrito
            try {
                WebElement nombreEnCarrito = driver.findElement(By.className("inventory_item_name"));
                WebElement precioEnCarrito = driver.findElement(By.className("inventory_item_price"));
                String nombreCarrito = nombreEnCarrito.getText();
                String precioCarrito = precioEnCarrito.getText();

                System.out.println("Producto en carrito: " + nombreCarrito + " | Precio: " + precioCarrito);

                if (nombreEsperado.equals(nombreCarrito) && precioEsperado.equals(precioCarrito)) {
                    System.out.println("*********Test #3: producto correcto en el carrito con el mismo precio*********");
                } else {
                    System.out.println("Test #3 fallido:");
                    System.out.println("   Esperado: " + nombreEsperado + " | Carrito: " + nombreCarrito);
                    System.out.println("   Precio esperado: " + precioEsperado + " | Carrito: " + precioCarrito);
                }
            } catch (Exception e) {
                System.err.println("No se pudo verificar el producto en el carrito");
                throw e;
            }

        } catch (Exception e) {
            System.err.println("Test #3 detenido por error en la ejecución.");
            e.printStackTrace();
        }
    }

// test 4 
    @Test(dependsOnMethods = "testAgregarAlCarrito")
    public void testCheckoutCampos() throws InterruptedException {
        System.out.println("Test #4: Verificar que el formulario de checkout tenga los campos requeridos");

        driver.get("https://www.saucedemo.com/cart.html");
        Thread.sleep(1000);

        // Bloque para hacer clic en el botón "Checkout"
        try {
            WebElement checkoutBtn = driver.findElement(By.id("checkout"));
            checkoutBtn.click();
            Thread.sleep(1000);
            System.out.println("Botón 'Checkout' clickeado");
        } catch (Exception e) {
            System.err.println("No se encontró el botón 'Checkout'");
            throw e;
        }

        // Validación de los campos
        try {
            WebElement firstName = driver.findElement(By.id("first-name"));
            WebElement lastName = driver.findElement(By.id("last-name"));
            WebElement postalCode = driver.findElement(By.id("postal-code"));

            boolean camposVisibles = firstName.isDisplayed() && lastName.isDisplayed() && postalCode.isDisplayed();

            if (camposVisibles) {
                System.out.println("********Test #4: Todos los campos del formulario de checkout están visibles********");
            } else {
                System.out.println("Test #4 fallido: algún campo no está visible");
            }
            assertTrue(camposVisibles, "Faltan campos visibles en el formulario de checkout");
        } catch (Exception e) {
            System.err.println("No se encontraron uno o más campos del formulario");
            throw e;
        }
    }

    
    
    //quinto test realizar el pedido validado con kla información porporcionada
    
    @Test(dependsOnMethods = "testCheckoutCampos")
    public void testCompletarCheckout() throws InterruptedException {
        System.out.println("Test #5: Completar el formulario y continuar al resumen de compra");

        try {
            // Se completan los campos con los datos del comprador
            driver.findElement(By.id("first-name")).sendKeys("Mauricio");
            driver.findElement(By.id("last-name")).sendKeys("Alvarado");
            driver.findElement(By.id("postal-code")).sendKeys("10501");

            System.out.println("Campos completados");

            // Hace clic en Continue
            driver.findElement(By.id("continue")).click();
            Thread.sleep(2000);

            // verifica que el porceso sea exitoso
            String urlActual = driver.getCurrentUrl();
            String urlEsperada = "https://www.saucedemo.com/checkout-step-two.html";

            if (urlActual.equals(urlEsperada)) {
                System.out.println("Redirigido a la página de resumen de compra");
            } else {
                System.out.println("No se redirigió correctamente al resumen. URL actual: " + urlActual);
            }

            // Valida que el producto esté listado y que exista un total
            boolean productoPresente = driver.getPageSource().contains("Sauce Labs Fleece Jacket");
            boolean totalVisible = driver.getPageSource().contains("Total");

            if (productoPresente && totalVisible) {
                System.out.println("**********Test #5: Producto presente y total visible en el resumen de compra********");
            } else {
                System.out.println("Test #5 fallido: faltan elementos en el resumen");
            }

            assertTrue(urlActual.equals(urlEsperada) && productoPresente && totalVisible,
                    "No se completó correctamente el formulario o no se accedió al resumen");

        } catch (Exception e) {
            System.err.println("Test #5 fallido por excepción inesperada");
            e.printStackTrace();
            throw e;
        }
}
    
    //Test 6. verifica que al aceptar la compra la paginarealice el proceso y de el mensaje de transacción exitos
    @Test(dependsOnMethods = "testCompletarCheckout")
    public void testFinalizarCompra() throws InterruptedException {
        System.out.println("Test 6, Finalizar la compra y verificar mensaje de confirmación");

        try {
            // Paso 1: Clic en el botón "Finish"
            WebElement finishBtn = driver.findElement(By.id("finish"));
            finishBtn.click();
            Thread.sleep(2000);
            System.out.println("Botón 'Finish' clickeado");

            // Paso 2: Verificar URL
            String urlActual = driver.getCurrentUrl();
            String urlEsperada = "https://www.saucedemo.com/checkout-complete.html";

            // Paso 3: Verificar el mensaje de éxito
            WebElement mensajeConfirmacion = driver.findElement(By.className("complete-header"));
            String textoConfirmacion = mensajeConfirmacion.getText();

            boolean urlCorrecta = urlActual.equals(urlEsperada);
            boolean mensajeCorrecto = textoConfirmacion.equalsIgnoreCase("Thank you for your order!");

            if (urlCorrecta && mensajeCorrecto) {
                System.out.println("*********Test #6: Compra finalizada correctamente y mensaje confirmado*********");
            } else {
                System.out.println("Test #6 fallido:");
                System.out.println("URL actual: " + urlActual);
                System.out.println("Mensaje recibido: " + textoConfirmacion);
            }

            assertTrue(urlCorrecta && mensajeCorrecto, "La compra no se finalizó correctamente o el mensaje fue incorrecto.");

        } catch (Exception e) {
            System.err.println("Test #6 fallido por excepción inesperada");
            e.printStackTrace();
            throw e;
        }
    }
    @AfterClass
    public void tearDown() {
        System.out.println("Finalizando pruebas. Safari se cerrará .");
       
        if (driver != null) driver.quit();
    }
}
