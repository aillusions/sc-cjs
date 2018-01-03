package agd.ign.ignition;

import agd.ign.ignition.app.PlaylistGetter;
import agd.ign.ignition.dto.put.NewSongDto;
import com.gmail.kunicins.olegs.libshout.Libshout;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * http://192.168.1.101:8000/java
 * http://192.168.1.101:8000/stream
 *
 * @author aillusions
 */
@EnableAsync
@Service
public class AsyncService {

    private final Libshout icecast;

    public AsyncService() throws IOException {
        icecast = new Libshout();
        icecast.setHost("192.168.1.101");
        icecast.setPort(8000);
        icecast.setProtocol(Libshout.PROTOCOL_HTTP);
        icecast.setPassword("hackme");
        icecast.setMount("/java");
        icecast.setFormat(Libshout.FORMAT_MP3);
        icecast.open();
    }

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

    @Async()
    public void playAsync() throws IOException {

        try {

            while (true) {

                byte[] buffer = new byte[8024];

                // InputStream mp3 = new BufferedInputStream(new FileInputStream(new File("/Users/mac/sc-cjs/down/3qE2mUmOdqTm.128.mp3/5.mp3")));
                // InputStream mp3 = new BufferedInputStream(new FileInputStream(new File("/Users/mac/sc-cjs/audiocheck.net_BrownNoise_15min.mp3")));
                InputStream mp3 = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("audio/audiocheck.net_BrownNoise_15min.mp3"));
                int read = mp3.read(buffer);

                while (read > 0) {
                    icecast.send(buffer, read);

                    System.out.println("Sent bytes: " + read);

                    read = mp3.read(buffer);
                }

                mp3.close();
            }

        } finally {

            System.out.println("Sending done.");
            icecast.close();
        }
    }
}
