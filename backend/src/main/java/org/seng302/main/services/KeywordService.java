package org.seng302.main.services;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.dto.request.KeywordRequest;
import org.seng302.main.dto.response.KeywordResponse;
import org.seng302.main.dto.responsefactory.ResponseFactory;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.*;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.KeywordRepository;
import org.seng302.main.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Keyword Service class
 * Executes all the functionality
 */
@Service
@Log4j2
public class KeywordService {

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    private ResponseFactory responseFactory = new ResponseFactory();

    /**
     * Create a new keyword, set created time and add to the repository
     *
     * @param creator Creator of keyword
     * @param keywordData contains data to create keyword
     * @return newly formatted keyword
     */
    public Keyword createKeyword(User creator, KeywordRequest keywordData) {
        Keyword keyword = new Keyword();
        keyword.setName(keywordData.getName());
        keyword.setCreated(LocalDate.now());
        keyword = keywordRepository.save(keyword);

        notifyAdmins(creator, keyword);

        return keyword;
    }

    /**
     * Notify all admins about a new keyword being created
     *
     * @param creator Creator of keyword
     * @param keyword Keyword made
     */
    private void notifyAdmins(User creator, Keyword keyword) {
        for (User user : userService.findAllAdmins()) {
            Notification notification = new Notification()
                    .withRecipient(user)
                    .withCreated(LocalDateTime.now())
                    .withType(NotificationType.KEYWORD_ADDED)
                    .withKeywordId(keyword.getId())
                    .withMessage(String.format("%s (ID: %d) has created a new keyword:%n%s",
                            creator.getFirstName(), creator.getId(), keyword.getName())
                    );

            notificationRepository.save(notification);
        }
    }

    /**
     * Check if the given name of the keyword is valid.
     *
     * @param keywordData instance of the keyword
     * @throws ResponseStatusException If invalid keyword request
     */
    public void validateKeywordRequest(KeywordRequest keywordData) {

        int maxKeywordLength = 25;

        String name = keywordData.getName();

        if (name == null || name.equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Keyword name cannot be null or empty.\n");
        }
        else if (name.length() > maxKeywordLength) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Keyword name exceeds max length.\n");
        }
        else if (doesKeywordExist(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Keyword with that name already exists.\n");
        }
    }

    /**
     * Check if given keyword exists in repository
     *
     * @param name Keyword name
     */
    public boolean doesKeywordExist(String name)
    {
        return keywordRepository.existsKeywordByName(name);
    }

    /**
     * Find keyword by given name
     *
     * @param name Keyword name
     */
    public Keyword findKeywordByName(String name)
    {
        return keywordRepository.findByName(name);
    }

    /**
     * Get all keywords similar to the search query
     *
     * @param query Query of keywords names
     * @return List of keywords as a response DTO
     */
    public List<KeywordResponse> getAllKeywordsLike(String query) {
        List<Keyword> keywords = keywordRepository.findByNameIgnoreCaseContaining(query);
        return responseFactory.getKeywordResponses(keywords);
    }

    /**
     * Deletes keyword by keyword id
     *
     * @param keywordId id of the keyword
     */
    @Transactional
    public void deleteKeyword(Long keywordId) {
        // Delete all notifications about the keyword
        notificationService.deleteKeywordNotifications(keywordId);

        Keyword keyword = keywordRepository.findKeywordById(keywordId);
        for (Card card : cardRepository.getCardsByKeywords(keyword)) {
            card.getKeywords().remove(keyword);
            cardRepository.save(card);
        }

        keywordRepository.deleteById(keywordId);
    }

}
