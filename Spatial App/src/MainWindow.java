import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import javax.swing.JCheckBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setTitle("Extra Credit Assignment");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private ArrayList<SpatialObject> lions;
	private ArrayList<SpatialObject> regions;
	private ArrayList<SpatialObject> ponds;
	private ArrayList<String> selected;
	
	public void getData() {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        lions = new ArrayList<>();
        regions = new ArrayList<>();
        ponds = new ArrayList<>();
		try{
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@AshwinH93:1521:XE", "ASHWIN", "ashwin");
            stmt = con.createStatement();
            String query="select lion.shape,lion.lion_id from lion";
            rs = stmt.executeQuery(query);
            while(rs.next())
            {            
	            STRUCT st = (oracle.sql.STRUCT) rs.getObject(1);
	            lions.add(new SpatialObject(JGeometry.load(st), rs.getString(2)));
            }
            
            String query2="select regions.shape,regions.region_id from regions";
            rs = stmt.executeQuery(query2);
            while (rs.next())
            {
            	STRUCT st = (oracle.sql.STRUCT) rs.getObject(1);
	            regions.add(new SpatialObject(JGeometry.load(st), rs.getString(2)));
            }
            String query3="select pond.shape,pond.pond_id,pond.r from pond";
            rs = stmt.executeQuery(query3);
            while(rs.next())
            {
            	STRUCT st = (oracle.sql.STRUCT) rs.getObject(1);
	            ponds.add(new SpatialObject(JGeometry.load(st), rs.getString(2), rs.getInt(3)));
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try{
                stmt.close();
                con.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		selected = new ArrayList<>();
		getData();
		
		JPanel panel = new JPanel() {
			public void paintComponent(Graphics g)
		    {
		        super.paintComponent(g);
		        Graphics2D g2=(Graphics2D) g;
		        this.setBackground(Color.WHITE);
		        int coord_x[] = new int[50];
		        int coord_y[] = new int[50];
		        
		        // Lions
		        for (SpatialObject lion : lions) {
		        	JGeometry j_geom = lion.geometry;
		        	if (selected.contains(lion.id)){
		        		g2.setColor(Color.RED);
		        	} else {
		        		g2.setColor(Color.GREEN);
		        	}					
					g2.drawLine((int)j_geom.getPoint()[0], (int)j_geom.getPoint()[1], (int)j_geom.getPoint()[0], (int)j_geom.getPoint()[1]);
					g2.setStroke(new BasicStroke(5));
					g2.setColor(new Color(200,100,100));
					String str = lion.id+"("+(int)j_geom.getPoint()[0]+","+(int)j_geom.getPoint()[1]+")";
					g2.drawString(str,(int)j_geom.getPoint()[0]-5,(int)j_geom.getPoint()[1]-5);
		        }
		        
		        // Regions
		        for (SpatialObject region : regions) {
		        	JGeometry j_geom = region.geometry;
		        	for(int i=0;i<(j_geom.getOrdinatesArray().length/2);i++)
					{
						coord_x[i]=(int)j_geom.getOrdinatesArray()[i*2];
						coord_y[i]=(int)j_geom.getOrdinatesArray()[i*2+1];
					}
					g2.setColor(Color.BLACK);
					Polygon p = new Polygon(coord_x, coord_y, 5);
					g2.drawPolygon(p);
							                
					int TextX = (coord_x[0]+coord_x[2])/2;
					int TextY = (coord_y[0]+coord_y[2])/2;
					g2.drawString(region.id, TextX-10, TextY-5);
		        }
		        
		        // Ponds
		        for (SpatialObject pond : ponds) {
		        	JGeometry j_geom = pond.geometry;
		        	int bottomLeftX=(int) j_geom.getOrdinatesArray()[0];
	                int bottomLeftY= (int)j_geom.getOrdinatesArray()[1]-pond.radius;
	                int radius = pond.radius;
	                if (selected.contains(pond.id)){
		        		g2.setColor(Color.RED);
		        	} else {
		        		g2.setColor(Color.BLUE);
		        	}
	                g2.fillOval(bottomLeftX, bottomLeftY, radius*2, radius*2);
	                g2.setColor(Color.BLACK);
	                g2.drawOval(bottomLeftX, bottomLeftY, radius*2, radius*2);
	                int cx=bottomLeftX+pond.radius;
	                int cy=bottomLeftY+pond.radius;
	                String str=pond.id+"("+cx+","+cy+")";
	                g2.setColor(Color.ORANGE);
	                
	                g2.drawString(str, cx, cy);
		        }
		    }
		};
		
		JCheckBox chckbxShowLionsAnd = new JCheckBox("Show lions and ponds");
		chckbxShowLionsAnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selected.clear();
				panel.repaint();
			}
		});
		chckbxShowLionsAnd.setBounds(588, 95, 215, 23);
		contentPane.add(chckbxShowLionsAnd);
		
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!chckbxShowLionsAnd.isSelected())
					return;
				String regionId = "";
				selected = new ArrayList<>();
				for (SpatialObject region : regions) {
					JGeometry j_geom = region.geometry;
					int coord_x[]=new int[50];
			        int coord_y[]=new int[50];
					for(int i=0;i<(j_geom.getOrdinatesArray().length/2);i++)
					{
						coord_x[i]=(int)j_geom.getOrdinatesArray()[i*2];
						coord_y[i]=(int)j_geom.getOrdinatesArray()[i*2+1];
					}
						                
					Polygon p = new Polygon(coord_x, coord_y, 5);
					if (p.contains(arg0.getX(), arg0.getY())) {
						regionId = region.id;
						break;
					}
				}
				
				if (regionId.isEmpty())
					return;
				
				Connection con = null;
		        Statement stmt = null;
		        ResultSet rs = null;
				String query="select lion.lion_id from lion where lion.lion_id  in (select lion.lion_id from lion,regions where sdo_contains(regions.shape,lion.shape)='TRUE' and regions.region_id="+"'"+regionId+"')";
	            try
	            {
	            	Class.forName("oracle.jdbc.OracleDriver");
	                con = DriverManager.getConnection("jdbc:oracle:thin:@AshwinH93:1521:XE", "ASHWIN", "ashwin");
	                stmt = con.createStatement();
					rs = stmt.executeQuery(query);
		            while(rs.next()){
		            	selected.add(rs.getString(1));
		            }
		            
		            query="select pond.pond_id from pond where pond.pond_id  in (select pond.pond_id from pond,regions where sdo_contains(regions.shape,pond.shape)='TRUE' and regions.region_id="+"'"+regionId+"')";
		            rs = stmt.executeQuery(query);
		            while(rs.next()){
		            	selected.add(rs.getString(1));
		            }
	            } catch (SQLException | ClassNotFoundException e) {
		        	e.printStackTrace();
		        } finally {
		        	try {
						stmt.close();
			        	con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
		        }
	            
	            for (String s : selected) {
	            	System.out.println("Selected " + s);
	            }
	            
	            panel.repaint();
			}
		});
		
		panel.setBounds(44, 24, 538, 542);
		contentPane.add(panel);
	}
}
