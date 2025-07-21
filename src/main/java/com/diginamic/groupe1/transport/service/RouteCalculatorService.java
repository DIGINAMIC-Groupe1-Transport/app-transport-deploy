package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.entity.Coordinates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RouteCalculatorService {


    private String apiKey =System.getenv("OPENROUTE_API_KEY");;

    private static final String OPENROUTE_API_URL = "https://api.openrouteservice.org/v2/directions/driving-car";

    public RouteInfo calculateRoute(Coordinates start, Coordinates end) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String url = OPENROUTE_API_URL;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            headers.set("Content-Type", "application/json");

            Map<String, Object> requestBody = Map.of(
                    "coordinates", List.of(
                            List.of(start.getX(), start.getY()),
                            List.of(end.getX(), end.getY())
                    )
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            Map<String, Object> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class
            ).getBody();

            if (response == null || !response.containsKey("routes")) {
                log.error("Réponse invalide (pas de 'routes') : {}", response);
                return new RouteInfo(0, 0);
            }

            List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
            if (routes == null || routes.isEmpty()) {
                log.error("Réponse invalide (routes vide) : {}", response);
                return new RouteInfo(0, 0);
            }

            Map<String, Object> route = routes.get(0);
            if (route == null || !route.containsKey("summary")) {
                log.error("Réponse invalide (pas de summary dans route) : {}", response);
                return new RouteInfo(0, 0);
            }

            Map<String, Object> summary = (Map<String, Object>) route.get("summary");
            if (summary == null || !summary.containsKey("distance") || !summary.containsKey("duration")) {
                log.error("Réponse invalide (summary incomplet) : {}", response);
                return new RouteInfo(0, 0);
            }

            int distance = ((Number) summary.get("distance")).intValue();
            int duration = ((Number) summary.get("duration")).intValue();

            return new RouteInfo(distance, duration);

        } catch (Exception e) {
            log.error("Erreur calcul route", e);
            return new RouteInfo(0, 0);
        }
    }

    public static class RouteInfo {
        public final int distanceMeters;
        public final int durationSeconds;

        public RouteInfo(int distanceMeters, int durationSeconds) {
            this.distanceMeters = distanceMeters;
            this.durationSeconds = durationSeconds;
        }
    }
}