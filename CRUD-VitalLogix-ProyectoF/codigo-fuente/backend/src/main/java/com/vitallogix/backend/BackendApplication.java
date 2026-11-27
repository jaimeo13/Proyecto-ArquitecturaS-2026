package com.vitallogix.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada principal de la aplicación Spring Boot de VitalLogix conectada a MySQL.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@SpringBootApplication
public class BackendApplication {

	/**
	 * Ejecuta la operación {@code main} del componente.
	 * @param args parámetro de entrada de la operación.
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
