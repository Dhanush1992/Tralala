/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicBox;
import MusicLibrary.*;
import MusicPlayer.MusicPlayer;
import MusicSynthesizer.PatternMatcher;
import MusicSynthesizer.SongConvertor;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Monkey D Alok
 */
public class MusicBox implements Serializable{
	
	MusicLibrary theLibrary; 
	MusicPlayer thePlayer = new MusicPlayer();
	HashMap<Long,ArrayList<OccurrenceInfo>> frequencyHashTable;
	Thread extract;
	SongMixer mixJob;
	UserInput theMic;
	int songIdCounter;
	Song currentSong = null;
	 ArrayList<Song> currentList,shuffleList;
	int songPosition;
	
	RepeatState repeat = RepeatState.REPEATLIST;
	boolean shuffle = false; 
	boolean exit_state; 
	
	
	VolumeControl volumeChanger = new VolumeControl();
        
	public MusicBox(){
		//holds the list of currently playing songs
		currentList = new ArrayList<>();

		//holds the list of currently playing songs in shuffled state
		shuffleList = new ArrayList<>();

		//starts the songMixer method
		startMixer();
		
		load();
	}
	
	public void load(){
		
		ObjectInputStream load = null;
		 try {
			 FileInputStream fileStream = null;
			 try {
				 fileStream = new FileInputStream( new File("SaveData.ser"));
				 load = new ObjectInputStream(fileStream);
				 try {
					theLibrary = (MusicLibrary)load.readObject();
					frequencyHashTable = (HashMap<Long,ArrayList<OccurrenceInfo>>)load.readObject();
					songIdCounter = (int)load.readObject();
					//System.out.println(songIdCounter);

				 } catch (ClassNotFoundException ex) {
					 Logger.getLogger(MusicBox.class.getName()).log(Level.SEVERE, null, ex);               
				 }
			 } catch (FileNotFoundException ex) {
				 theLibrary = new MusicLibrary();
				 frequencyHashTable = new HashMap<>();
				 songIdCounter = 0;
			 }

		 } catch (IOException ex) {

			 Logger.getLogger(MusicBox.class.getName()).log(Level.SEVERE, null, ex);
		 }
	}
        
