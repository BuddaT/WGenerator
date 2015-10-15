package net.buddat.wgenerator.util;


/**
 * @author Budda
 *
 * Class containing all default settings for a new map generation.
 */
public class Constants {
	
	public static final String WINDOW_TITLE = "WGenerator - Map Generator for Wurm Unlimited";
	public static final int WINDOW_SIZE = 1024;
	
	public static final int MAP_SIZE = 2048;
	public static final float MAP_HEIGHT = 4096f;
	
	public static final float SINGLE_DIRT = 1.0f / MAP_HEIGHT;
	
	public static final double RESOLUTION = MAP_SIZE / 4;
	
	public static final int HEIGHTMAP_ITERATIONS = 10;
	
	public static final int MIN_SLOPE = 40; 
	public static final int MAX_SEDIMENT = 40;
	public static final float SEDIMENT_BASE = 0.15f, MAX_SLOPE = 0.9f;
	
	public static final int DIRT_DROP_COUNT = 60;
	public static final int MAX_DIRT_SLOPE = 40;
	
	public static final int EROSION_ITERATIONS = 25;
	
	public static final int GRASS_ITERATIONS = 50;
	public static final float BIOME_RATE = 0.6f;
	public static final float BIOME_MAX_SLOPE = SINGLE_DIRT * 20;
	
	public static final float WATER_WEIGHT = 0.125f;
	public static final float SAND_WEIGHT = 1.075f;
	public static final float ROCK_WEIGHT = 0.95f;
	public static final float ROCK_SLOPE_WEIGHT = 0.0005f;
	
	public static final float NORMAL_LOW = 0.5f;
	public static final float NORMAL_HIGH = 1.0f - NORMAL_LOW;
	
	public static final int MIN_EDGE = 64;
	public static final double BORDER_WEIGHT = 4.0;
	public static final boolean MORE_LAND = false;
	
}
