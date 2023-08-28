package de.sonamesolutions.articlesservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserScheduleService {

    @Value("${user-schedule-service.get-schedule.url}")
    private String userScheduleServiceGetScheduleUrl;

    @Value("${user-schedule-service.update-schedule.url}")
    private String userScheduleServiceUpdateScheduleUrl;

    @Autowired
    private WebClient cognitoWebClient;

    public Mono<String> getUserSchedule(String userName) {
        return cognitoWebClient
                .get()
                .uri(userScheduleServiceGetScheduleUrl, uriBuilder ->
                        uriBuilder.queryParam("userName", userName).build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ClientResponse::createException)
                .bodyToMono(String.class);
    }

    public Mono<String> updateUserSchedule(String scheduleId) {
        return cognitoWebClient
                .put()
                .uri(userScheduleServiceUpdateScheduleUrl, scheduleId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ClientResponse::createException)
                .bodyToMono(String.class);
    }
}
