/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicLibrary;

/**
 *
 * @author Monkey D Alok
 */
public enum Tags
{
    ARTIST{
	    public String toString(){
		    return "Artist";
	    }
    },
    ALBUM{
	     public String toString(){
		    return "Album";
	    }
    },
    TITLE{
	     public String toString(){
		    return "Title";
	    }
    },
    COMMENT{
	     public String toString(){
		    return "Comment";
	    }
    },
    YEAR{
	     public String toString(){
		    return "Year";
	    }
    },
    TRACK{
	     public String toString(){
		    return "Track";
	    }
    },
    DISC_NO{
	     public String toString(){
		    return "Disc No";
	    }
    },
    COMPOSER{
	     public String toString(){
		    return "Composer";
	    }
    },
    ARTIST_SORT{
	     public String toString(){
		    return "Artist Sort";
	    }
    },
    GENRE{
	    public String toString(){
		    return "Genre";
	    }
    },
    LENGTH{
	    public String toString(){
		    return "Length";
	    }
    }
    
    
};
