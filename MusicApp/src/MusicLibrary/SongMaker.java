/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicLibrary;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import org.jaudiotagger.audio.*;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.*;
import org.jaudiotagger.tag.datatype.Artwork;
/**
 *
 * @author Monkey D Alok
 */
public class SongMaker implements Serializable
{
	Tag tag;
	int length;
	int id;

	SongMaker(int songId) {
		id = songId;
	}
	
    
	public Song makeASong(File path){
		Song song;
		MP3File musicFile = null;
		try {
			musicFile = (MP3File)AudioFileIO.read(path);
		} catch(Exception ex) {
			System.out.println("Could not read file");
			return null;
		}
		
		tag = musicFile.getID3v2Tag();
		
		String album = null;
		try {
			album = tag.getFirst(FieldKey.ALBUM);
		} catch(NullPointerException ex) {
			tag = musicFile.getID3v1Tag();
		}
		
		if((tag.getFirst(FieldKey.ALBUM).equals(""))) {
			try {
				tag.setField(FieldKey.ALBUM,"Unknown Album");
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		if(tag.getFirst(FieldKey.TITLE).equals("")) {
			try {	
				String name = (path.getName()).replaceFirst("[.][^.]+$", "");
				tag.setField(FieldKey.TITLE,name);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		try {
		length =  (int)musicFile.getAudioHeader().getTrackLength();
		StringBuilder s = new StringBuilder();
		if(length/60 < 10){
			s.append("0");
		}
		s.append(length/60).append(" : ");
		if(length%60 < 10){
			s.append("0");
		}
		s.append(length%60);
		
		String[] tags =	{tag.getFirst(FieldKey.ARTIST), 
			tag.getFirst(FieldKey.ALBUM), 
			tag.getFirst(FieldKey.TITLE), 
			tag.getFirst(FieldKey.COMMENT), 
		tag.getFirst(FieldKey.YEAR), 
		tag.getFirst(FieldKey.TRACK), 
		tag.getFirst(FieldKey.DISC_NO), 
		tag.getFirst(FieldKey.COMPOSER), 
		tag.getFirst(FieldKey.ARTIST_SORT),
		tag.getFirst(FieldKey.GENRE),
		s.toString()};
		
		System.out.println(tags[1]);
		
		tag = musicFile.getID3v2Tag();
		Artwork cover = null;
		
		try {
			cover = tag.getFirstArtwork();
		} catch(NullPointerException ex) {
			System.out.println("The file doesn't have an image");
		}
		
		if(cover == null) {
			song = new Song(id,path,tags, length);
			return song;
		}
		
		BufferedImage image = null;
		
		try {
			image = cover.getImage();
		} catch(NullPointerException nx) {
			image = null;
		} catch(Exception ix) {
			System.out.println("The image couldn't be read");
		}
		
		String save = tags[1];
		try {
			File outputFile = new File("resources//coverart//" + save + ".png");
			ImageIO.write(image, "png", outputFile);
		} catch (Exception ex) {
			System.out.println("File couldn't be written");
		}
	
		song = new Song(id,path,tags, length);
		return song;
		} catch(NullPointerException ex) {
			song = new Song(id,path,null, length);
			return song;
		}
	}
}
