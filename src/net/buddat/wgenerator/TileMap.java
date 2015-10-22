package net.buddat.wgenerator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wurmonline.mesh.Tiles.Tile;

public class TileMap {
	
	private static final Logger logger = Logger.getLogger(TileMap.class.getName());
	
	private Random biomeRandom;
	
	private HeightMap heightMap;
	
	private Tile[][] typeMap;
	
	private Tile[][] oreTypeMap;
	
	private short[][] oreResourceMap;
	
	private short[][] dirtMap;
	
	private double singleDirt;
	
	private int biomeSeed;
	
	private double waterHeight;
	
	private boolean hasOres;
	
	private HashMap<Point, Tile> lastBiomeChanges;
	
	public TileMap(HeightMap heightMap) {
		this.heightMap = heightMap;
		this.singleDirt = heightMap.getSingleDirt();
		
		this.typeMap = new Tile[heightMap.getMapSize()][heightMap.getMapSize()];
		this.oreTypeMap = new Tile[heightMap.getMapSize()][heightMap.getMapSize()];
		this.oreResourceMap = new short[heightMap.getMapSize()][heightMap.getMapSize()];
		this.dirtMap = new short[heightMap.getMapSize()][heightMap.getMapSize()];
		
		this.hasOres = false;
		
		this.lastBiomeChanges = new HashMap<Point, Tile>();
	}
	
	public void dropDirt(int dirtCount, int maxSlope, int maxDiagSlope, int maxDirtHeight) {
		double maxSlopeHeight = maxSlope * singleDirt;
		double maxDiagSlopeHeight = maxDiagSlope * singleDirt;
		double maxHeight = maxDirtHeight * singleDirt;
		double taperHeight = maxHeight - ((dirtCount * 2) * singleDirt);
		
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < dirtCount; i++) {
			for (int x = 0; x < heightMap.getMapSize(); x++) {
				for (int y = 0; y < heightMap.getMapSize(); y++) {
					if (heightMap.getHeight(x, y) > maxHeight)
						continue;
					
					if (heightMap.getHeight(x, y) > taperHeight)
						if ((maxHeight - heightMap.getHeight(x, y)) * heightMap.getMaxHeight() < i)
							continue;
					
					Point dropTile = findDropTile(x, y, maxSlopeHeight, maxDiagSlopeHeight);
					
					addDirt((int) dropTile.getX(), (int) dropTile.getY(), 1);
				}
			}
		}
		
		logger.log(Level.INFO, "Dirt Dropping (" + dirtCount + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}
	
