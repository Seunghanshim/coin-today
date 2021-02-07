package RankingCoin.Coin.repository;

import RankingCoin.Coin.domain.CoinLog;
import RankingCoin.Coin.domain.Exchange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CoinLogRepository {
    private final EntityManager em;

    public void save(CoinLog coinLog){
        em.persist(coinLog);
    }

    public void remove(CoinLog coinLog){
        em.remove(coinLog);
    }

    public List<CoinLog> findByDate(LocalDate date){
        return em.createQuery("select cl from CoinLog cl where cl.date =: date", CoinLog.class)
                .setParameter("date", date)
                .getResultList();
    }

    public List<CoinLog> findBeforeByDate(LocalDate date){
        return em.createQuery("select cl from CoinLog cl where cl.date < :date", CoinLog.class)
                .setParameter("date", date)
                .getResultList();
    }

    public List<CoinLog> findByCoin(Long id){
        return em.createQuery("select cl from CoinLog cl where cl.coin.id =: id", CoinLog.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<CoinLog> findByMarketDate(String market, Exchange exchange, LocalDate date){
        return em.createQuery("select cl from CoinLog cl where cl.coin.market =: market and cl.coin.exchange =: exchange and cl.date =: date", CoinLog.class)
                .setParameter("market", market)
                .setParameter("exchange", exchange)
                .setParameter("date", date)
                .getResultList();
    }
}
