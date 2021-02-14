package RankingCoin.Coin.service;

import RankingCoin.Coin.domain.CoinLog;
import RankingCoin.Coin.domain.CoinValLog;
import RankingCoin.Coin.domain.Exchange;
import RankingCoin.Coin.repository.CoinRepository;
import RankingCoin.Coin.repository.CoinValLogRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinValLogService {
    private final CoinValLogRepository coinValLogRepository;
    private final CoinRepository coinRepository;

    @Transactional
    public void createLog(CoinValLog coinValLog){
        coinValLogRepository.save(coinValLog);
    }

    @Transactional
    public void removeCoinLog(Long id){
        List<CoinValLog> byCoin = coinValLogRepository.findByCoin(id);
        for(CoinValLog coinValLog : byCoin){
            coinValLogRepository.remove(coinValLog);
        }
    }

    @Transactional(readOnly = true)
    public List<CoinValLog> findByDate(LocalDate date){
        return coinValLogRepository.findByDate(date);
    }

    @Transactional(readOnly = true)
    public List<CoinValLog> findBeforeByDate(LocalDate date){
        return coinValLogRepository.findBeforeByDate(date);
    }

    @Transactional(readOnly = true)
    public List<CoinValLog> findById(Long id){
        return coinValLogRepository.findByCoin(id);
    }

    @Transactional
    public void AddValueUpbit(JSONArray list, Long id){
        for(int j = 0; j < list.length(); j++){
            JSONObject obj = list.getJSONObject(j);
            String candle_date_time_kst = obj.getString("candle_date_time_kst");
            LocalDate date = LocalDate.parse(candle_date_time_kst, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            CoinValLog coinValLog = CoinValLog.create(coinRepository.find(id), date, obj.getDouble("prev_closing_price"));

            coinValLogRepository.save(coinValLog);
        }
    }
}
