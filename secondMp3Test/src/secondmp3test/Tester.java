/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package secondmp3test;

import java.io.*;
import java.util.logging.*;
import javax.sound.sampled.*;

/**
 *
 * @author Monkey D Alok
 */
public class Tester
{
	public void go() throws LineUnavailableException{
		byte[] data = load();
		AudioFormat decodedFormat;
			decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
				   (float)44100.0,
				   8,
				   1,
				   1,
				   (float)44100.0,
				   true);
		 SourceDataLine line = getLine(decodedFormat); 
		  if (line != null){
			// Start
			line.start();
			int nBytesRead;
			nBytesRead = data.length;
			int nBytesWritten = line.write(data, 0, nBytesRead);
		  }
	}
	private byte[] load(){
		ObjectInputStream load = null;
		try
		{
			byte[] saveData = null;
			FileInputStream fileStream = null;
			try
			{
				fileStream = new FileInputStream( new File("SaveData.ser"));
			} catch (FileNotFoundException ex)
			{
				Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
			}
			load = new ObjectInputStream(fileStream);
			try
			{
				saveData = (byte[])load.readObject();
			} catch (ClassNotFoundException ex)
			{
				Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
			}
			return saveData;
		} catch (IOException ex)
		{
			Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
		} finally
		{
			try
			{
				load.close();
			} catch (IOException ex)
			{
				Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return null;
	}
	private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException{
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		res = (SourceDataLine) AudioSystem.getLine(info);
		res.open(audioFormat);
		return res;
	} 
}
