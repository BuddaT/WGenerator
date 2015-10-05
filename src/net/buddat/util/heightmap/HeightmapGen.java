package net.buddat.util.heightmap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.wurmonline.mesh.Tiles.Tile;
import com.wurmonline.wurmapi.api.MapData;
import com.wurmonline.wurmapi.api.WurmAPI;

public class HeightmapGen extends JFrame implements KeyListener {
	
	private static final long serialVersionUID = 1500559112995998883L;

	public static final int MAP_SIZE = 1024;
	public static final int WINDOW_SIZE = 1024;
	
	public static final double RESOLUTION = MAP_SIZE / 1.5;
	
	public static final float MIN_SLOPE = 0.001f, MAX_SLOPE = 0.9f;
	public static final float MAX_SEDIMENT = 0.01f, SEDIMENT_BASE = 0.15f;
	
	public static final float WATER_WEIGHT = 0.20f;
	public static final float MAP_HEIGHT = 2048f;
	
	public static final int EROSION_ITERATIONS = 50;
	public static final int GRASS_ITERATIONS = 50;
	
	public static final float GRASS_RATE = 0.5f;
	public static final float GRASS_MAX_SLOPE = 0.01f;
	
	private MapTile[][] tileMap;
	private BufferedImage bI;
	
	private int currentBaseIteration;
	
	private WurmAPI api;

