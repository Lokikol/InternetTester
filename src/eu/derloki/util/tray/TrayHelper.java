package eu.derloki.util.tray;



import java.awt.AWTException;
import java.awt.Component;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;






public class TrayHelper {

	private TrayIcon tray;
	private SystemTray sTray;
	private JPopupMenu pop;
	
	private HashMap<String, Component> componentList;
	
	
	public TrayHelper(String imagePath,String toolTip){
		this(imagePath,toolTip,null);
	
	}
	public TrayHelper(String imagePath,String toolTip,Consumer<MouseEvent> trayLeftClick){
		
		componentList = new HashMap<String, Component>();
		
		sTray = SystemTray.getSystemTray();
		tray = new TrayIcon(getImage(imagePath), toolTip,null);
		tray.setImageAutoSize(true);
		
		pop = new JPopupMenu("");
				
		
		//tray.setPopupMenu(pop);
		
		MouseAdapter mouseAdapter = new MouseAdapter() {
	        public void mouseReleased(MouseEvent e) {
	            if (e.isPopupTrigger()) {
	        	pop.setInvoker(pop);   
	        	pop.setLocation(e.getX(), e.getY() - pop.getHeight());
                pop.setVisible(true);
	                
	               // System.out.println(e.getY()-pop.getHeight() + " - "+ pop.getLocationOnScreen().y);
	            }
	            
	            if(e.getButton() == MouseEvent.BUTTON1){
	            	if(trayLeftClick != null){
	            		trayLeftClick.accept(e);
	            	}
	            	else{
	            		pop.setInvoker(pop);   
	    	        	pop.setLocation(e.getX(), e.getY() - pop.getHeight());
	                    pop.setVisible(true);
	            	}
	            }
	            
	        }
	    };
	    
	  pop.addMouseListener(new MouseListener() {
		
		  //private boolean in = false;
		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getXOnScreen()<pop.getLocationOnScreen().x || e.getXOnScreen() > pop.getLocationOnScreen().x + pop.getWidth() || e.getYOnScreen() < pop.getLocationOnScreen().y || e.getYOnScreen() > pop.getLocationOnScreen().y + pop.getHeight()){
				pop.setVisible(false);
			}
		}
	
	});
		
		
			tray.addMouseListener(mouseAdapter);
	
		
		try {
			sTray.add(tray);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
		}
	}
	
	
	public void exit(){
		sTray.remove(tray);
	}
	
	
	public static Image getImage(String path) {
	    ImageIcon icon = new ImageIcon(path);
	    return icon.getImage();
	}
	
	public void displayMessage(String caption, String text){
		tray.displayMessage(caption, text, TrayIcon.MessageType.NONE);
	}
	
	public void displayError(String caption, String text){
		tray.displayMessage(caption, text, TrayIcon.MessageType.ERROR);
	}
	
	public void displayInfo(String caption, String text){
		tray.displayMessage(caption, text, TrayIcon.MessageType.INFO);
	}
	
	public void displayWarning(String caption, String text){
		tray.displayMessage(caption, text, TrayIcon.MessageType.WARNING);
	}

	
	public JMenuItem addMenuItem(String label, final Runnable ex){
		JMenuItem m = new JMenuItem(label);
		m.addActionListener(e->ex.run());
		
		pop.add(m);
		componentList.put(label, m);
		
		return m;
	}
	
	public void removeMenuItem(String label){
		Component c = componentList.get(label);
		if(c != null){
			componentList.remove(label);
			pop.remove(c);
		}
	}
	
	public JCheckBoxMenuItem addCheckBoxMenuItem(String label, final Function<Boolean,?> ca){
		final JCheckBoxMenuItem m = new JCheckBoxMenuItem(label);
		m.addChangeListener(e->ca.apply(m.isSelected()));
		
		pop.add(m);
		componentList.put(label, m);
		return m;
	}
	
	

	public JMenu addMenu(String label, Function<JMenu,?> ca){
		JMenu m = new JMenu(label);
		ca.apply(m);
		pop.add(m);
		componentList.put(label, m);
		return m;
	}
	
	
	public void addSeparator(String separatorName){
		pop.addSeparator();
		int index = pop.getComponentCount()-1;
		Component sep = pop.getComponent(index);
		componentList.put(separatorName, sep);
	}
	
	public static JMenuItem createMenuItem(String label, final Runnable ex){
		JMenuItem ret = new JMenuItem(label);
		ret.addActionListener(e->ex.run());
		
		return ret;
	}
	
	public static JCheckBoxMenuItem createCheckBoxMenuItem(String label, final Function<Boolean,?> ca){
		final JCheckBoxMenuItem m = new JCheckBoxMenuItem(label);
		m.addChangeListener(e->ca.apply(m.isArmed()));
		
		return m;
	}
	
	public void setImage(String path){
		tray.setImage(getImage(path));
	}
	
	public void setImage(Image image){
		tray.setImage(image);
	}
	
}
