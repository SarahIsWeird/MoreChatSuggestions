package com.sarahisweird.morechatsuggestions.mixin;

import com.sarahisweird.morechatsuggestions.client.MoreChatSuggestions;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {
    @Final
    @Shadow
    TextFieldWidget textField;

    @Shadow
    private static int getStartOfCurrentWord(String input) {
        return 0;
    }

    @Shadow public abstract void show(boolean narrateFirstSuggestion);

    /*
     * We target the point in ChatInputSuggestor#refresh after this.pendingSuggestions = CommandSuggestor.suggestMatching(),
     * in the else branch of if (bl2)
     */
    @Inject(
            method = "refresh()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;pendingSuggestions:Ljava/util/concurrent/CompletableFuture;",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER,
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;getStartOfCurrentWord(Ljava/lang/String;)I"
                    )
            )
    )
    private void afterGetChatSuggestions(CallbackInfo ci) {
        if (MoreChatSuggestions.wereLastSuggestionsConditional()) {
            this.show(true); // Shows the autocomplete popup
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature") // This is the correct way, you doofus
    @ModifyVariable(method = "refresh()V", at = @At("STORE"), ordinal = 0, name = "collection")
    private Collection<String> modifySuggestionsOutsideOfCommands(Collection<String> vanillaSuggestions) {
        ArrayList<String> newSuggestions = new ArrayList<>(vanillaSuggestions);

        // We need the current word to filter the additional suggestions, hence all of this
        String currentInput = this.textField.getText();
        int currentCursorPosition = this.textField.getCursor();

        String textBeforeCursor = currentInput.substring(0, currentCursorPosition);
        int startOfCurrentWord = getStartOfCurrentWord(textBeforeCursor);

        String currentWord = textBeforeCursor.substring(startOfCurrentWord);

        Collection<String> additionalSuggestions = MoreChatSuggestions.getSuggestions(currentWord);

        newSuggestions.addAll(additionalSuggestions);

        return newSuggestions;
    }
}
