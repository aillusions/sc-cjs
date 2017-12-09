package agd.ign.ignition.ctr.play;

import agd.ign.ignition.AsyncService;
import agd.ign.ignition.dto.get.GetAvailSongsDto;
import agd.ign.ignition.sys.ExecutionTime;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author aillusions
 */
@RestController()
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/rest")
@Getter
public class FeedRestController {

    @Autowired
    private AsyncService asyncService;


    // http://localhost:8090/ignition/rest/feed
    @ExecutionTime(ms = 20)
    @RequestMapping(value = "/feed", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public GetAvailSongsDto listSongs() {

        GetAvailSongsDto rv = new GetAvailSongsDto();

        return rv;
    }

}
