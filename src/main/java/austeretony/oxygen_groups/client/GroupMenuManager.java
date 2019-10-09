package austeretony.oxygen_groups.client;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_groups.client.gui.group.GroupMenuGUIScreen;

public class GroupMenuManager {

    public void sharedDataSynchronized() {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((GroupMenuGUIScreen) ClientReference.getCurrentScreen()).sharedDataSynchronized();
        });
    }

    public static boolean isMenuOpened() {
        return ClientReference.hasActiveGUI() && ClientReference.getCurrentScreen() instanceof GroupMenuGUIScreen;
    }
}
