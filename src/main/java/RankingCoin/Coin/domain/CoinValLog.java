package RankingCoin.Coin.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoinValLog {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id")
    private Coin coin;
    private LocalDate date;

    private Double krw;

    public static CoinValLog create(Coin coin, LocalDate date, Double krw){
        CoinValLog coinValLog = new CoinValLog();

        coinValLog.coin = coin;
        coinValLog.date = date;
        coinValLog.krw = krw;

        return coinValLog;
    }

    public void updateValue(Double val){
        this.krw = val;
    }
}
