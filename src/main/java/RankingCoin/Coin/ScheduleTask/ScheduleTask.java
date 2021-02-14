package RankingCoin.Coin.ScheduleTask;

import RankingCoin.Coin.domain.Coin;
import RankingCoin.Coin.domain.CoinLog;
import RankingCoin.Coin.domain.Event;
import RankingCoin.Coin.domain.Exchange;
import RankingCoin.Coin.repository.CoinLogRepository;
import RankingCoin.Coin.service.CoinLogService;
import RankingCoin.Coin.service.CoinService;
import RankingCoin.Coin.service.CoinValLogService;
import RankingCoin.Coin.service.EventService;
import jdk.nashorn.internal.ir.RuntimeNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RestController
@RequiredArgsConstructor
@Slf4j
public class ScheduleTask {
    private final CoinService coinService;
    private final EventService eventService;
    private final CoinLogService coinLogService;
    private final CoinValLogService coinValLogService;

    final String aiUrl = "http://116.46.225.202:5000/";

    @Scheduled(cron = "0 * * * * *")
    public void getCoinValue() throws InterruptedException {
        getCoinValueByUpbit();
    }

    public void getCoinValueByUpbit() throws InterruptedException {
        final String url = "https://api.upbit.com/v1/candles/minutes/1?market={market}&count=1";
        RestTemplate restTemplate = new RestTemplate();
        JSONArray All_list = new JSONArray();

        List<Coin> coinList = coinService.findByExchange(Exchange.Upbit);
        for (Coin coin : coinList) {
            log.info(coin.getEng() + " start! in Coin's Value");
            ResponseEntity<String> result = restTemplate.getForEntity(url, String.class, coin.getMarket());
            String res = "{\"coin_price\":" + result.getBody() + "}";

            JSONObject jsonObject = new JSONObject(res);
            JSONArray list = jsonObject.getJSONArray("coin_price");

            All_list.put(list.get(0));
            Thread.sleep(500);
        }

        coinService.AddPriceUpbit(All_list);
    }

    @Scheduled(cron = "0 5 10 * * *")
    public void updateAI(){
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> result = restTemplate.getForEntity(aiUrl + "ai", String.class);
        String res = "{\"coin_ai\":" + result.getBody() + "}";

        JSONObject jsonObject = new JSONObject(res);
        JSONObject jo = jsonObject.getJSONObject("coin_ai");

        coinService.updateAI(jo);

        ResponseEntity<String> result2 = restTemplate.getForEntity(aiUrl + "fb", String.class);
        String res2 = "{\"coin_fb\":" + result2.getBody() + "}";

        JSONObject jsonObject2 = new JSONObject(res2);
        JSONObject jo2 = jsonObject2.getJSONObject("coin_fb");

        coinService.updateFB(jo2);
    }

    @Scheduled(cron = "30 10 0 * * *")
    public void updateCoinDayLog() throws InterruptedException {
        updateMarketCapId();

        LocalDate now = LocalDate.now();
        LocalDate fst = now.minusWeeks(1);

        List<Coin> all = coinService.findAll();

        for (LocalDate i = fst; i.isBefore(now); i = i.plusDays(1)) {
            if (coinLogService.findByDate(i).isEmpty()) {
                for (Coin coin : all) {
                    CoinLog coinLog = CoinLog.createLog(coin, i);
                    coinLogService.createCoinLog(coinLog);
                }

                log.info(i.toString() + " start!! to " + now.toString() + " Coin's DayLog update..");
                getCoinVolumeByUpbit(i);
                getCoinMarketCap(Exchange.Upbit, i);
            }
        }

        coinLogService.removeCoinLog(fst);
    }

    public void getCoinValueLogByUpbit() throws InterruptedException {
        final String url = "https://api.upbit.com/v1/candles/days?market={market}&count=50";
        RestTemplate restTemplate = new RestTemplate();

        List<Coin> coinList = coinService.findByExchange(Exchange.Upbit);
        for (Coin coin : coinList) {
            coinValLogService.removeCoinLog(coin.getId());

            log.info(coin.getEng() + " start! in Coin's Value");
            ResponseEntity<String> result = restTemplate.getForEntity(url, String.class, coin.getMarket());
            String res = "{\"coin_val\":" + result.getBody() + "}";

            JSONObject jsonObject = new JSONObject(res);
            JSONArray list = jsonObject.getJSONArray("coin_val");

            coinValLogService.AddValueUpbit(list, coin.getId());
            Thread.sleep(500);
        }
    }

    public void updateMarketCapId(){
        log.info("update MarketCap Id");
        final String url = "https://api.coinmarketcap.com/data-api/v3/map/all";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        JSONObject jsonObject = new JSONObject(result.getBody()).getJSONObject("data");
        String data = jsonObject.keys().next();
        JSONArray jsonArray = jsonObject.getJSONArray(data);

        List<Coin> all = coinService.findAll();
        for(int i = 0;i < jsonArray.length();i++){
            JSONObject object = jsonArray.getJSONObject(i);
            String symbol = "KRW-" + object.getString("symbol");
            String name = object.getString("name");
            for (Coin coin : all) {
                if(symbol.equals(coin.getMarket()) || name.equals(coin.getEng())){
                    log.info(coin.getEng() + "'s id update to" + object.getLong("id"));
                    coinService.updateId(coin.getMarket(), object.getLong("id"));
                    break;
                }
            }
        }
    }

