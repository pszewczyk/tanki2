package view;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import controller.event.ShootEvent;
import view.GameWindow;
import view.GameCanvas;
import common.Constants;
import controller.EventHandler;
import controller.GameTimer;
import controller.GameController;
import controller.event.GenericEvent;
import view.handler.ShootHandler;

public final class GameView {
	private GameWindow window;
	private GameCanvas canvas;
	private GameTimer renderTimer;
	GameController controller;
	KeyboardHandler keyboard;
	EventHandler handler;
	
	public void SetController (GameController c)
	{
		controller = c;
	}

	public void handle (GenericEvent e) { handler.handle(e); }

	public class BasicWindowMonitor extends WindowAdapter {
		/**
		 * Closing callback of the main window
		 */
		  public void windowClosing(WindowEvent e) {
		    Window w = e.getWindow();
		    w.setVisible(false);
		    w.dispose();
		    System.exit(0);
		  }
	}

	/**
	 * Create new game view
	 * @throws IOException Can be thrown when some files cannot be opened
	 */
	public GameView() throws IOException {
		handler = new EventHandler ();
		handler.put (ShootEvent.class, new ShootHandler (this));

		keyboard = new KeyboardHandler (this);

		window = new GameWindow(Constants.DefaultWindowWidth, Constants.DefaultWindowHeight);
		// todo: consider moving the following to GameWindow constructor
		window.setVisible(true);
		window.addWindowListener(new BasicWindowMonitor());
		window.addKeyListener (keyboard);
		window.requestFocusInWindow();

		// ugly hack to make sure focus is retained
		window.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) { }
            public void focusLost(FocusEvent e) { e.getComponent().requestFocus(); }
        });
		
		canvas = new GameCanvas(window.width, window.height);
		window.add(canvas);

		renderTimer = new GameTimer(Constants.FPS);
	}

	/**
	 * @return Rendering canvas associated with this view
	 */
	public GameCanvas getCanvas() {
		return canvas;
	}

	public void Shoot () {
		controller.AddEvent(new ShootEvent (0, 0.9, 3.0));
	}

	/**
	 * Start rendering canvas
	 */
	public void enableCanvas() {
		renderTimer.scheduleRenderTask(canvas.getRenderTask());
	}
}
