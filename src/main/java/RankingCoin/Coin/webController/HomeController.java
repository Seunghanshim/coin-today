package RankingCoin.Coin.webController;

import RankingCoin.Coin.service.CoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {
    private final CoinService coinService;

    @RequestMapping("/")
    public String home(Model model){
        log.info("home controller");
        model.addAttribute("coinList", coinService.findAll());
        return "home";
    }
}
