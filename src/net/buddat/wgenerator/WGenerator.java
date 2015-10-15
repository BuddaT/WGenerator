package net.buddat.wgenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wurmonline.wurmapi.api.WurmAPI;

import net.buddat.wgenerator.ui.MapPanel;
import net.buddat.wgenerator.util.Constants;

public class WGenerator extends JFrame implements ActionListener {

	private static final long serialVersionUID = 3099528642510249703L;
	
	private WurmAPI api;
	
	private HeightMap heightMap;
	
	private MapPanel pnlMap;
	
	private JPanel pnlControls, pnlHeightMapControls, pnlHeightMapOptions, pnlGenHeightMapButton;
	
	private JPanel pnlErodeControls, pnlErodeOptions, pnlErodeButton;
	
	private JLabel lblMapSize, lblSeed, lblRes, lblIterations, lblMinEdge, lblBorderWeight, lblMaxHeight;
	
	private JLabel lblErodeIterations, lblErodeMinSlope, lblErodeMaxSediment;
	
	private JTextField txtSeed, txtRes, txtIterations, txtMinEdge, txtBorderWeight, txtMaxHeight;
	
	private JTextField txtErodeIterations, txtErodeMinSlope, txtErodeMaxSediment;
	
	private JCheckBox chkLand;
	
	private JComboBox cmbMapSize;
	
	private JButton btnGenHeightMap, btnErodeHeightMap;
	
	private int mapSize = Constants.MAP_SIZE;

	@SuppressWarnings("unchecked")
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
		pnlGenHeightMapButton = new JPanel();
		pnlGenHeightMapButton.setLayout(new FlowLayout());
		
		pnlErodeControls = new JPanel();
		pnlErodeControls.setLayout(new BorderLayout());
		pnlErodeOptions = new JPanel();
		pnlErodeOptions.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlErodeButton = new JPanel();
		pnlErodeButton.setLayout(new FlowLayout());
		
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
		pnlHeightMapOptions.add(lblMinEdge);
		pnlHeightMapOptions.add(txtMinEdge);
		pnlHeightMapOptions.add(lblBorderWeight);
		pnlHeightMapOptions.add(txtBorderWeight);
		pnlHeightMapOptions.add(lblMaxHeight);
		pnlHeightMapOptions.add(txtMaxHeight);
		pnlHeightMapOptions.add(chkLand);
		
		pnlGenHeightMapButton.add(btnGenHeightMap);
		
		pnlHeightMapControls.add(pnlHeightMapOptions, BorderLayout.WEST);
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
		
		pnlControls.add(pnlHeightMapControls);
		pnlControls.add(pnlErodeControls);
		
		pnlMap = new MapPanel(width, height);
		
		this.add(pnlControls, BorderLayout.PAGE_START);
		this.add(pnlMap, BorderLayout.CENTER);
		
		this.setSize(width, height + pnlControls.getHeight());
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public void setMapSize(int newMapSize) {
		try {
            api = WurmAPI.create("./", (int) (Math.log(newMapSize) / Math.log(2)));
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
		
		pnlMap.setMapSize(newMapSize);
		
		mapSize = newMapSize;
	}
	
	public void newHeightMap(long seed, int mapSize, double resolution, int iterations, int minEdge, double borderWeight, int maxHeight, boolean moreLand) {
		heightMap = new HeightMap(seed, mapSize, resolution, iterations, minEdge, borderWeight, maxHeight, moreLand);
		heightMap.generateHeights();
		
		updateMapView();
	}
	
	public void updateMapView() {
		Graphics g = pnlMap.getMapImage().getGraphics();
		Color c;
		for (int i = 0; i < heightMap.getMapSize(); i++) {
			for (int j = 0; j < heightMap.getMapSize(); j++) {
				g.setColor(new Color((float) heightMap.getHeight(i, j), (float) heightMap.getHeight(i, j), (float) heightMap.getHeight(i, j)));
				g.fillRect(i, j, 1, 1);
			}
		}
		pnlMap.repaint();
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
				
				updateMapView();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Eroding HeightMap", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public static void main(String[] args) {
		new WGenerator(Constants.WINDOW_TITLE, Constants.WINDOW_SIZE, Constants.WINDOW_SIZE);
	}
}
