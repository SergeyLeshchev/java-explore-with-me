package ru.practicum.ewm;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate rest;

    public void createHit(HttpServletRequest request, String app) {
        HitRequestDto newHitDto = new HitRequestDto(
                app,
                request.getRequestURI(),
                request.getLocalAddr(),
                null
        );
        makeAndSendRequest(HttpMethod.POST, "/hit", null, newHitDto);
    }

    public ResponseEntity<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", URLEncoder.encode(start, StandardCharsets.UTF_8),
                "end", URLEncoder.encode(end, StandardCharsets.UTF_8),
                "uris", String.join(",", uris),
                "unique", unique
        );
        return makeAndSendRequest(HttpMethod.GET, "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                parameters, null);
    }

    private <T> ResponseEntity<ViewStatsDto> makeAndSendRequest(HttpMethod method,
                                                                String path,
                                                                @Nullable Map<String, Object> parameters,
                                                                @Nullable T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ViewStatsDto> serverResponse;
        try {
            if (parameters != null) {
                serverResponse = rest.exchange(path, method, requestEntity, ViewStatsDto.class, parameters);
            } else {
                serverResponse = rest.exchange(path, method, requestEntity, ViewStatsDto.class);
            }
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Ошибка при обработке HTTP запроса к сервису статистики: " + e.getStatusCode(), e);
        }
        return prepareResponse(serverResponse);
    }

    private static ResponseEntity<ViewStatsDto> prepareResponse(ResponseEntity<ViewStatsDto> response) {
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
