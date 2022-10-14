package org.seng302.main.config;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.DGAAHelper;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

/**
 * Configures Spring, has scheduling enabled for period checks
 */
@Configuration
@EnableScheduling
@Log4j2
public class SpringConfig {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CardService cardService;

    @Autowired
    DGAAHelper dgaaHelper;

    /**
     * Periodically check if a DGAA exists, and add one if it does not.
     */
    @Scheduled(fixedRateString = "${dgaaCheck.fixedRate.in.milliseconds}", initialDelayString = "${dgaaCheck.initialDelay.in.milliseconds}")
    public void scheduleFixedRateWithInitialDelayTask() {

        boolean exists = dgaaHelper.checkDGAAExists();
        if (!exists) {
            log.warn("Periodic DGAA check failed, adding new DGAA");
            dgaaHelper.addDGAA();
        }
    }

    @Scheduled(cron = "30 * * * * *")
    public void scheduleFetchNotification() {
        cardService.notifyNearExpireCard();
    }

    /**
     * Execute every minute at exactly zeroth second
     */
    @Scheduled(cron = "40 * * * * *")
    @Transactional
    public void scheduleEveryMinuteTimeStamp() {
        cardService.deleteExpiredCard();
    }

}
