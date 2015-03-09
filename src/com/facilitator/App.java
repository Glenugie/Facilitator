package com.facilitator;

import javax.swing.JFrame;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import com.facilitator.view.MainView;

/**
 * Main application class, used to start the system and UI
 *
 * @author Samuel Cauvin
 */
public class App extends SingleFrameApplication {
    @Override
    protected void startup() {
        MainView mw = new MainView(this);
        show(mw);
        mw.getFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    @Override
    protected void configureWindow(java.awt.Window root) {
    	//No code required
    }

    public static App getApplication() {
        return Application.getInstance(App.class);
    }

    public static void main(String[] args) {
        launch(App.class, args);
    }
}
