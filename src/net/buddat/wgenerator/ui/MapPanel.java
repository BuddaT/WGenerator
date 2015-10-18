package net.buddat.wgenerator.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class MapPanel extends JPanel {
	
	private static final long serialVersionUID = -6072723167611034006L;
	
	private BufferedImage mapImage;
	
	private int mapSize;
	private double scale = 0.0f;
	private double minScale = 1.0f;
	private int imageX = 0;
	private int imageY = 0;
	private int startX = 0;
	private int startY = 0;

	public MapPanel(int width, int height) {
		super();
		
		this.setSize(width, height);
		this.setMapSize(1024);

	    addMouseWheelListener(new MouseAdapter() {

	        @Override
	        public void mouseWheelMoved(MouseWheelEvent e) {
	            double delta = 0.05f * e.getPreciseWheelRotation();
	            if(e.isShiftDown())
	            	delta *= 2;
	            int preH = getImageHeight();
	            int preW = getImageWidth();
	            scale -= delta;
	            if(scale <= minScale)
	            	scale = minScale;
	            int offY = (int)((getImageHeight() - preH) / 2);
	            int offX = (int)((getImageWidth() - preW) / 2);
            	imageX -= offX;
            	imageY -= offY;
	            checkBounds();
	            revalidate();
	            repaint();
	        }

	    });
	    
	    this.addComponentListener(new ComponentAdapter() {
	    	public void componentResized(ComponentEvent e) {
	    		updateScale();
	    		checkBounds();
	    	}
	    });
	    
	    this.addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mousePressed(MouseEvent e) {
	    		super.mousePressed(e);
	    		startX = e.getX();
	    		startY = e.getY();
	    	}
	    });
	    
	    this.addMouseMotionListener(new MouseMotionAdapter() {
	    	@Override
	    	public void mouseDragged(MouseEvent e) {
	    		if(e.getX() < startX)
	    			imageX -= (startX - e.getX());
	    		else if(e.getX() > startX)
	    			imageX += (e.getX() - startX);
	    		if(e.getY() < startY)
	    			imageY -= (startY - e.getY());
	    		else if(e.getY() > startY)
	    			imageY += (e.getY() - startY);
	    		startX = e.getX();
	    		startY = e.getY();
	    		checkBounds();
	    		repaint();
	    	}
	    });
		
	}

	public void updateScale() {
		if(this.getWidth() < this.getHeight())
			this.minScale = (double)this.getWidth() / (double)mapImage.getWidth();
		if(this.getHeight() < this.getWidth())
			this.minScale = (double)this.getHeight() / (double)mapImage.getHeight();
		if(this.scale < this.minScale)
			this.scale = this.minScale;
	}

	private int getImageWidth() {
		return (int)Math.round(this.mapImage.getWidth() * this.scale);
	}
	
	private int getImageHeight() {
		return (int)Math.round(this.mapImage.getHeight() * this.scale);
	}
	
	public void checkBounds() {
		int wH = this.getHeight();
		int wW = this.getWidth();
		int iH = this.getImageHeight();
		int iW = this.getImageWidth();
		int minY = wH - iH;
		int minX = wW - iW;

		if(wW > iW)
			imageX = (wW / 2) - (iW / 2);
		else if(imageX < minX)
			imageX = minX;
		else if(imageX > 0)
			imageX = 0;

		if(wH > iH)
			imageY = (wH / 2) - (iH / 2);
		else if(imageY < minY)
			imageY = minY;
		else if(imageY > 0)
			imageY = 0;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.drawImage(this.mapImage, imageX, imageY, getImageWidth(), getImageHeight(), null);
		this.repaint();
	}
	
	public void setMapSize(int newMapSize) {
		mapSize = newMapSize;
		
		mapImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_BYTE_GRAY);
		updateScale();
		checkBounds();
		scale = minScale;
	}
	
	public void setMapImage(BufferedImage newImage) {
		mapImage = newImage;
		updateScale();
		checkBounds();
		scale = minScale;
	}
	
	public BufferedImage getMapImage() {
		return mapImage;
	}
	
	
}
