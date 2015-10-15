package net.buddat.util.heightmap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.wurmonline.mesh.Tiles.Tile;
import com.wurmonline.wurmapi.api.MapData;
import com.wurmonline.wurmapi.api.WurmAPI;

import net.buddat.wgenerator.HeightMap;
import net.buddat.wgenerator.util.SimplexNoise;

public class HeightmapGen extends JFrame implements KeyListener {
	
	private static final long serialVersionUID = 1500559112995998883L;

	public static final int MAP_SIZE = 2048;
	public static final int WINDOW_SIZE = 1024;
	
	public static final float MAP_HEIGHT = 4096f;
	
	public static final float SINGLE_DIRT = 1.0f / MAP_HEIGHT;
	
	public static final double RESOLUTION = MAP_SIZE / 8;
	
	public static final float MIN_SLOPE = 0.0001f, MAX_SLOPE = 0.9f;
	public static final float MAX_SEDIMENT = 0.01f, SEDIMENT_BASE = 0.15f;
	
	public static final int DIRT_DROP_COUNT = 60;
	public static final int MAX_DIRT_SLOPE = 40;
	
	public static final int EROSION_ITERATIONS = 100;
	
	public static final int GRASS_ITERATIONS = 50;
	public static final float BIOME_RATE = 0.6f;
	public static final float BIOME_MAX_SLOPE = SINGLE_DIRT * 20;
	
	public static final float WATER_WEIGHT = 0.125f;
	public static final float SAND_WEIGHT = 1.075f;
	public static final float ROCK_WEIGHT = 0.95f;
	public static final float ROCK_SLOPE_WEIGHT = 0.0005f;
	
	public static final float NORMAL_LOW = 0.5f;
	public static final float NORMAL_HIGH = 1.0f - NORMAL_LOW;
	
	private MapTile[][] tileMap;
	private BufferedImage bI;
	
	private int currentBaseIteration;
	public static float waterWeight = 0.20f;
	
	public float dirtDrop = SINGLE_DIRT * DIRT_DROP_COUNT;
	public float dirtSlope = SINGLE_DIRT * MAX_DIRT_SLOPE;
	public float dirtDiagonalSlope = dirtSlope * 1.1f;
	
	public static final Random GEN_RAND = new Random(System.currentTimeMillis());
	
	private WurmAPI api;
	
	private HeightMap testing;

	public HeightmapGen() {
		super("WGenerator - Wurm Unlimited Map Generator");
		this.setSize(WINDOW_SIZE, WINDOW_SIZE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.addKeyListener(this);
		
        try {
            api = WurmAPI.create("./", (int) (Math.log(MAP_SIZE) / Math.log(2)));
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        
        if (bI == null)
			bI = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_BYTE_GRAY);
		
		fullRun();
	}
	
	public void fullRun() {
		newMap(false);
		normalizeHeights();
		waterWeight = WATER_WEIGHT;
		
		System.out.print("Erode Rock" + "(" + EROSION_ITERATIONS +") ");
		for (int i = 0; i < EROSION_ITERATIONS; i++) {
			System.out.print((i + 1) + ((i + 1) % 20 == 0 ? "\r\n" : " "));
			erode(false);
		}
		
		dropDirt();
		
		generateBiomes();
		
		showMapDump(false);
	}
	
	public void newMap(boolean old) {
		if (old) {
			long startTime = System.currentTimeMillis();
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
			System.out.println("Old Heightmap Generation (" + MAP_SIZE + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
		} else {
			testing = new HeightMap(System.currentTimeMillis(), MAP_SIZE, RESOLUTION, 10, 40, 8, 4096, true);
	        testing.generateHeights();
	        
	        tileMap = new MapTile[MAP_SIZE][MAP_SIZE];
	        for (int i = 0; i < MAP_SIZE; i++) {
				for (int j = 0; j < MAP_SIZE; j++) {
					MapTile t = new MapTile(i, j, (float) testing.getHeight(i, j));
					tileMap[i][j] = t;
				}
			}
		}
        
        updateMap();
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
				
				tileMap[i][j].setHeight(tileMap[i][j].getHeight() + inc, false);
			}
		}
		
		updateMap();
	}
	
