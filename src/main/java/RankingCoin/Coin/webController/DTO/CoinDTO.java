package RankingCoin.Coin.webController.DTO;

import lombok.Data;

@Data
public class CoinDTO {
    private String market;
    private String eng;
    private String kor;

    private Double pre_krw; // 전일 종가
    private Double krw; // 1시간

    private Float ai1;
    private Float ai2;
    private Float ai3;

    private String twit;

    public CoinDTO(String market, String eng, String kor, Double pre_krw, Double krw, Float ai1, Float ai2, Float ai3, Float op, Float interest, String twit) {
        this.market = market;
        this.eng = eng;
        this.kor = kor;
        this.pre_krw = pre_krw;
        this.krw = krw;
        this.ai1 = ai1;
        this.ai2 = ai2;
        this.ai3 = ai3;
        this.op = op;
        this.interest = interest;
        this.twit = twit;
    }

    private Float op;
    private Float interest;
}
