package RankingCoin.Coin.webController.DTO;

import RankingCoin.Coin.domain.Category;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
public class EventDTO {
    private String what;
    private Long when;
    private Category category;

    public EventDTO(String what, Long when, Category category) {
        this.what = what;
        this.when = when;
        this.category =category;
    }
}
