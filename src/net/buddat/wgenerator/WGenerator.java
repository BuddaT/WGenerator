package net.buddat.wgenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.GrassData.GrowthTreeStage;
import com.wurmonline.mesh.Tiles.Tile;
import com.wurmonline.wurmapi.api.MapData;
import com.wurmonline.wurmapi.api.WurmAPI;

import net.buddat.wgenerator.ui.MapPanel;
import net.buddat.wgenerator.util.Constants;

public class WGenerator extends JFrame implements ActionListener, FocusListener {

	private static final long serialVersionUID = 3099528642510249703L;
	
	private static final Logger logger = Logger.getLogger(WGenerator.class.getName());
	
	private WurmAPI api;
	
	private HeightMap heightMap;
	
	private TileMap tileMap;
	
	private MapPanel pnlMap;
	
	private JPanel pnlControls, pnlHeightMapControls, pnlHeightMapOptions, pnlHeightMapOptions2, pnlHeightMapOptions3, 
			pnlHeightMapOptionsMiddle, pnlHeightMapOptionsMiddleTop, pnlHeightMapOptionsMiddleBottom;
	private JPanel pnlErodeControls, pnlErodeOptions, pnlErodeOptions2, pnlErodeButton;
	private JPanel pnlDirtControls, pnlDirtOptions, pnlDirtOptions2, pnlDirtOptions3, pnlDirtOptionsMiddle, pnlDirtButton;
	private JPanel pnlBiomeControls, pnlBiomeOptions, pnlBiomeOptions2, pnlBiomeOptions3, pnlBiomeOptionsMiddle, pnlBiomeButton;
	private JPanel pnlOreControls, pnlOreOptions, pnlOreOptions2, pnlOreOptions3, pnlOreOptions4, pnlOreOptionsMiddle, pnlOreButton;
	private JPanel pnlSaveControls, pnlSaveOptions;
	
	private JLabel lblMapSize, lblSeed, lblRes, lblIterations, lblMinEdge, lblBorderWeight, lblMaxHeight;
	private JLabel lblErodeIterations, lblErodeMinSlope, lblErodeMaxSediment;
	private JLabel lblBiomeSeed, lblDirtAmnt, lblDirtSlope, lblDirtDiagSlope, lblMaxDirtHeight, lblWaterHeight;
	private JLabel lblBiomeSeedCount, lblBiomeSize, lblBiomeMaxSlope, lblBiomeRate, lblBiomeMinHeight, lblBiomeMaxHeight;
	private JLabel lblRock, lblIron, lblGold, lblSilver, lblZinc, lblCopper, lblLead, lblTin, lblAddy, lblGlimmer, lblMarble, lblSlate;
	
	private JTextField txtSeed, txtRes, txtIterations, txtMinEdge, txtBorderWeight, txtMaxHeight;
	private JTextField txtErodeIterations, txtErodeMinSlope, txtErodeMaxSediment;
	private JTextField txtBiomeSeed, txtDirtAmnt, txtDirtSlope, txtDirtDiagSlope, txtMaxDirtHeight, txtWaterHeight;
	private JTextField txtBiomeSeedCount, txtBiomeSize, txtBiomeMaxSlope, txtBiomeRateN, txtBiomeRateS, txtBiomeRateE, txtBiomeRateW, 
			txtBiomeMinHeight, txtBiomeMaxHeight;
	private JTextField txtRock, txtIron, txtGold, txtSilver, txtZinc, txtCopper, txtLead, txtTin, txtAddy, txtGlimmer, txtMarble, txtSlate;
	
	private JCheckBox chkLand;
	
	@SuppressWarnings("rawtypes")
	private JComboBox cmbMapSize, cmbBiomeType;
	private Border compound, raisedbevel, loweredbevel;
	
	private JButton btnGenHeightMap, btnErodeHeightMap, btnDropDirt, btnSeedBiome, btnUndoBiome, btnResetBiomes, btnGenOres;
	private JButton btnResetHeightSeed, btnResetBiomeSeed;
	private JButton btnSaveActions, btnLoadActions, btnSaveImages, btnSaveMap, btnShowDump, btnShowTopo, btnShowCave, btnShowHeightMap;
	
