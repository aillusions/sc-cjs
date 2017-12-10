package agd.ign.ignition.ctr.play;

import agd.ign.ignition.AsyncService;
import agd.ign.ignition.dto.get.AvailSongDto;
import agd.ign.ignition.dto.get.GetAvailSongsDto;
import agd.ign.ignition.sys.ExecutionTime;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author aillusions
 */
@RestController()
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/rest")
@Getter
public class FeedRestController {

    private final int MAX_RECS_FEED = 3;

    @Autowired
    private AsyncService asyncService;

    private final Set<String> DELIVERED_RECS = Collections.synchronizedSet(new HashSet<>());

    // http://localhost:8090/ignition/rest/feed
    @ExecutionTime(ms = 10)
    @RequestMapping(value = "/feed/next", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public GetAvailSongsDto listSongs() {

        GetAvailSongsDto rv = new GetAvailSongsDto();

        Set<String> recIds = PlaySongsRestController.getRecIds(asyncService.getPlaylistGetter());
        recIds.removeAll(DELIVERED_RECS);
        recIds = recIds.stream().limit(MAX_RECS_FEED).collect(Collectors.toSet());

        for (String songId : recIds) {
            AvailSongDto songDto = new AvailSongDto();
            songDto.setAvailSongId(songId);
            rv.getAvailableSongsList().add(songDto);
        }

        Collections.shuffle(rv.getAvailableSongsList());

        DELIVERED_RECS.addAll(recIds);

        return rv;
    }


    // http://localhost:8090/ignition/rest/feed/play/2EFq0rCJ3Zz3.128.mp3
    @RequestMapping(value = "/play/{songId:.+}/{fragIdx}", method = RequestMethod.GET)
    @ExecutionTime(ms = 20)
    public void getSongFragment(@PathVariable(name = "songId") String songId,
                                @PathVariable(name = "fragIdx") Integer fragIdx,
                                HttpServletResponse response) throws IOException, InterruptedException {


        Path fragPath = asyncService.getPlaylistGetter().getSongFragmentPath(songId, String.valueOf(4 + fragIdx) + ".mp3");

        System.out.println("Transferring: " + fragPath);

        File songFragment = fragPath.toFile();
        InputStream in = new FileInputStream(songFragment);

        response.setContentType("audio/mp3");

        response.addHeader("Cache-Control", CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic().getHeaderValue());

        //Thread.sleep(500);

        IOUtils.copy(in, response.getOutputStream());

        in.close();
    }

}