    public void getCoinVolumeByUpbit(LocalDate date) throws InterruptedException {
        final String url = "https://api.upbit.com/v1/candles/days?market={market}&count=1&to=" + date.toString() + " 00:00:00";
        RestTemplate restTemplate = new RestTemplate();
        JSONArray All_list = new JSONArray();

        List<Coin> coinList = coinService.findByExchange(Exchange.Upbit);
        for (Coin coin : coinList) {
            log.info(coin.getEng() + " start! in Coin's Volume");
            ResponseEntity<String> result = restTemplate.getForEntity(url, String.class, coin.getMarket());
            String res = "{\"coin_vol\":" + result.getBody() + "}";

            JSONObject jsonObject = new JSONObject(res);
            JSONArray list = jsonObject.getJSONArray("coin_vol");

            All_list.put(list.get(0));
            Thread.sleep(500);
        }

        coinLogService.AddVolumeUpbit(All_list, date);
        coinService.AddPrePriceUpbit(All_list);
    }

    public void getCoinMarketCap(Exchange exchange, LocalDate date) throws InterruptedException {
        final String global = "https://web-api.coinmarketcap.com/v1.1/global-metrics/quotes/historical?convert=KRW&format=chart&time_start={time_start}&time_end={time_end}";
        final String url = "https://web-api.coinmarketcap.com/v1.1/cryptocurrency/quotes/historical?convert=KRW&format=chart_crypto_details&id={id}&time_start={time_start}";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> gl_result = restTemplate.exchange(global, HttpMethod.GET, entity, String.class, date, date.plusDays(1));

        JSONObject gl_jsonObject = new JSONObject(gl_result.getBody()).getJSONObject("data");
        String gl_data = gl_jsonObject.keys().next();

        Map<String, Double> all_list = new HashMap<>();
        all_list.put("global", gl_jsonObject.getJSONArray(gl_data).getDouble(0));

        List<Coin> coinList = coinService.findByExchange(exchange);
        for (Coin coin : coinList) {
            log.info(coin.getEng() + " start! in Coin's MarketCap");

            if(coin.getMarketCapId() == null) continue;

            try {
                ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, coin.getMarketCapId(), date);
                JSONObject jsonObject = new JSONObject(result.getBody()).getJSONObject("data");
                String data = jsonObject.keys().next();
                if(jsonObject.getJSONObject(data).keySet().contains("KRW")) {
                    JSONArray jsonArray = jsonObject.getJSONObject(data).getJSONArray("KRW");
                    all_list.put(coin.getMarket(), jsonArray.getDouble(2));
                }
                else log.info(coin.getEng() + " symbol error!");
                Thread.sleep(500);
            }catch (RestClientException e){
                log.info(coin.getEng() + " error!");
            }
        }

        coinLogService.AddMarketCap(all_list, exchange, date);
    }

    @Scheduled(cron = "30 0 2 * * *")
    public void getCoinList(){
        getCoinListByUpbit();
//        getCoinListByBithumb();
    }

    public void getCoinListByUpbit(){//using API
        final String url = "https://api.upbit.com/v1/market/all";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        String res = "{\"coin_list\":" + result.getBody() + "}";

        JSONObject jsonObject = new JSONObject(res);
        JSONArray list = jsonObject.getJSONArray("coin_list");

        coinService.addAllUpbit(list);
    }

    public void getCoinListByBithumb(){//using API
        final String url = "https://api.bithumb.com/public/ticker/ALL_KRW";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);

        JSONObject jsonObject = new JSONObject(result.getBody());
        List<String> coinList = jsonObject.getJSONObject("data").keySet().stream().collect(Collectors.toList());

        coinService.addAllBithumb(coinList);
    }

    @Scheduled(cron = "30 0 3 * * *")
    public void getEventList() throws IOException, ParseException {
        getEventListByExchange(Exchange.Upbit);
//        getEventListByExchange(Exchange.Bithumb);
    }

    public void getEventListByExchange(Exchange exchange) throws IOException, ParseException { //not using API
        eventService.removeEvents(); //모든 호재 삭제(취소되거나 지난 호재 삭제 해주기 위함)

        LocalDateTime today = LocalDateTime.now();
        String url = "https://coinmarketcal.com/en/?form[date_range]=" + today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "+-+";
        String url2 = "&form[exchanges][]=" + exchange.name().toLowerCase() + "&form[categories][]=";
        url += today.plusMonths(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); //향후 2달

        List<Coin> coinList = coinService.findByExchange(exchange);
        Map<String,Event> eventList = new HashMap<>();

        for(int c = 1; c <= 16; c++) {
            if(c == 10 || c == 12) continue;
            log.info(url + url2 + c + "&page=");

            for (int page = 1; ; page++) {
                log.info("Initializing Coin's Event..("+ c + "/ 16)...page " + page);
                Document doc = Jsoup.connect(url + url2 + c + "&page=" + page).get();
                if (!doc.select("div[class=\"alert alert-transparent\"]").isEmpty()) break;

                Elements elem = doc.select("a").select("[href=\"#reminderModal\"]");
                for (Element e : elem) {
                    String name = e.attr("data-coin");
                    String[] data = name.split("\\(");
                    StringTokenizer t = new StringTokenizer(data[1], ")");
                    name = "KRW-" + t.nextToken();

                    for (Coin co: coinList){
                        if(co.getMarket().equals(name)){
                            String date = e.attr("data-date");
                            String what = e.attr("title");
                            Date time = new SimpleDateFormat("dd MMMMM yyyy", Locale.US).parse(date);
                            LocalDateTime when = LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());

                            eventList.put(what, Event.createEvent(co, Duration.between(today,when).toDays(), what, c));
                            break;
                        }
                    }
                }
            }
        }

        eventService.AddEventAll(eventList);
    }
}
