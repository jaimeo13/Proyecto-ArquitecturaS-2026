package com.vitallogix.backend.observer;

import com.vitallogix.backend.model.Sale;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sujeto observable (patrón Observer) encargado de notificar eventos de ventas completadas.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Component
public class SaleEventNotifier {
    private final List<SaleObserver> observers;

    public SaleEventNotifier(List<SaleObserver> observers) {
        this.observers = observers;
    }

    /**
     * Ejecuta la operación {@code notifyObservers} del componente.
     * @param sale parámetro de entrada de la operación.
     */
    public void notifyObservers(Sale sale) {
        for (SaleObserver observer : observers) {
            observer.onSaleCompleted(sale);
        }
    }
}
