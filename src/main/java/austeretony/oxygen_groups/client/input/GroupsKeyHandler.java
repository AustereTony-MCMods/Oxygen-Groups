package austeretony.oxygen_groups.client.input;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_groups.client.GroupMenuManager;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class GroupsKeyHandler {

    private KeyBinding groupMenuKeybinding;

    public GroupsKeyHandler() {
        if (GroupsConfig.ENABLE_GROUP_MENU_KEY.asBoolean() && !OxygenGUIHelper.isOxygenMenuEnabled())
            ClientReference.registerKeyBinding(this.groupMenuKeybinding = new KeyBinding("key.oxygen_groups.groupMenu", GroupsConfig.GROUP_MENU_KEY.asInt(), "Oxygen"));
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {        
        if (this.groupMenuKeybinding != null && this.groupMenuKeybinding.isPressed())
            GroupMenuManager.openGroupMenu();
    }

    public KeyBinding getGroupMenuKeybinding() {
        return this.groupMenuKeybinding;
    }
}
