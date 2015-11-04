/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicBox;

/**
 *
 * @author Monkey D Alok
 */
public enum SearchOption
{
	/**
	 *
	 */
	PLAYLIST{
		@Override
		public String toString(){
			return "playlist";
		}
	},
	/**
	 *
	 */
	ALBUM{
		@Override
		public String toString(){
			return"album";
		}
	},
	/**
	 *
	 */
	SONG{
		@Override
		public String toString(){
			return "song";
	
		}
	}
}