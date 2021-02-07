package RankingCoin.Coin.service;

import RankingCoin.Coin.domain.Coin;
import RankingCoin.Coin.domain.CoinLog;
import RankingCoin.Coin.domain.Exchange;
import RankingCoin.Coin.repository.CoinLogRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CoinLogService {
    private final CoinLogRepository coinLogRepository;

    @Transactional
    public void createCoinLog(CoinLog coinLog){
        coinLogRepository.save(coinLog);
    }

    @Transactional
    public void removeCoinLog(LocalDate fst){
        List<CoinLog> beforeByDate = findBeforeByDate(fst);
        for (CoinLog coinLog : beforeByDate) {
            coinLogRepository.remove(coinLog);
        }
    }

    @Transactional(readOnly = true)
    public List<CoinLog> findByDate(LocalDate date){
        return coinLogRepository.findByDate(date);
    }

    @Transactional(readOnly = true)
    public List<CoinLog> findBeforeByDate(LocalDate date){
        return coinLogRepository.findBeforeByDate(date);
    }


    @Transactional(readOnly = true)
    public List<CoinLog> findByCoin(Long id){return coinLogRepository.findByCoin(id);}

    @Transactional
    public void AddVolumeUpbit(JSONArray list, LocalDate date){
        for(int j = 0; j < list.length(); j++){
            JSONObject obj = list.getJSONObject(j);
            String market = obj.getString("market");

            List<CoinLog> Logs = coinLogRepository.findByMarketDate(market, Exchange.Upbit, date);

            if(Logs.isEmpty()) continue;
            CoinLog coinLog = Logs.get(0);

            coinLog.updateVol(obj.getDouble("candle_acc_trade_volume"));
        }
    }

    @Transactional
    public void AddMarketCap(Map<String, Double> list, Exchange exchange, LocalDate date){
        Double global = list.get("global");
        for (String s : list.keySet()) {
            List<CoinLog> Logs = coinLogRepository.findByMarketDate(s, exchange, date);

            if(Logs.isEmpty()) continue;
            CoinLog coinLog = Logs.get(0);

            coinLog.updateMarketCap(Math.round(list.get(s)));
            coinLog.updateDominance((float) (Math.round(list.get(s) / global * 100 * 1000)/1000.0));
        }
    }
}
