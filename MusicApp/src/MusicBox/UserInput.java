/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicBox;

import java.io.*;
import javax.sound.sampled.*;

/**
 *
 * @author Monkey D Alok
 */
class UserInput
{
	boolean stopCapture = true;
	boolean stopPlaying;
	byte[] audioData;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	TargetDataLine targetDataLine;
	Mixer mixer;

	public UserInput() {
	}

	void startACapture() {
		try{
			stopCapture = true;
			//Get and display a list of
			// available mixers.
			Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

			//Get everything set up for capture
			AudioFormat audioFormat = getAudioFormat();

			DataLine.Info dataLineInfo =new DataLine.Info(TargetDataLine.class , audioFormat);
			//Select one of the available mixers.
			mixer = AudioSystem.getMixer(mixerInfo[3]);
      
			//Get a TargetDataLine on the selected mixer.
			targetDataLine = (TargetDataLine)mixer.getLine(dataLineInfo);
			//Prepare the line for use.
			
			targetDataLine.open(audioFormat);
			targetDataLine.start();

			//Create a thread to capture the microphone
			// data and start it running.  It will run until the Stop button is clicked.
			Thread captureThread = new CaptureThread();
			captureThread.start();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}//end catch
	}//end captureAudio method
	
	private AudioFormat getAudioFormat(){
		float sampleRate = 44100.0F;
		//8000,11025,16000,22050,44100
		int sampleSizeInBits = 8;
		//8,16
		 int channels = 1;
		//1,2
		boolean signed = true;
		//true,false
		boolean bigEndian = false;
		//true,false
		return new AudioFormat(sampleRate , sampleSizeInBits , channels , signed , bigEndian);
	}//end getAudioFormat
	
	void StopCurrentCapture() {
		stopCapture = true;
	}
	
	boolean captureUnderway(){
		return !stopCapture;
	}
	
	boolean needsCleanUp(){
		if(audioData == null){
			return false;
		}
		return true;
	}

	void discardAudioData() {
		audioData = null;
	}

	void playAudioData() {
		try{
			 //Get an input stream on the byte array containing the data
			InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
			AudioFormat audioFormat = getAudioFormat();
			audioInputStream = new AudioInputStream(byteArrayInputStream , audioFormat , audioData.length/audioFormat.getFrameSize());
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class , audioFormat);
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();

			Thread playThread =new Thread(new PlayThread());
			playThread.start();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}//end catch
	}//end playAudio

	void stopPlay() {
		sourceDataLine.stop();
		sourceDataLine.drain();
		sourceDataLine.close();
		sourceDataLine = null;
	}

	boolean isSongDone() {
		if(sourceDataLine == null){
			return true;
		}
		return false;
	}

	byte[] getAudioData() {
		return audioData;
	}
	
	class CaptureThread extends Thread{
		//An arbitrary-size temporary holding buffer
		byte tempBuffer[] = new byte[10000];
		@Override
		public void run(){
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			stopCapture = false;
			try{//Loop until stopCapture is set by another thread that services the Stop button.
				 while(!stopCapture){
					 //Read data from the internal buffer of the data line.
					 int cnt = targetDataLine.read(tempBuffer , 0 , tempBuffer.length);
					 if(cnt > 0){
						//Save data in output stream object.
						byteArrayOutputStream.write(tempBuffer , 0 , cnt);
					}//end if
				}//end while
				byteArrayOutputStream.close();
				audioData = byteArrayOutputStream.toByteArray();
				targetDataLine.drain();
				targetDataLine.close();
				mixer.close();
			}catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}//end catch
		}//end run
	}//end inner class CaptureThread
	
	class PlayThread extends Thread{
		byte tempBuffer[] = new byte[10000];

		@Override
		public void run(){
			try{
				int length = audioData.length;
				
				sourceDataLine.write(audioData, 0, length);
				if(sourceDataLine != null){
					sourceDataLine.drain();
					sourceDataLine.close();
					sourceDataLine = null;
				}
			      }catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			      }//end catch
		}//end run
	}//end inner class PlayThread

	
}
