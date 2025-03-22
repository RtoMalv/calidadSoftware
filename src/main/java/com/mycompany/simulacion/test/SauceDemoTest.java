package com.mycompany.simulacion.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SauceDemoTest {

    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        System.out.println("🔄 Iniciando Safari para los tests...");
        driver = new SafariDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testLoginExitoso() throws InterruptedException {
        System.out.println("▶️ Test #1: Login con usuario válido");

        driver.get("https://www.saucedemo.com");

        // Localizar e ingresar datos
        WebElement username = driver.findElement(By.id("user-name"));
        WebElement password = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("login-button"));

        username.sendKeys("standard_user");
        password.sendKeys("secret_sauce");
        loginBtn.click();

        Thread.sleep(2000); // esperar a que cargue

        // Verificación del título en la página siguiente
        WebElement productsTitle = driver.findElement(By.className("title"));
        String titulo = productsTitle.getText();

        assertEquals(titulo, "Products", "❌ El login no fue exitoso.");
        System.out.println("✅ Test #1 prueba de loging de usuario exitosa");
    }

    @AfterClass
    public void tearDown() {
        System.out.println("⚠️ El navegador permanece abierto para los siguientes tests.");
        // Si deseas cerrar al final, descomenta:
        // if (driver != null) driver.quit();
    }

    // Puedes continuar agregando más tests aquí con @Test
}
