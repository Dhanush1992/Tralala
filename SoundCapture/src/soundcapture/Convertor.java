/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package soundcapture;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.sound.sampled.*;

/**
 *
 * @author Monkey D Alok
 */
public class Convertor
{
	byte[] byteArray;
	final int NOISE = 2;
	final int CHUNK = 4096;
	public Convertor(byte[] data){
		byteArray = data;
		//byteArray = load();
	}
	
	public void go(){
		int size = byteArray.length;
		//int size = 44100 * 20;
		int numberOfChunks = size / CHUNK;
		Complex[][] convertedArray = new Complex[numberOfChunks][];
		
		for(int chunkNumber = 0; chunkNumber < numberOfChunks; chunkNumber++){
			Complex[] audioChunk = new Complex[CHUNK];
			for(int byteInChunk = 0; byteInChunk < CHUNK; byteInChunk++){
				audioChunk[byteInChunk] = new Complex(byteArray[(chunkNumber*CHUNK) + byteInChunk] , 0);
				//audioChunk[byteInChunk].toString();
			}
			convertedArray[chunkNumber] = Transformer.fastFourierTransform(audioChunk);
		}
		
		double[] highestMagnitudes = {0.0 , 0.0 , 0.0 , 0.0 }; 
		int [] frequenciesOfHighestMagnitudes = new int[4];
		int[][] bestFrequencies = new int[numberOfChunks][];
		
		for(int chunkNumber = 0; chunkNumber < numberOfChunks; chunkNumber++){
			for(int byteInChunk = 60; byteInChunk < 330; byteInChunk++){
				double candidate = Math.log(convertedArray[chunkNumber][byteInChunk].abs());
				int range = getRange(byteInChunk);
				if(highestMagnitudes[range] < candidate){
					highestMagnitudes[range] = candidate;
					 frequenciesOfHighestMagnitudes[range] = byteInChunk;
				}
			}
			//for(int i = 0; i < 4; i++){
			//	System.out.print(frequenciesOfHighestMagnitudes[i] + "\t");
			//}
			//System.out.println("");
			bestFrequencies[chunkNumber] =  frequenciesOfHighestMagnitudes;
			highestMagnitudes = new double[5];
			 frequenciesOfHighestMagnitudes = new int[4];
		}
		
		ArrayList<Long> hashes = new ArrayList<>();
		
		save(hashes);
		
		for(int[] sample : bestFrequencies){
			hashes.add(makeHash(sample));
		}
		
		//for(Long hash : hashes){
			//System.out.println(hash);
		//}
		
		Plotter graphMaker = new Plotter(convertedArray , bestFrequencies);
		graphMaker.plotGraph();
		
	}
	
	private int getRange(int index){
		int[] ranges = {100 , 160 , 240 , 330};
		int i = 0;
		while(ranges[i] < index){
			i++;
		}
		return i;
	}
	
	private long makeHash(int[] samples){
		
		//System.out.println("Samples : " + samples[0] + " " + samples[1] + " " + samples[2] + " " + samples[3]);
		
		long hash = (((samples[3] - (samples[3] % NOISE)) * 1000000000l) 
			+ ((samples[2] - (samples[2] % NOISE)) * 1000000l) 
			+ ((samples[1] - (samples[1] % NOISE)) * 1000l) 
			+ ((samples[0] - (samples[0] % NOISE)) * 1l));
		//System.out.println(hash);
		return hash;
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
				Logger.getLogger(Plotter.class.getName()).log(Level.SEVERE, null, ex);
			}
			load = new ObjectInputStream(fileStream);
			try
			{
				saveData = (byte[])load.readObject();
			} catch (ClassNotFoundException ex)
			{
				Logger.getLogger(Plotter.class.getName()).log(Level.SEVERE, null, ex);
			}
			return saveData;
		} catch (IOException ex)
		{
			Logger.getLogger(Plotter.class.getName()).log(Level.SEVERE, null, ex);
		} finally
		{
			try
			{
				load.close();
			} catch (IOException ex)
			{
				Logger.getLogger(Plotter.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return null;
	}
	
	private void save(ArrayList<Long> saveData){
		
		ObjectOutputStream save = null;
		try
		{
			FileOutputStream  fileStream = null;
			try
			{
				fileStream = new FileOutputStream( new File("MicHash.ser"));
			} catch (FileNotFoundException ex)
			{
				//Logger.getLogger(mp3Test.class.getName()).log(Level.SEVERE, null, ex);
			}
			save = new ObjectOutputStream(fileStream);
			save.writeObject(saveData);
			//save.writeObject(target);
		} catch (IOException ex)
		{
			//Logger.getLogger(mp3Test.class.getName()).log(Level.SEVERE, null, ex);
		} finally
		{
			try
			{
				save.close();
			} catch (IOException ex)
			{
				//Logger.getLogger(mp3Test.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	
}
