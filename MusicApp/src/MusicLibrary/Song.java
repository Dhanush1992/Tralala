/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicLibrary;
import java.io.*;
import java.util.HashMap;

/**
 *
 * @author Monkey D Alok
 */
public class Song implements Serializable
{
    private File path;
    private HashMap<Tags,String> metadata = new HashMap<>(); 
    private int length;
    private int songId;
    
	public Song(int id, File path, String[] metadata, int length){
		System.out.println("Id in Song : " + id);
		songId = id;
		int i =0;
		this.path = path;
		if(metadata!= null){
			for(Tags tag : Tags.values()){
				this.metadata.put(tag,metadata[i]);
				 i++;
			}     
		}
		this.length = length;
	}
	
    public HashMap<Tags,String> getTags(){
        
        return metadata;
    }
    
    public File getPath(){
        return path;
    }
    
    public String getAlbumName(){
	    return (String)metadata.get(Tags.ALBUM);
    }
    
     public String toString(){
	    return (String)metadata.get(Tags.TITLE);
    }
     
     public int getLength(){
	     return length;
     }

	public int getId() {
		return songId;
	}
    
    
}
