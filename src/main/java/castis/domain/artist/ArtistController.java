package castis.domain.artist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ArtistController {

    private ArtistDao artistDao;

    @RequestMapping(value = "/artistlist", method = RequestMethod.GET)
    public String artistList(Locale locale, Model model) {
        log.info("[GET]artist list");
        List<Artist> artistList = artistDao.getAllArtist();

        model.addAttribute("artistList", artistList);

        return "artistlist";
    }

    @RequestMapping(value = "/artist/{team}", method = RequestMethod.GET)
    public String JobStats(@PathVariable("team") String teamName, Model model, HttpServletRequest req) {
        String id = req.getParameter("id");
        log.info("[GET] artist team:{}, id:{}", teamName, id);
        Long idLongValue = Long.parseLong(id);
        if (idLongValue != null && idLongValue.longValue() > 0) {
            Artist curArtist = artistDao.get(idLongValue);
            model.addAttribute("artist", curArtist);
        }
        return "artist";
    }
}
