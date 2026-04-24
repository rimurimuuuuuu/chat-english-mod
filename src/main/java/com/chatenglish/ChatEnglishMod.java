package com.chatenglish;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatEnglishMod implements ClientModInitializer {

    public static final String MOD_ID = "chatenglish";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean enabled = true;
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.chatenglish.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (client.player != null) {
                    String status = enabled
                        ? "\u00a7a[Chat English] ON \u00a77- 英語翻訳が有効です"
                        : "\u00a7c[Chat English] OFF \u00a77- 英語翻訳が無効です";
                    client.player.sendMessage(Text.literal(status), false);
                }
            }
        });

        LOGGER.info("[ChatEnglish] Mod initialized!");
    }
}