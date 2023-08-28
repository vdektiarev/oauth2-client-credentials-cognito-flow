package de.sonamesolutions.articlesservice.controller;

import de.sonamesolutions.articlesservice.model.Article;
import de.sonamesolutions.articlesservice.service.UserScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/articles")
public class ArticlesController {

    @Autowired
    private UserScheduleService userScheduleService;

    @GetMapping("/user-availability/{name}")
    public Mono<String> getUserAvailabilityForArticles(@PathVariable String name) {
        return userScheduleService.getUserSchedule(name);
    }

    @PostMapping
    public Mono<String> postArticle(@RequestBody Article article) throws ExecutionException, InterruptedException {
        Mono<String> scheduleServiceResponse = userScheduleService.updateUserSchedule(article.getScheduleId());
        String schedulingResponse = scheduleServiceResponse.toFuture().get();
        // call to some DB to save the article
        return Mono.just("Article from userId:" + article.getUserId() +
                " has been posted! Schedule update: " + schedulingResponse);
    }
}