	public void save(){
		//try {
			//if(extract.isAlive()){
				//extract.join();
			//}
		//} catch (InterruptedException ex) {
			//Logger.getLogger(MusicBox.class.getName()).log(Level.SEVERE, null, ex);
		//}
		ObjectOutputStream save = null;
		try {
			FileOutputStream fileStream = null;
			try {
				fileStream = new FileOutputStream( new File("SaveData.ser"));
			} catch (FileNotFoundException ex) {
				Logger.getLogger(MusicBox.class.getName()).log(Level.SEVERE, null, ex);
			}
			save = new ObjectOutputStream(fileStream);
			save.writeObject(theLibrary);
			save.writeObject(frequencyHashTable);
			save.writeObject(songIdCounter);
			exit_state = true;
		} catch (IOException ex) {
			Logger.getLogger(MusicBox.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				save.close();
			} catch (IOException ex) {
				Logger.getLogger(MusicBox.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
    
	public boolean isPlaying(){
		if(thePlayer.canResume() || thePlayer.isSongDone()){
			return false;
		}
		return true;
	}
	public boolean playSong(){
		if(currentSong == null){
			return true;
		}
		if(thePlayer.getPath() == null || !thePlayer.getPath().equals(currentSong.getPath())){
			thePlayer.setPath(currentSong.getPath());
			thePlayer.play();
			return true;
		}
		else{
			thePlayer.resume();
			return false;
		}
	}
    
	public void pauseSong(){
		thePlayer.pause();
	}
    
	public void changeSong(Song s){
		currentSong = s;
	}
	
	public void stopSong(){
		if(!thePlayer.isSongDone()){
			thePlayer.stop();
		}
		thePlayer.init();
	}
    
	public void addSong(String containerName, File songPath, boolean isPlayList){    
		songIdCounter++;
		System.out.println("Adding Song: " + songIdCounter);
		theLibrary.addSong(songIdCounter , containerName, songPath, isPlayList);
				
		Runnable extractHashes = new Extractor(songPath , songIdCounter);
		extract = new Thread(extractHashes);
		extract.start();
		
	}
    
	public int getCurrentSongLength(){
		if(currentSong == null){
			return -1;
		}
		return currentSong.getLength();
	}
	
	public String getCurrentSongTitle(){
		return currentSong.toString();
	}
	
	public void findSong(String containerName, String title, boolean isPlayList){
		changeSong(theLibrary.findSong(containerName, title, isPlayList));
	}
    
	public String[][] getTableContents(String containerName, boolean isPlayList){
		ArrayList<HashMap<Tags,String>> tableContents = theLibrary.getContainerInfo(containerName, isPlayList);
		if(tableContents == null){
			return null;
		}
		
		return convert(tableContents);
	}
    
	public String[] getTableHeader(){
		ArrayList<String> header = new ArrayList<>();
		String[] headerArray;
		Object[] objArray;
		for(Tags tag : Tags.values()){
			header.add(tag.toString());
		}
		objArray = header.toArray();
		headerArray = new String[objArray.length];
		for(int i=0; i< objArray.length;i++){
			headerArray[i] = objArray[i].toString();    
		}
		return headerArray;
	}
    
	public String[] getAlbumList(){
		Set<String> list;
		list = theLibrary.getAlbumNames();
		Object[] listObjArray = list.toArray();
		String[] listArray = new String[listObjArray.length];
		for(int i = 0; i < listObjArray.length; i++){
			listArray[i] = listObjArray[i].toString();
		}
		return listArray;
	}
        
	public String[] getPlayListList(){
		Set<String> list;
		 list = theLibrary.getPlayListNames();
		Object[] listObjArray = list.toArray();
		String[] listArray = new String[listObjArray.length];
		for(int i = 0; i < listObjArray.length; i++){
			listArray[i] = listObjArray[i].toString();
		}
		return listArray;
	}
    
	public void changeVolume(float slider){
		float volume = slider/20;
		volumeChanger.changeSpeaker(volume);    
	}
	
	/**
	 *
	 * @param slider
	 */
	public synchronized  void scrollTo(int slider){
		
		int length = getCurrentSongLength();
		int total = thePlayer.getTotal();
		float posFloat = ((float)slider/length)*(float)total;
		int pos = (int)posFloat;
		if(Math.abs(slider - songPosition) < 2 ){
			return;
		}
		else{
			songPosition = slider;
		}
		
		thePlayer.pause();
		thePlayer.play(pos);

	}
	
	public String[][] search (String text , String option){
		ArrayList<HashMap<Tags,String>> listOfSongs;
		
		if(option.toLowerCase().equals(SearchOption.ALBUM.toString())){
			listOfSongs = theLibrary.albumSearch(text);
		}
		else if(option.toLowerCase().equals(SearchOption.PLAYLIST.toString())){
			listOfSongs = theLibrary.playListSearch(text);
		}
		else{
			listOfSongs = theLibrary.songSearch(text);
		}
                
		 if (listOfSongs == null) {
			return null;
		}
                
		return convert(listOfSongs);
            
		
	}
	public String[][] convert(ArrayList<HashMap<Tags,String>> tableContents){
		ArrayList<String> tableRow ;
		ArrayList<String[]> table = new ArrayList<>();
		for(HashMap<Tags,String> row : tableContents){
			tableRow = new ArrayList<>();
			for(Tags tag : Tags.values()){
				tableRow.add(row.get(tag));
			}
			String[] tableRowArray = tableRow.toArray(new String[0]);
			table.add(tableRowArray);
		}
		String[][] tableArray = table.toArray(new String[0][0]);
		return tableArray;
		
	}

	public void updateNextSong() {
		Song toPlay = mixJob.getNextSong();
		stopSong();
		changeSong(toPlay);
	}
		
	public void updatePreviousSong() {
		Song toPlay = mixJob.getPrevSong();
		stopSong();
		changeSong(toPlay);
	}
        
	public void setRepeatState(RepeatState state) {
		repeat = state;
	}
        
	public void setShuffleState(boolean state) {
		shuffle = state;
	}
        
	/**
         *
         */
	public final void startMixer() {
		mixJob = new SongMixer();
		Thread mixThread = new Thread(mixJob);
		mixThread.start();
	}
	
	public void letTheHummingBegin() {
		theMic = new UserInput();
	}
		
	public void captureMicInput() {
		theMic.startACapture();
	}

	public void stopCapture() {
		theMic.StopCurrentCapture();
	}

	public void cleanUpMic() {
		theMic.discardAudioData();
	}

	public void playCapturedSong() {
		theMic.playAudioData();
	}

	public void stopPlaying() {
		theMic.stopPlay();
	}
	
	public UserInputStatus micStatus(){
		if(theMic.captureUnderway()){
			return UserInputStatus.CAPTURING;
		}
		if(theMic.needsCleanUp()){
			return UserInputStatus.FULL;
		}
		return UserInputStatus.IDLE;
	}

	/**
	 *
	 * @return
	 */
	public String[][] searchTheSong() {
		byte[] audioData = theMic.getAudioData();
		ArrayList<Long> micHashes;
		micHashes = SongConvertor.convertThis(audioData);
		int hitId = PatternMatcher.matchThis(micHashes , frequencyHashTable);
		System.out.println(hitId);
		ArrayList<HashMap<Tags,String>> tableContents = theLibrary.getSongListById(hitId);
		return convert(tableContents);
		
	}

	public String[] getPlayLists(String currentSong) {
		ArrayList<String> results;
		results = new ArrayList<>();
		
		Set<String> playListNames = theLibrary.getPlayListNames();
		for(String playList : playListNames) {
			ArrayList<Song> songs = theLibrary.getPlayListTable(playList);
			for(Song song : songs) {
				if(song.toString().equals(currentSong)) {
					results.add(playList);
					break;
				}
			}
		}
		
		String[] allPlayLists = null;
		try {
			allPlayLists = results.toArray(new String[0]);
		} catch(NullPointerException ex) {
			return null;
		}
		return allPlayLists;
	}

	public String getAlbum(String selectedSong) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public boolean isMicDone() {
		return theMic.isSongDone();
	}

	public void endOfHumming() {
		theMic = null;;		
	}

	private class Extractor
		implements Runnable
	{
		
		File songPath;
		int id;
		public Extractor(File songPath , int id) {
			this.songPath = songPath;
			this.id = id;
		}

		@Override
		public void run() {
			ArrayList<Long> songHashes;
			songHashes = SongConvertor.convertThis(songPath);
		
			for(int i = 0; i < songHashes.size(); i++){
				OccurrenceInfo selectedDataPoint = new OccurrenceInfo(i , songIdCounter);
				ArrayList<OccurrenceInfo> listOfHits;
				if(frequencyHashTable.containsKey(songHashes.get(i))){
					listOfHits = frequencyHashTable.get(songHashes.get(i));
					listOfHits.add(selectedDataPoint);
				}
				else{
					listOfHits = new ArrayList<>();
					listOfHits.add(selectedDataPoint);
				}
				frequencyHashTable.put(songHashes.get(i) , listOfHits);
			
			}
			System.out.println("done");
		}
	}

	
	public class SongMixer implements Runnable {
		Song nextSong;
		@Override
		public void run() {
			
			while(exit_state == false && thePlayer.isSongDone() == true) {
				try {
					Thread.sleep(1000);
				} catch(InterruptedException ex) {
				}
			}
			
			//the thread runs as long as the program runs
			while(exit_state == false) {
				//nobody will notice if the change in song is 
				//	just one second late
				try {
					Thread.sleep(1000);
				} catch(InterruptedException ex) {
				}
                                
				//loops until the song has finished playing
				if(!thePlayer.isSongDone()) {
					continue;
				}
				
				//then updates the song list
				updateList();
                                
				//loops back if there is nothing to play
				//not sure about later, but this is useful 
				//at least at the start, I think. 
			
				if(currentList.isEmpty()) {
					continue;
				}
                                
				//if it isn't supposed to repeat, it'll keep looping
				//it's up to the user to play a song.
			
				if (repeat == RepeatState.NOREPEAT) {
					changeSong(null);
					continue;
				}
                                
				//this is used to repeat a song over and over
				//it can only get this far after a song is complete,
				//then it just changes the song to itself, and starts
				//all over again.
			
				if (repeat == RepeatState.REPEATSONG) {
					changeSong(currentSong);
					while(thePlayer.isSongDone()) {
						try {
							Thread.sleep(1000);
						} catch(InterruptedException ex) {
							ex.printStackTrace();
						}
					}
					continue;
				}
                                
				//whether it's a repeat-list, or play-list-once
				//is up to the function
					
				nextSong = getNextSong();
                                
				/*if (nextSong == null) {
						
					//loop back, there'll be nothing more to
					//play until the user chooses to
					changeSong(null);
					continue;
				}*/
                                
				changeSong(nextSong);
				while(thePlayer.isSongDone()) {
					try {
						Thread.sleep(1000);
					} catch(InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
                
		public Song getNextSong() {
			ArrayList<Song> theList;

			 //this selects whether the list is shuffled or not
			//this is mainly for abstraction purposes
			updateList();
			
			if(shuffle) {
				theList = shuffleList;
			}
			else {
				theList = currentList;
			}

			int index;
			index = theList.indexOf(currentSong);
			index = (index + 1) % theList.size();

			//if the condition was to repeat the list once, and if we
			//had reached the end of the list in the last song, the
			//new index would be 0, which is when we should stop playing

			if ((repeat == RepeatState.REPEATLISTONCE) && (index == 0)) {
				return null;
			}

			//otherwise, there is no difference between the repeat-list
			//and repeat-list-once. We just return the new song in the list
			return theList.get(index);
		}
		
		public Song getPrevSong() {
			ArrayList<Song> theList;

			 //this selects whether the list is shuffled or not
			//this is mainly for abstraction purposes
			
			updateList();
			if(shuffle) {
				theList = shuffleList;
			}
			else {
				theList = currentList;
			}

			int index;
			index = theList.indexOf(currentSong);
			
			if(index == 0) {
				index = theList.size() - 1;
			}
			
			else {
				index--;
			}

			return theList.get(index);
		}
		
		public void updateList() {

			ArrayList<Song> temp;
			temp = theLibrary.getSongList();


			//If nothin is being played, the currentList must be empty
			//The same applies to the shuffleList

			if (temp.isEmpty() || temp == null) {
				currentList.clear();
				shuffleList.clear();
				return;
			}

			//If the lists are the same, there is nothing to update
			//This also adds the benefit that you can click on a song 
			//in the same list and not change the state of the list

			if (temp.equals(currentList)) {
				return;
			}

			//otherwise, the list needs to be updated
			//the lists are cleared;
			currentList.clear();

			shuffleList.clear();

			//populated with the new list;
			currentList.addAll(temp);
			shuffleList.addAll(temp);

			//and finally, the shuffleList is shuffled
			Collections.shuffle(shuffleList);
		}
		

                
	}
	
}

 