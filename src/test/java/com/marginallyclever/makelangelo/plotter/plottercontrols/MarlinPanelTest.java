package com.marginallyclever.makelangelo.plotter.plottercontrols;

import com.marginallyclever.communications.NetworkSessionEvent;
import com.marginallyclever.makelangelo.Translator;
import com.marginallyclever.util.PreferencesHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

public class MarlinPanelTest {
    @BeforeAll
    public static void init() {
        PreferencesHelper.start();
        Translator.start();
    }

    @Test
    public void onHearHomeXYFirst() {
        MarlinPanel mi = new MarlinPanel(new ChooseConnection());

        AtomicReference<MarlinPanelEvent> ae = new AtomicReference<>();

        mi.addListener(ae::set);
        String message = "echo:Home XY First";
        mi.onDataReceived(new NetworkSessionEvent(this, NetworkSessionEvent.DATA_RECEIVED, message));

        Assertions.assertNotNull(ae.get());
        Assertions.assertEquals(MarlinPanelEvent.HOME_XY_FIRST, ae.get().getID());
    }

    @Test
    public void onHearActionCommand() {
        MarlinPanel mi = new MarlinPanel(new ChooseConnection());

        AtomicReference<MarlinPanelEvent> ae = new AtomicReference<>();

        mi.addListener(ae::set);
        String message = MarlinPanel.STR_ACTION_COMMAND+MarlinPanel.PROMPT_BEGIN+" Ready black and click";
        mi.onDataReceived(new NetworkSessionEvent(this, NetworkSessionEvent.DATA_RECEIVED, message));

        Assertions.assertNotNull(ae.get());
        Assertions.assertEquals(MarlinPanelEvent.ACTION_COMMAND, ae.get().getID());
        Assertions.assertEquals(MarlinPanel.PROMPT_BEGIN+" Ready black and click", ae.get().getActionCommand());
    }

    @Test
    public void onHearError() {
        MarlinPanel mi = new MarlinPanel(new ChooseConnection());

        AtomicReference<MarlinPanelEvent> ae = new AtomicReference<>();

        mi.addListener(ae::set);
        String message = MarlinPanel.STR_ERROR +" "+MarlinPanel.STR_PRINTER_HALTED;
        mi.onDataReceived(new NetworkSessionEvent(this, NetworkSessionEvent.DATA_RECEIVED, message));

        Assertions.assertNotNull(ae.get());
        Assertions.assertEquals(MarlinPanelEvent.ERROR, ae.get().getID());
        Assertions.assertEquals(MarlinPanel.STR_PRINTER_HALTED, ae.get().getActionCommand());
    }

    @Test
    public void didNotFind() {
        MarlinPanel mi = new MarlinPanel(new ChooseConnection());
        AtomicReference<MarlinPanelEvent> ae = new AtomicReference<>();
        mi.addListener(ae::set);

        mi.queueAndSendCommand("M400");

        Assertions.assertNotNull(ae.get());
        Assertions.assertEquals(MarlinPanelEvent.DID_NOT_FIND, ae.get().getID());
    }

    // Début des tests pour IFT3913 (automne 2024)
    @Test
    public void testRemoveListener() {
        // Arrange
        MarlinPanel mi = new MarlinPanel(new ChooseConnection());
        AtomicReference<MarlinPanelEvent> ae = new AtomicReference<>();
        MarlinPanelListener listener = ae::set;

        // Testing the list before adding a listener
        Assertions.assertTrue(mi.getListeners().isEmpty(), "Listener list should be empty");

        // Adding the listener and asserting if it worked
        mi.addListener(listener);
        Assertions.assertFalse(mi.getListeners().isEmpty(), "Listener list should not be empty");

        // Removing the listener and asserting if it worked
        mi.removeListener(listener);
        Assertions.assertTrue(mi.getListeners().isEmpty(), "Listener list should be empty");
    }

    @Test
    public void testGetIsBusy() {
        // Arrange
        MarlinPanel mi = new MarlinPanel(new ChooseConnection());
        AtomicReference<MarlinPanelEvent> ae = new AtomicReference<>();
        mi.addListener(ae::set);

        // Asserting if the busy count equals to 20 (starting value) before initializing it
        Assertions.assertEquals(20, mi.getBusyCount());
        Assertions.assertFalse(mi.getIsBusy(), "Busy");

        // Act and asserting that if the busyCount ever reaches 0 or less, it getIsBusy will return True
        mi.onHearOK();
        if (mi.getBusyCount() <= 0) {
            Assertions.assertTrue(mi.getIsBusy(), "Not busy");
        }
    }
}
