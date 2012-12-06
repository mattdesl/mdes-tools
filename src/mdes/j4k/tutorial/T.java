package mdes.j4k.tutorial;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;

public class T extends Applet implements Runnable {

	//TODO: remove these if not needed
	public static final Random RANDOM = new Random();
	boolean[] keys = new boolean[32767];
	float mouseX, mouseY;
	
	public void start() {
		enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
		new Thread(this).start();
	}
		
	public void run() {
		//Set up graphics
		final int WIDTH = 240;
		final int HEIGHT = 160;
		final int SCALE = 2;
		setSize(WIDTH*SCALE, HEIGHT*SCALE); // For AppletViewer, remove later.
		
		//Set up our screen image
		final BufferedImage screen = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		final int[] pixels = ((DataBufferInt)screen.getRaster().getDataBuffer()).getData();
		final Graphics g = screen.getGraphics();
		final Graphics appletGraphics = getGraphics();

		// Some variables to use for the fps.
		int tick = 0, fps = 0, acc = 0;
		long lastTime = System.nanoTime();
		
		final int[] TABLE = new int[] {
			0, 0, 0, 255,		//TRANSPARENT
			101, 166, 232, 255,	//CLOTHING
			182, 166, 111, 255,	//SKIN
			235, 235, 235, 255	//BEARD
		};
		final int spriteW = 6;
		final int spriteH = 13;
		final int spriteX = 40;
		final int spriteY = 50;
		final int spriteScale = 3;
		final int[] IMG = new int[spriteW*spriteH*2];
		
		for (int i=0; i<IMG.length; i++) {
			IMG[i] = (byte)("010000001000001100011110111111022220022220022330023220003300000000101101001100010000001000001100011110111111022220022220022330023220003300000000101101001100".charAt(i)-48);
		}
		
		
		// ---------------- GAME LOOP ---------------- //		
		while (true) {
			long now = System.nanoTime();
			long delta = now - lastTime;
			acc += delta;
			tick++;
			if (acc >= 1000000000L) {
				acc -= 1000000000L;
				fps = tick;
				tick = 0;
			}

			// Update
			lastTime = now;
			
			//TODO: remove later if not needed
			g.clearRect(0, 0, WIDTH, HEIGHT);
			
			for (int i=0; i<WIDTH*HEIGHT; i++) {
				int y = i / WIDTH;
				int x = i - WIDTH*y;

				int R=0,G=0,B=0;
				if (x>=spriteX && x<spriteX+spriteW*spriteScale && y>=spriteY && y<spriteY+spriteH*spriteScale) {
					int xx = x-spriteX;
					int yy = y-spriteY;
					
					float xratio = spriteW / (float)(spriteW*spriteScale);
					float yratio = spriteH / (float)(spriteH*spriteScale);
					xx = (int)Math.floor(xx*xratio);
					yy = (int)Math.floor(yy*yratio);
					int offset = IMG[xx + (yy * spriteW)] * 4;
					
					R = TABLE[offset];
					G = TABLE[offset+1];
					B = TABLE[offset+2];					
				}
				//place pixel
				pixels[i] = (R << 16) | (G << 8) | B;
			}
			
			//TODO: remove later
			g.setColor(Color.white);
			g.drawString("FPS " + String.valueOf(fps), 20, 30);

			// Draw the entire results on the screen.
			appletGraphics.drawImage(screen, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
			
			do {
				Thread.yield();
			} while (System.nanoTime() - lastTime < 1000000000/60);
			
			if (!isActive()) {
				return;
			}
		}
	}
	
	public void processEvent(AWTEvent e)
    {
        boolean down = false;
        switch (e.getID())
        {
            case KeyEvent.KEY_PRESSED:
                down = true;
            case KeyEvent.KEY_RELEASED:
            	keys[((KeyEvent) e).getKeyCode()] = down;
                break;
            case MouseEvent.MOUSE_PRESSED:
                down = true;
            case MouseEvent.MOUSE_RELEASED:
                keys[((MouseEvent) e).getButton()] = down;
            case MouseEvent.MOUSE_MOVED:
            case MouseEvent.MOUSE_DRAGGED:
                mouseX = ((MouseEvent) e).getX();
                mouseY = ((MouseEvent) e).getY();
        }
    }
}