	public void updateMap() {
		Graphics g = bI.getGraphics();
		for (int i = 0; i < MAP_SIZE; i++) {
			for (int j = 0; j < MAP_SIZE; j++) {
				g.setColor(new Color(tileMap[i][j].getHeight(), tileMap[i][j].getHeight(), tileMap[i][j].getHeight()));
				//g.setColor(new Color((float) testing.getHeight(i, j), (float) testing.getHeight(i, j), (float) testing.getHeight(i, j)));
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
					tileMap[i][j].setHeight(0f, false);
			}
		}
		
		updateMap();
	}
	
	public void normalizeHeights() {
		float maxHeight = 0.0f;
		for (int i = 0; i < MAP_SIZE; i++) {
			for (int j = 0; j < MAP_SIZE; j++) {
				if (tileMap[i][j].getHeight() > maxHeight)
					maxHeight = tileMap[i][j].getHeight();
			}
		}
		
		float normalize = 1.0f / maxHeight;
		System.out.println("Normalizing: " + normalize + " MaxEdgeHeight: " + 0.13 * normalize);
		for (int i = 0; i < MAP_SIZE; i++)
			for (int j = 0; j < MAP_SIZE; j++)
				tileMap[i][j].setHeight(tileMap[i][j].getHeight() * normalize, true);
		
		float normalizeLow = 1.0f / NORMAL_LOW;
		float normalizeHigh = 2.0f / NORMAL_HIGH;
		for (int i = 0; i < MAP_SIZE; i++)
			for (int j = 0; j < MAP_SIZE; j++) {
				if (tileMap[i][j].getHeight() < NORMAL_LOW) {
					tileMap[i][j].setHeight((tileMap[i][j].getHeight() * normalizeLow) / 3f, true);
				} else {
					float newHeight = 1.0f + (tileMap[i][j].getHeight() - NORMAL_LOW) * normalizeHigh;
					tileMap[i][j].setHeight(newHeight / 3f, true);
				}
			}
	}
	
	public void erode(boolean erodeDirt) {
		for (int i = 0; i < MAP_SIZE; i++) {
			for (int j = 0; j < MAP_SIZE; j++) {
				float neighbours[] = new float[4];
				float current = tileMap[i][j].getHeight();
				neighbours[0] = tileMap[clamp(i-1, 0, MAP_SIZE-1)][j].getHeight();
				neighbours[1] = tileMap[i][clamp(j-1, 0, MAP_SIZE-1)].getHeight();
				neighbours[2] = tileMap[clamp(i+1, 0, MAP_SIZE-1)][j].getHeight();
				neighbours[3] = tileMap[i][clamp(j+1, 0, MAP_SIZE-1)].getHeight();
				
				if (erodeDirt) {
					current += tileMap[i][j].getDirt();
					neighbours[0] += tileMap[clamp(i-1, 0, MAP_SIZE-1)][j].getDirt();
					neighbours[1] += tileMap[i][clamp(j-1, 0, MAP_SIZE-1)].getDirt();
					neighbours[2] += tileMap[clamp(i+1, 0, MAP_SIZE-1)][j].getDirt();
					neighbours[3] += tileMap[i][clamp(j+1, 0, MAP_SIZE-1)].getDirt();
				}
				
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
				if (maxDiff > MIN_SLOPE/* && maxDiff <= MAX_SLOPE*/) {
					sediment = SEDIMENT_BASE * maxDiff;
					if (erodeDirt)
						sediment = dirtDrop / 10f;
					current -= sediment;
					neighbours[lowest] += sediment;
				}
				
				if (erodeDirt) {
					if (maxDiff > ROCK_SLOPE_WEIGHT)
						tileMap[i][j].setDirt(0);
					else
						tileMap[i][j].setDirt(current - tileMap[i][j].getHeight());
					
					tileMap[clamp(i-1, 0, MAP_SIZE-1)][j].setDirt(neighbours[0] - tileMap[clamp(i-1, 0, MAP_SIZE-1)][j].getHeight());
					tileMap[i][clamp(j-1, 0, MAP_SIZE-1)].setDirt(neighbours[1] - tileMap[i][clamp(j-1, 0, MAP_SIZE-1)].getHeight());
					tileMap[clamp(i+1, 0, MAP_SIZE-1)][j].setDirt(neighbours[2] - tileMap[clamp(i+1, 0, MAP_SIZE-1)][j].getHeight());
					tileMap[i][clamp(j+1, 0, MAP_SIZE-1)].setDirt(neighbours[3] - tileMap[i][clamp(j+1, 0, MAP_SIZE-1)].getHeight());
				} else {
					tileMap[i][j].setHeight(current, true);
					tileMap[clamp(i-1, 0, MAP_SIZE-1)][j].setHeight(neighbours[0], true);
					tileMap[i][clamp(j-1, 0, MAP_SIZE-1)].setHeight(neighbours[1], true);
					tileMap[clamp(i+1, 0, MAP_SIZE-1)][j].setHeight(neighbours[2], true);
					tileMap[i][clamp(j+1, 0, MAP_SIZE-1)].setHeight(neighbours[3], true);
				}
			}
		}
	}
	
