package RankingCoin.Coin;

import RankingCoin.Coin.ScheduleTask.ScheduleTask;
import RankingCoin.Coin.service.CoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.text.ParseException;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class CoinApplication {
	private final ScheduleTask scheduleTask;
	private final CoinService coinService;

	public static void main(String[] args) {
		SpringApplication.run(CoinApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void init() throws InterruptedException, IOException, ParseException {
		if(coinService.findAll().isEmpty()) {
			scheduleTask.getCoinList();
			log.info("Coin List Initialized!");
			scheduleTask.getCoinValue();
			log.info("Coin's Value Initialized!");
			scheduleTask.updateAI();
			log.info("Coin's AI Initialized!");
			scheduleTask.getEventList();
			log.info("Coin's Event Initialized!");
			scheduleTask.updateCoinDayLog();
			log.info("Coin's Log Initialized!");

			scheduleTask.getCoinValueLogByUpbit();
			log.info("Coin's Value Log Initialized!");
		}

		log.info("Init Complete!");
	}
}
