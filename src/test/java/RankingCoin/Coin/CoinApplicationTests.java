package RankingCoin.Coin;

import RankingCoin.Coin.domain.Coin;
import RankingCoin.Coin.domain.CoinLog;
import RankingCoin.Coin.repository.CoinLogRepository;
import RankingCoin.Coin.repository.CoinRepository;
import RankingCoin.Coin.repository.EventRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CoinApplicationTests {
	@Autowired
	CoinLogRepository coinLogRepository;

	@Test
	@Transactional
	@Rollback(false)
	public void contextLoads() {
		LocalDate now = LocalDate.now();
		List<CoinLog> byDate = coinLogRepository.findBeforeByDate(now);

		System.out.println(byDate);
	}
}
