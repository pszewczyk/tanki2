package view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite implements Drawable {
	protected BufferedImage img;
	private int x, y;
	
	/** should this sprite be drawn? */
	protected boolean enabled;

	/**
	 * Create new sprite from file
	 * @param filename Name of image file
	 * @throws IOException Can throw this exception when image cannot be read
	 */
	public Sprite(String filename) throws IOException {
		img = ImageIO.read(new File(filename));
		img.setAccelerationPriority(1.0f);
		
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}
	
	public void enable() {
		enabled = true;
	}

	/**
	 * Set the position in which this sprite will be painted
	 * @param x X position of sprite
	 * @param y Y position of sprite
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return X position of this sprite
	 */
	public int getX() {
		return x - img.getWidth()/2;
	}
	
	/**
	 * @return Y position of this sprite
	 */
	public int getY() {
		return y - img.getHeight();
	}
	
	/**
	 * @return Image associated with this sprite
	 */
	public Image getImg() {
		return img;
	}
	
	public void paint(Graphics g) {
		if(enabled)
			g.drawImage(img, getX(), getY(), null);
	}
}
