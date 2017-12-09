package agd.ign.ignition.ctr;

import agd.ign.ignition.app.PlaylistGetter;
import agd.ign.ignition.dto.get.AvailSongDto;
import agd.ign.ignition.dto.get.GetAvailSongsDto;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author aillusions
 */
@RestController()
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/rest")
public class PlaySongsRestController {

    // https://localhost/ignition/rest/play/2EFq0rCJ3Zz3.128.mp3
    @RequestMapping(value = "/play/{songId:.+}/{fragIdx}", method = RequestMethod.GET)
    public void getSongFragment(@PathVariable(name = "songId") String songId,
                                @PathVariable(name = "fragIdx") Integer fragIdx,
                                HttpServletResponse response) throws IOException, InterruptedException {


        Path fragPath = PlaylistGetter.getSongFragmentPath(songId, String.valueOf(4 + fragIdx) + ".mp3");

        System.out.println("Transferring: " + fragPath);

        File songFragment = fragPath.toFile();
        InputStream in = new FileInputStream(songFragment);

        response.setContentType("audio/mp3");

        response.addHeader("Cache-Control", CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic().getHeaderValue());

        //Thread.sleep(500);

        IOUtils.copy(in, response.getOutputStream());

        in.close();
    }

    // https://localhost/ignition/rest/list
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public GetAvailSongsDto listSongs() {

        GetAvailSongsDto rv = new GetAvailSongsDto();

        File folder = PlaylistGetter.getAbsoluteStoragePath().toFile();
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {

            String songId = file.getName();
            if (file.isDirectory()) {
                StringUtils.containsIgnoreCase(songId, ".mp3");
                System.out.println("Found recording: " + songId);

                AvailSongDto songDto = new AvailSongDto();
                songDto.setAvailSongId(songId);
                rv.getAvailableSongsList().add(songDto);
            }
        }

        Collections.shuffle(rv.getAvailableSongsList());

        return rv;
    }

}
