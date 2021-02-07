package RankingCoin.Coin.api;

import RankingCoin.Coin.domain.Category;
import RankingCoin.Coin.domain.Coin;
import RankingCoin.Coin.domain.Event;
import RankingCoin.Coin.repository.CoinRepository;
import RankingCoin.Coin.service.CoinService;
import RankingCoin.Coin.service.EventService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CoinApiController {
    private final CoinService coinService;
    private final EventService eventService;

    @GetMapping("/api/coin")
    public List<CoinDto> Coins(){
        List<Coin> findCoins = coinService.findAll();

        List<CoinDto> collect = findCoins.stream()
                .map(m -> new CoinDto(m.getId(), m.getMarket(), m.getKor()))
                .collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/api/coin/{market}/twit/{name}")
    public boolean CoinsEvents(@PathVariable("market") String market, @PathVariable("name") String name){
        return coinService.updateCoinTwit(market, name);
    }

    @Data
    @AllArgsConstructor
    class CoinDto {
        private Long id;
        private String market;
        private String kor;
    }

    @Data
    @AllArgsConstructor
    class EventDto {
        private String event;
        private Category category;
        private Long whenDate;
    }
}
