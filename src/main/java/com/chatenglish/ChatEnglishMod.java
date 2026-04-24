package com.chatenglish;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatEnglishMod implements ClientModInitializer {

    public static final String MOD_ID = "chatenglish";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean enabled = true;

    @Override
    public void onInitializeClient() {
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (message.equals(",english on")) {
                enabled = true;
                return false;
            }
            if (message.equals(",english off")) {
                enabled = false;
                return false;
            }
            return true;
        });

        LOGGER.info("[ChatEnglish] Mod initialized! ,english on/off で切り替えできます。");
    }
}