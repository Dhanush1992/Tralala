/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicApplication;

import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;
import java.util.Properties;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import com.jtattoo.plaf.noire.NoireLookAndFeel;

/**
 *
 * @author Monkey D Alok
 */
public class Program
{
	public static void main(String[] args) {
		try {
			Properties props = new Properties();
			props.put("logoString", "Tra Laa Laa !!!");
			AluminiumLookAndFeel.setCurrentTheme(props);
			UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
			//UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
		} 
		catch ( UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			System.out.println("Oops with the tatoo!!!");// handle exception
		}
		Runnable setupFrontEnd = new RunMusicApplication();
		Thread gooeyThread = new Thread(setupFrontEnd);
		gooeyThread.start();
		try{
			gooeyThread.join();
		}
		catch(Exception e){
			System.out.println("Oops");
		}
	}
}
