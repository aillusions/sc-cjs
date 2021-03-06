package agd.ign.ignition.ctr.play;

import agd.ign.ignition.AsyncService;
import agd.ign.ignition.app.PlaylistGetter;
import agd.ign.ignition.dto.get.AvailSongDto;
import agd.ign.ignition.dto.get.GetAvailSongsDto;
import agd.ign.ignition.sys.ExecutionTime;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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

/**
 * @author aillusions
 */
@RestController()
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/rest")
@Getter
public class PlaySongsRestController {

    @Autowired
    private AsyncService asyncService;

    // http://localhost:8090/ignition/rest/play/2EFq0rCJ3Zz3.128.mp3/0
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

    // http://localhost:8090/ignition/rest/list
    @ExecutionTime(ms = 20)
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public GetAvailSongsDto listSongs() {

        GetAvailSongsDto rv = new GetAvailSongsDto();

        for (String songId : getRecIds(asyncService.getPlaylistGetter())) {
            AvailSongDto songDto = new AvailSongDto();
            songDto.setAvailSongId(songId);
            rv.getAvailableSongsList().add(songDto);
        }

        Collections.shuffle(rv.getAvailableSongsList());

        return rv;
    }

    public static Set<String> getRecIds(PlaylistGetter playlistGetter) {
        Set<String> rv = new HashSet<>();

        File folder = playlistGetter.getAbsoluteStoragePath().toFile();
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {

            String songId = file.getName();
            if (file.isDirectory() && StringUtils.containsIgnoreCase(songId, ".mp3")) {
                rv.add(songId);
            }
        }
        return rv;
    }

}
