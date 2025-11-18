package ru.practicum.ewm;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate rest;
    @Value("${app.name}")
    private String app;

    public void createHit(HttpServletRequest request) {
        HitRequestDto newHitDto = new HitRequestDto(
                app,
                request.getRequestURI(),
                request.getRemoteAddr(),
                null
        );
        makeAndSendRequest(HttpMethod.POST, "/hit", null, newHitDto);
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", String.join(",", uris),
                "unique", unique
        );
        ResponseEntity<List<ViewStatsDto>> response = makeAndSendRequest(HttpMethod.GET, "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                parameters, null);
        return response.getBody();
    }

    private <T> ResponseEntity<List<ViewStatsDto>> makeAndSendRequest(HttpMethod method,
                                                                      String path,
                                                                      @Nullable Map<String, Object> parameters,
                                                                      @Nullable T body) {
        String url = "http://stats-server:9090" + path;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, headers);

        ParameterizedTypeReference<List<ViewStatsDto>> typeRef =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<List<ViewStatsDto>> serverResponse;
        try {
            if (parameters != null) {
                serverResponse = rest.exchange(url, method, requestEntity, typeRef, parameters);
            } else {
                serverResponse = rest.exchange(url, method, requestEntity, typeRef);
            }
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Ошибка при обработке HTTP запроса к сервису статистики: " + e.getStatusCode(), e);
        }
        return prepareResponse(serverResponse);
    }

    private static ResponseEntity<List<ViewStatsDto>> prepareResponse(ResponseEntity<List<ViewStatsDto>> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