	public void dropDirt() {
		System.out.print("Drop Dirt(" + DIRT_DROP_COUNT +") ");
		for (int i = 0; i < DIRT_DROP_COUNT; i++) {
			System.out.print((i + 1) + ((i + 1) % 20 == 0 ? "\r\n" : " "));
			
			for (int x = 0; x < MAP_SIZE; x++) {
				for (int y = 0; y < MAP_SIZE; y++) {
					Point dropTile = findDropTile(x, y);
					
					tileMap[(int) dropTile.getX()][(int) dropTile.getY()].addDirt(SINGLE_DIRT);
				}
			}
		}
	}
	
	
	private Point findDropTile(int x, int y) {
		ArrayList<Point> slopes = new ArrayList<Point>();
		float currentHeight = tileMap[x][y].getHeight() + tileMap[x][y].getDirt();
		
		for (int i = x + 1; i > x - 1; i--) {
			for (int j = y + 1; j > y - 1; j--) {
				if (i < 0 || j < 0 || i >= MAP_SIZE || j >= MAP_SIZE)
					continue;
				
				float thisHeight = tileMap[i][j].getHeight() + tileMap[i][j].getDirt();
				if ((i == 0 && j != 0) || (i != 0 && j == 0))
					if (thisHeight <= currentHeight - dirtSlope)
						slopes.add(new Point(i, j));
				
				if (i != 0 && y != 0)
					if (thisHeight <= currentHeight - dirtDiagonalSlope)
						slopes.add(new Point(i, j));
			}
		}
		
		if (slopes.size() > 0) {
			int r = GEN_RAND.nextInt(slopes.size());
			return findDropTile((int) slopes.get(r).getX(), (int) slopes.get(r).getY());
		} else {
			return new Point(x, y);
		}
	}
	
	public void generateBiomes() {
		plantBiome(MAP_SIZE / 64, 64, Tile.TILE_SAND);
		plantBiome(MAP_SIZE / 128, 80, Tile.TILE_STEPPE);
		plantBiome(MAP_SIZE / 256, 16, Tile.TILE_PEAT);
		plantBiome(MAP_SIZE / 512, 64, Tile.TILE_TUNDRA);
	}
	
	public void plantBiome(int seedCount, int growthIterations, Tile type) {
		ArrayList<MapTile> grassList = new ArrayList<MapTile>();
		ArrayList<MapTile> nextList = new ArrayList<MapTile>();
		
		for (int i = 0; i < seedCount; i++)
			grassList.add(tileMap[GEN_RAND.nextInt(MAP_SIZE)][GEN_RAND.nextInt(MAP_SIZE)]);
		
		System.out.print("Biome(" + growthIterations + ") ");
		for (int i = 0; i < growthIterations; i++) {
			System.out.print((i + 1) + ((i + 1) % 16 == 0 ? "\r\n" : " "));
			nextList = growGrass(grassList, type);
			grassList = growGrass(nextList, type);
		}
		
		this.repaint();
	}
	
