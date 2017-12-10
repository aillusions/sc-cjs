package agd.ign.ignition.ctr.play;

import agd.ign.ignition.AsyncService;
import agd.ign.ignition.dto.get.AvailSongDto;
import agd.ign.ignition.dto.get.GetAvailSongsDto;
import agd.ign.ignition.sys.ExecutionTime;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author aillusions
 */
@RestController()
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/rest")
@Getter
public class FeedRestController {

    private final int MAX_RECS_FEED = 10;

    @Autowired
    private AsyncService asyncService;

    private final Set<String> DELIVERED_RECS = Collections.synchronizedSet(new HashSet<>());

    // http://localhost:8090/ignition/rest/feed
    @ExecutionTime(ms = 10)
    @RequestMapping(value = "/feed", method = RequestMethod.GET, produces = "application/json")
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

}
