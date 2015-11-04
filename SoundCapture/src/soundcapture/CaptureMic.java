/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package soundcapture;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 *
 * @author Monkey D Alok
 */
public class CaptureMic extends JFrame
{
	boolean stopCapture = false;
	ByteArrayOutputStream byteArrayOutputStream;
	AudioFormat audioFormat;
	TargetDataLine targetDataLine;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	byte[] audioData;
	
	public CaptureMic(){
		final JButton captureBtn = new JButton("Capture");
		final JButton stopBtn = new JButton("Stop");
		final JButton playBtn = new JButton("Playback");
		final JButton stats = new JButton("Stats");

		captureBtn.setEnabled(true);
		stopBtn.setEnabled(false);
		playBtn.setEnabled(false);

		//Register anonymous listeners
		captureBtn.addActionListener(new ActionListener(){
			@Override
					public void actionPerformed(ActionEvent e){
						captureBtn.setEnabled(false);
						stopBtn.setEnabled(true);
						playBtn.setEnabled(false);
						//Capture input data from the
						// microphone until the Stop button is clicked.
						captureAudio();
					}//end actionPerformed
				}//end ActionListener
			);//end addActionListener()
		getContentPane().add(captureBtn);

		stopBtn.addActionListener(new ActionListener(){
			@Override
					public void actionPerformed(ActionEvent e){
						captureBtn.setEnabled(true);
						stopBtn.setEnabled(false);
						playBtn.setEnabled(true);
						//Terminate the capturing of input data
						// from the microphone.
						stopCapture = true;
					}//end actionPerformed
				}//end ActionListener
			);//end addActionListener()
		
		getContentPane().add(stopBtn);

		playBtn.addActionListener(new ActionListener(){
			@Override
					public void actionPerformed(ActionEvent e){
						//Play back all of the data that was
						// saved during capture.
						playAudio();
					}//end actionPerformed
				}//end ActionListener
			);//end addActionListener()
    
		getContentPane().add(playBtn);
		
		stats.addActionListener(new PlotTheGraph());
		getContentPane().add(stats);

    
		getContentPane().setLayout(new FlowLayout());
		setTitle("Capture/Playback Demo");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(250,70);
		pack();
		setVisible(true);
	}//end constructor

	//This method captures audio input from a
	// microphone and saves it in a ByteArrayOutputStream object.
	private void captureAudio(){
		try{
			//Get and display a list of
			// available mixers.
			Mixer.Info[] mixerInfo = 
			AudioSystem.getMixerInfo();
			System.out.println("Available mixers:");
			for(int cnt = 0; cnt < mixerInfo.length; cnt++){
				System.out.println(mixerInfo[cnt].getName());
			}//end for loop

			//Get everything set up for capture
			audioFormat = getAudioFormat();

			DataLine.Info dataLineInfo =new DataLine.Info(TargetDataLine.class , audioFormat);
			//Select one of the available mixers.
			Mixer mixer = AudioSystem.getMixer(mixerInfo[3]);
      
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

	private class PlotTheGraph
		implements ActionListener
	{

		public PlotTheGraph()
		{
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Convertor convertSound = new Convertor(audioData);
			convertSound.go();
		}
	}
	
	class CaptureThread extends Thread{
		//An arbitrary-size temporary holding buffer
		byte tempBuffer[] = new byte[10000];
		@Override
		public void run(){
			byteArrayOutputStream = new ByteArrayOutputStream();
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
			}catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}//end catch
		}//end run
	}//end inner class CaptureThread
	
	private void playAudio() {
		try{
			 //Get an input stream on the byte array containing the data
			InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
			audioFormat = getAudioFormat();
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
	
	class PlayThread extends Thread{
		byte tempBuffer[] = new byte[10000];

		@Override
		public void run(){
			try{
				int cnt;
				while((cnt = audioInputStream.read(tempBuffer , 0 , tempBuffer.length)) != -1){
					if(cnt > 0){
						sourceDataLine.write(tempBuffer, 0, cnt);
					}//end if
				}//end while
				sourceDataLine.drain();
				sourceDataLine.close();
			      }catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			      }//end catch
		}//end run
	}//end inner class PlayThread
	
	/*private void save(byte[] saveData){
		
		ObjectOutputStream save = null;
		try
		{
			FileOutputStream  fileStream = null;
			try
			{
				fileStream = new FileOutputStream( new File("SaveData.ser"));
			} catch (FileNotFoundException ex)
			{
				Logger.getLogger(CaptureMic.class.getName()).log(Level.SEVERE, null, ex);
			}
			save = new ObjectOutputStream(fileStream);
			save.writeObject(saveData);
			//save.writeObject(target);
		} catch (IOException ex)
		{
			Logger.getLogger(CaptureMic.class.getName()).log(Level.SEVERE, null, ex);
		} finally
		{
			try
			{
				save.close();
			} catch (IOException ex)
			{
				Logger.getLogger(CaptureMic.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}*/
	
}
