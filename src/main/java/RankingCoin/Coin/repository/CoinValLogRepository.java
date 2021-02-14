package RankingCoin.Coin.repository;

import RankingCoin.Coin.domain.CoinLog;
import RankingCoin.Coin.domain.CoinValLog;
import RankingCoin.Coin.domain.Event;
import RankingCoin.Coin.domain.Exchange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CoinValLogRepository {

    private final EntityManager em;

    public void save(CoinValLog coinValLog){
        em.persist(coinValLog);
    }

    public void remove(CoinValLog coinValLog){
        em.remove(coinValLog);
    }

    public void removeByCoin(String market, Exchange exchange){
        List<CoinValLog> coinValLogList = em.createQuery("select cl from CoinValLog cl where cl.coin.market = :market and cl.coin.exchange = :exchange", CoinValLog.class)
                .setParameter("market", market)
                .setParameter("exchange", exchange)
                .getResultList();

        for (CoinValLog coinValLog : coinValLogList) {
            em.remove(coinValLog);
        }
    }

    public List<CoinValLog> findBeforeByDate(LocalDate date){
        return em.createQuery("select cl from CoinValLog cl where cl.date < :date", CoinValLog.class)
                .setParameter("date", date)
                .getResultList();
    }

    public List<CoinValLog> findByDate(LocalDate date){
        return em.createQuery("select cl from CoinValLog cl where cl.date =: date", CoinValLog.class)
                .setParameter("date", date)
                .getResultList();
    }

    public List<CoinValLog> findByCoin(Long id){
        return em.createQuery("select cl from CoinValLog cl where cl.coin.id =: id order by cl.date", CoinValLog.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<CoinValLog> findByMarketDate(String market, Exchange exchange, LocalDate date){
        return em.createQuery("select cl from CoinValLog cl where cl.coin.market =: market and cl.coin.exchange =: exchange and cl.date =: date", CoinValLog.class)
                .setParameter("market", market)
                .setParameter("exchange", exchange)
                .setParameter("date", date)
                .getResultList();
    }
}
