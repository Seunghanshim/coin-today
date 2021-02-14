package RankingCoin.Coin.service;

import RankingCoin.Coin.domain.Coin;
import RankingCoin.Coin.domain.Exchange;
import RankingCoin.Coin.repository.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CoinService {
    private final CoinRepository coinRepository;
    private final EventRepository eventRepository;
    private final CoinLogRepository coinLogRepository;
    private final CoinValLogRepository coinValLogRepository;

    @Transactional
    public void addAllUpbit(JSONArray list){
        List<Coin> coinList = coinRepository.findByExchange(Exchange.Upbit);
        Map<String, Boolean> listCheck = new HashMap<String,Boolean>();

        for(Coin c:coinList){ listCheck.put(c.getMarket(), false);}

        for(int i = 0; i < list.length(); i++) {
            JSONObject obj = list.getJSONObject(i);
            String market = obj.getString("market");
            StringTokenizer t = new StringTokenizer(market,"-");

            if(t.nextToken().equals("KRW")) {
                Coin coin = Coin.CreatCoin(Exchange.Upbit, market, obj.getString("english_name"), obj.getString("korean_name"));
                if (!listCheck.containsKey(market)) {
                    coinRepository.save(coin);
                }
                else{
                    listCheck.put(market, true);
                }
            }
        }

        for(String market: listCheck.keySet()){
            if(!listCheck.get(market)){
                eventRepository.removeByCoin(market, Exchange.Upbit);
                coinLogRepository.removeByCoin(market, Exchange.Upbit);
                coinValLogRepository.removeByCoin(market, Exchange.Upbit);
                Coin findCoin = coinRepository.findByMarket(market, Exchange.Upbit).get(0);
                coinRepository.remove(findCoin);
            }
        }
    }

    @Transactional
    public void addAllBithumb(List<String> list){
        List<Coin> coinList = coinRepository.findByExchange(Exchange.Bithumb);
        Map<String, Boolean> listCheck = new HashMap<String, Boolean>();

                for(Coin c:coinList){ listCheck.put(c.getMarket(), false);}

                for (String market : list) {
                    String name = "KRW-" + market;
                    List<Coin> findCoin = coinRepository.findByMarket(name, Exchange.Upbit);
                    if(findCoin.size() == 0) continue;

                    Coin coin = Coin.CreatCoin(Exchange.Bithumb, name, findCoin.get(0).getEng(), findCoin.get(0).getKor());
                    if (!listCheck.containsKey(market)) {
                        coinRepository.save(coin);
                    }
                    else{
                listCheck.put(market, true);
            }
        }

        for(String market: listCheck.keySet()){
            if(!listCheck.get(market)){
                eventRepository.removeByCoin(market, Exchange.Bithumb);
                Coin findCoin = coinRepository.findByMarket(market, Exchange.Bithumb).get(0);
                coinRepository.remove(findCoin);
            }
        }
    }

    @Transactional
    public void AddPriceUpbit(JSONArray list){
        for(int j = 0; j < list.length(); j++){
            JSONObject obj = list.getJSONObject(j);
            String market = obj.getString("market");

            Coin coin = coinRepository.findByMarket(market, Exchange.Upbit).get(0);

            coin.updateKrw(obj.getDouble("trade_price"));
        }
    }

    @Transactional
    public void AddPrePriceUpbit(JSONArray list){
        for(int j = 0; j < list.length(); j++){
            JSONObject obj = list.getJSONObject(j);
            String market = obj.getString("market");

            Coin coin = coinRepository.findByMarket(market, Exchange.Upbit).get(0);

            coin.updatePre(obj.getDouble("trade_price"));
        }
    }

    @Transactional(readOnly = true)
    public List<Coin> findAll(){
        return coinRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Coin> findByExchange(Exchange exchange){
        return coinRepository.findByExchange(exchange);
    }

    @Transactional(readOnly = true)
    public Coin find(Long id){
        return coinRepository.find(id);
    }

    @Transactional
    public boolean updateCoinTwit(String market, String twit){
        List<Coin> byMarket = coinRepository.findByMarket(market, Exchange.Upbit);
        if(!byMarket.isEmpty()){
            byMarket.get(0).updateTwit(twit);
            return true;
        }
        else{
            return false;
        }
    }

    @Transactional
    public void updateId(String market, Long id){
        coinRepository.findByMarket(market, Exchange.Upbit).get(0).updateId(id);
    }

    @Transactional
    public void updateAI(JSONObject jsonObject){
        List<Coin> byExchange = coinRepository.findByExchange(Exchange.Upbit);
        for (Coin coin : byExchange) {
            if(jsonObject.has(coin.getMarket())) {
                coin.updateAi1((float) (jsonObject.getJSONObject(coin.getMarket()).getInt("proba_1") / 100.0));
            }
        }
    }

    @Transactional
    public void updateFB(JSONObject jsonObject){
        List<Coin> byExchange = coinRepository.findByExchange(Exchange.Upbit);
        for (Coin coin : byExchange) {
            if(jsonObject.has(coin.getMarket())) {
                coin.updateAi2(jsonObject.getFloat(coin.getMarket()) * 100);
            }
        }
    }
}