	public void generateOres(double[] rates) {
		long startTime = System.currentTimeMillis();
		
		for (int x = 0; x < heightMap.getMapSize(); x++) {
			for (int y = 0; y < heightMap.getMapSize(); y++) {
				double rand = biomeRandom.nextDouble() * 100;
				double total;
				
				if (rand < (total = rates[0]))
					setOreType(x, y, Tile.TILE_CAVE_WALL, biomeRandom.nextInt(20) + 40);
				else if (rand < (total += rates[1]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_IRON, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[2]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_GOLD, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[3]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_SILVER, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[4]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_ZINC, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[5]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_COPPER, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[6]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_LEAD, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[7]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_TIN, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[8]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_ADAMANTINE, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[9]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_GLIMMERSTEEL, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[10]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_MARBLE, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[11]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_SLATE, biomeRandom.nextInt(15000) + 90);
				else
					setOreType(x, y, Tile.TILE_CAVE_WALL, biomeRandom.nextInt(20) + 40);
			}
		}
		
		hasOres = true;
		
		logger.log(Level.INFO, "Ore Generation completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}
	
	public void undoLastBiome() {
		for (Point p : lastBiomeChanges.keySet())
			setType(p, lastBiomeChanges.get(p));
	}
	
	public void plantBiome(int seedCount, int growthIterations, double[] growthRate, int maxBiomeSlope, int minHeight, int maxHeight, Tile type) {
		long startTime = System.currentTimeMillis();
		
		ArrayList<Point> grassList = new ArrayList<Point>();
		ArrayList<Point> nextList = new ArrayList<Point>();
		
		lastBiomeChanges.clear();
		
		for (int i = 0; i < seedCount; i++)
			grassList.add(new Point(biomeRandom.nextInt(heightMap.getMapSize()), biomeRandom.nextInt(heightMap.getMapSize())));
		
		for (int i = 0; i < growthIterations; i++) {
			nextList = growBiome(grassList, type, growthRate, maxBiomeSlope, minHeight, maxHeight);
			grassList = growBiome(nextList, type, growthRate, maxBiomeSlope, minHeight, maxHeight);
		}
		
		logger.log(Level.INFO, "Biome Seeding (" + type.tilename + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}
	
	public ArrayList<Point> growBiome(ArrayList<Point> fromList, Tile type, double[] growthRate, int maxBiomeSlope, int minHeight, int maxHeight) {
		ArrayList<Point> nextList = new ArrayList<Point>();
		
		int dirMod = (type.isTree() ? biomeRandom.nextInt(6) + 2 : (type.isBush() ? biomeRandom.nextInt(3) + 2 : 1));
		
		for (Point p : fromList) {
			if (biomeRandom.nextDouble() < growthRate[0]) { //North
				Point nT = new Point((int) p.getX(), HeightMap.clamp((int) (p.getY() - dirMod), 0, heightMap.getMapSize() - 1));
				if (setBiome(p, nT, maxBiomeSlope * dirMod, type, minHeight, maxHeight))
					nextList.add(nT);
			}
			
			if (biomeRandom.nextDouble() < growthRate[1]) { //South
				Point nT = new Point((int) p.getX(), HeightMap.clamp((int) (p.getY() + dirMod), 0, heightMap.getMapSize() - 1));
				if (setBiome(p, nT, maxBiomeSlope * dirMod, type, minHeight, maxHeight))
					nextList.add(nT);
			}
			
			if (biomeRandom.nextDouble() < growthRate[2]) { //East
				Point nT = new Point(HeightMap.clamp((int) (p.getX() + dirMod), 0, heightMap.getMapSize() - 1), (int) p.getY());
				if (setBiome(p, nT, maxBiomeSlope * dirMod, type, minHeight, maxHeight))
					nextList.add(nT);
			}
			
			if (biomeRandom.nextDouble() < growthRate[3]) { //West
				Point nT = new Point(HeightMap.clamp((int) (p.getX() - dirMod), 0, heightMap.getMapSize() - 1), (int) p.getY());
				if (setBiome(p, nT, maxBiomeSlope * dirMod, type, minHeight, maxHeight))
					nextList.add(nT);
			}
		}
		
		return nextList;
	}
	
	private boolean setBiome(Point from, Point to, int maxBiomeSlope, Tile type, int minHeight, int maxHeight) {
		if (getType((int) to.getX(), (int) to.getY()) == Tile.TILE_ROCK)
			return false;
		
		if (from != null)
			if (getDifference(from, to) > (singleDirt * maxBiomeSlope))
				return false;
		
		if (getTileHeight((int) to.getX(), (int) to.getY()) < (singleDirt * minHeight))
			return false;
		
		if (getTileHeight((int) to.getX(), (int) to.getY()) > (singleDirt * maxHeight))
			return false;
		
		Tile originalTileType = getType((int) to.getX(), (int) to.getY());
		if (originalTileType != type) {
			if((!type.isTree() && !type.isBush()) || originalTileType == Tile.TILE_GRASS) {
				lastBiomeChanges.put(to, getType(to));
				
				setType(to, type);
				return true;
			}
		}
		
		return false;
	}
	
	public Tile getType(int x, int y) {
		if (typeMap[x][y] == null)
			return Tile.TILE_ROCK;
		
		return typeMap[x][y];
	}
	
	public Tile getType(Point p) {
		return getType((int) p.getX(), (int) p.getY());
	}
	
	public void setType(int x, int y, Tile newType) {
		typeMap[x][y] = newType;
	}
	
	public void setType(Point p, Tile newType) {
		setType((int) p.getX(), (int) p.getY(), newType);
	}
	
	public Tile getOreType(int x, int y) {
		if (oreTypeMap[x][y] == null)
			return Tile.TILE_CAVE_WALL;
		
		return oreTypeMap[x][y];
	}
	
	public Tile getOreType(Point p) {
		return getOreType((int) p.getX(), (int) p.getY());
	}
	
	public void setOreCount(int x, int y, int resourceCount) {
		oreResourceMap[x][y] = (short) resourceCount;
	}
	
	public short getOreCount(int x, int y) {
		return oreResourceMap[x][y];
	}
	
	public void setOreType(int x, int y, Tile newType, int resourceCount) {
		if (!newType.isCave())
			newType = Tile.TILE_CAVE_WALL;
		
		oreTypeMap[x][y] = newType;
		setOreCount(x, y, resourceCount);
	}
	
	public void setOreType(Point p, Tile newType, short resourceCount) {
		setOreType((int) p.getX(), (int) p.getY(), newType, resourceCount);
	}
	
	public boolean hasOres() {
		return hasOres;
	}
	
	public short getDirt(int x, int y) {
		return dirtMap[x][y];
	}
	
	public void setDirt(int x, int y, short newDirt) {
		if (newDirt < 0)
			newDirt = 0;
		
		if (newDirt > 0) {
			if (getTileHeight(x, y) >= waterHeight)
				setType(x, y, Tile.TILE_GRASS);
			else
				setType(x, y, Tile.TILE_DIRT);
		}
		
		dirtMap[x][y] = newDirt;
	}
	
	public void addDirt(int x, int y, int count) {
		setDirt(x, y, (short) (getDirt(x, y) + count));
	}
	
	public double getDirtHeight(int x, int y) {
		return getDirt(x, y) * singleDirt;
	}
	
	public double getTileHeight(int x, int y) {
		return heightMap.getHeight(x, y) + getDirtHeight(x, y);
	}
	
	public short getSurfaceHeight(int x, int y) {
		return (short) ((getTileHeight(x, y) - getWaterHeight()) * heightMap.getMaxHeight());
	}
	
	public short getRockHeight(int x, int y) {
		return (short) ((heightMap.getHeight(x, y) - getWaterHeight()) * heightMap.getMaxHeight());
	}
	
	public double getDifference(int x1, int y1, int x2, int y2) {
		return Math.abs(getTileHeight(x1, y1) - getTileHeight(x2, y2));
	}
	
	public double getDifference(Point p, Point p2) {
		return getDifference((int) p.getX(), (int) p.getY(), (int) p2.getX(), (int) p2.getY());
	}
	
	public HeightMap getHeightMap() {
		return heightMap;
	}
	
	public void setHeightMap(HeightMap newMap) {
		this.heightMap = newMap;
	}
	
	public int getBiomeSeed() {
		return biomeSeed;
	}
	
	public void setBiomeSeed(int newSeed) {
		this.biomeSeed = newSeed;
		
		biomeRandom = new Random(newSeed);
	}
	
	public double getWaterHeight() {
		return waterHeight;
	}
	
	public void setWaterHeight(int newHeight) {
		this.waterHeight = newHeight * singleDirt;
	}

	private Point findDropTile(int x, int y, double maxSlope, double maxDiagSlope) {
		ArrayList<Point> slopes = new ArrayList<Point>();
		double currentHeight = getTileHeight(x, y);
		
		for (int i = x + 1; i >= x - 1; i--) {
			for (int j = y + 1; j >= y - 1; j--) {
				if (i < 0 || j < 0 || i >= heightMap.getMapSize() || j >= heightMap.getMapSize())
					continue;
				
				double thisHeight = getTileHeight(i, j);
				if ((i == 0 && j != 0) || (i != 0 && j == 0))
					if (thisHeight <= currentHeight - maxSlope)
						slopes.add(new Point(i, j));
				
				if (i != 0 && y != 0)
					if (thisHeight <= currentHeight - maxDiagSlope)
						slopes.add(new Point(i, j));
			}
		}
		
		if (slopes.size() > 0) {
			int r = biomeRandom.nextInt(slopes.size());
			return findDropTile((int) slopes.get(r).getX(), (int) slopes.get(r).getY(), maxSlope, maxDiagSlope);
		} else {
			return new Point(x, y);
		}
	}
}
