package com.mycompany.simulacion;

import org.testng.TestNG;
import com.mycompany.simulacion.test.TestSafari;

public class Simulacion {

    public static void main(String[] args) {
        System.out.println("Iniciando pruebas desde clase Simulacion");

        TestNG testng = new TestNG();

        testng.setTestClasses(new Class[]{TestSafari.class});

        testng.run();

        System.out.println("Pruebas completadas desde clase Simulacion.");
    }
}
