package net.buddat.wgenerator;

import com.wurmonline.mesh.Tiles;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by luceat on 2015-10-21.
 *
 *  Tries to put out as much dirt as it can, then shaves off dirt to lower dirt slopes.
 */
public class GreedyTileMap extends TileMap {
    private static final Logger logger = Logger.getLogger(TileMap.class.getName());

    private Random biomeRandom;

    private HeightMap heightMap;

    private Tiles.Tile[][] typeMap;

    private Tiles.Tile[][] oreTypeMap;

    private short[][] oreResourceMap;

    private short[][] dirtMap;

    private double singleDirt;

    private int biomeSeed;

    private double waterHeight;

    private boolean hasOres;

    private HashMap<Point, Tiles.Tile> lastBiomeChanges;

    public GreedyTileMap(HeightMap heightMap) {
        super(heightMap);
        this.heightMap = heightMap;
        this.singleDirt = heightMap.getSingleDirt();

        this.typeMap = new Tiles.Tile[heightMap.getMapSize()][heightMap.getMapSize()];
        this.oreTypeMap = new Tiles.Tile[heightMap.getMapSize()][heightMap.getMapSize()];
        this.oreResourceMap = new short[heightMap.getMapSize()][heightMap.getMapSize()];
        this.dirtMap = new short[heightMap.getMapSize()][heightMap.getMapSize()];

        this.hasOres = false;

        this.lastBiomeChanges = new HashMap<Point, Tiles.Tile>();
    }

