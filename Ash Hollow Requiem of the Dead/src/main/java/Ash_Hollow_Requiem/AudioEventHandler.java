package Ash_Hollow_Requiem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.mojang.blaze3d.platform.InputConstants;

    /**
     * Handles audio-related keybindings and input logic for Ash Hollow: Requiem of the Dead.
     */
    @OnlyIn(Dist.CLIENT)
    public class AudioEventHandler {

        // ======== Key Name Accessors ======== //
        public static String getPushToTalkKeyName() {
            return KeyBindings.PUSH_TO_TALK_KEY.getKey().getDisplayName().getString();
        }

        public static String getShoutKeyName() {
            return KeyBindings.SHOUT_KEY.getKey().getDisplayName().getString();
        }

        public static String getWhisperKeyName() {
            return KeyBindings.WHISPER_KEY.getKey().getDisplayName().getString();
        }

        // ======== Key Press Checks ======== //
        public static boolean isPushToTalkPressed() {
            return KeyBindings.PUSH_TO_TALK_KEY.isDown();
        }

        public static boolean isShoutPressed() {
            return KeyBindings.SHOUT_KEY.isDown();
        }

        public static boolean isWhisperPressed() {
            return KeyBindings.WHISPER_KEY.isDown();
        }

        // ======== Microphone Selection ======== //
        private static String selectedMicrophone = "Default Microphone";

        public static void setSelectedMicrophone(String micName) {
            selectedMicrophone = micName;
            System.out.println("Selected microphone: " + micName);
        }

        public static String getSelectedMicrophone() {
            return selectedMicrophone;
        }
    }
