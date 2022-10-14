package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.CardResponse;
import org.seng302.main.models.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Card Response factory builder class
 */
public class CardResponses {

    /**
     * Add explicit private constructor
     */
    private CardResponses() {}

    /**
     * Creates a single response for card
     *
     * @param card instance of Card
     * @return card response
     */
    public static CardResponse getResponse(Card card) {
        CardResponse cardResponse = new CardResponse();

        cardResponse.setId(card.getId());
        cardResponse.setCreator(UserResponses.getResponse(card.getCreator(), false, false));
        cardResponse.setSection(card.getSection());
        cardResponse.setCreated(card.getCreated());
        cardResponse.setDisplayPeriodEnd(card.getDisplayPeriodEnd());
        cardResponse.setTitle(card.getTitle());
        cardResponse.setDescription(card.getDescription());
        cardResponse.setKeywords(KeywordResponses.getAllResponses(card.getKeywords()));

        return cardResponse;
    }

    /**
     * Creates a list of card responses
     *
     * @param cards List of Card instances
     * @return List of card responses
     */
    public static List<CardResponse> getAllResponses(List<Card> cards) {
        List<CardResponse> cardResponses = new ArrayList<>();

        for (Card card: cards) {
            cardResponses.add(getResponse(card));
        }

        return cardResponses;
    }

}
