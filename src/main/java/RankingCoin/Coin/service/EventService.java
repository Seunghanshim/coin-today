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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CoinRepository coinRepository;

    @Transactional
    public void AddEventAll(Map<String, Event> eventList){
        for (String key: eventList.keySet()) {
            Event event = eventList.get(key);
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