	public HeightmapGen() {
		super("Generator");
		this.setSize(WINDOW_SIZE, WINDOW_SIZE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.addKeyListener(this);
		
		newMap();
		
        try {
            api = WurmAPI.create("./", (int) (Math.log(MAP_SIZE) / Math.log(2)));
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
	}
	
	public void newMap() {
		generate();
		increaseLand(++currentBaseIteration);
		increaseLand(++currentBaseIteration);
		increaseLand(++currentBaseIteration);
		//cleanupDregs();
		increaseLand(++currentBaseIteration);
		increaseLand(++currentBaseIteration);
		//cleanupDregs();
		increaseLand(++currentBaseIteration);
		increaseLand(++currentBaseIteration);
		//cleanupDregs();
	}
	
	@Override
	public void paint(Graphics g) {
	    super.paint(g);
	    g.drawImage(bI, 0, 0, WINDOW_SIZE, WINDOW_SIZE, null);
	}
	
	public void generate() {
		tileMap = new MapTile[MAP_SIZE][MAP_SIZE];
		currentBaseIteration = -1;
		
		SimplexNoise.genGrad(System.nanoTime());
		System.out.println("Generating Base Map");
		for (int i = 0; i < MAP_SIZE; i++) {
			for (int j = 0; j < MAP_SIZE; j++) {
				MapTile t = new MapTile(i, j, (float) (SimplexNoise.noise(i / RESOLUTION, j / RESOLUTION)));
				tileMap[i][j] = t;
			}
		}
		
		if (bI == null)
			bI = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_BYTE_GRAY);
		
		updateMap();
	}
	
	public void increaseLand(int iteration) {
		SimplexNoise.genGrad(System.nanoTime());
		
		System.out.println("Adding detail to base map: " + iteration);
		for (int i = 0; i < MAP_SIZE; i++) {
			for (int j = 0; j < MAP_SIZE; j++) {
				float inc = (float) (SimplexNoise.noise(
						i / (RESOLUTION / Math.pow(2, iteration)), 
						j / (RESOLUTION / Math.pow(2, iteration))) 
						/ (Math.pow(2, iteration) * 2.0));
				
				tileMap[i][j].setHeight(tileMap[i][j].getHeight() + inc);
			}
		}
		
		updateMap();
	}
	
	public void updateMap() {
		Graphics g = bI.getGraphics();
		for (int i = 0; i < MAP_SIZE; i++) {
			for (int j = 0; j < MAP_SIZE; j++) {
				g.setColor(new Color(tileMap[i][j].getHeight(), tileMap[i][j].getHeight(), tileMap[i][j].getHeight()));
				g.fillRect(i, j, 1, 1);
			}
		}
		
		this.paint(this.getGraphics());
	}
	
	public void cleanupDregs() {
		System.out.println("Cleaning up map");
		for (int i = 0; i < MAP_SIZE; i++) {
			for (int j = 0; j < MAP_SIZE; j++) {
				if (tileMap[i][j].getHeight() < (MAX_SLOPE * 0.2f))
					tileMap[i][j].setHeight(0f);
			}
		}
		
		updateMap();
	}
	
	public void erode() {
		for (int i = 0; i < MAP_SIZE; i++) {
			for (int j = 0; j < MAP_SIZE; j++) {
				float neighbours[] = new float[4];
				float current = tileMap[i][j].getHeight();
				neighbours[0] = tileMap[clamp(i-1, 0, MAP_SIZE-1)][j].getHeight();
				neighbours[1] = tileMap[i][clamp(j-1, 0, MAP_SIZE-1)].getHeight();
				neighbours[2] = tileMap[clamp(i+1, 0, MAP_SIZE-1)][j].getHeight();
				neighbours[3] = tileMap[i][clamp(j+1, 0, MAP_SIZE-1)].getHeight();
				
				int lowest = 0;
				float maxDiff = 0f;
				for (int k = 0; k < 3; k++) {
					float diff = current - neighbours[k];
					if (diff > maxDiff) {
						maxDiff = diff;
						lowest = k;
					}
				}
				
				float sediment = 0f;
				if (maxDiff > MIN_SLOPE && maxDiff <= MAX_SLOPE) {
					sediment = Math.min(MIN_SLOPE, (SEDIMENT_BASE * maxDiff));
					current -= sediment;
					neighbours[lowest] += sediment;
				}
				
				tileMap[i][j].setHeight(current);
				tileMap[clamp(i-1, 0, MAP_SIZE-1)][j].setHeight(neighbours[0]);
				tileMap[i][clamp(j-1, 0, MAP_SIZE-1)].setHeight(neighbours[1]);
				tileMap[clamp(i+1, 0, MAP_SIZE-1)][j].setHeight(neighbours[2]);
				tileMap[i][clamp(j+1, 0, MAP_SIZE-1)].setHeight(neighbours[3]);
			}
		}
	}
	
	public void plantGrass(int grassSeeds, int growthIterations) {
		ArrayList<MapTile> grassList = new ArrayList<MapTile>();
		ArrayList<MapTile> nextList = new ArrayList<MapTile>();
		
		Random gRand = new Random(System.currentTimeMillis());
		for (int i = 0; i < grassSeeds; i++)
			grassList.add(tileMap[gRand.nextInt(MAP_SIZE)][gRand.nextInt(MAP_SIZE)]);
		
		System.out.print("Grass(" + growthIterations + ") ");
		for (int i = 0; i < growthIterations; i++) {
			System.out.print((i + 1) + (i + 1 % 20 == 0 ? "\r\n" : " "));
			nextList = growGrass(grassList);
			grassList = growGrass(nextList);
		}
		System.out.println();
		
		this.repaint();
	}
	
	public ArrayList<MapTile> growGrass(ArrayList<MapTile> fromList) {
		ArrayList<MapTile> nextList = new ArrayList<MapTile>();
		
		for (MapTile t : fromList) {
			if (t.getType() != Tile.TILE_GRASS)
				t.setType(Tile.TILE_GRASS);
			
			if (Math.random() < GRASS_RATE) { //North
				MapTile nT = tileMap[t.getX()][clamp(t.getY() - 1, 0, MAP_SIZE-1)];
				if (setGrass(t, nT))
					nextList.add(nT);
			}
			
			if (Math.random() < GRASS_RATE) { //South
				MapTile nT = tileMap[t.getX()][clamp(t.getY() + 1, 0, MAP_SIZE-1)];
				if (setGrass(t, nT))
					nextList.add(nT);
			}
			
			if (Math.random() < GRASS_RATE) { //East
				MapTile nT = tileMap[clamp(t.getX() + 1, 0, MAP_SIZE-1)][t.getY()];
				if (setGrass(t, nT))
					nextList.add(nT);
			}
			
			if (Math.random() < GRASS_RATE) { //West
				MapTile nT = tileMap[clamp(t.getX() - 1, 0, MAP_SIZE-1)][t.getY()];
				if (setGrass(t, nT))
					nextList.add(nT);
			}
		}
		
		return nextList;
	}
	
	private boolean setGrass(MapTile from, MapTile to) {
		if (Math.abs(from.getHeight() - to.getHeight()) > GRASS_MAX_SLOPE)
			return false;
		
		if (to.getHeight() < WATER_WEIGHT)
			return false;
		
		if (to.getType() != Tile.TILE_GRASS) {
			to.setType(Tile.TILE_GRASS);
			return true;
		}
		
		return false;
	}
	
	public static int clamp(int val, int min, int max) {
	    return Math.max(min, Math.min(max, val));
	}
	
	public static void main(String[] args) {
		new HeightmapGen();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			generate();
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
			increaseLand(++currentBaseIteration);
		if (e.getKeyCode() == KeyEvent.VK_C)
			cleanupDregs();
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			newMap();
		
		if (e.getKeyCode() == KeyEvent.VK_E) {
			System.out.print("Erode(" + EROSION_ITERATIONS +") ");
			for (int i = 0; i < EROSION_ITERATIONS; i++) {
				System.out.print((i + 1) + ((i + 1) % 20 == 0 ? "\r\n" : " "));
				erode();
			}
			System.out.println();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			MapData map = api.getMapData();
			
			for (int i = 0; i < MAP_SIZE; i++) {
				for (int j = 0; j < MAP_SIZE; j++) {
					map.setSurfaceHeight(i, j, (short) ((tileMap[i][j].getHeight() - WATER_WEIGHT) * MAP_HEIGHT));
					map.setRockHeight(i, j, (short) ((tileMap[i][j].getHeight() - WATER_WEIGHT) * MAP_HEIGHT / 2.0));
					
					map.setSurfaceTile(i, j, tileMap[i][j].getType());
				}
			}
			//bI = map.createTopographicDump(true, (short) 100);
			bI = map.createMapDump();
			this.repaint();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_G)
			plantGrass(MAP_SIZE / 2, GRASS_ITERATIONS);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
