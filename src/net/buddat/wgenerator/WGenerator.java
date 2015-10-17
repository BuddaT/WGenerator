package net.buddat.wgenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.GrassData.GrowthTreeStage;
import com.wurmonline.mesh.Tiles.Tile;
import com.wurmonline.wurmapi.api.MapData;
import com.wurmonline.wurmapi.api.WurmAPI;

import net.buddat.wgenerator.ui.MapPanel;
import net.buddat.wgenerator.util.Constants;

public class WGenerator extends JFrame implements ActionListener {

	private static final long serialVersionUID = 3099528642510249703L;
	
	private static final Logger logger = Logger.getLogger(WGenerator.class.getName());
	
	private WurmAPI api;
	
	private HeightMap heightMap;
	
	private TileMap tileMap;
	
	private MapPanel pnlMap;
	
	private JPanel pnlControls, pnlHeightMapControls, pnlHeightMapOptions, pnlHeightMapOptions2, pnlGenHeightMapButton;
	private JPanel pnlErodeControls, pnlErodeOptions, pnlErodeButton;
	private JPanel pnlDirtControls, pnlDirtOptions, pnlDirtOptions2, pnlDirtButton;
	private JPanel pnlBiomeControls, pnlBiomeOptions, pnlBiomeOptions2, pnlBiomeButton;
	private JPanel pnlOreControls, pnlOreOptions, pnlOreOptions2, pnlOreButton;
	private JPanel pnlSaveControls, pnlSaveOptions;
	
	private JLabel lblMapSize, lblSeed, lblRes, lblIterations, lblMinEdge, lblBorderWeight, lblMaxHeight;
	private JLabel lblErodeIterations, lblErodeMinSlope, lblErodeMaxSediment;
	private JLabel lblBiomeSeed, lblDirtAmnt, lblDirtSlope, lblDirtDiagSlope, lblMaxDirtHeight, lblWaterHeight;
	private JLabel lblBiomeSeedCount, lblBiomeSize, lblBiomeMaxSlope, lblBiomeRate, lblBiomeMinHeight, lblBiomeMaxHeight;
	private JLabel lblRock, lblIron, lblGold, lblSilver, lblZinc, lblCopper, lblLead, lblTin, lblAddy, lblGlimmer, lblMarble, lblSlate;
	
	private JTextField txtSeed, txtRes, txtIterations, txtMinEdge, txtBorderWeight, txtMaxHeight;
	private JTextField txtErodeIterations, txtErodeMinSlope, txtErodeMaxSediment;
	private JTextField txtBiomeSeed, txtDirtAmnt, txtDirtSlope, txtDirtDiagSlope, txtMaxDirtHeight, txtWaterHeight;
	private JTextField txtBiomeSeedCount, txtBiomeSize, txtBiomeMaxSlope, txtBiomeRateN, txtBiomeRateS, txtBiomeRateE, txtBiomeRateW, txtBiomeMinHeight, txtBiomeMaxHeight;
	private JTextField txtRock, txtIron, txtGold, txtSilver, txtZinc, txtCopper, txtLead, txtTin, txtAddy, txtGlimmer, txtMarble, txtSlate;
	
	private JCheckBox chkLand;
	
	@SuppressWarnings("rawtypes")
	private JComboBox cmbMapSize, cmbBiomeType;
	
