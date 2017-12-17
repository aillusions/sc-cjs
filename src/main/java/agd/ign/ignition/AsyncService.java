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
import org.springframework.stereotype.Component;

import java.io.*;

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

    @Async()
    public void playAsync() throws IOException {

        Libshout icecast = new Libshout();

        try {


            icecast.setHost("192.168.1.102");
            icecast.setPort(8000);
            icecast.setProtocol(Libshout.PROTOCOL_HTTP);
            icecast.setPassword("hackme");
            icecast.setMount("/java");
            icecast.setFormat(Libshout.FORMAT_MP3);
            icecast.open();

            while (true) {

                byte[] buffer = new byte[8024];

                InputStream mp3 = new BufferedInputStream(new FileInputStream(new File("/Users/mac/sc-cjs/down/3qE2mUmOdqTm.128.mp3/15.mp3")));
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
