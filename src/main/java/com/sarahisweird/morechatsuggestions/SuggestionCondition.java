package com.sarahisweird.morechatsuggestions;

/**
 * Determines whether a suggestion call will be added to the autocomplete suggestions, based on the current word.
 * <br />
 * Examples for the current word are: {@code hel, SuggestionCo, MoreChatSug}
 */
@FunctionalInterface
public interface SuggestionCondition {
    boolean shouldAddSuggestions(String currentWord);

    SuggestionCondition ALWAYS = currentWord -> true;
}
