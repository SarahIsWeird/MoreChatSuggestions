# MoreChatSuggestions

MoreChatSuggestions is a small 1.19.2 FabricMC library that allows you to add autocomplete suggestions in the chat,
outside of commands. It can also be utilized by third parties in other places.

## Installation

Add this to your repositories:

```groovy
// build.gradle
maven {
    url "https://maven.sarahisweird.com/releases"
}

// build.gradle.kts
maven("https://maven.sarahisweird.com/releases")
```

And this to your dependencies:

```groovy
// build.gradle
modImplementation("com.sarahisweird:morechatsuggestions:1.0")

// build.gradle.kts
modImplementation("com.sarahisweird", "morechatsuggestions", "1.0")
```

## Registering suggestions

Autocomplete suggestions are registered based on unique identifiers. This allows you to update only certain parts of the
suggestions. Each suggestion also has a `SuggestionCondition`. It determines, based on the current word, whether to
include or exclude a group of suggestions. Please note that while the examples here use `List<String>`s, suggestions are
actually handled internally as `Collection<String>`s, as Minecraft handles them like that too. This means that the order
of autocomplete suggestions isn't guaranteed and will follow Vanilla logic.

While it is probably a good idea to initialize these suggestions in your mod initializer, you don't have to. This also
means that you can update the suggestions wherever you want, dynamically.

### Registering conditional suggestions

Conditional suggestions are only included in the final suggestion list if your `SuggestionCondition` returns `true`.
You **don't** need to filter the suggestions by the current input, Minecraft already takes care of that for you.

For example, if we wanted to add a list of emoji shortcodes to the chat suggestions, we could do that like this:

```java
Identifier id = new Identifier("my_mod", "emoji");
List<String> emoji = List.of(":smile:", ":confused:", ":neutral_face:");

MoreChatSuggestions.registerSuggestions(id, emoji, (currentWord) -> currentWord.startsWith(":"));
```

If there is at least one conditional suggestion group present in the final suggestion list, the autocomplete pop-up will
be automatically triggered. This pop-up will also include unconditional suggestions!

### Registering unconditional suggestions

Unconditional suggestions are always included in the final suggestion list. However, this also means that random
suggestions would pop up while the user is typing normally. For this reason, the suggestion pop-up isn't automatically
triggered if a suggestion matches. The user can still press `TAB` to see the suggestions, though.

For example, if we wanted to add a list of common words to the chat suggestions, we would do that like this:

```java
Identifier id = new Identifier("my_mod", "common_words");
List<String> commonWords = List.of("hello", "goodbye", "supercalifragilisticexpialidocious");

MoreChatSuggestions.registerSuggestions(id, commonWords);
// Alternatively:
MoreChatSuggestions.registerSuggestions(id, commonWords, SuggestionCondition.ALWAYS);
```

## Consuming autocomplete suggestions elsewhere

If for whatever reason you wish to utilize the suggestions in your own mod outside the chat UI, you can do so. The
library exposes two methods it uses internally, namely `getSuggestions` and `wereLastSuggestionsConditional`. These
functions behave as you'd expect them to. While you don't *need to* respect the conditionality of the suggestions,
please try to anyway.

Please note that if you do want to respect the conditionality of the suggestions, you should call
`wereLastSuggestionsConditional` right after, or at least soon after `getSuggestions`, so that another call to
`getSuggestions` won't change the return value. This shouldn't be a huge problem since Minecraft's UI isn't
multithreaded, but do be aware of it.

For examples on how to use these functions, you can take a look at the `ChatInputSuggestorMixin`.