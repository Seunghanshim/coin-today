package RankingCoin.Coin.repository;

import RankingCoin.Coin.domain.Event;
import RankingCoin.Coin.domain.Exchange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventRepository {
    private final EntityManager em;

    public void save(Event event){ em.persist(event); }

    public void remove(Event event){ em.remove(event); }

    public List<Event> findAll(){
        return em.createQuery("select e from Event e join fetch e.coin", Event.class).getResultList();
    }

    public List<Event> findByCoin(Long id){
        return em.createQuery("select e from Event e where e.coin.id =: id", Event.class)
                .setParameter("id", id)
                .getResultList();
    }

    public void removeByCoin(String market, Exchange exchange){
        List<Event> eventList = em.createQuery("select e from Event e where e.coin.market = :market and e.coin.exchange = :exchange", Event.class)
                .setParameter("market", market)
                .setParameter("exchange", exchange)
                .getResultList();

        for (Event event : eventList) {
            em.remove(event);
        }
    }
}
