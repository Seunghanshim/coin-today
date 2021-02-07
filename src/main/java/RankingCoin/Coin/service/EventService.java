package RankingCoin.Coin.service;

import RankingCoin.Coin.domain.Category;
import RankingCoin.Coin.domain.Coin;
import RankingCoin.Coin.domain.Event;
import RankingCoin.Coin.repository.CoinRepository;
import RankingCoin.Coin.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CoinRepository coinRepository;

    @Transactional
    public void AddEventAll(List<Event> eventList){
        for (Event event : eventList) {
            eventRepository.save(event);
            Coin coin = coinRepository.find(event.getCoin().getId());
            coin.updateHasEvent(true);
        }
    }

    @Transactional(readOnly = true)
    public List<Event> findByCoin(Long id){
        return eventRepository.findByCoin(id);
    }

    @Transactional
    public void removeEvents(){
        List<Event> eventList = eventRepository.findAll();
        for(Event e: eventList){
            e.getCoin().updateHasEvent(false);
            eventRepository.remove(e);
        }
    }
}

