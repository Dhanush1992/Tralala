/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicSynthesizer;

import MusicBox.OccurrenceInfo;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author The Speed Phantom
 */
public class PatternMatcher
{

	public static int matchThis(ArrayList<Long> micHashes, HashMap<Long, ArrayList<OccurrenceInfo>> frequencyHashTable) {
		HashMap<Integer , Integer> hitCount = new HashMap<>();
		Set<Long> keys;
		//keys = frequencyHashTable.keySet();
		//for(Long key : keys){
			//System.out.println(key);
		//}
		for(Long songHash : micHashes){
			if(frequencyHashTable.containsKey(songHash)){
				 ArrayList<OccurrenceInfo> candidates = frequencyHashTable.get(songHash);
				 for(OccurrenceInfo candidate : candidates){
					 int songId = candidate.getId();
					// System.out.println(songId);
					  int count = 0;
					 if(hitCount.containsKey(songId)){
						 count = hitCount.get(songId);
						
					 }
					count++;
					hitCount.put(songId, count);
					hitCount.put(songId, count);
				 }
			}
		}
		
		return keyOfHighestValue(hitCount);
	}
	
	public static <K, V extends Comparable<V>> K keyOfHighestValue(Map<K, V> map) {
		K bestKey = null;
		V bestValue = null;
		for (Entry<K, V> entry : map.entrySet()) {
			if (bestValue == null || entry.getValue().compareTo(bestValue) > 0) {
				bestKey = entry.getKey();
				bestValue = entry.getValue();
			}
		}
		return bestKey;
	}
	
}
