package austeretony.oxygen_groups.client.gui.group.callback;

import java.util.UUID;

import org.lwjgl.input.Keyboard;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.PrivilegesProviderClient;
import austeretony.oxygen_core.client.gui.elements.OxygenCallbackBackgroundFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenKeyButton;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.client.gui.elements.OxygenUsernameField;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.group.GroupMenuScreen;
import austeretony.oxygen_groups.client.gui.group.GroupSection;
import austeretony.oxygen_groups.common.main.EnumGroupsPrivilege;

public class InvitePlayerCallback extends AbstractGUICallback {

    private final GroupMenuScreen screen;

    private final GroupSection section;

    private OxygenUsernameField usernameField;

    private OxygenKeyButton confirmButton, cancelButton;

    //cache

    private boolean initialized;

    private UUID playerUUID;

    public InvitePlayerCallback(GroupMenuScreen screen, GroupSection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.enableDefaultBackground(EnumBaseGUISetting.FILL_CALLBACK_COLOR.get().asInt());
        this.addElement(new OxygenCallbackBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_groups.gui.callback.invitePlayer"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));
        this.addElement(new OxygenTextLabel(6, 23, ClientReference.localize("oxygen_groups.gui.callback.invitePlayer.request"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.confirmButton = new OxygenKeyButton(15, this.getHeight() - 10, ClientReference.localize("oxygen_core.gui.confirm"), Keyboard.KEY_R, ()->this.confirm(false)).disable());
        this.addElement(this.cancelButton = new OxygenKeyButton(this.getWidth() - 55, this.getHeight() - 10, ClientReference.localize("oxygen_core.gui.cancel"), Keyboard.KEY_X, ()->this.close(false)));

        this.addElement(this.usernameField = new OxygenUsernameField(6, 25, this.getWidth() - 12));     
        this.usernameField.setUsernameSelectListener((sharedData)->{
            this.playerUUID = sharedData.getPlayerUUID();
            this.confirmButton.setEnabled(PrivilegesProviderClient.getAsBoolean(EnumGroupsPrivilege.ALLOW_GROUP_CREATION.id(), true));
        });    
    }

    @Override
    protected void onOpen() {
        this.usernameField.reset();
        this.confirmButton.disable();

        if (!this.initialized) {
            this.initialized = true;
            this.usernameField.load();
        }
    }

    private void confirm(boolean mouseClick) {
        if (mouseClick || !this.usernameField.isDragged()) {
            GroupsManagerClient.instance().getGroupDataManager().inviteToGroupSynced(this.playerUUID);
            this.close();
        }
    }

    private void close(boolean mouseClick) {
        if (mouseClick || !this.usernameField.isDragged())
            this.close();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.cancelButton)
                this.close(true);
            else if (element == this.confirmButton)
                this.confirm(true);
        }
    }
}