	public ArrayList<MapTile> growGrass(ArrayList<MapTile> fromList, Tile type) {
		ArrayList<MapTile> nextList = new ArrayList<MapTile>();
		
		for (MapTile t : fromList) {
			if (t.getTypeOverride() != type)
				t.setTypeOverride(type);
			
			if (Math.random() < BIOME_RATE) { //North
				MapTile nT = tileMap[t.getX()][clamp(t.getY() - 1, 0, MAP_SIZE-1)];
				if (setGrass(t, nT, type))
					nextList.add(nT);
			}
			
			if (Math.random() < BIOME_RATE) { //South
				MapTile nT = tileMap[t.getX()][clamp(t.getY() + 1, 0, MAP_SIZE-1)];
				if (setGrass(t, nT, type))
					nextList.add(nT);
			}
			
			if (Math.random() < BIOME_RATE) { //East
				MapTile nT = tileMap[clamp(t.getX() + 1, 0, MAP_SIZE-1)][t.getY()];
				if (setGrass(t, nT, type))
					nextList.add(nT);
			}
			
			if (Math.random() < BIOME_RATE) { //West
				MapTile nT = tileMap[clamp(t.getX() - 1, 0, MAP_SIZE-1)][t.getY()];
				if (setGrass(t, nT, type))
					nextList.add(nT);
			}
		}
		
		return nextList;
	}
	
	private boolean setGrass(MapTile from, MapTile to, Tile type) {
		if (Math.abs(from.getHeight() - to.getHeight()) > BIOME_MAX_SLOPE)
			return false;
		
		if (to.getHeight() < waterWeight)
			return false;
		
		if (to.getTypeOverride() != type) {
			to.setTypeOverride(type);
			return true;
		}
		
		return false;
	}
	
	public void showMapDump(boolean topographic) {
		MapData map = api.getMapData();
		
		for (int i = 0; i < MAP_SIZE; i++) {
			for (int j = 0; j < MAP_SIZE; j++) {
				tileMap[i][j].resetTypes();
				
				map.setSurfaceHeight(i, j, (short) ((tileMap[i][j].getHeight() - waterWeight) * MAP_HEIGHT));
				map.setRockHeight(i, j, (short) ((tileMap[i][j].getHeight() - tileMap[i][j].getDirt() - waterWeight) * MAP_HEIGHT));
				
				map.setSurfaceTile(i, j, tileMap[i][j].getType());
			}
		}
		
		if (topographic)
			bI = map.createTopographicDump(true, (short) 100);
		else
			bI = map.createMapDump();
		
		this.repaint();
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
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			fullRun();
		
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			generate();
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
			increaseLand(++currentBaseIteration);
		if (e.getKeyCode() == KeyEvent.VK_C)
			cleanupDregs();
		
		if (e.getKeyCode() == KeyEvent.VK_D)
			dropDirt();
		
		if (e.getKeyCode() == KeyEvent.VK_N)
			normalizeHeights();
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			newMap(true);
		
		if (e.getKeyCode() == KeyEvent.VK_E || e.getKeyCode() == KeyEvent.VK_R) {
			System.out.print("Erode " + (e.getKeyCode() == KeyEvent.VK_R ? "Dirt" : "Rock") + "(" + EROSION_ITERATIONS +") ");
			for (int i = 0; i < EROSION_ITERATIONS; i++) {
				System.out.print((i + 1) + ((i + 1) % 20 == 0 ? "\r\n" : " "));
				erode(e.getKeyCode() == KeyEvent.VK_R);
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_S)
			showMapDump(false);
		
		if (e.getKeyCode() == KeyEvent.VK_M) {
			System.out.println("Generating *.pngs");
			MapData map = api.getMapData();
			try {
				ImageIO.write(map.createMapDump(), "png", new File("map.png"));
				ImageIO.write(map.createTopographicDump(true, (short) 250), "png", new File("topography.png"));
			} catch (IOException ex) {
				Logger.getLogger(HeightmapGen.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_P) {
			System.out.println("Saving map");
			api.getMapData().saveChanges();
			api.close();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_G)
			plantBiome(MAP_SIZE / 2, GRASS_ITERATIONS, Tile.TILE_GRASS);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
