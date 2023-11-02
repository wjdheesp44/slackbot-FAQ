package com.example.session;
import com.slack.api.Slack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SlackService {

    @Value(value = "${slack.token}")
    String token;
    @Value(value = "${slack.channel}")
    String channel;

    @Value(value = "${slack.webhook-url}")
    String webhookUrl;

    public void sendMessageByWebhook(Map<String, Object> eventMap) {
        Slack slackInst = Slack.getInstance();
        String payload = getPayLoadByType(eventMap);

        try {
            slackInst.send(webhookUrl, payload);
        } catch (Exception e) {
            log.error(e.getMessage());
//            throw new BusinessException(ErrorCode.SLACK_MESSAGE_DONT_SEND);
        }
    }

    // Event type에 따라 Slack 메시지 Payload 설정
    public String getPayLoadByType(Map<String, Object> eventMap) {
        String payload = "";

        payload = customizeMsgByCondition(eventMap);

        return "{\"text\":\"" + payload + "\"}";
    }

    // app_mention일 때 조건에 따라서 다른 메세지 전송
    public String customizeMsgByCondition(Map<String, Object> eventValue) {
        Object item = eventValue.get("event");
        Map<String, String> event = (Map<String, String>)item;
        String text = event.get("text");

        if (isGreetingCondition(text)) {
            return "반가워요!";
        } else if (whenHeadington(text)) {
            return "11월 6일부터 11월 10일입니다!";
        } else {
            return "무슨 말인지 잘 모르겠어요😅";
        }
    }

    public boolean isGreetingCondition(String text) {
        List<String> greetings = List.of("안녕", "하이", "헬로", "반갑", "hello", "Hello");
        String[] split = text.split(" ");

        if (split.length == 1 || greetings.stream().anyMatch(greeting -> text.toLowerCase().contains(greeting.toLowerCase()))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean whenHeadington(String text) {
        // 문자열을 소문자로 변환하여 대소문자 구분을 없애고 비교
        String lowerText = text.toLowerCase();

        // "헤딩톤"과 "언제" 문자열이 동시에 있는지 확인
        boolean containsHedington = lowerText.contains("헤딩톤");
        boolean containsWhen = lowerText.contains("언제");

        // "헤딩톤"과 "언제" 문자열이 동시에 있는 경우 true 반환
        return containsHedington && containsWhen;
    }
}
