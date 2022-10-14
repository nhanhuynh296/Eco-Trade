package org.seng302.main.services;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.dto.request.AddressRequest;
import org.seng302.main.dto.request.CardRequest;
import org.seng302.main.dto.request.KeywordRequest;
import org.seng302.main.dto.response.CardResponse;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.responsefactory.ResponseFactory;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.Card;
import org.seng302.main.models.Keyword;
import org.seng302.main.models.Notification;
import org.seng302.main.models.User;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.KeywordRepository;
import org.seng302.main.repository.NotificationRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.repository.specificationHelper.CardSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Card Service class
 * <p>
 * Executes all the functionality
 */
@Service
@Log4j2
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationRepository notificationRepository;

    private ResponseFactory responseFactory = new ResponseFactory();

    static final String TITLE = "title";
    static final String CREATED = "created";

    /**
     * Creates a new card and sets the createdCard
     *
     * @param currentUser instance of the current user
     * @param cardData contains all the required data for creating a new card
     * @return -1 if the card details were wrong
     * -2 if the current user is not a GAA or the user in the card
     * 0 if created successfully
     */
    public Card createCard(User currentUser, CardRequest cardData) {
        User cardUser = userRepository.findUserById(cardData.getCreatorId());
        List<String> keywords = cardData.getKeywords();

        Card card = new Card();
        card.setSection(cardData.getSection());
        card.setTitle(cardData.getTitle());
        card.setDescription(cardData.getDescription());

        String errorMessage = isValidCard(card);
        if (!errorMessage.equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        if (!isGAAorMainUser(cardUser, currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the card user or GAA can create a card.");
        }

        List<Keyword> cardKeywords = addKeywords(currentUser, keywords);

        card.setCreated(LocalDateTime.now());
        card.setDisplayPeriodEnd(LocalDateTime.now().plusWeeks(2));
        card.setKeywords(cardKeywords);
        card.setCreator(cardUser);
        return cardRepository.save(card);
    }

    /**
     * Adds keywords to a card
     *
     * @param keywords list of keyword strings to add to card
     * @return cardKeywords Array list of keywords
     */
    public List<Keyword> addKeywords(User currentUser, List<String> keywords) {
        List<Keyword> cardKeywords = new ArrayList<>();
        KeywordRequest currentRequest;
        if (keywords != null) {
            for (String keyword : keywords) {
                currentRequest = new KeywordRequest(keyword);
                if (!keywordService.doesKeywordExist(keyword)) {
                    Keyword createdKeyword = keywordService.createKeyword(currentUser, currentRequest);
                    cardKeywords.add(createdKeyword);
                } else {
                    cardKeywords.add(keywordService.findKeywordByName(keyword));
                }
            }
        }
        return cardKeywords;
    }

    /**
     * Updates an existing card
     *
     * @param cardData contains all the required data for updating a card
     * @return -1 if the card details were wrong
     * 0 if created successfully
     */
    public Card updateCard(User currentUser, CardRequest cardData, long id) {
        List<String> keywords = cardData.getKeywords();
        List<Keyword> cardKeywords = addKeywords(currentUser, keywords);
        Card card = cardRepository.getCardById(id);

        // If the card title or description are null, do not attempt to change it
        if (cardData.getTitle() != null){
            card.setTitle(cardData.getTitle());
        }
        if (cardData.getDescription() != null){
            card.setDescription(cardData.getDescription());
        }
        card.setKeywords(cardKeywords);

        String errorMessage = isValidCard(card);
        if (!(errorMessage.equals(""))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        card = cardRepository.save(card);
        return card;
    }

    /**
     * Delete a card using card id
     *
     * @param cardId card id
     */
    @Transactional
    public void deleteCard(Long cardId) {
        notificationRepository.deleteNotificationByCardIdAndType(cardId, NotificationType.CARD_EXPIRING);
        cardRepository.deleteById(cardId);
    }

    /**
     * Get paginated cards from a given section (default to first page)
     *
     * @param section Card section
     * @return JSON serialized cards
     */
    public PaginationInfo<CardResponse> getCards(String section, int pageNumber, int pageSize,
                               String sortBy, String country, String region, String city) {
        Sort sort;
        switch (sortBy) {
            case "DATE_ASC":
                sort = Sort.by(Sort.Direction.ASC, CREATED);
                break;
            case "DATE_DESC":
                sort = Sort.by(Sort.Direction.DESC, CREATED);
                break;
            case "TITLE_AZ":
                sort = Sort.by(Sort.Direction.ASC, TITLE);
                break;
            case "TITLE_ZA":
                sort = Sort.by(Sort.Direction.DESC, TITLE);
                break;
            default:
                sort = Sort.by(Sort.Direction.DESC, CREATED);
        }
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Card> cardPage = cardRepository
                .findCardsBySectionAndDisplayPeriodEndAfter(section, LocalDateTime
                        .now(), pageable);
        if (city != null) {
            cardPage = cardRepository.findAllBySectionAndCountryAndRegionAndCity(section, country, region, city, pageable);
        } else if (region != null) {
            cardPage = cardRepository.findAllBySectionAndCountryAndRegion(section, country, region, pageable);
        } else if (country != null) {
            cardPage = cardRepository.findAllBySectionAndCountry(section, country, pageable);
        }

        return new PaginationInfo<>(responseFactory.getCardResponses(cardPage.getContent()), cardPage.getTotalPages(), cardPage.getTotalElements());
    }

    /**
     * Get card based on card ID
     *
     * @param id card id
     * @return A {@link Card} with corresponding id
     */
    public CardResponse getCard(Long id) {
        Card card = cardRepository.getCardById(id);

        if (card == null) {
            return null;
        }

        return responseFactory.getCardResponse(card);
    }

    /**
     * Checks if the card details are valid
     *
     * @param card instance of the card
     * @return a message that contain either error messages if card details were invalid
     * or is empty if all the details were valid
     */
    public String isValidCard(Card card) {
        int maxTitleLength = 70;
        int maxDescriptionLength = 250;
        String message = "";

        if (card.getSection() == null) {
            message += "Card section cannot be empty.\n";
        }

        if (card.getTitle() == null || card.getTitle().strip().length() == 0) {
            message += "Card title cannot be empty.\n";
        } else if (card.getTitle().length() > maxTitleLength) {
            message += "Card title name exceeds character limit.\n";
        }

        if ((card.getDescription() != null && card.getDescription().length() != 0) && card.getDescription().length() > maxDescriptionLength) {
            message += "Card description exceeds character limit.\n";
        }

        return message.trim();
    }

    /**
     * Checks if the current user is a GAA or the user in the card and if both users are not null
     *
     * @param cardUser instance of the card user
     * @param currentUser instance of the current user
     * @return boolean True if the user is valid and False if the user is invalid
     */
    public boolean isGAAorMainUser(User cardUser, User currentUser) {
        return cardUser != null && currentUser != null && (
                cardUser.getId().equals(currentUser.getId()) || userService.isApplicationAdmin(currentUser));
    }


    /**
     * Extend the card display end period for 2 week started from when the request is sent
     *
     * @param id of the card to be extend display period
     */
    @Transactional
    public void extendCardDisplayPeriod(Long id) {
        Card card = cardRepository.getCardById(id);
        card.setCreated(LocalDateTime.now());
        card.setDisplayPeriodEnd(LocalDateTime.now().plusWeeks(2));
        card.removeNotification(notificationRepository.getByCardIdAndType(card.getId(), NotificationType.CARD_EXPIRING));
        cardRepository.save(card);
        notificationRepository.deleteNotificationByCardIdAndType(card.getId(), NotificationType.CARD_EXPIRING);

    }

    /**
     * Gets user cards
     *
     * @param user instance of User
     * @param pageNumber number of the page
     * @param pageSize size of the page
     * @return PaginationInfo<CardResponse> paginated list of cards of the user
     */
    public PaginationInfo<CardResponse> getCardsFromUser(User user, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Card> cardPage = cardRepository.findCardsByCreator(user, pageable);
        return new PaginationInfo<>(responseFactory.getCardResponses(cardPage.getContent()), cardPage.getTotalPages(), cardPage.getTotalElements());
    }

    /**
     * Create notification based off card state
     *
     * @param card instance of Card
     * @param message String of the message
     * @param type notification type
     * @return new notification
     */
    public Notification createNotification(Card card, String message, NotificationType type) {
        Notification notification = new Notification()
                .withCreated(LocalDateTime.now())
                .withRecipient(card.getCreator())
                .withMessage(message)
                .withType(type)
                .withExpiry(card.getDisplayPeriodEnd());

        if (type == NotificationType.CARD_EXPIRING) {
            notification.withCard(card);
        }

        notification = notificationRepository.save(notification);
        return notification;
    }

    /**
     * Delete all expired card using display period end field
     */
    public void deleteExpiredCard() {
        List<Card> expiredCards = cardRepository.getExpiredCards(LocalDateTime.now());
        log.trace("Deleting all expired card...");
        for (Card expiredCard : expiredCards) {
            notificationRepository.deleteNotificationByCardIdAndType(expiredCard.getId(), NotificationType.CARD_EXPIRING);
            String message = String.format("Community listing for '%s' has expired and has been removed.", expiredCard.getTitle());
            createNotification(expiredCard, message, NotificationType.CARD_DELETED);
            cardRepository.delete(expiredCard);
        }

    }

    /**
     * Helper get all the card that will be expire with in 1 day and have not been added to
     * notification before 1 day
     *
     * @return {@link List<Card> List of card}
     */
    public List<Card> fetchExpiredCard() {
        return cardRepository.findCardsByDisplayPeriodEndBeforeAndNotifiedFalse(
                LocalDateTime.now().plusDays(1));
    }

    /**
     * Create notification for each card that is near expiry. Save the newely created
     * notification to notification table
     */
    @Transactional
    public void notifyNearExpireCard() {
        log.trace("Fetching expired card and create notification");
        for (Card nearExpireCard : fetchExpiredCard()) {
            String message = String.format("Marketplace listing for '%s' is expiring soon", nearExpireCard.getTitle());
            Notification notification = createNotification(nearExpireCard, message, NotificationType.CARD_EXPIRING);
            nearExpireCard.addNotification(notification);
            cardRepository.save(nearExpireCard);
        }
    }

    /**
     * Retrieves the search query result in a paginated form with total pages and elements values
     *
     * @param keywords list of keywords, max 7
     * @param type search type, either "and" or "or"
     * @param pageNumber int value of the page
     * @param pageSize int value of the page size
     * @return List<?> of all the cards in the page and number of total pages and elements
     */
    public PaginationInfo<CardResponse> cardSearch(List<String> keywords, String type, String section, String sortBy, AddressRequest address, int pageNumber, int pageSize) {
        Sort sort = getCardSort(sortBy);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Specification<Card> cardSpecification;
        cardSpecification = Specification.where(CardSpecification.matchesAllFilters(keywords, section, type, address));
        Page<Card> page = cardRepository.findAll(cardSpecification, pageable);

        List<CardResponse> cards = new ArrayList<>();
        for(Card card: page.getContent()) {
            cards.add(responseFactory.getCardResponse(card));
        }
        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();

        return new PaginationInfo<>(cards, totalPages, totalElements);
    }

    /**
     * Uses a switch to determine the sort to be used in the search specification. Accounts for lower case titles.
     *
     * @param sortBy a string that represents a specific sort
     * @return a relevant sort parameter to be used by the search specification
     */
    public Sort getCardSort(String sortBy) {
        Sort sort;
        switch (sortBy) {
            case "DATE_ASC":
                sort = Sort.by(Sort.Order.asc(CREATED), Sort.Order.desc("id"));
                break;
            case "DATE_DESC":
                sort = Sort.by(Sort.Order.desc(CREATED), Sort.Order.desc("id"));
                break;
            case "TITLE_AZ":
                sort = Sort.by(Sort.Order.asc(TITLE).ignoreCase(), Sort.Order.desc("id"));
                break;
            case "TITLE_ZA":
                sort = Sort.by(Sort.Order.desc(TITLE).ignoreCase(), Sort.Order.desc("id"));
                break;
            default:
                sort = Sort.by(Sort.Order.desc(CREATED), Sort.Order.desc("id"));
        }
        return sort;
    }

}
