package org.seng302.main;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Executes DGAA checks/creation upon the application being ready.
 */
@Component
@Log4j2
@Order(0) // This will be executed first
class DGAAStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    UserRepository repository;

    @Autowired
    DGAAHelper dgaaHelper;

    /**
     * Checks if DGAA exists, if not, creates one.
     *
     * @param event ApplicationReadyEvent
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        boolean dgaaExists = dgaaHelper.checkDGAAExists();
        if (!dgaaExists) {
            log.warn("DGAA Does not exist");
            dgaaHelper.addDGAA();
            log.info("DGAA has been created");
        }
    }

}
