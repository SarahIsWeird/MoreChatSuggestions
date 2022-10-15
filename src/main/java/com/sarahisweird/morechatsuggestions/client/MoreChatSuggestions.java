package com.sarahisweird.morechatsuggestions.client;

import com.sarahisweird.morechatsuggestions.SuggestionCondition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.*;

@Environment(EnvType.CLIENT)
public class MoreChatSuggestions {
    private static final Map<Identifier, Collection<String>> suggestions;
    private static final Map<Identifier, SuggestionCondition> suggestionConditions;

    private static boolean lastSuggestionsWereConditional;

    static {
        suggestions = new HashMap<>();
        suggestionConditions = new HashMap<>();
        lastSuggestionsWereConditional = true;
    }

    /**
     * Registers suggestions unconditionally. Same as
     * {@link #registerSuggestions(Identifier, Collection, SuggestionCondition) registerSuggestions(suggestionClass, suggestions, SuggestionCondition.ALWAYS)}.
     *
     * @param suggestionClass A unique identifier for this group of suggestions
     * @param suggestions The suggestions themselves
     * @see #registerSuggestions(Identifier, Collection, SuggestionCondition)
     */
    public static void registerSuggestions(Identifier suggestionClass, Collection<String> suggestions) {
        registerSuggestions(suggestionClass, suggestions, SuggestionCondition.ALWAYS);
    }

    /**
     * Registers autocomplete suggestions. You can call this method multiple times for the same {@code suggestionClass},
     * with the previously registered suggestions being overwritten by the new suggestions.
     * <br />
     * Changes in the suggestions will be applied for the next suggestion retrieval (The next time a character is
     * entered or the player presses {@code TAB}).
     * <br />
     * <b>Important:</b> The condition applies to the suggestion class as a whole. It's intended as a filter for the
     * classes. For example, if there was a class "foo:emoji" with the suggestions being {@code :smile:, :frown:}
     * and the player is only supposed to see the suggestions if they've started typing an emoji, the condition could be
     * {@code (currentWord) -> currentWord.startsWith(":")}.
     *
     * @param suggestionClass A unique identifier for this group of suggestions.
     * @param suggestions The suggestions themselves
     * @param condition A condition to hide the suggestions depending on the current input.
     */
    public static void registerSuggestions(Identifier suggestionClass, Collection<String> suggestions, SuggestionCondition condition) {
        MoreChatSuggestions.suggestions.put(suggestionClass, suggestions);
        MoreChatSuggestions.suggestionConditions.put(suggestionClass, condition);
    }

    /**
     * Returns a collection of autocomplete suggestions for the currently entered word. Used internally to enhance the
     * chat UI, but this can also be used elsewhere without any problems.
     * <br />
     * <b>Important:</b> Just because a word appears in the collection, that doesn't mean it is actually applicable to
     * the current input. For example, the input {@code he} could return a collection with these contents:
     * {@code hello, hey, bye}. This is how Minecraft handles autocomplete suggestions internally, and it is up to
     * whoever consumes the collection to filter out the suggestions that aren't applicable.
     *
     * @param currentWord The current word, for example: {@code he}
     * @return A list of autocomplete suggestions, for example: {@code hello, hey, unrelated}
     */
    public static Collection<String> getSuggestions(String currentWord) {
        ArrayList<String> applicableSuggestions = new ArrayList<>();

        MoreChatSuggestions.lastSuggestionsWereConditional = false;

        MoreChatSuggestions.suggestionConditions.forEach((suggestionClass, condition) -> {
            boolean shouldSuggest = condition.shouldAddSuggestions(currentWord);
            if (!shouldSuggest) return;

            /*
             * If *one* of the conditions isn't SuggestionCondition.ALWAYS, we're justified to show the player the
             * suggestions. If all of them are ALWAYS, we'd just clutter up the UI. The player can still press TAB
             * to see the suggestions anyway.
             */
            if (!MoreChatSuggestions.lastSuggestionsWereConditional && condition != SuggestionCondition.ALWAYS) {
                MoreChatSuggestions.lastSuggestionsWereConditional = true;
            }

            applicableSuggestions.addAll(MoreChatSuggestions.suggestions.get(suggestionClass));
        });

        return applicableSuggestions;
    }

    /**
     * Returns whether the last autocomplete suggestions contained a conditional suggestion class. This is used in the
     * chat UI to automatically show the autocomplete popup if there was at least one condition on the suggestions.
     * <br />
     * This should be called right after (or at least soon after) calling {@link #getSuggestions(String)}.
     *
     * @return {@code true} if the last autocomplete suggestions contained a conditional suggestion class.
     */
    public static boolean wereLastSuggestionsConditional() {
        return MoreChatSuggestions.lastSuggestionsWereConditional;
    }

    private MoreChatSuggestions() {}
}
