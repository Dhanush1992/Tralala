/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicBox;
import java.io.Serializable;
import javax.sound.sampled.*;

/**
 *
 * @author Monkey D Alok
 */
public class VolumeControl implements Serializable
{
	Line.Info source = Port.Info.SPEAKER;
	public void changeSpeaker(float vol){
		if (AudioSystem.isLineSupported(source)) 
		{
			try 
			{
				Port outline = (Port) AudioSystem.getLine(source);
				outline.open();                
				FloatControl volumeControl = (FloatControl) outline.getControl(FloatControl.Type.VOLUME);     
				volumeControl.setValue(vol);
			} 
			catch (LineUnavailableException ex) 
			{
				System.err.println("source not supported");
				ex.printStackTrace();
			}            
		}
	}
	
}