	private ArrayList<String> genHistory;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WGenerator(String title, int width, int height) {
		super(title);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		compound = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
		
		pnlControls = new JPanel();
		pnlControls.setLayout(new BoxLayout(pnlControls, BoxLayout.Y_AXIS));
		pnlControls.setPreferredSize(new Dimension(400, 768));
		pnlControls.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		pnlHeightMapControls = new JPanel();
		pnlHeightMapControls.setLayout(new BorderLayout());
		pnlHeightMapControls.setPreferredSize(new Dimension(380, 75));
		pnlHeightMapControls.setBorder(compound);
		pnlHeightMapOptions = new JPanel();
		pnlHeightMapOptions.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlHeightMapOptions2 = new JPanel();
		pnlHeightMapOptions2.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlHeightMapOptionsMiddle = new JPanel();
		pnlHeightMapOptionsMiddle.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlHeightMapOptions3 = new JPanel();
		pnlHeightMapOptions3.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlHeightMapOptionsMiddleTop = new JPanel();
		pnlHeightMapOptionsMiddleTop.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlHeightMapOptionsMiddleBottom = new JPanel();
		pnlHeightMapOptionsMiddleBottom.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		pnlErodeControls = new JPanel();
		pnlErodeControls.setLayout(new BorderLayout());
		pnlErodeControls.setPreferredSize(new Dimension(380, 30));
		pnlErodeControls.setBorder(compound);
		pnlErodeOptions = new JPanel();
		pnlErodeOptions.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlErodeOptions2 = new JPanel();
		pnlErodeOptions2.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlErodeButton = new JPanel();
		pnlErodeButton.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		pnlDirtControls = new JPanel();
		pnlDirtControls.setLayout(new BorderLayout());
		pnlDirtControls.setPreferredSize(new Dimension(380, 75));
		pnlDirtControls.setBorder(compound);
		pnlDirtOptions = new JPanel();
		pnlDirtOptions.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlDirtOptions2 = new JPanel();
		pnlDirtOptions2.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlDirtOptions3 = new JPanel();
		pnlDirtOptions3.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlDirtOptionsMiddle = new JPanel();
		pnlDirtOptionsMiddle.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlDirtButton = new JPanel();
		pnlDirtButton.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		pnlBiomeControls = new JPanel();
		pnlBiomeControls.setLayout(new BorderLayout());
		pnlBiomeControls.setPreferredSize(new Dimension(380, 75));
		pnlBiomeControls.setBorder(compound);
		pnlBiomeOptions = new JPanel();
		pnlBiomeOptions.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlBiomeOptions2 = new JPanel();
		pnlBiomeOptions2.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlBiomeOptions3 = new JPanel();
		pnlBiomeOptions3.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlBiomeOptionsMiddle = new JPanel();
		pnlBiomeOptionsMiddle.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlBiomeButton = new JPanel();
		pnlBiomeButton.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		pnlOreControls = new JPanel();
		pnlOreControls.setLayout(new BorderLayout());
		pnlOreControls.setPreferredSize(new Dimension(380, 110));
		pnlOreControls.setBorder(compound);
		pnlOreOptions = new JPanel();
		pnlOreOptions.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlOreOptions2 = new JPanel();
		pnlOreOptions2.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlOreOptions3 = new JPanel();
		pnlOreOptions3.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlOreOptions4 = new JPanel();
		pnlOreOptions4.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlOreOptionsMiddle = new JPanel();
		pnlOreOptionsMiddle.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlOreButton = new JPanel();
		pnlOreButton.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		pnlSaveControls = new JPanel();
		pnlSaveControls.setLayout(new BoxLayout(pnlSaveControls, BoxLayout.Y_AXIS));
		pnlSaveOptions = new JPanel();
		pnlSaveOptions.setLayout(new FlowLayout(FlowLayout.CENTER));
		
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
		
		btnResetHeightSeed = new JButton("#");
		btnResetHeightSeed.addActionListener(this);
		btnGenHeightMap = new JButton("Gen Heightmap");
		btnGenHeightMap.addActionListener(this);
		
		pnlHeightMapOptions.add(lblMapSize);
		pnlHeightMapOptions.add(cmbMapSize);
		pnlHeightMapOptions.add(lblSeed);
		pnlHeightMapOptions.add(txtSeed);
		pnlHeightMapOptions.add(btnResetHeightSeed);
		pnlHeightMapOptionsMiddleTop.add(lblRes);
		pnlHeightMapOptionsMiddleTop.add(txtRes);
		pnlHeightMapOptionsMiddleTop.add(lblIterations);
		pnlHeightMapOptionsMiddleTop.add(txtIterations);
		pnlHeightMapOptionsMiddleBottom.add(lblMinEdge);
		pnlHeightMapOptionsMiddleBottom.add(txtMinEdge);
		pnlHeightMapOptionsMiddleBottom.add(lblBorderWeight);
		pnlHeightMapOptionsMiddleBottom.add(txtBorderWeight);
		pnlHeightMapOptionsMiddleBottom.add(lblMaxHeight);
		pnlHeightMapOptionsMiddleBottom.add(txtMaxHeight);
		pnlHeightMapOptions3.add(chkLand);
		pnlHeightMapOptions3.add(btnGenHeightMap);
		
		pnlHeightMapOptionsMiddle.add(pnlHeightMapOptionsMiddleTop);
		pnlHeightMapOptionsMiddle.add(pnlHeightMapOptionsMiddleBottom);
		
		pnlHeightMapControls.add(pnlHeightMapOptions, BorderLayout.NORTH);
		pnlHeightMapControls.add(pnlHeightMapOptionsMiddle, BorderLayout.CENTER);
		pnlHeightMapControls.add(pnlHeightMapOptions3,  BorderLayout.SOUTH);
		
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
		pnlErodeOptions2.add(lblErodeMaxSediment);
		pnlErodeOptions2.add(txtErodeMaxSediment);
		
		pnlErodeButton.add(btnErodeHeightMap);
		
		pnlErodeControls.add(pnlErodeOptions, BorderLayout.NORTH);
		pnlErodeControls.add(pnlErodeOptions2, BorderLayout.CENTER);
		pnlErodeControls.add(pnlErodeButton, BorderLayout.SOUTH);
		
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
		
		btnResetBiomeSeed = new JButton("#");
		btnResetBiomeSeed.addActionListener(this);
		btnDropDirt = new JButton("Drop Dirt");
		btnDropDirt.addActionListener(this);
		
		pnlDirtOptions.add(lblBiomeSeed);
		pnlDirtOptions.add(txtBiomeSeed);
		pnlDirtOptions.add(btnResetBiomeSeed);
		pnlDirtOptions.add(lblDirtAmnt);
		pnlDirtOptions.add(txtDirtAmnt);
		pnlDirtOptions2.add(lblDirtSlope);
		pnlDirtOptions2.add(txtDirtSlope);
		pnlDirtOptions2.add(lblDirtDiagSlope);
		pnlDirtOptions2.add(txtDirtDiagSlope);
		pnlDirtOptions3.add(lblMaxDirtHeight);
		pnlDirtOptions3.add(txtMaxDirtHeight);
		pnlDirtOptions3.add(lblWaterHeight);
		pnlDirtOptions3.add(txtWaterHeight);
		
		pnlDirtOptionsMiddle.add(pnlDirtOptions2);
		pnlDirtOptionsMiddle.add(pnlDirtOptions3);
		
		pnlDirtButton.add(btnDropDirt);
		
		pnlDirtControls.add(pnlDirtOptions, BorderLayout.NORTH);
		pnlDirtControls.add(pnlDirtOptionsMiddle, BorderLayout.CENTER);
		pnlDirtControls.add(pnlDirtButton, BorderLayout.SOUTH);
		
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
		pnlBiomeOptions2.add(lblBiomeMaxSlope);
		pnlBiomeOptions2.add(txtBiomeMaxSlope);
		pnlBiomeOptions3.add(lblBiomeRate);
		pnlBiomeOptions3.add(txtBiomeRateN);
		pnlBiomeOptions3.add(txtBiomeRateS);
		pnlBiomeOptions3.add(txtBiomeRateE);
		pnlBiomeOptions3.add(txtBiomeRateW);
		pnlBiomeOptions2.add(lblBiomeMinHeight);
		pnlBiomeOptions2.add(txtBiomeMinHeight);
		pnlBiomeOptions2.add(lblBiomeMaxHeight);
		pnlBiomeOptions2.add(txtBiomeMaxHeight);
		
		pnlBiomeOptionsMiddle.add(pnlBiomeOptions2);
		pnlBiomeOptionsMiddle.add(pnlBiomeOptions3);
		
		pnlBiomeButton.add(btnSeedBiome);
		pnlBiomeButton.add(btnUndoBiome);
		pnlBiomeButton.add(btnResetBiomes);
		
		pnlBiomeControls.add(pnlBiomeOptions, BorderLayout.NORTH);
		pnlBiomeControls.add(pnlBiomeOptionsMiddle, BorderLayout.CENTER);
		pnlBiomeControls.add(pnlBiomeButton, BorderLayout.SOUTH);
		
		btnGenOres = new JButton("Generate Ores");
		btnGenOres.addActionListener(this);
		
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
		lblRock = new JLabel("Rock %:");
		
		txtIron = new JTextField("" + Constants.ORE_IRON, 3);
		txtIron.addFocusListener(this);
		txtGold = new JTextField("" + Constants.ORE_GOLD, 3);
		txtGold.addFocusListener(this);
		txtSilver = new JTextField("" + Constants.ORE_SILVER, 3);
		txtSilver.addFocusListener(this);
		txtZinc = new JTextField("" + Constants.ORE_ZINC, 3);
		txtZinc.addFocusListener(this);
		txtCopper = new JTextField("" + Constants.ORE_COPPER, 3);
		txtCopper.addFocusListener(this);
		txtLead = new JTextField("" + Constants.ORE_LEAD, 3);
		txtLead.addFocusListener(this);
		txtTin = new JTextField("" + Constants.ORE_TIN, 3);
		txtTin.addFocusListener(this);
		txtAddy = new JTextField("" + Constants.ORE_ADDY, 3);
		txtAddy.addFocusListener(this);
		txtGlimmer = new JTextField("" + Constants.ORE_GLIMMER, 3);
		txtGlimmer.addFocusListener(this);
		txtMarble = new JTextField("" + Constants.ORE_MARBLE, 3);
		txtMarble.addFocusListener(this);
		txtSlate = new JTextField("" + Constants.ORE_SLATE, 3);
		txtSlate.addFocusListener(this);
		txtRock = new JTextField("" + Constants.ORE_ROCK, 3);
		txtRock.setEditable(false);
		
		pnlOreOptions.add(lblIron);
		pnlOreOptions.add(txtIron);
		pnlOreOptions.add(lblGold);
		pnlOreOptions.add(txtGold);
		pnlOreOptions.add(lblSilver);
		pnlOreOptions.add(txtSilver);
		pnlOreOptions2.add(lblZinc);
		pnlOreOptions2.add(txtZinc);
		pnlOreOptions2.add(lblCopper);
		pnlOreOptions2.add(txtCopper);
		pnlOreOptions2.add(lblLead);
		pnlOreOptions2.add(txtLead);
		pnlOreOptions3.add(lblTin);
		pnlOreOptions3.add(txtTin);
		pnlOreOptions4.add(lblAddy);
		pnlOreOptions4.add(txtAddy);
		pnlOreOptions4.add(lblGlimmer);
		pnlOreOptions4.add(txtGlimmer);
		pnlOreOptions3.add(lblMarble);
		pnlOreOptions3.add(txtMarble);
		pnlOreOptions3.add(lblSlate);
		pnlOreOptions3.add(txtSlate);
		pnlOreOptions4.add(lblRock);
		pnlOreOptions4.add(txtRock);
		
		pnlOreOptionsMiddle.add(pnlOreOptions2);
		pnlOreOptionsMiddle.add(pnlOreOptions3);
		pnlOreOptionsMiddle.add(pnlOreOptions4);
		
		pnlOreButton.add(btnGenOres);
		
		pnlOreControls.add(pnlOreOptions, BorderLayout.NORTH);
		pnlOreControls.add(pnlOreOptionsMiddle, BorderLayout.CENTER);
		pnlOreControls.add(pnlOreButton, BorderLayout.SOUTH);
		
		btnSaveActions = new JButton("Save Actions");
		btnSaveActions.addActionListener(this);
		btnLoadActions = new JButton("Load Actions");
		btnLoadActions.addActionListener(this);
		btnSaveImages = new JButton("Save Image Dumps");
		btnSaveImages.addActionListener(this);
		btnSaveMap = new JButton("Save Map Files");
		btnSaveMap.addActionListener(this);
		btnShowDump = new JButton("Show Map View");
		btnShowDump.addActionListener(this);
		btnShowTopo = new JButton("Show Topo View");
		btnShowTopo.addActionListener(this);
		btnShowCave = new JButton("Show Cave View");
		btnShowCave.addActionListener(this);
		btnShowHeightMap = new JButton("Show Height View");
		btnShowHeightMap.addActionListener(this);
		
		pnlSaveOptions.add(btnSaveActions);
		pnlSaveOptions.add(btnLoadActions);
		pnlSaveOptions.add(btnSaveImages);
		pnlSaveOptions.add(btnSaveMap);
		pnlSaveOptions.add(btnShowDump);
		pnlSaveOptions.add(btnShowTopo);
		pnlSaveOptions.add(btnShowCave);
		pnlSaveOptions.add(btnShowHeightMap);
		
		pnlSaveControls.add(pnlSaveOptions, BorderLayout.CENTER);
		pnlSaveControls.setPreferredSize(new Dimension(1000, 35));
		
		pnlControls.add(pnlHeightMapControls);
		pnlControls.add(pnlErodeControls);
		pnlControls.add(pnlDirtControls);
		pnlControls.add(pnlBiomeControls);
		pnlControls.add(pnlOreControls);
		
		pnlMap = new MapPanel(width, height);
		pnlMap.setBackground(Color.BLACK);
		
		this.add(pnlControls, BorderLayout.EAST);
		this.add(pnlMap, BorderLayout.CENTER);
		this.add(pnlSaveControls, BorderLayout.SOUTH);
		
		this.setResizable(false);
		this.setBounds(0, 0, width + 400, height + 35);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public WurmAPI getAPI() {
		if (api == null)
			try {
				api = WurmAPI.create("./maps/" + heightMap.getSeed() + "/", (int) (Math.log(heightMap.getMapSize()) / Math.log(2)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return api;
	}
	
	public void newHeightMap(long seed, int mapSize, double resolution, int iterations, int minEdge, double borderWeight, int maxHeight, boolean moreLand) {
		heightMap = new HeightMap(seed, mapSize, resolution, iterations, minEdge, borderWeight, maxHeight, moreLand);
		heightMap.generateHeights();
		
		updateMapView(false, 0);
	}
	
	public void updateMapView(boolean apiView, int viewType) {
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
			
			if (viewType == 1)
				pnlMap.setMapImage(getAPI().getMapData().createTopographicDump(true, (short) 250));
			else if (viewType == 2)
				pnlMap.setMapImage(getAPI().getMapData().createCaveDump(true));
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
				
				if (tileMap.hasOres())
					map.setCaveTile(i, j, tileMap.getOreType(i, j), tileMap.getOreCount(i, j));
				
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
	
	private void parseAction(String action) {
		String[] parts = action.split(":");
		if (parts.length < 2)
			return;
		
		String[] options = parts[1].split(",");		
		switch (parts[0]) {
			case "HEIGHTMAP":
				if (options.length < 8) {
					JOptionPane.showMessageDialog(this, "Not enough options for HEIGHTMAP", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					txtSeed.setText(options[0]);
					cmbMapSize.setSelectedIndex(Integer.parseInt(options[1]));
					txtRes.setText(options[2]);
					txtIterations.setText(options[3]);
					txtMinEdge.setText(options[4]);
					txtBorderWeight.setText(options[5]);
					txtMaxHeight.setText(options[6]);
					chkLand.setSelected(Boolean.parseBoolean(options[7]));
					
					btnGenHeightMap.doClick();
				} catch (Exception nfe) {
					JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				}
				break;
			case "ERODE":
				if (options.length < 3) {
					JOptionPane.showMessageDialog(this, "Not enough options for ERODE", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				txtErodeIterations.setText(options[0]);
				txtErodeMinSlope.setText(options[1]);
				txtErodeMaxSediment.setText(options[2]);
					
				btnErodeHeightMap.doClick();
				break;
			case "DROPDIRT":
				if (options.length < 6) {
					JOptionPane.showMessageDialog(this, "Not enough options for DROPDIRT", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				txtBiomeSeed.setText(options[0]);
				txtWaterHeight.setText(options[1]);
				txtDirtAmnt.setText(options[2]);
				txtDirtSlope.setText(options[3]);
				txtDirtDiagSlope.setText(options[4]);
				txtMaxDirtHeight.setText(options[5]);
					
				btnDropDirt.doClick();
				break;
			case "UNDOBIOME":
				btnUndoBiome.doClick();
				break;
			case "RESETBIOMES":
				btnResetBiomes.doClick();
				break;
			case "GENORES":
				if (options.length < 12) {
					JOptionPane.showMessageDialog(this, "Not enough options for GENORES", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				txtRock.setText(options[0]);
				txtIron.setText(options[1]);
				txtGold.setText(options[2]);
				txtSilver.setText(options[3]);
				txtZinc.setText(options[4]);
				txtCopper.setText(options[5]);
				txtLead.setText(options[6]);
				txtTin.setText(options[7]);
				txtAddy.setText(options[8]);
				txtGlimmer.setText(options[9]);
				txtMarble.setText(options[10]);
				txtSlate.setText(options[11]);
					
				btnGenOres.doClick();
				break;
			default:
				if(parts[0].startsWith("SEEDBIOME")){
                                    if (options.length < 10) {
                                            JOptionPane.showMessageDialog(this, "Not enough options for SEEDBIOME", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
                                            return;
                                    }

                                    try {
                                            cmbBiomeType.setSelectedIndex(Integer.parseInt(options[0]));
                                            txtBiomeSeedCount.setText(options[1]);
                                            txtBiomeSize.setText(options[2]);
                                            txtBiomeMaxSlope.setText(options[3]);
                                            txtBiomeRateN.setText(options[4]);
                                            txtBiomeRateS.setText(options[5]);
                                            txtBiomeRateE.setText(options[6]);
                                            txtBiomeRateW.setText(options[7]);
                                            txtBiomeMinHeight.setText(options[8]);
                                            txtBiomeMaxHeight.setText(options[9]);

                                            btnSeedBiome.doClick();
                                    } catch (Exception nfe) {
                                            JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                        break;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnGenHeightMap) {
			try {
				api = null;
				genHistory = new ArrayList<String>();
				
				pnlMap.setMapSize((int) cmbMapSize.getSelectedItem());
				
				newHeightMap(txtSeed.getText().hashCode(), (int) cmbMapSize.getSelectedItem(), 
						Double.parseDouble(txtRes.getText()), Integer.parseInt(txtIterations.getText()), 
						Integer.parseInt(txtMinEdge.getText()), Double.parseDouble(txtBorderWeight.getText()), 
						Integer.parseInt(txtMaxHeight.getText()), chkLand.isSelected());
				
				genHistory.add("HEIGHTMAP:" + txtSeed.getText() + "," + cmbMapSize.getSelectedIndex() + "," + txtRes.getText() + "," +
						txtIterations.getText() + "," + txtMinEdge.getText() + "," + txtBorderWeight.getText() + "," +
						txtMaxHeight.getText() + "," + chkLand.isSelected());
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
				
				updateMapView(false, 0);
				
				genHistory.add("ERODE:" + txtErodeIterations.getText() + "," + txtErodeMinSlope.getText() + "," + txtErodeMaxSediment.getText());
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
				
				updateMapView(true, 0);
				
				genHistory.add("DROPDIRT:" + txtBiomeSeed.getText() + "," + txtWaterHeight.getText() + "," + txtDirtAmnt.getText() + "," +
						txtDirtSlope.getText() + "," + txtDirtDiagSlope.getText() + "," + txtMaxDirtHeight.getText());
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
				
				updateMapView(true, 0);
				
				genHistory.add("SEEDBIOME("+cmbBiomeType.getSelectedItem()+"):" + cmbBiomeType.getSelectedIndex() + "," + txtBiomeSeedCount.getText() + "," + txtBiomeSize.getText() + "," +
						txtBiomeMaxSlope.getText() + "," + txtBiomeRateN.getText() + "," + txtBiomeRateS.getText() + "," +
						txtBiomeRateE.getText() + "," + txtBiomeRateW.getText() + "," + txtBiomeMinHeight.getText() + "," +
						txtBiomeMaxHeight.getText());
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
			
			updateMapView(true, 0);
			
			genHistory.add("UNDOBIOME:null");
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
			
			updateMapView(true, 0);
			
			genHistory.add("RESETBIOMES:null");
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
				
				tileMap.generateOres(rates);
				
				updateAPIMap();
				
				updateMapView(true, 2);
				
				genHistory.add("GENORES:" + txtRock.getText() + "," + txtIron.getText() + "," + txtGold.getText() + "," +
						txtSilver.getText() + "," + txtZinc.getText() + "," + txtCopper.getText() + "," +
						txtLead.getText() + "," + txtTin.getText() + "," + txtAddy.getText() + "," +
						txtGlimmer.getText() + "," + txtMarble.getText() + "," + txtSlate.getText());
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Generating Ores", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnShowDump) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateMapView(true, 0);
		}
		
		if (e.getSource() == btnShowTopo) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateMapView(true, 1);
		}
		
		if (e.getSource() == btnShowCave) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (!tileMap.hasOres()) {
				JOptionPane.showMessageDialog(this, "No Cave Map - Generate Ores first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateMapView(true, 2);
		}
		
		if (e.getSource() == btnShowHeightMap) {
			if (heightMap == null) {
				JOptionPane.showMessageDialog(this, "HeightMap does not exist", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateMapView(false, 0);
		}
		
		if (e.getSource() == btnSaveImages) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Saving Images", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateAPIMap();
			
			MapData map = getAPI().getMapData();
			try {
				ImageIO.write(map.createMapDump(), "png", new File("./maps/" + heightMap.getSeed() + "/map.png"));
				ImageIO.write(map.createTopographicDump(true, (short) 250), "png", new File("./maps/" + heightMap.getSeed() + "/topography.png"));
				ImageIO.write(map.createCaveDump(true), "png", new File("./maps/" + heightMap.getSeed() + "/cave.png"));
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
		}
		
		if (e.getSource() == btnSaveMap) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Saving Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateAPIMap();
			getAPI().getMapData().saveChanges();
			getAPI().close();
			
			api = null;
			updateAPIMap();
		}
		
		if (e.getSource() == btnSaveActions) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Saving Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				File actionsFile = new File("./maps/" + heightMap.getSeed() + "/map_actions.txt");
				actionsFile.createNewFile();
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(actionsFile));
				for (String s : genHistory)
					bw.write(s + "\r\n");
				
				bw.close();
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
		}
		
		if (e.getSource() == btnLoadActions) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Saving Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				File actionsFile = new File("./maps/" + heightMap.getSeed() + "/map_actions.txt");
				
				BufferedReader br = new BufferedReader(new FileReader(actionsFile));
				String line;
				while ((line = br.readLine()) != null) {
					parseAction(line);
				}
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
		}
		
		if (e.getSource() == btnResetHeightSeed) {
			txtSeed.setText("" + System.currentTimeMillis());
		}
		
		if (e.getSource() == btnResetBiomeSeed) {
			txtBiomeSeed.setText("" + System.currentTimeMillis());
		}
	}
	
	public static void main(String[] args) {
		new WGenerator(Constants.WINDOW_TITLE, Constants.WINDOW_SIZE, Constants.WINDOW_SIZE);
	}

	@Override
	public void focusGained(FocusEvent e) {
		// Do Nothing
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() instanceof JTextField) {
			try {
				double[] rates = { Double.parseDouble(txtIron.getText()), Double.parseDouble(txtGold.getText()),
						Double.parseDouble(txtSilver.getText()), Double.parseDouble(txtZinc.getText()), Double.parseDouble(txtCopper.getText()),
						Double.parseDouble(txtLead.getText()), Double.parseDouble(txtTin.getText()), Double.parseDouble(txtAddy.getText()),
						Double.parseDouble(txtGlimmer.getText()), Double.parseDouble(txtMarble.getText()), Double.parseDouble(txtSlate.getText())					
				};
				
				double total = 0;
				for (int i = 0; i < rates.length; i++)
					total += rates[i];
				
				txtRock.setText("" + (100.0 - total));
			} catch (NumberFormatException nfe) {
			
			}
		}
	}
}
