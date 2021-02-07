package RankingCoin.Coin.webController;

import RankingCoin.Coin.domain.Coin;
import RankingCoin.Coin.domain.CoinLog;
import RankingCoin.Coin.domain.Event;
import RankingCoin.Coin.service.CoinLogService;
import RankingCoin.Coin.service.CoinService;
import RankingCoin.Coin.service.EventService;
import RankingCoin.Coin.webController.DTO.CoinDTO;
import RankingCoin.Coin.webController.DTO.EventDTO;
import RankingCoin.Coin.webController.DTO.LogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CoinController {
    private final CoinService coinService;
    private final EventService eventService;
    private final CoinLogService coinLogService;

    @GetMapping("/coin/list")
    public String getCoinList(Model model){
        List<Coin> coinList = coinService.findAll();
        model.addAttribute("coinList", coinList);
        return "coin/coinList";
    }

    @GetMapping("/coin/{id}/view")
    public String getCoinInfo(@PathVariable("id")Long id, Model model) {
        List<Event> eventList = eventService.findByCoin(id);
        List<EventDTO> eventDTOS = new ArrayList<>();
        for (Event event : eventList) {
            eventDTOS.add(new EventDTO(event.getWhat(), event.getWhen(), event.getCategory()));
        }

        List<CoinLog> coinLogs = coinLogService.findByCoin(id);
        LogDTO logDTO = new LogDTO();
        for (CoinLog coinLog : coinLogs) {
            logDTO.getDate().add(coinLog.getDate());
            logDTO.getOp().add(coinLog.getOp());
            logDTO.getInterest().add(coinLog.getInterest());
            logDTO.getDominance().add(coinLog.getDominance());
            logDTO.getVol().add(coinLog.getVol());
            logDTO.getMarketCap().add(coinLog.getMarketCap());
        }

        Coin coin = coinService.find(id);
        CoinDTO coinDTO = new CoinDTO(coin.getMarket(),coin.getEng(),coin.getKor(),coin.getPre_krw(),coin.getKrw()
                ,coin.getAi1(),coin.getAi2(),coin.getAi3(),coin.getOp(),coin.getInterest(),coin.getTwit());

        CoinLog coinLog = coinLogs.get(coinLogs.size() - 1);

        model.addAttribute("today", coinLog);
        model.addAttribute("coinLog", logDTO);
        model.addAttribute("eventList", eventDTOS);
        model.addAttribute("coin", coinDTO);

        return "coin/coinView";
    }

}