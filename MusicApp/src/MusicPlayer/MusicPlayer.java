/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicPlayer;

import java.io.*;
import javazoom.jl.player.Player;


/**
 *
 * @author Monkey D Alok
 */
public class MusicPlayer implements Runnable , Serializable
{
    private Player player;
    private FileInputStream FileStream;
    private BufferedInputStream BufferStream;
    private boolean canResume;
    private File path;
    private int total;
    private int stopped;
    private boolean valid;

    public MusicPlayer(){   
	path = null;    
	init();
    }
    
    public void init(){
        player = null;
        FileStream = null;
        valid = false;
        BufferStream = null;
        total = 0;
        stopped = 0;
        canResume = false;
    }

    public boolean canResume(){
        return canResume;
    }
    
    public boolean isSongDone(){
            if(player == null ){
                   return true; 
            }
            return false;
    }

    public void setPath(File path){
        this.path = path;
    }
    
    public int getTotal(){
	return total;
    }
    
    public File getPath(){
	    return path;
    }
    public boolean play(){
        return play(-1);
    }

    public boolean play(int pos){
        valid = true;
        canResume = false;
        if(path == null){
            return false;
        }    
        try{
            FileStream = new FileInputStream(path);
	    System.out.println("ha");
            total = FileStream.available();
            if(pos > -1) {
                FileStream.skip(pos);
            }
            BufferStream = new BufferedInputStream(FileStream);
            player = new Player(BufferStream);
            Thread song = new Thread(this);
            song.start();
        }
        catch(Exception e){
           //JOptionPane.showMessageDialog(null, "Error playing mp3 file");
            valid = false;
	    pos = -1;
        }
        return valid;
    }
    
    public void pause(){
        try{
        stopped = FileStream.available();
        player.close();
        FileStream = null;
        BufferStream = null;
        if(valid) canResume = true;
        }catch(Exception e){
        }
    }

    public void resume(){
        if(!canResume){
            return;
        }
        if(play(total-stopped)){
            canResume = false;
        }
    }

	public void stop(){
		player.close();
		init();
	}
    
    
	public void run(){
		try{
			player.play();
		}
		catch(Exception e){
		//JOptionPane.showMessageDialog(null, "Error playing mp3 file");
		valid = false;
		}
	}
    
}
