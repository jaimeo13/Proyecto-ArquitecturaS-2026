package com.vitallogix.backend.controller;

import com.vitallogix.backend.dto.CustomerRequest;
import com.vitallogix.backend.dto.CustomerResponse;
import com.vitallogix.backend.model.Customer;
import com.vitallogix.backend.model.Sale;
import com.vitallogix.backend.repository.CustomerRepository;
import com.vitallogix.backend.repository.SaleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión CRUD de clientes, historial de compras y programa ClienteAmigo.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerRepository repository;
    private final SaleRepository saleRepository;

    public CustomerController(CustomerRepository repository, SaleRepository saleRepository) {
        this.repository = repository;
        this.saleRepository = saleRepository;
    }

    /**
     * Consulta registros mediante la operación {@code findAll}.
     * @return resultado de la operación.
     */
    @GetMapping
    public List<CustomerResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Consulta registros mediante la operación {@code findById}.
     * @param id parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable Long id) {
        Customer customer = repository.findById(id).orElseThrow();
        return toResponse(customer);
    }

    /**
     * Obtiene el valor de {@code purchaseHistory}.
     * @param id parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    @GetMapping("/{id}/purchases")
    public List<Sale> getPurchaseHistory(@PathVariable Long id) {
        repository.findById(id).orElseThrow(); // Verifies that the customer exists
        return saleRepository.findByCustomer_IdOrderBySaleDateDesc(id);
    }

    // Validates a clienteamigo code and returns the status plus basic customer data.
    /**
     * Valida las reglas de negocio correspondientes (validateClienteAmigo).
     * @param code parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    @GetMapping("/validate-clienteamigo")
    public Map<String, Object> validateClienteAmigo(@RequestParam String code) {
        if (code == null || code.trim().isEmpty()) {
            return Map.of(
                "valid", false,
                "message", "Ingresa un numero clienteamigo"
            );
        }

        String normalizedCode = code.trim().toUpperCase(Locale.ROOT);
        Customer customer = repository.findByClienteAmigoNumber(normalizedCode).orElse(null);

        if (customer == null || !customer.isFriend()) {
            return Map.of(
                "valid", false,
                "message", "Codigo clienteamigo invalido"
            );
        }

        return Map.of(
            "valid", true,
            "message", "Codigo clienteamigo valido",
            "customerId", customer.getId(),
            "customerName", customer.getName() == null ? "" : customer.getName(),
            "customerPhone", customer.getPhone() == null ? "" : customer.getPhone()
        );
    }

    /**
     * Registra una nueva entidad mediante {@code create}.
     * @param request parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@RequestBody CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        customer.setClienteAmigoNumber(request.getClienteAmigoNumber());
        customer.setFriend(request.isFriend());
        return toResponse(repository.save(customer));
    }

    /**
     * Actualiza el estado o los atributos de la entidad (update).
     * @param id parámetro de entrada de la operación.
     * @param request parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @RequestBody CustomerRequest request) {
        Customer customer = repository.findById(id).orElseThrow();
        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        customer.setClienteAmigoNumber(request.getClienteAmigoNumber());
        customer.setFriend(request.isFriend());
        return toResponse(repository.save(customer));
    }

    /**
     * Elimina o rechaza el registro indicado (delete).
     * @param id parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(java.util.Map.of("message", "No se puede eliminar el cliente porque tiene ventas con receta en el historial."));
        }
    }

    private CustomerResponse toResponse(Customer customer) {
        long purchaseCount = customer.getId() != null ? saleRepository.countByCustomer_Id(customer.getId()) : 0;
        return new CustomerResponse(
            customer.getId(),
            customer.getName(),
            customer.getAddress(),
            customer.getPhone(),
            customer.getClienteAmigoNumber(),
            customer.isFriend(),
            purchaseCount
        );
    }
}
