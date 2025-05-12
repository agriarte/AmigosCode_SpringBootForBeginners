package com.tallerdeapps.demo;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/customers")
public class ClienteController {

	private final ClienteRepository clienteRepository;

	public ClienteController (ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
		
	}
	
	@GetMapping("")
	public String getInicio() {
		return "Entrada del crud";
	}
	

	// ok
	@GetMapping("/list")
	public List<Cliente> getClientes() {
		return clienteRepository.findAll();
	}

	// ok
	record NewClienteRequest(String nombre, String email, Integer edad) {
	}

	@PostMapping("/add")
	public void addClienteRecord(@RequestBody NewClienteRequest clienteRequest) {
		Cliente cliente = new Cliente();
		cliente.setNombre(clienteRequest.nombre());
		cliente.setEmail(clienteRequest.email());
		cliente.setEdad(clienteRequest.edad());
		clienteRepository.save(cliente);
	}

	// ok
	@DeleteMapping("/delete/{clienteId}")
	public void deleteCustomer(@PathVariable("clienteId") Integer id) {
		clienteRepository.deleteById(id);
	}
	
	// ok
	@GetMapping ("/count")
	public String getCount() {
		return "La base de datos tiene " + clienteRepository.count() + " registros";
	}
	
	@PutMapping("/update/{clienteId}")
	public void updateClienteRecord(@PathVariable("clienteId") Integer id,
	                                @RequestBody NewClienteRequest clienteRequest) {
	    // 1. Buscar el cliente existente
	    Cliente cliente = clienteRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));

	    // 2. Actualizar los campos
	    cliente.setNombre(clienteRequest.nombre());
	    cliente.setEmail(clienteRequest.email());
	    cliente.setEdad(clienteRequest.edad());

	    // 3. Guardar el cliente actualizado
	    clienteRepository.save(cliente);
	}
	
}
