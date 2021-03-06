package view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import model.Shot;
import model.Tank;

/**
 * Represent a area on which game is rendered
 * @author Severus
 *
 */
public class GameCanvas extends Canvas {
	/**
	 * Task for rendering a canvas regularly
	 * @author Severus
	 *
	 */
	public class RenderTask extends TimerTask{
		private Canvas canvas;

		RenderTask(Canvas canvas) {
			this.canvas = canvas;
		}
		
		public void run() {
			canvas.repaint();
		}
	}
	
	/**
	 * Task for delayed animation disposal when it's ended
	 * @author Severus
	 *
	 */
	public class AnimationDisposalTask extends TimerTask{
		private GameCanvas canvas;
		private Animation animation;
		
		AnimationDisposalTask(Animation a, GameCanvas c) {
			this.canvas = c;
			this.animation = a;
		}
		
		public void run() {
			canvas.removeDrawable(animation);
		}
	}
	
	private List<Drawable> drawables;
	/** Animations to draw one after another */
	private Image bufferImage;
	private DirtMap map;
	private AimArrow arrow;
	RenderTask render;
	private Color skyColor;
	/** Timer for scheduling drawing jobs and disposals of animations */
	Timer delayer;
	
	/**
	 * Create new canvas
	 * @param w Width of the canvas
	 * @param h Height of the canvas
	 */
	public GameCanvas(int w, int h) throws IOException {
		this.setSize(w, h);
		skyColor = new Color (0, 128, 255);
		drawables = new CopyOnWriteArrayList<Drawable>();
		render = new RenderTask(this);
		delayer = new Timer();
		
		arrow = new AimArrow();
		arrow.disable();
	}
	
	/**
	 * Remove drawable
	 * @param d
	 */
	public void removeDrawable(Drawable d) {
		drawables.remove(d);
	}

	/**
	 * Flush frame buffer and recreate it
	 */
	public void resetBuffer() {
		if (bufferImage != null) {
			bufferImage.flush();
			bufferImage = null;
		}
		
		bufferImage = createImage(getWidth(), getHeight());
	}
	
	/**
	 * Draw all elements on frame buffer
	 */
	public void fillBuffer() {
		Graphics g = bufferImage.getGraphics();

		bufferImage.flush();
		g.setColor(skyColor);
		g.fillRect (0, 0, getWidth(), getHeight());
		
		if (map != null) {
			map.paint(g);
		}
		
		for(Iterator<Drawable> i = drawables.iterator(); i.hasNext(); ) {
		    Drawable item = i.next();
		    item.paint(g);
		}
		
		arrow.paint(g);
	}

	/**
	 * Update this canvas, repaint it, but without cleaning itself (buffer will handle it)
	 */
	public void update(Graphics g) {
		paint(g);
	}
	
	/**
	 * Paint everything to be painted using frame buffer
	 */
	public void paint (Graphics g)
	{
		resetBuffer();
		fillBuffer();
		getGraphics().drawImage(bufferImage, 0, 0, null);
	}
	
	/**
	 * Add another sprite to this canvas, will be painted always
	 * @param s Sprite to be added
	 */
	public void addSprite(Sprite s) {
		drawables.add(s);
	}
	
	/**
	 * Add animation to be showed and schedule its disposal after its duration
	 * @param a
	 */
	public void addAnimation(Animation a) {
		drawables.add(a);
		delayer.schedule(new AnimationDisposalTask(a, this), a.duration);
		a.start();
	}
	
	public void addDrawable(Drawable d) {
		drawables.add(d);
	}
	
	/**
	 * Get task which do the rendering of this canvas
	 * @return Rendering task
	 */
	public TimerTask getRenderTask() {
		return render;
	}
	
	/**
	 * Set a map
	 * @param m
	 */
	public void setMap(DirtMap m) {
		map = m;
		map.prettyPaint();
	}
	
	/**
	 * Set tank to be drawn as focused
	 * @param tank
	 */
	public void setFocusedTank(Tank tank) {
		arrow.setTank(tank);
		arrow.enable();
	}

	/**
	 * Remove drawable corresponding to given shot
	 * @param target Shot object to be removed
	 */
	public void removeBullet(Shot shot) {
		for(Iterator<Drawable> i = drawables.iterator(); i.hasNext(); ) {
		    Drawable item = i.next();
		    if (item instanceof ShotSprite) {
		    	ShotSprite sprite = (ShotSprite)item;
		    	if (sprite.getShot() == shot) {
		    		drawables.remove(item);
		    		return;
		    	}
		    }
		}
	}

	/**
	 * Remove drawables related to this tank. They are no longer needed.
	 * @param tank No longer existing tank
	 */
	public void removeTank(Tank tank) {
		for (Drawable s : drawables) {
			if (s instanceof TankSprite) {
				TankSprite sprite = (TankSprite)s;
				if (sprite.getTank() == tank)
					drawables.remove(s);
			}
		}
	}
}
