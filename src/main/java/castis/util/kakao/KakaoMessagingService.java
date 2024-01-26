package castis.util.kakao;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.KakaoOption;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class KakaoMessagingService {
    private final Environment environment;

    @Async
    public void sendMessage(String phone, KakaoOption kakaoOption) {
        DefaultMessageService messageService = NurigoApp.INSTANCE
                .initialize(environment.getProperty("kakao.api-key"), environment.getProperty("kakao.secret"),
                        environment.getProperty("kakao.messaging.url"));

        kakaoOption.setPfId(environment.getProperty("kakao.pfid"));
        kakaoOption.setDisableSms(true);

        Message message = new Message();
        message.setFrom(environment.getProperty("kakao.messaging.tel-origin"));
        message.setTo(phone);
        message.setKakaoOptions(kakaoOption);
        try {
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            // 발송에 실패한 메시지 목록을 확인할 수 있습니다!
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Async
    public void sendAssistanceApplyMessage(String phone, String applierName, String appliedAssistanceName) {
        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setTemplateId(environment.getProperty("kakao.messaging.template.assistance-apply-alarm"));
        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{name}", applierName);
        variables.put("#{service}", appliedAssistanceName);
        kakaoOption.setVariables(variables);
        sendMessage(phone, kakaoOption);
    }
}
