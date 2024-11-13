
package org.example.orchestrator.controller;

import org.example.orchestrator.model.Order;
import org.example.orchestrator.model.SagaResponse;
import org.example.orchestrator.service.SagaOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/saga")
public class SagaController {

    private final SagaOrchestratorService orchestratorService;

    public SagaController(SagaOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/orders")
    public ResponseEntity<SagaResponse> createOrder(@RequestBody Order order) {
        SagaResponse response = orchestratorService.createOrderSaga(order);
        return ResponseEntity.ok(response);
    }
}
