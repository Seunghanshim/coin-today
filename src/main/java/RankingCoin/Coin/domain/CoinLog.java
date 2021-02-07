package RankingCoin.Coin.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.DoubleBinaryOperator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoinLog {
    @Id @Column(name = "coinlog_id")
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id")
    private Coin coin;
    private LocalDate date;

    private Float op;
    private Float interest;
//    private Double krw;

    private Float dominance;
    private Double vol;
    private Long marketCap;

    public static CoinLog createLog(Coin coin, LocalDate date){
        CoinLog coinLog = new CoinLog();

        coinLog.coin = coin;
        coinLog.date = date;

        return coinLog;
    }

    public void updateOP(Float op){
        this.op = op;
    }

    public void updateInterest(Float interest){
        this.interest = interest;
    }

    public void updateDominance(Float dominance){
        this.dominance = dominance;
    }

    public void updateVol(Double vol){
        this.vol = vol;
    }

//    public void updateKrw(Double krw){this.krw = krw;}

    public void updateMarketCap(Long marketCap){
        this.marketCap = marketCap;
    }
}
