package RankingCoin.Coin.repository;

import RankingCoin.Coin.domain.Coin;
import RankingCoin.Coin.domain.Exchange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CoinRepository {
    private final EntityManager em;

    public void save(Coin coin){ em.persist(coin); }

    public void remove(Coin coin){ em.remove(coin); }

    public Coin find(Long id){return em.find(Coin.class, id);}

    public List<Coin> findByMarket(String market, Exchange exchange){
        return em.createQuery("select c from Coin c where c.market =: market and c.exchange =: exchange", Coin.class)
                .setParameter("market", market)
                .setParameter("exchange", exchange)
                .getResultList();
    }

    public List<Coin> findByExchange(Exchange exchange){
        return em.createQuery("select c from Coin c where c.exchange =: exchange", Coin.class)
                .setParameter("exchange", exchange)
                .getResultList();
    }

    public List<Coin> findAll(){
        return em.createQuery("select c from Coin c order by c.ai1 desc", Coin.class).getResultList();
    }
}
