package com.mycompany.simulacion.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;
import org.testng.TestNG;

public class TestSafari {

    public static WebDriver driver;

    public static void main(String[] args) {
        try {
            System.out.println("Iniciando pruebas");

            // Inicializar Safari y hacer login una única vez para el inio de las pruebas
            //se hace de esta manera para realizar prubas como un único usuario
            driver = new SafariDriver();
            driver.manage().window().maximize();
            driver.get("https://www.saucedemo.com");

            driver.findElement(By.id("user-name")).sendKeys("standard_user");
            driver.findElement(By.id("password")).sendKeys("secret_sauce");
            driver.findElement(By.id("login-button")).click();

            Thread.sleep(2000);

            // Ejecutar los tests anotados con @Test
            TestNG testng = new TestNG();
            testng.setTestClasses(new Class[]{TestSafari.class});
            testng.run();

            System.out.println("Pruebas completadas. El navegador permanece abierto.");

        } catch (Exception e) {
            System.err.println("Error de ejecución: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // primer test para verificar que el acceso fue exitoso

    @Test
    public void testIngreso() throws InterruptedException {
        System.out.println("▶️ Test #1: Verificando que el login redirige a inventory.html...");

        try {
            String urlEsperada = "https://www.saucedemo.com/inventory.html";
            String urlActual = driver.getCurrentUrl();

            if (urlActual.equals(urlEsperada)) {
                System.out.println("Test #1: login exitoso. URL correcta: " + urlActual);
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
        System.out.println("▶️ Test #2: Acceder al detalle del producto desde el listado...");

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
        System.out.println("Test #2 acceso a detalle del producto exitoso");
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
                    System.out.println("Test #3: producto correcto en el carrito con el mismo precio");
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
                System.out.println("Test #4: Todos los campos del formulario de checkout están visibles");
            } else {
                System.out.println("Test #4 fallido: algún campo no está visible");
            }
            assertTrue(camposVisibles, "Faltan campos visibles en el formulario de checkout");
        } catch (Exception e) {
            System.err.println("No se encontraron uno o más campos del formulario");
            throw e;
        }
    }

    @AfterClass
    public void tearDown() {
        System.out.println("Finalizar pruebas. Safari permanece abierto.");
        // Si se quiere cerrar el navegador al final:
        // if (driver != null) driver.quit();
    }
}
