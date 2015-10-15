package net.buddat.util.heightmap;

import com.wurmonline.mesh.Tiles.Tile;

public class MapTile {

	private int x, y;
	private float height;
	
	private float dirtHeight;
	
	private Tile type;
	private Tile typeOverride = null;
	
	public MapTile (int x, int y) {
		this(x, y, 0.0f);
	}
	
	public MapTile(int x, int y, float height) {
		setX(x);
		setY(y);
		setHeight(height, true);
		
		dirtHeight = 0f;
		
		type = Tile.TILE_ROCK;
	}
	
	public Tile getType() {
		return type;
	}
	
	public void setType(Tile newType) {
		type = newType;
	}
	
	public void setTypeOverride(Tile newType) {
		typeOverride = newType;
	}
	
	public Tile getTypeOverride() {
		return typeOverride;
	}
	
	public float getDirt() {
		return dirtHeight;
	}
	
	public void addDirt(float toAdd) {
		setDirt(getDirt() + toAdd);
	}
	
	public void setDirt(float newDirt) {
		if (newDirt < 0)
			newDirt = 0;
		if (height > HeightmapGen.ROCK_WEIGHT)
			newDirt = 0;
		
		dirtHeight = newDirt;
	}
	
	public void resetTypes() {
		if (dirtHeight > HeightmapGen.SINGLE_DIRT * HeightmapGen.DIRT_DROP_COUNT / 5) {
			if (typeOverride == null) {
				if (height > HeightmapGen.waterWeight * HeightmapGen.SAND_WEIGHT)
					setType(Tile.TILE_GRASS);
				else
					setType(Tile.TILE_SAND);
			} else {
				setType(typeOverride);
			}
		} else {
			dirtHeight = 0;
			setType(Tile.TILE_ROCK);
		}
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int newX) {
		if (newX < 0)
			newX = 0;
		if (newX >= HeightmapGen.MAP_SIZE)
			newX = HeightmapGen.MAP_SIZE;
		
		x = newX;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int newY) {
		if (newY < 0)
			newY = 0;
		if (newY >= HeightmapGen.MAP_SIZE)
			newY = HeightmapGen.MAP_SIZE;
		
		y = newY;
	}
	
	public float getHeight() {
		return height;
	}
	
	private float maxHeight = -1f;
	public float getMaxHeight() {
		if (maxHeight == -1f)
			maxHeight = 1.0f - ((1.0f / (HeightmapGen.MAP_SIZE / 1.75f)) * (getDist()));
		
		return maxHeight;
	}
	
	public void setHeight(float newHeight, boolean ignoreMaxHeight) {
		if (newHeight == 0f) {
			height = newHeight;
			return;
		}
		
		if (!ignoreMaxHeight) {
			if (newHeight > getMaxHeight())
				newHeight = getMaxHeight();
		} else {
			if (newHeight > 1.0f)
				newHeight = 1.0f;
		}
		
		if (newHeight < 0f)
			newHeight = 0f;
		
		height = newHeight;
	}
	
	private float getDist() {
		float midPoint = HeightmapGen.MAP_SIZE / 2f;
		
		return (float) Math.hypot(Math.abs(x - midPoint), Math.abs(y - midPoint));
	}
}
