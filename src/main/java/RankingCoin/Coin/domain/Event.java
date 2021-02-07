package RankingCoin.Coin.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id")
    private Coin coin;

    private Long when;
    private String what;

    @Enumerated(EnumType.STRING)
    private Category category;

    public static Event createEvent(Coin coin, Long when, String what, int c){
        Event event = new Event();

        event.coin = coin;
        event.when = when;
        event.what = what;
        event.category = Category.values()[c];

        return event;
    }
}
