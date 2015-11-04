/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicLibrary;
import MusicSynthesizer.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author Monkey D Alok
 */
public class MusicLibrary implements Serializable
{
	ArrayList<Song> songList;
	HashMap<String,ArrayList<Song>> albumTable = new HashMap<>();
	HashMap<String,ArrayList<Song>> playListTable = new HashMap<>();
	
	public ArrayList<Song> getPlayListTable(String s) {
		return playListTable.get(s);
	}
	
    
	public void addSong(int songId , String containerName, File path, boolean isPlayList){
		
		SongMaker theMaker = new SongMaker(songId);
		Song theSong = theMaker.makeASong(path);
		if(albumTable.get(theSong.getAlbumName()) == null){
			songList = new ArrayList<>();
			songList.add(theSong);
			albumTable.put(theSong.getAlbumName(), songList);
			if(containerName.toLowerCase().equals("library")){
				return;
			}
		}
		else{
			songList = albumTable.get(theSong.getAlbumName());
			songList.add(theSong);
		}
		if(isPlayList){
			
			if(playListTable.get(containerName) == null){
				songList = new ArrayList<>();
				songList.add(theSong);
				playListTable.put(containerName, songList);
			}
			else{
				songList = playListTable.get(containerName);
				songList.add(theSong);
				
			}
			
		}
	
		
	}
    
	public Song findSong(String containerName, String title, boolean isPlayList){
		
		if(isPlayList){
			songList = playListTable.get(containerName);
			for(Song aSong: songList ){
				if(aSong.toString().equals(title)){
					return aSong;
				}
			}
			return null;
		}
		else{
			songList = albumTable.get(containerName);
			for(Song aSong: songList ){
				if(aSong.toString().equals(title)){
					return aSong;
				}
			}
			return null;
		}
		
	}
	
	public ArrayList<HashMap<Tags,String>> getContainerInfo(String containerName, boolean isPlayList){
		
		
		if(isPlayList){
			songList = playListTable.get(containerName);
		}
		else{
			songList = albumTable.get(containerName);
		}
		
		return extractInfo(songList);
		
		
	}
	
	public Set<String> getAlbumNames(){
		return albumTable.keySet();
	}
        
	 /**
	*
	* @return
	*/
	public Set<String> getPlayListNames(){
		return playListTable.keySet();
	}
	
	public ArrayList<HashMap<Tags,String>> albumSearch(String text){
		System.out.print(text);
		// stores results
		ArrayList<Song> results = new ArrayList<>();	
		
		// holds all album names
		Set<String> albumNames = getAlbumNames();
		
		// iterates over all the album names
		for (String s : albumNames) {	       
			
			//places lowercase content in temp variable
			String temp = s.toLowerCase();
                        
			// if the given search query is contained in the album name:
			if ( temp.contains(text.toLowerCase())){       
				
				// all the songs in the album are added to results
				results.addAll(albumTable.get(s)); 
			}
		}
		for(Song s : results){
			System.out.println(s.toString());
		}
		return extractInfo(results);
	}
	
	public ArrayList<HashMap<Tags,String>> playListSearch(String text){
		
                //stores results
                ArrayList<Song> results = new ArrayList<>();       
		
                // holds all playlist names
                Set<String> playListNames = getPlayListNames();         
		
		// iterates over all the playlist names
                for (String s : playListNames) {          
		
                        //places lowercase content in temp variable
                        String temp = s.toLowerCase();    
                        
                        // if the given search query is contained in the playlist name:
                        if ( temp.contains(text.toLowerCase())){            
			
                                // all the songs in the playlist are added to results
                                results.addAll(playListTable.get(s));           
			}
		}
		for(Song s : results){
			System.out.println(s.toString());
		}
		return extractInfo(results);
		
		
	}
	
	public ArrayList<HashMap<Tags,String>> songSearch(String text){
		
                // stores results
                ArrayList<Song> results = new ArrayList<>();           
		
                // holds all album names
                Set<String> albumNames = getAlbumNames();             
		
		// iterates over all album names
                for (String s : albumNames) {              
		
                        // all songs in the album are stored in a songlist
                        ArrayList<Song> temp = albumTable.get(s);                
			
                        // iterating over all the songs in the songlist
                        for (Song trill : temp) {              
			
                                // if a given search query is contained in the song-name:
                                if (((trill.toString()).toLowerCase()).contains(text.toLowerCase())) {           
				
                                        // it is added to the results
                                        results.add(trill);            
				}
			}
		}
		return extractInfo(results);
	}
	public ArrayList<HashMap<Tags,String>> extractInfo(ArrayList<Song> songList){
		HashMap<Tags,String> info;
		 ArrayList<HashMap<Tags,String>> infoList = new ArrayList<>();
		if(songList == null){
			return null;
		}
		for(Song s : songList){
			 info = s.getTags();
			 infoList.add(info);
			 
		}
		return infoList;
		
	}
        
        public ArrayList<Song> getSongList(){
                return songList;
        }

	public ArrayList<HashMap<Tags, String>> getSongListById(int[] hitIds) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public ArrayList<HashMap<Tags, String>> getSongListById(int hitIds) {
		System.out.println(hitIds);
		ArrayList<HashMap<Tags, String>> data = new ArrayList<>();
		Set<String> albumNames = getAlbumNames();
		for(String album : albumNames){
			System.out.println(album);
			ArrayList<Song> songs = albumTable.get(album);
			for(Song song : songs){
				System.out.println("Song Id : " + song.getId());
				if(song.getId() == hitIds){
					System.out.println(song.toString());
					 data.add(song.getTags());
				}
			}
		}
		return data;
	}
}