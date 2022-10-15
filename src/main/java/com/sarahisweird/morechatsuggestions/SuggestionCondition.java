package com.sarahisweird.morechatsuggestions;

/**
 * Determines whether a suggestion call will be added to the autocomplete suggestions, based on the current word.
 * <br />
 * Examples for the current word are: {@code hel, SuggestionCo, MoreChatSug}
 */
@FunctionalInterface
public interface SuggestionCondition {
    /**
     * See {@link SuggestionCondition} for more information.
     *
     * @param currentWord The current word
     * @return {@code true} if the suggestion class should be added to the autocomplete suggestions.
     */
    boolean shouldAddSuggestions(String currentWord);

    /**
     * This should be used if the suggestions should be always added to the collection.
     */
    SuggestionCondition ALWAYS = currentWord -> true;
}
