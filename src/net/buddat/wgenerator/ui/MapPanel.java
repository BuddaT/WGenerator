package net.buddat.wgenerator.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class MapPanel extends JPanel {
	
	private static final long serialVersionUID = -6072723167611034006L;
	
	private BufferedImage mapImage;
	
	private int mapSize;

	public MapPanel(int width, int height) {
		super();
		
		this.setSize(width, height);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (mapImage != null)
			g.drawImage(mapImage, 0, 0, this.getWidth(), this.getHeight(), null);
	}
	
	public void setMapSize(int newMapSize) {
		mapSize = newMapSize;
		
		mapImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_BYTE_GRAY);
	}
	
	public void setMapImage(BufferedImage newImage) {
		mapImage = newImage;
	}
	
	public BufferedImage getMapImage() {
		return mapImage;
	}
	
	
}
