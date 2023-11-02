package com.example.session;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/slack")
public class SlackController {

    private final SlackService slackService;

    @Autowired
    public SlackController(SlackService slackService) {
        this.slackService = slackService;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ValidDto {
        private String token;
        private String challenge;
        private String type;

        public String getChallenge() {
            return this.challenge;
        }
    }

//    // Slack Request URL 검증용
//    @PostMapping("/event")
//    public String validateURL(@RequestBody ValidDto req) {
//
//        return req.getChallenge();
//    }

    @PostMapping(value = "/event")
    public void event(@RequestBody Map<String, Object> data) {
        log.info("event ::::: "+data.toString());
        slackService.sendMessageByWebhook(data);
    }
}