    @Override
    public void dropDirt(int dirtCount, int maxSlope, int maxDiagSlope, int maxDirtHeight) {
        double maxHeight = maxDirtHeight * singleDirt;

        int totShavedDirts = 0;
        int origDirtCount = dirtCount;

        long startTime = System.currentTimeMillis();


        int x = 0;
        int y = 0;

        //Add dirt to borders
        for (y = 0; y < heightMap.getMapSize(); y++){
            addDirt(x, y, dirtCount);
            addDirt(heightMap.getMapSize()-1, y, dirtCount);
        }
        y = 0;
        for (x = 1; x < heightMap.getMapSize()-1; x++){
            addDirt(x, y, dirtCount);
            addDirt(x, heightMap.getMapSize()-1, dirtCount);
        }

        //Add dirt to interior tiles
        while(dirtCount > 0) {
            for (x = 1; x < heightMap.getMapSize()-1; x++) {
                for (y = 1; y < heightMap.getMapSize()-1; y++) {

                    if (heightMap.getHeight(x, y) > maxHeight)
                        continue;

                    int dirtMax = maxDirtHeight(x, y, dirtCount, maxSlope, maxDiagSlope);
                    if (dirtMax > 0)
                        addDirt(x, y, dirtMax);
                }
            }
            dirtCount -= maxSlope;
        }

        //Shaving - Now take into consideration dirt slopes and try to shave off dirt
        for (x = 1; x < heightMap.getMapSize()-1; x++) {
            for (y = 1; y < heightMap.getMapSize()-1; y++) {

                if (heightMap.getHeight(x, y) > maxHeight)
                    continue;

                totShavedDirts += shaveDirt(x, y, maxSlope, maxDiagSlope);
            }
        }

        logger.log(Level.INFO, "Dirt Dropping (" + origDirtCount + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
        logger.log(Level.INFO, "Shaved away a total of: " + totShavedDirts + " dirts.");
        logger.log(Level.INFO, "The maximum slope found was: " + findMaximumSlope());
        logger.log(Level.INFO, "The maximum diagonal slope found was: " + findMaximumDiagSlope());
        logger.log(Level.INFO, "The maximum dirt depth found was: " + findMaximumDirtDepth());
        logger.log(Level.INFO, "The average dirt depth on dirt tiles was: " + findAvgDirtDepth());
    }

    public int shaveDirt(int x, int y, int maxSlope, int maxDiagSlope){
        int shavedDirts = 0;
        int toShave = 0;
        int perfectRemove;

        for (int i = x-1; i < x+1; i++){
            for (int j = y-1; j < y+1; j++){
                if (i == x && j == y)
                    continue;
                int currSlope = getSignedDifferenceWithDirts(x, y, i, j);
                if (currSlope > 0) {
                    if ( Math.abs(x-i) == 1 && Math.abs(y-j) == 1 )
                        if (maxDiagSlope - currSlope < 0 && getDirt(i, j) > 0) {
                            perfectRemove = currSlope - maxDiagSlope;
                            toShave = Math.min((int) getDirt(i, j), perfectRemove);
                            setDirt(i, j, (short) toShave);
                            shavedDirts += toShave;
                        }
                        else
                        if (maxSlope - Math.abs(currSlope) < 0){
                            perfectRemove = currSlope - maxSlope;
                            toShave = Math.min((int) getDirt(i, j), perfectRemove);
                            setDirt(i, j, (short) toShave);
                            shavedDirts += toShave;
                        }
                }
            }
        }
        return shavedDirts;
    }

    public int maxDirtHeight(int x, int y, int dirtCount, int maxSlope, int maxDiagSlope){
        //If its on the edge
        if( x == 0 || x == heightMap.getMapSize()-1 || y == 0 || y == heightMap.getMapSize()-1)
            return dirtCount;

        int minSlope = 1000;

        for (int i = x-1; i < x+1; i++){
            for (int j = y-1; j < y+1; j++){
                if (i == x && j == y)
                    continue;
                int currSlope = getSignedDifferenceInDirts(x, y, i, j);
                //Diagonal
                if ( Math.abs(x-i) == 1 && Math.abs(y-j) == 1 ) {
                    if (maxDiagSlope - Math.abs(currSlope) < 0)
                        return 0;
                }
                else if (maxSlope - Math.abs(currSlope) < 0)
                    return 0;
                minSlope = Math.min(minSlope, currSlope);
            }
        }
        return Math.min(dirtCount, Math.min(maxSlope, (minSlope + maxSlope)));
    }

    public int findMaximumSlope(){
        int maxSlope = -1;
        for (int x = 0; x < heightMap.getMapSize()-1; x++) {
            for (int y = 0; y < heightMap.getMapSize()-1; y++) {
                maxSlope = Math.max(maxSlope, Math.max((int) (getDifference(x, y, x, y+1)* heightMap.getMaxHeight()),
                        (int) (getDifference(x, y, x+1, y)* heightMap.getMaxHeight())));
            }
        }
        return maxSlope;
    }

    public int findMaximumDiagSlope(){
        int maxSlope = -1;
        for (int x = 0; x < heightMap.getMapSize()-1; x++) {
            for (int y = 0; y < heightMap.getMapSize()-1; y++) {
                maxSlope = Math.max(maxSlope, (int) (getDifference(x, y, x+1, y+1)* heightMap.getMaxHeight()));
            }
        }
        return maxSlope;
    }

    public int findMaximumDirtDepth(){
        int maxDirt = -1;
        for (int x = 0; x < heightMap.getMapSize()-1; x++) {
            for (int y = 0; y < heightMap.getMapSize()-1; y++) {
                maxDirt = Math.max(maxDirt, (int) getDirt(x, y));
            }
        }
        return maxDirt;
    }

    public double findAvgDirtDepth(){
        int accDirt = 0;
        int count = 0;
        int currDirt = 0;
        double avgDirt;
        for (int x = 0; x < heightMap.getMapSize()-1; x++) {
            for (int y = 0; y < heightMap.getMapSize()-1; y++) {
                currDirt = (int) getDirt(x,y);
                if (currDirt > 0) {
                    accDirt += currDirt;
                    count++;
                }
            }
        }
        avgDirt = ((double) accDirt)/ ((double) count);
        return avgDirt;
    }

    public int getSignedDifferenceWithDirts(int x1, int y1, int x2, int y2) {
        return (int) (getDifference(x1, y1, x2, y2) * heightMap.getMaxHeight());
    }

    public int getSignedDifferenceInDirts(int x1, int y1, int x2, int y2) {
        return getRockHeight(x1, y1) - getRockHeight(x2, y2);
    }

    @Override
    public short getRockHeight(int x, int y) {
        return (short) (heightMap.getHeight(x, y) * heightMap.getMaxHeight());
    }

    @Override
    public double getDifference(int x1, int y1, int x2, int y2) {
        return getTileHeight(x1, y1) - getTileHeight(x2, y2);
    }
}
