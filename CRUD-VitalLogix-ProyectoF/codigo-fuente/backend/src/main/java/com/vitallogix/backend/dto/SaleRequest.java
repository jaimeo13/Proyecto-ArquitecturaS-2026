package com.vitallogix.backend.dto;

import java.util.List;

/**
 * DTO de entrada con los ítems, cliente opcional y cupón para registrar una venta.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record SaleRequest(
    List<SaleItemRequest> items,
    Long customerId,
    boolean prescription,
    CustomerData customer,
    String couponCode
) {
    // Legacy getters
    /**
     * Obtiene el valor de {@code items}.
     * @return resultado de la operación.
     */
    public List<SaleItemRequest> getItems() { return items(); }
    /**
     * Obtiene el valor de {@code customerId}.
     * @return resultado de la operación.
     */
    public Long getCustomerId() { return customerId(); }
    /**
     * Indica el estado booleano de {@code prescription}.
     * @return resultado de la operación.
     */
    public boolean isPrescription() { return prescription(); }
    /**
     * Obtiene el valor de {@code customer}.
     * @return resultado de la operación.
     */
    public CustomerData getCustomer() { return customer(); }
    /**
     * Obtiene el valor de {@code couponCode}.
     * @return resultado de la operación.
     */
    public String getCouponCode() { return couponCode(); }

    public static record CustomerData(String name, String address, String phone, boolean friend, String clienteAmigoNumber) {
        /**
         * Obtiene el valor de {@code name}.
         * @return resultado de la operación.
         */
        public String getName() { return name(); }
        /**
         * Obtiene el valor de {@code address}.
         * @return resultado de la operación.
         */
        public String getAddress() { return address(); }
        /**
         * Obtiene el valor de {@code phone}.
         * @return resultado de la operación.
         */
        public String getPhone() { return phone(); }
        /**
         * Indica el estado booleano de {@code friend}.
         * @return resultado de la operación.
         */
        public boolean isFriend() { return friend(); }
        /**
         * Obtiene el valor de {@code clienteAmigoNumber}.
         * @return resultado de la operación.
         */
        public String getClienteAmigoNumber() { return clienteAmigoNumber(); }
    }

    public static record SaleItemRequest(Long productId, Integer quantity) {
        /**
         * Obtiene el valor de {@code productId}.
         * @return resultado de la operación.
         */
        public Long getProductId() { return productId(); }
        /**
         * Obtiene el valor de {@code quantity}.
         * @return resultado de la operación.
         */
        public Integer getQuantity() { return quantity(); }
    }
}
