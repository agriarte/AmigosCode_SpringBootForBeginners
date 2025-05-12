package com.tallerdeapps.demo;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Cliente {

	// Esta clase por medio de Hibernate cada vez que se arranque la aplicación
	// creará la tabla en la base de datos y la secuencia para el ID. Por defecto,
	// la tabla tomará el nombre
	// de la clase pero en minúsculas. Otra opción es añadir una anotación debajo de
	// @Entity con el nombre @Table(name = "cliente")

	// Se puede operar con tablas sin secuencia para ID al estilo MySQL.
	// Trabajar con secuencias es un poco más complejo. Solo es necesario cuando
	// necesitas ID únicos entre múltiples tablas.

	@Id
	@SequenceGenerator(name = "cliente_id_sequence", 
		sequenceName = "cliente_id_sequence", 
		allocationSize = 1
	// por defecto, el autoincremento salía en 50, en el video no pasaba
	)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cliente_id_sequence")
	private Integer ID;
	private String nombre;
	private String email;
	private Integer edad;

	public Cliente(Integer iD, String nombre, String email, Integer edad) {
		super();
		ID = iD;
		this.nombre = nombre;
		this.email = email;
		this.edad = edad;
	}

	public Cliente() {
		super();
	}

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getEdad() {
		return edad;
	}

	public void setEdad(Integer edad) {
		this.edad = edad;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ID, edad, email, nombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		return Objects.equals(ID, other.ID) && Objects.equals(edad, other.edad) && Objects.equals(email, other.email)
				&& Objects.equals(nombre, other.nombre);
	}

	@Override
	public String toString() {
		return "Customer [ID=" + ID + ", nombre=" + nombre + ", email=" + email + ", edad=" + edad + "]";
	}

}
