/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicBox;

/**
 *
 * @author The Speed Phantom
 * The GUI needs to implement this enumeration. 
 * A click of the repeat button should cycle through each of these states,
 * and should call the repeat() function in MusicBox, passing a RepeatState.
 */
public enum RepeatState
{
        /**
	 *
	 */
	REPEATSONG,

	/**
	 *
	 */
	REPEATLIST,
        
	/**
	 *
	 */
	REPEATLISTONCE,
        
	/**
	 *
	 */
	NOREPEAT
}
