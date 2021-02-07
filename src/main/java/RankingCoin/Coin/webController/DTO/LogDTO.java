package RankingCoin.Coin.webController.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class LogDTO {
    private List<LocalDate> date = new ArrayList<>();

    private List<Float> op = new ArrayList<>();
    private List<Float> interest = new ArrayList<>();

    private List<Float> dominance = new ArrayList<>();
    private List<Double> vol = new ArrayList<>();
    private List<Long> marketCap = new ArrayList<>();

}
