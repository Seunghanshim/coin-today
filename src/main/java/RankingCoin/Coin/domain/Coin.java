package RankingCoin.Coin.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coin{
    @Id @Column(name = "coin_id")
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private Exchange exchange;

    private String market;
    private String eng;
    private String kor;
    private String twit;
    private Long marketCapId;

    private Double pre_krw; // 전일 종가
    private Double krw; // 1시간

    private Float ai1;
    private Float ai2;
    private Float ai3;

    private Float op;
    private Float interest;

    @Nullable
    private Boolean hasEvent;

    public void updateHasEvent(Boolean hasEvent){
        this.hasEvent = hasEvent;
    }

    public static Coin CreatCoin(Exchange exchange, String market, String eng, String kor){
        Coin coin = new Coin();
        coin.exchange = exchange;
        coin.market = market;
        coin.eng = eng;
        coin.kor = kor;

        return coin;
    }

    public void updateKrw(Double krw){
        this.krw = krw;
    }

    public void updatePre(Double krw){
        this.pre_krw = krw;
    }

    public void updateAi1(Float ai){
        this.ai1 = ai;
    }

    public void updateAi2(Float ai){
        this.ai2 = ai;
    }

    public void updateAi3(Float ai){
        this.ai3 = ai;
    }

    public void updateTwit(String twit){this.twit = twit;}

    public void updateId(Long id){this.marketCapId = id;}
}
