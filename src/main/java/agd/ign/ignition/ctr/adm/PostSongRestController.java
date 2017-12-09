package agd.ign.ignition.ctr.adm;

import agd.ign.ignition.AsyncService;
import agd.ign.ignition.dto.AboutIgnitionDto;
import agd.ign.ignition.dto.put.NewSongDto;
import agd.ign.ignition.dto.put.NewSongResponseDto;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author aillusions
 */
@RestController()
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/rest")
@Setter
public class PostSongRestController {

    @Autowired
    private AsyncService asyncService;

    // http://localhost:8090/ignition/rest/about
    // https://localhost/ignition/rest/about
    // https://192.168.0.103/ignition/rest/about
    // https://zalizniak.com/ignition/rest/about
    @RequestMapping(value = "/about", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public AboutIgnitionDto about() throws InterruptedException {

        return new AboutIgnitionDto();
    }

    @RequestMapping(value = "/song/new", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public NewSongResponseDto sendSong(@RequestBody NewSongDto dto) throws IOException {

        System.out.println("Processing: " + dto.getScCjsSongTitle());

        asyncService.asyncDownloadFragments(dto);

        return new NewSongResponseDto();
    }

    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity handle() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
