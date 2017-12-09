package agd.ign.ignition;

import agd.ign.ignition.app.PlaylistGetter;
import agd.ign.ignition.dto.put.NewSongDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

/**
 * @author aillusions
 */
@EnableAsync
@Service
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
}
