/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicBox;

import java.io.*;

/**
 *
 * @author The Speed Phantom
 */
public class OccurrenceInfo implements Serializable
{
	int chunkNumber;
	int songId;
	public OccurrenceInfo(int chunkNumber , int songId){
		this.chunkNumber = chunkNumber;
		this.songId = songId;
	}

	public int getId() {
		return songId;
	}
	
}
