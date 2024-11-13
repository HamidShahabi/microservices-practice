
package org.example.orchestrator.controller;

import org.example.kafka.dto.OrderRequestDto;
import org.example.orchestrator.model.Order;
import org.example.orchestrator.model.SagaResponse;
import org.example.orchestrator.service.SagaOrchestratorServiceAsync;
import org.example.orchestrator.service.SagaOrchestratorServiceSync;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/saga")
public class SagaController {

    private final SagaOrchestratorServiceAsync orchestratorService;

    public SagaController(SagaOrchestratorServiceAsync orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/orders")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequestDto order) {
        String response = orchestratorService.initiateOrderSaga(order);
        return ResponseEntity.ok(response);
    }
}
