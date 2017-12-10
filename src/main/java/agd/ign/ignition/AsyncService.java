package agd.ign.ignition;

import agd.ign.ignition.app.PlaylistGetter;
import agd.ign.ignition.dto.put.NewSongDto;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author aillusions
 */
@EnableAsync
@Component
public class AsyncService {

    @Async()
    public void asyncDownloadFragments(NewSongDto dto) {
        getPlaylistGetter().downloadFragments(dto);
    }

    @Value("${ignition.data.path}")
    private String dataPath;

    public PlaylistGetter getPlaylistGetter() {
        return new PlaylistGetter(dataPath);
    }

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Scheduled(cron = "0 * * * * *")// Every minute
    public void scheduledTask() {

        TestDataDto dto = new TestDataDto();
        dto.setTestField("Hi hi");
        messagingTemplate.convertAndSend("/topic/greetings", dto);
    }

    @Data
    public static class TestDataDto {
        private String testField;
    }
}
