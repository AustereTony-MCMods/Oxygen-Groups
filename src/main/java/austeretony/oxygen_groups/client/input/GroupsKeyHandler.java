package austeretony.oxygen_groups.client.input;

import org.lwjgl.input.Keyboard;

import austeretony.oxygen.client.input.KeyBindingWrapper;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class GroupsKeyHandler {

    public static final KeyBindingWrapper GROUP_MENU = new KeyBindingWrapper();

    public GroupsKeyHandler() {
        GROUP_MENU.register("key.groups.groupMenu", Keyboard.KEY_P, OxygenMain.NAME);
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {        
        if (GROUP_MENU.registered() && GROUP_MENU.getKeyBinding().isPressed())
            GroupsManagerClient.instance().openGroupMenuSynced();
    }
}
