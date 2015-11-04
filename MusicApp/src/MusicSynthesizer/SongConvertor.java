/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicSynthesizer;

import java.io.*;
import java.util.ArrayList;
import javax.sound.sampled.*;

/**
 *
 * @author The Speed Phantom
 */
public class SongConvertor
{
	static final int NOISE = 2;
	static final int CHUNK = 4096;

	public static ArrayList<Long> convertThis(File fileName) {
		try {
			File songPath;
			songPath = fileName;
			try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(songPath))
			{
				AudioInputStream convertedInputStream = null;
				AudioFormat baseFormat = inputStream.getFormat();
				AudioFormat decodedFormat;
				decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
							baseFormat.getSampleRate(),
							16,
							baseFormat.getChannels(),
							baseFormat.getChannels() * 2,
							baseFormat.getSampleRate(),
							true);
				
				convertedInputStream = AudioSystem.getAudioInputStream(decodedFormat, inputStream);
				convertedInputStream = convertStream(1 , 8 , true , convertedInputStream);
				return rawBytes(convertedInputStream);
			}
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
			System.out.println("Sad");
		} 
		return null;
	}
	
	private static AudioInputStream convertStream(int channels , int sampleSize , boolean isBigEndian , AudioInputStream sourceStream){
		AudioFormat sourceFormat = sourceStream.getFormat();
		AudioFormat targetFormat;
		targetFormat = new AudioFormat(sourceFormat.getEncoding(),
				sourceFormat.getSampleRate(),
				sampleSize,
				channels,
				calculateFrameSize(channels, sampleSize),
				sourceFormat.getFrameRate(),
				isBigEndian);
		
		
		return AudioSystem.getAudioInputStream(targetFormat , sourceStream);
	}

	private static int calculateFrameSize(int nChannels, int nSampleSizeInBits){
		return ((nSampleSizeInBits + 7) / 8) * nChannels;
	}

	
	private static ArrayList<Long> rawBytes(AudioInputStream inputStream) throws IOException , LineUnavailableException{

		ByteArrayOutputStream byteArrayBuffer = new ByteArrayOutputStream();

		int nRead;
		byte[] intermediateBuffer = new byte[16384];

		while ((nRead = inputStream.read(intermediateBuffer, 0, intermediateBuffer.length)) != -1) {
			byteArrayBuffer.write(intermediateBuffer, 0, nRead);
		}

		byteArrayBuffer.flush();

		byte[] byteArray = byteArrayBuffer.toByteArray();
   
		inputStream.close();
			
		return convertThis(byteArray);
		
	}
	
	
	public static ArrayList<Long> convertThis(byte[] byteArray) {
		int size = byteArray.length;
		System.gc();
		//int size = 44100 * 20;
		int numberOfChunks = size / CHUNK;
		Complex[] convertedArray = new Complex[CHUNK];
		
		double[] highestMagnitudes = {0.0 , 0.0 , 0.0 , 0.0}; 
		int [] frequenciesOfHighestMagnitudes = new int[4];
		int[][] bestFrequencies = new int[numberOfChunks][];
		
		for(int chunkNumber = 0; chunkNumber < numberOfChunks; chunkNumber++){
			Complex[] audioChunk = new Complex[CHUNK];
			for(int byteInChunk = 0; byteInChunk < CHUNK; byteInChunk++){
				audioChunk[byteInChunk] = new Complex(byteArray[(chunkNumber*CHUNK) + byteInChunk] , 0);
			}
			convertedArray = Transformer.fastFourierTransform(audioChunk);
			for(int byteInChunk = 60; byteInChunk < 330; byteInChunk++){
				double candidate = Math.log(convertedArray[byteInChunk].abs());
				int range = getRange(byteInChunk);
				if(highestMagnitudes[range] < candidate){
					highestMagnitudes[range] = candidate;
					 frequenciesOfHighestMagnitudes[range] = byteInChunk;
				}
			}
			bestFrequencies[chunkNumber] =  frequenciesOfHighestMagnitudes;
			highestMagnitudes = new double[5];
			frequenciesOfHighestMagnitudes = new int[4];
		}
		
		ArrayList<Long> hashes = new ArrayList<>();
		
		
		
		for(int[] sample : bestFrequencies){
			hashes.add(makeHash(sample));
		}
		
		//for(Long hash : hashes){
			//System.out.println(hash);
		//}
		
		return hashes;
	}
	
	private static int getRange(int index){
		int[] ranges = {100 , 160 , 240 , 330};
		int i = 0;
		while(ranges[i] < index){
			i++;
		}
		return i;
	}
	
	private static long makeHash(int[] samples){
		
		//System.out.println("Samples : " + samples[0] + " " + samples[1] + " " + samples[2] + " " + samples[3]);
		
		long hash = (((samples[3] - (samples[3] % NOISE)) * 1000000000l) 
			+ ((samples[2] - (samples[2] % NOISE)) * 1000000l) 
			+ ((samples[1] - (samples[1] % NOISE)) * 1000l) 
			+ ((samples[0] - (samples[0] % NOISE)) * 1l));
		//System.out.println(hash);
		return hash;
	}
	
}
