package austeretony.oxygen_groups.client.input;

import org.lwjgl.input.Keyboard;

import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class GroupsKeyHandler {

    public static final KeyBinding GROUP_MENU = new KeyBinding("key.groups.groupMenu", Keyboard.KEY_P, OxygenMain.NAME);

    public GroupsKeyHandler() {
        ClientReference.registerKeyBinding(GROUP_MENU);
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {        
        if (GROUP_MENU.isPressed())
            GroupsManagerClient.instance().openGroupMenuSynced();
    }
}
