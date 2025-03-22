package com.mycompany.simulacion.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.Test;
import org.testng.TestNG;

public class TestSafari {

    public static WebDriver driver;

    public static void main(String[] args) {
        try {
            System.out.println("Iniciando Safari...");
            driver = new SafariDriver();
            driver.manage().window().maximize();

            // Ejecutar pruebas usando TestNG desde el main
            TestNG testng = new TestNG();
            testng.setTestClasses(new Class[] { TestSafari.class });
            testng.run();

            System.out.println("Pruebas finalizadas. Safari queda abierto.");

        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    //Test número uno prueba de ingreso de usuario
    @Test
    public void testLoginUsuarioExitoso() throws InterruptedException {
        System.out.println("▶️ Test #1: Login exitoso con usuario válido...");

        driver.get("https://www.saucedemo.com");

        WebElement username = driver.findElement(By.id("user-name"));
        WebElement password = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("login-button"));

        username.sendKeys("standard_user");
        password.sendKeys("secret_sauce");
        loginBtn.click();

        Thread.sleep(2000);

        WebElement productsTitle = driver.findElement(By.className("title"));
        String titulo = productsTitle.getText();

        if ("Products".equalsIgnoreCase(titulo)) {
            System.out.println("Test #1 prueba de loging de usuario exitosa");
        } else {
            System.out.println("Test #1 falló: no se cargó la página de productos.");
        }
    }

    
    // @Test
   
}