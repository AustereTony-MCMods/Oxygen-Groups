package austeretony.oxygen_groups.client;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_groups.client.gui.group.GroupMenuScreen;

public class GroupMenuManager {

    public void sharedDataSynchronized() {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((GroupMenuScreen) ClientReference.getCurrentScreen()).sharedDataSynchronized();
        });
    }

    public static boolean isMenuOpened() {
        return ClientReference.hasActiveGUI() && ClientReference.getCurrentScreen() instanceof GroupMenuScreen;
    }
}