	private JButton btnGenHeightMap, btnErodeHeightMap, btnDropDirt, btnSeedBiome, btnUndoBiome, btnResetBiomes, btnGenOres;
	private JButton btnSaveImages, btnSaveMap, btnShowDump, btnShowTopo, btnShowHeightMap;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WGenerator(String title, int width, int height) {
		super(title);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		
		pnlControls = new JPanel();
		pnlControls.setLayout(new BoxLayout(pnlControls, BoxLayout.Y_AXIS));
		
		pnlHeightMapControls = new JPanel();
		pnlHeightMapControls.setLayout(new BorderLayout());
		pnlHeightMapOptions = new JPanel();
		pnlHeightMapOptions.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlHeightMapOptions2 = new JPanel();
		pnlHeightMapOptions2.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlGenHeightMapButton = new JPanel();
		pnlGenHeightMapButton.setLayout(new FlowLayout());
		
		pnlErodeControls = new JPanel();
		pnlErodeControls.setLayout(new BorderLayout());
		pnlErodeOptions = new JPanel();
		pnlErodeOptions.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlErodeButton = new JPanel();
		pnlErodeButton.setLayout(new FlowLayout());
		
		pnlDirtControls = new JPanel();
		pnlDirtControls.setLayout(new BorderLayout());
		pnlDirtOptions = new JPanel();
		pnlDirtOptions.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlDirtOptions2 = new JPanel();
		pnlDirtOptions2.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlDirtButton = new JPanel();
		pnlDirtButton.setLayout(new FlowLayout());
		
		pnlBiomeControls = new JPanel();
		pnlBiomeControls.setLayout(new BorderLayout());
		pnlBiomeOptions = new JPanel();
		pnlBiomeOptions.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlBiomeOptions2 = new JPanel();
		pnlBiomeOptions2.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlBiomeButton = new JPanel();
		pnlBiomeButton.setLayout(new FlowLayout());
		
		pnlOreControls = new JPanel();
		pnlOreControls.setLayout(new BorderLayout());
		pnlOreOptions = new JPanel();
		pnlOreOptions.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlOreOptions2 = new JPanel();
		pnlOreOptions2.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlOreButton = new JPanel();
		pnlOreButton.setLayout(new FlowLayout());
		
		pnlSaveControls = new JPanel();
		pnlSaveControls.setLayout(new BorderLayout());
		pnlSaveOptions = new JPanel();
		pnlSaveOptions.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		lblMapSize = new JLabel("Map Size:");
		cmbMapSize = new JComboBox(new Integer[] {1024, 2048, 4096, 8192, 16384});
		cmbMapSize.setSelectedIndex(1);
		cmbMapSize.setEditable(false);
		
		lblSeed = new JLabel("Seed:");
		txtSeed = new JTextField("" + System.currentTimeMillis(), 10);
		lblRes = new JLabel("Res:");
		txtRes = new JTextField("" + (int) Constants.RESOLUTION, 3);
		lblIterations = new JLabel("Iterations:");
		txtIterations = new JTextField("" + (int) Constants.HEIGHTMAP_ITERATIONS, 2);
		lblMinEdge = new JLabel("Min Edge:");
		txtMinEdge = new JTextField("" + (int) Constants.MIN_EDGE, 3);
		lblBorderWeight = new JLabel("Border Weight:");
		txtBorderWeight = new JTextField("" + (int) Constants.BORDER_WEIGHT, 2);
		lblMaxHeight = new JLabel("Max Height:");
		txtMaxHeight = new JTextField("" + (int) Constants.MAP_HEIGHT, 3);
		chkLand = new JCheckBox("More Land", Constants.MORE_LAND);
		
		btnGenHeightMap = new JButton("Gen Heightmap");
		btnGenHeightMap.addActionListener(this);
		
		pnlHeightMapOptions.add(lblMapSize);
		pnlHeightMapOptions.add(cmbMapSize);
		pnlHeightMapOptions.add(lblSeed);
		pnlHeightMapOptions.add(txtSeed);
		pnlHeightMapOptions.add(lblRes);
		pnlHeightMapOptions.add(txtRes);
		pnlHeightMapOptions.add(lblIterations);
		pnlHeightMapOptions.add(txtIterations);
		pnlHeightMapOptions2.add(lblMinEdge);
		pnlHeightMapOptions2.add(txtMinEdge);
		pnlHeightMapOptions2.add(lblBorderWeight);
		pnlHeightMapOptions2.add(txtBorderWeight);
		pnlHeightMapOptions2.add(lblMaxHeight);
		pnlHeightMapOptions2.add(txtMaxHeight);
		pnlHeightMapOptions2.add(chkLand);
		
		pnlGenHeightMapButton.add(btnGenHeightMap);
		
		pnlHeightMapControls.add(pnlHeightMapOptions, BorderLayout.NORTH);
		pnlHeightMapControls.add(pnlHeightMapOptions2, BorderLayout.CENTER);
		pnlHeightMapControls.add(pnlGenHeightMapButton, BorderLayout.EAST);
		
		lblErodeIterations = new JLabel("Erosion Iterations:");
		txtErodeIterations = new JTextField("" + Constants.EROSION_ITERATIONS, 3);
		lblErodeMinSlope = new JLabel("Erosion Min Slope:");
		txtErodeMinSlope = new JTextField("" + Constants.MIN_SLOPE, 3);
		lblErodeMaxSediment = new JLabel("Max Sediment Per Iteration:");
		txtErodeMaxSediment = new JTextField("" + Constants.MAX_SEDIMENT, 3);
		
		btnErodeHeightMap = new JButton("Erode HeightMap");
		btnErodeHeightMap.addActionListener(this);
		
		pnlErodeOptions.add(lblErodeIterations);
		pnlErodeOptions.add(txtErodeIterations);
		pnlErodeOptions.add(lblErodeMinSlope);
		pnlErodeOptions.add(txtErodeMinSlope);
		pnlErodeOptions.add(lblErodeMaxSediment);
		pnlErodeOptions.add(txtErodeMaxSediment);
		
		pnlErodeButton.add(btnErodeHeightMap);
		
		pnlErodeControls.add(pnlErodeOptions, BorderLayout.WEST);
		pnlErodeControls.add(pnlErodeButton, BorderLayout.EAST);
		
		lblBiomeSeed = new JLabel("Biome Seed:");
		txtBiomeSeed = new JTextField("" + System.currentTimeMillis(), 10);
		lblDirtAmnt = new JLabel("Dirt Per Tile:");
		txtDirtAmnt = new JTextField("" + Constants.DIRT_DROP_COUNT, 3);
		lblDirtSlope = new JLabel("Max Dirt Slope:");
		txtDirtSlope = new JTextField("" + Constants.MAX_DIRT_SLOPE, 3);
		lblDirtDiagSlope = new JLabel("Max Dirt Slope (Diagonal):");
		txtDirtDiagSlope = new JTextField("" + Constants.MAX_DIRT_DIAG_SLOPE, 3);
		lblMaxDirtHeight = new JLabel("Max Dirt Height:");
		txtMaxDirtHeight = new JTextField("" + Constants.ROCK_WEIGHT, 4);
		lblWaterHeight = new JLabel("Water Height:");
		txtWaterHeight = new JTextField("" + Constants.WATER_HEIGHT, 4);
		
		btnDropDirt = new JButton("Drop Dirt");
		btnDropDirt.addActionListener(this);
		
		pnlDirtOptions.add(lblBiomeSeed);
		pnlDirtOptions.add(txtBiomeSeed);
		pnlDirtOptions.add(lblDirtAmnt);
		pnlDirtOptions.add(txtDirtAmnt);
		pnlDirtOptions.add(lblDirtSlope);
		pnlDirtOptions.add(txtDirtSlope);
		pnlDirtOptions2.add(lblDirtDiagSlope);
		pnlDirtOptions2.add(txtDirtDiagSlope);
		pnlDirtOptions2.add(lblMaxDirtHeight);
		pnlDirtOptions2.add(txtMaxDirtHeight);
		pnlDirtOptions2.add(lblWaterHeight);
		pnlDirtOptions2.add(txtWaterHeight);
		
		pnlDirtButton.add(btnDropDirt);
		
		pnlDirtControls.add(pnlDirtOptions, BorderLayout.NORTH);
		pnlDirtControls.add(pnlDirtOptions2, BorderLayout.CENTER);
		pnlDirtControls.add(pnlDirtButton, BorderLayout.EAST);
		
		btnSeedBiome = new JButton("Add Biome");
		btnSeedBiome.addActionListener(this);
		btnUndoBiome = new JButton("Undo Last Biome");
		btnUndoBiome.addActionListener(this);
		btnResetBiomes = new JButton("Reset Biomes");
		btnResetBiomes.addActionListener(this);
		
		cmbBiomeType = new JComboBox(new Tile[] { Tile.TILE_CLAY, Tile.TILE_DIRT, Tile.TILE_DIRT_PACKED, Tile.TILE_GRASS, Tile.TILE_GRAVEL, Tile.TILE_KELP,
				Tile.TILE_LAVA, Tile.TILE_MARSH, Tile.TILE_MOSS, Tile.TILE_MYCELIUM, Tile.TILE_PEAT, Tile.TILE_REED, Tile.TILE_SAND, Tile.TILE_STEPPE, 
				Tile.TILE_TAR, Tile.TILE_TUNDRA, Tile.TILE_TREE_APPLE, Tile.TILE_TREE_BIRCH, Tile.TILE_TREE_CEDAR, Tile.TILE_TREE_CHERRY, Tile.TILE_TREE_CHESTNUT, 
				Tile.TILE_TREE_FIR, Tile.TILE_TREE_LEMON, Tile.TILE_TREE_LINDEN, Tile.TILE_TREE_MAPLE, Tile.TILE_TREE_OAK, Tile.TILE_TREE_OLIVE, Tile.TILE_TREE_PINE,
				Tile.TILE_TREE_WALNUT, Tile.TILE_TREE_WILLOW, Tile.TILE_BUSH_CAMELLIA, Tile.TILE_BUSH_GRAPE, Tile.TILE_BUSH_LAVENDER, Tile.TILE_BUSH_OLEANDER,
				Tile.TILE_BUSH_ROSE, Tile.TILE_BUSH_THORN
		});
		cmbBiomeType.setSelectedIndex(12); // 12 = SAND
		cmbBiomeType.setEditable(false);
		
		lblBiomeSeedCount = new JLabel("Seed Count:");
		txtBiomeSeedCount = new JTextField("" + Constants.BIOME_SEEDS, 3);
		lblBiomeSize = new JLabel("Size:");
		txtBiomeSize = new JTextField("" + Constants.BIOME_SIZE, 3);
		lblBiomeMaxSlope = new JLabel("Max Slope:");
		txtBiomeMaxSlope = new JTextField("" + Constants.BIOME_MAX_SLOPE, 3);
		lblBiomeRate = new JLabel("Growth % N/S/E/W:");
		txtBiomeRateN = new JTextField("" + Constants.BIOME_RATE / 2, 2);
		txtBiomeRateS = new JTextField("" + (int) (Constants.BIOME_RATE * 1.3), 2);
		txtBiomeRateE = new JTextField("" + (int) (Constants.BIOME_RATE * 0.6), 2);
		txtBiomeRateW = new JTextField("" + Constants.BIOME_RATE, 2);
		lblBiomeMinHeight = new JLabel("Min Height:");
		txtBiomeMinHeight = new JTextField("" + Constants.BIOME_MIN_HEIGHT, 4);
		lblBiomeMaxHeight = new JLabel("Max Height:");
		txtBiomeMaxHeight = new JTextField("" + Constants.BIOME_MAX_HEIGHT, 4);
		
		pnlBiomeOptions.add(cmbBiomeType);
		pnlBiomeOptions.add(lblBiomeSeedCount);
		pnlBiomeOptions.add(txtBiomeSeedCount);
		pnlBiomeOptions.add(lblBiomeSize);
		pnlBiomeOptions.add(txtBiomeSize);
		pnlBiomeOptions.add(lblBiomeMaxSlope);
		pnlBiomeOptions.add(txtBiomeMaxSlope);
		pnlBiomeOptions.add(lblBiomeRate);
		pnlBiomeOptions.add(txtBiomeRateN);
		pnlBiomeOptions.add(txtBiomeRateS);
		pnlBiomeOptions.add(txtBiomeRateE);
		pnlBiomeOptions.add(txtBiomeRateW);
		pnlBiomeOptions2.add(lblBiomeMinHeight);
		pnlBiomeOptions2.add(txtBiomeMinHeight);
		pnlBiomeOptions2.add(lblBiomeMaxHeight);
		pnlBiomeOptions2.add(txtBiomeMaxHeight);
		
		pnlBiomeButton.add(btnSeedBiome);
		pnlBiomeButton.add(btnUndoBiome);
		pnlBiomeButton.add(btnResetBiomes);
		
		pnlBiomeControls.add(pnlBiomeOptions, BorderLayout.NORTH);
		pnlBiomeControls.add(pnlBiomeOptions2, BorderLayout.CENTER);
		pnlBiomeControls.add(pnlBiomeButton, BorderLayout.EAST);
		
		btnGenOres = new JButton("Generate Ores");
		btnGenOres.addActionListener(this);
		
		lblRock = new JLabel("Rock %:");
		lblIron = new JLabel("Iron %:");
		lblGold = new JLabel("Gold %:");
		lblSilver = new JLabel("Silver %:");
		lblZinc = new JLabel("Zinc %:");
		lblCopper = new JLabel("Copper %:");
		lblLead = new JLabel("Lead %:");
		lblTin = new JLabel("Tin %:");
		lblAddy = new JLabel("Addy %:");
		lblGlimmer = new JLabel("Glimmer %:");
		lblMarble = new JLabel("Marble %:");
		lblSlate = new JLabel("Slate %:");
		
		txtRock = new JTextField("" + Constants.ORE_ROCK, 3);
		txtIron = new JTextField("" + Constants.ORE_IRON, 3);
		txtGold = new JTextField("" + Constants.ORE_GOLD, 3);
		txtSilver = new JTextField("" + Constants.ORE_SILVER, 3);
		txtZinc = new JTextField("" + Constants.ORE_ZINC, 3);
		txtCopper = new JTextField("" + Constants.ORE_COPPER, 3);
		txtLead = new JTextField("" + Constants.ORE_LEAD, 3);
		txtTin = new JTextField("" + Constants.ORE_TIN, 3);
		txtAddy = new JTextField("" + Constants.ORE_ADDY, 3);
		txtGlimmer = new JTextField("" + Constants.ORE_GLIMMER, 3);
		txtMarble = new JTextField("" + Constants.ORE_MARBLE, 3);
		txtSlate = new JTextField("" + Constants.ORE_SLATE, 3);
		
		pnlOreOptions.add(lblRock);
		pnlOreOptions.add(txtRock);
		pnlOreOptions.add(lblIron);
		pnlOreOptions.add(txtIron);
		pnlOreOptions.add(lblGold);
		pnlOreOptions.add(txtGold);
		pnlOreOptions.add(lblSilver);
		pnlOreOptions.add(txtSilver);
		pnlOreOptions.add(lblZinc);
		pnlOreOptions.add(txtZinc);
		pnlOreOptions.add(lblCopper);
		pnlOreOptions.add(txtCopper);
		pnlOreOptions.add(lblLead);
		pnlOreOptions.add(txtLead);
		pnlOreOptions2.add(lblTin);
		pnlOreOptions2.add(txtTin);
		pnlOreOptions2.add(lblAddy);
		pnlOreOptions2.add(txtAddy);
		pnlOreOptions2.add(lblGlimmer);
		pnlOreOptions2.add(txtGlimmer);
		pnlOreOptions2.add(lblMarble);
		pnlOreOptions2.add(txtMarble);
		pnlOreOptions2.add(lblSlate);
		pnlOreOptions2.add(txtSlate);
		
		pnlOreButton.add(btnGenOres);
		
		pnlOreControls.add(pnlOreOptions, BorderLayout.NORTH);
		pnlOreControls.add(pnlOreOptions2, BorderLayout.CENTER);
		pnlOreControls.add(pnlOreButton, BorderLayout.EAST);
		
		btnSaveImages = new JButton("Save Image Dumps");
		btnSaveImages.addActionListener(this);
		btnSaveMap = new JButton("Save Map Files");
		btnSaveMap.addActionListener(this);
		btnShowDump = new JButton("Show Map View");
		btnShowDump.addActionListener(this);
		btnShowTopo = new JButton("Show Topo View");
		btnShowTopo.addActionListener(this);
		btnShowHeightMap = new JButton("Show Height View");
		btnShowHeightMap.addActionListener(this);
		
		pnlSaveOptions.add(btnSaveImages);
		pnlSaveOptions.add(btnSaveMap);
		pnlSaveOptions.add(btnShowDump);
		pnlSaveOptions.add(btnShowTopo);
		pnlSaveOptions.add(btnShowHeightMap);
		
		pnlSaveControls.add(pnlSaveOptions, BorderLayout.EAST);
		
		pnlControls.add(pnlHeightMapControls);
		pnlControls.add(pnlErodeControls);
		pnlControls.add(pnlDirtControls);
		pnlControls.add(pnlBiomeControls);
		pnlControls.add(pnlOreControls);
		pnlControls.add(pnlSaveControls);
		
		pnlMap = new MapPanel(width, height);
		
		this.add(pnlControls, BorderLayout.PAGE_START);
		this.add(pnlMap, BorderLayout.CENTER);
		
		this.setBounds(0, 0, width, height + 300);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public WurmAPI getAPI() {
		if (api == null)
			try {
				api = WurmAPI.create("./", (int) (Math.log(heightMap.getMapSize()) / Math.log(2)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return api;
	}
	
	public void newHeightMap(long seed, int mapSize, double resolution, int iterations, int minEdge, double borderWeight, int maxHeight, boolean moreLand) {
		heightMap = new HeightMap(seed, mapSize, resolution, iterations, minEdge, borderWeight, maxHeight, moreLand);
		heightMap.generateHeights();
		
		updateMapView(false, false);
	}
	
	public void updateMapView(boolean apiView, boolean topoView) {
		if (!apiView) {
			Graphics g = pnlMap.getMapImage().getGraphics();
			
			for (int i = 0; i < heightMap.getMapSize(); i++) {
				for (int j = 0; j < heightMap.getMapSize(); j++) {
					g.setColor(new Color((float) heightMap.getHeight(i, j), (float) heightMap.getHeight(i, j), (float) heightMap.getHeight(i, j)));
					g.fillRect(i, j, 1, 1);
				}
			}
		} else {
			updateAPIMap();
			
			if (topoView)
				pnlMap.setMapImage(getAPI().getMapData().createTopographicDump(true, (short) 250));
			else
				pnlMap.setMapImage(getAPI().getMapData().createMapDump());
		}
		
		pnlMap.repaint();
	}
	
	private void updateAPIMap() {
		MapData map = getAPI().getMapData();
		Random treeRand = new Random(System.currentTimeMillis());
		
		for (int i = 0; i < heightMap.getMapSize(); i++) {
			for (int j = 0; j < heightMap.getMapSize(); j++) {
				map.setSurfaceHeight(i, j, tileMap.getSurfaceHeight(i, j));
				map.setRockHeight(i, j, tileMap.getRockHeight(i, j));
				
				if (tileMap.getType(i, j).isTree())
					map.setTree(i, j, tileMap.getType(i, j).getTreeType((byte) 0), 
							FoliageAge.values()[treeRand.nextInt(FoliageAge.values().length)], GrowthTreeStage.MEDIUM);
				else if (tileMap.getType(i, j).isBush())
					map.setBush(i, j, tileMap.getType(i, j).getBushType((byte) 0), 
							FoliageAge.values()[treeRand.nextInt(FoliageAge.values().length)], GrowthTreeStage.MEDIUM);
				else 
					map.setSurfaceTile(i, j, tileMap.getType(i, j));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnGenHeightMap) {
			try {
				pnlMap.setMapSize((int) cmbMapSize.getSelectedItem());
				
				newHeightMap(txtSeed.getText().hashCode(), (int) cmbMapSize.getSelectedItem(), 
						Double.parseDouble(txtRes.getText()), Integer.parseInt(txtIterations.getText()), 
						Integer.parseInt(txtMinEdge.getText()), Double.parseDouble(txtBorderWeight.getText()), 
						Integer.parseInt(txtMaxHeight.getText()), chkLand.isSelected());
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Generating HeightMap", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnErodeHeightMap) {
			if (heightMap == null) {
				JOptionPane.showMessageDialog(this, "HeightMap does not exist", "Error Eroding HeightMap", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				heightMap.erode(Integer.parseInt(txtErodeIterations.getText()), Integer.parseInt(txtErodeMinSlope.getText()), 
						Integer.parseInt(txtErodeMaxSediment.getText()));
				
				updateMapView(false, false);
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Eroding HeightMap", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnDropDirt) {
			if (heightMap == null) {
				JOptionPane.showMessageDialog(this, "HeightMap does not exist", "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				tileMap = new TileMap(heightMap);
				tileMap.setBiomeSeed(txtBiomeSeed.getText().hashCode());
				tileMap.setWaterHeight(Integer.parseInt(txtWaterHeight.getText()));
				tileMap.dropDirt(Integer.parseInt(txtDirtAmnt.getText()), Integer.parseInt(txtDirtSlope.getText()), 
						Integer.parseInt(txtDirtDiagSlope.getText()), Integer.parseInt(txtMaxDirtHeight.getText()));
				
				updateMapView(true, false);
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnSeedBiome) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Adding Biome", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				double[] rates = new double[4];
				
				rates[0] = Integer.parseInt(txtBiomeRateN.getText()) / 100.0;
				rates[1] = Integer.parseInt(txtBiomeRateS.getText()) / 100.0; 
				rates[2] = Integer.parseInt(txtBiomeRateE.getText()) / 100.0; 
				rates[3] = Integer.parseInt(txtBiomeRateW.getText()) / 100.0; 
				
				tileMap.plantBiome(Integer.parseInt(txtBiomeSeedCount.getText()), Integer.parseInt(txtBiomeSize.getText()), 
						rates, Integer.parseInt(txtBiomeMaxSlope.getText()), Integer.parseInt(txtBiomeMinHeight.getText()),
						Integer.parseInt(txtBiomeMaxHeight.getText()), (Tile) cmbBiomeType.getSelectedItem());
				
				updateMapView(true, false);
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnUndoBiome) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			tileMap.undoLastBiome();
			
			updateMapView(true, false);
		}
		
		if (e.getSource() == btnResetBiomes) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			for (int i = 0; i < heightMap.getMapSize(); i++) {
				for (int j = 0; j < heightMap.getMapSize(); j++) {
					tileMap.addDirt(i, j, 0);
				}
			}
			
			updateMapView(true, false);
		}
		
		if (e.getSource() == btnGenOres) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				double[] rates = { Double.parseDouble(txtRock.getText()), Double.parseDouble(txtIron.getText()), Double.parseDouble(txtGold.getText()),
						Double.parseDouble(txtSilver.getText()), Double.parseDouble(txtZinc.getText()), Double.parseDouble(txtCopper.getText()),
						Double.parseDouble(txtLead.getText()), Double.parseDouble(txtTin.getText()), Double.parseDouble(txtAddy.getText()),
						Double.parseDouble(txtGlimmer.getText()), Double.parseDouble(txtMarble.getText()), Double.parseDouble(txtSlate.getText())					
				};
				
				double total = 0;
				for (int i = 0; i < rates.length; i++)
					total += rates[i];
				
				if (total != 100.0) {
					JOptionPane.showMessageDialog(this, "Ore totals do not match 100%: " + total, "Error Generating Ores", JOptionPane.ERROR_MESSAGE);
				} else {
					tileMap.generateOres(rates);
				}
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Generating Ores", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnShowDump) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateMapView(true, false);
		}
		
		if (e.getSource() == btnShowTopo) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateMapView(true, true);
		}
		
		if (e.getSource() == btnShowHeightMap) {
			if (heightMap == null) {
				JOptionPane.showMessageDialog(this, "HeightMap does not exist", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateMapView(false, false);
		}
		
		if (e.getSource() == btnSaveImages) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Saving Images", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			MapData map = getAPI().getMapData();
			try {
				ImageIO.write(map.createMapDump(), "png", new File("map.png"));
				ImageIO.write(map.createTopographicDump(true, (short) 250), "png", new File("topography.png"));
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
		}
		
		if (e.getSource() == btnSaveMap) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Saving Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			getAPI().getMapData().saveChanges();
		}
	}
	
	public static void main(String[] args) {
		new WGenerator(Constants.WINDOW_TITLE, Constants.WINDOW_SIZE, Constants.WINDOW_SIZE);
	}
}
