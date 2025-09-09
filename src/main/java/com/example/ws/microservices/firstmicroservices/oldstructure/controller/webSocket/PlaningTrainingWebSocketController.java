package com.example.ws.microservices.firstmicroservices.oldstructure.controller.webSocket;

import com.example.ws.microservices.firstmicroservices.oldstructure.request.websocket.SelectedEmployeeRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.websocket.SelectionPayloadPlaning;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class PlaningTrainingWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<Long, List<SelectedEmployeeRequest>> currentSelections = new ConcurrentHashMap<>();

    @MessageMapping("/planing/{trainingId}/participants")
    public void updateFormSelection(
            @DestinationVariable Long trainingId,
            @Payload SelectionPayloadPlaning payload,
            Principal principal
    ) {
        currentSelections.put(trainingId, payload.selectedEmployees());

        if(payload.type().equalsIgnoreCase("finalized")){
            currentSelections.remove(trainingId);
        }

        messagingTemplate.convertAndSend("/topic/planing/" + trainingId + "/participants", payload);
    }

    @MessageMapping("/planing/{trainingId}/get-current")
    public void getCurrentFormSelection(
            @DestinationVariable Long trainingId,
            Principal principal
    ) {
        List<SelectedEmployeeRequest> current = currentSelections.getOrDefault(trainingId, List.of());
        SelectionPayloadPlaning response = new SelectionPayloadPlaning(trainingId,"CURRENT", current);
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/topic/planing/" + trainingId + "/current",
                response
        );
    }
}
