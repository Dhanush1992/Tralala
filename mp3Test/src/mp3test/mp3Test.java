/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3test;
import java.io.*;
import java.util.logging.*;
import javax.sound.sampled.*;

/**
 *
 * @author Monkey D Alok
 */
public class mp3Test
{
	AudioFormat targetFormat;
	public void testPlay(String fileName){
		try {
			File songPath = new File(fileName);
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
				rawplay(targetFormat, convertedInputStream);

				convertedInputStream.close();
			}
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
			System.out.println("Sad");
		} 
	}

	private AudioInputStream convertStream(int channels , int sampleSize , boolean isBigEndian , AudioInputStream sourceStream){
		AudioFormat sourceFormat = sourceStream.getFormat();
		targetFormat = new AudioFormat(sourceFormat.getEncoding(),
				sourceFormat.getSampleRate(),
				sampleSize,
				channels,
				calculateFrameSize(channels, sampleSize),
				sourceFormat.getFrameRate(),
				isBigEndian);
		
		
		return AudioSystem.getAudioInputStream(targetFormat , sourceStream);
	}

	private int calculateFrameSize(int nChannels, int nSampleSizeInBits){
		return ((nSampleSizeInBits + 7) / 8) * nChannels;
	}

	
	private void rawplay(AudioFormat targetFormat, AudioInputStream inputStream) throws IOException , LineUnavailableException{
		SourceDataLine dataLine = getLine(targetFormat); 
		if (dataLine != null)
		{
			dataLine.start();
			int bytesToRead = 0;
			ByteArrayOutputStream byteArrayBuffer = new ByteArrayOutputStream();

			int nRead;
			byte[] intermediateBuffer = new byte[16384];

			while ((nRead = inputStream.read(intermediateBuffer, 0, intermediateBuffer.length)) != -1) {
				byteArrayBuffer.write(intermediateBuffer, 0, nRead);
			}

			byteArrayBuffer.flush();

			byte[] byteArray = byteArrayBuffer.toByteArray();
			bytesToRead = byteArray.length;
			save(targetFormat,byteArray);
			dataLine.write(byteArray, 0, bytesToRead);
    
			dataLine.drain();
			dataLine.stop();
			dataLine.close();
			inputStream.close();
		
		} 
	}

	private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException{
		SourceDataLine line = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(audioFormat);
		return line;
	} 

	private void save(AudioFormat target, byte[] saveData){
		
		ObjectOutputStream save = null;
		try
		{
			FileOutputStream  fileStream = null;
			try
			{
				fileStream = new FileOutputStream( new File("SaveData.ser"));
			} catch (FileNotFoundException ex)
			{
				Logger.getLogger(mp3Test.class.getName()).log(Level.SEVERE, null, ex);
			}
			save = new ObjectOutputStream(fileStream);
			save.writeObject(saveData);
			//save.writeObject(target);
		} catch (IOException ex)
		{
			Logger.getLogger(mp3Test.class.getName()).log(Level.SEVERE, null, ex);
		} finally
		{
			try
			{
				save.close();
			} catch (IOException ex)
			{
				Logger.getLogger(mp3Test.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
}
