package austeretony.oxygen_groups.client.input;

import org.lwjgl.input.Keyboard;

import austeretony.oxygen.client.api.OxygenGUIHelper;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen_groups.common.main.GroupsMain;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class GroupsKeyHandler {

    public static final KeyBinding GROUP_MENU = new KeyBinding("key.oxygen_groups.groupMenu", Keyboard.KEY_P, "Oxygen");

    public GroupsKeyHandler() {
        ClientReference.registerKeyBinding(GROUP_MENU);
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {        
        if (GROUP_MENU.isPressed())
            OxygenGUIHelper.openSharedDataListenerScreen(GroupsMain.GROUP_MENU_SCREEN_ID);
    }
}
