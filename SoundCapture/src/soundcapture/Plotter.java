/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package soundcapture;

import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Monkey D Alok
 */
class Plotter
{
	private Complex[][] soundArray;
	private int[][] markers;

	Plotter(Complex[][] convertedArray , int[][] bestFrequencies)
	{
		soundArray = convertedArray;
		markers = bestFrequencies;
	}

	void plotGraph()
	{
		JFrame graphFrame = new JFrame();
		JPanel panel = new MyPanel();
		
		
		graphFrame.setContentPane(panel);
		graphFrame.pack();
		graphFrame.setMinimumSize(new Dimension(840,480));
		graphFrame.setVisible(true);
	}

	private class MyPanel extends JPanel
	{

		public void paintComponent(Graphics g){
			
			int xMax = this.getWidth() , yMax = this.getHeight();
			
			for(int chunkNumber = 0; chunkNumber < soundArray.length; chunkNumber++){
				for(int frequency = 0; frequency < this.getHeight(); frequency++){
					double magnitude = Math.log(soundArray[chunkNumber][frequency].abs() + 1);
					g.setColor(new Color(0 , (int)magnitude * 10 , (int)magnitude * 20));
					for(int i = 0; i < 4; i++){
						if(markers[chunkNumber][i] == frequency){
							g.setColor(new Color(255 , 0 , 0));
						}
					}
					g.fillRect((chunkNumber*5) , (frequency), xMax , yMax);
				}
			}
		}
	}
	
	

}
