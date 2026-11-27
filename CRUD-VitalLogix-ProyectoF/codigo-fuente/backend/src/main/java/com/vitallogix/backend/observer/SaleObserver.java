package com.vitallogix.backend.observer;

import com.vitallogix.backend.model.Sale;

/**
 * Interfaz del patrón Observer para componentes suscritos al evento de venta completada.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public interface SaleObserver {
    void onSaleCompleted(Sale sale);
}
