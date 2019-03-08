/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Quinten Holmes
 */
public class GlobalDataCore implements Serializable{
    private static final long serialVersionUID = 1;
    private final HashMap<Long, SoundFile> soundfileMap = new HashMap();
    private final HashMap<Long, PlayList> playlistMap = new HashMap();
    private final HashMap<Long, BellRegion> regionMap = new HashMap();
    
    
    
    /**
     * Returns the SoundFile with the given ID or null if non exists.
     * @param fileID ID of SoundFile to return
     * @return 
     */
    public SoundFile getSoundFile(long fileID){
        return soundfileMap.get(fileID);
    }
    
     /**
     * Adds the given SoundFile to the Map and returns true if there are no 
     * conflicts, else it returns false. All new SoundFiles must have a unique ID.
     * @param newSoundFile SoundFile to add to the Map
     * @return true if the SoundFile was added, else false
     */
    public boolean addSoundFile(SoundFile newSoundFile){
        if(soundfileMap.get(newSoundFile.getID()) == null){
            soundfileMap.put(newSoundFile.getID(), newSoundFile);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the SoundFile with the given fileID. If there is a week with the
     * ID it will be removed and returns true, else it will return false.
     * @param fileID ID of SoundFile to remove
     * @return true if the SoundFile was found and removed.
     */
    public boolean removeSoundFile(long fileID){
        return soundfileMap.remove(fileID) != null;
    }
    
    /**
     * Returns the PlayList with the given ID or null if non exists.
     * @param playlistID ID of PlayList to return
     * @return 
     */
    public PlayList getPlayList(long playlistID){
        return playlistMap.get(playlistID);
    }
    
     /**
     * Adds the given PlayList to the Map and returns true if there are no 
     * conflicts, else it returns false. All new PlayLists must have a unique ID.
     * @param newPlayList PlayList to add to the Map
     * @return true if the PlayList was added, else false
     */
    public boolean addPlayList(PlayList newPlayList){
        if(playlistMap.get(newPlayList.getID()) == null){
            playlistMap.put(newPlayList.getID(), newPlayList);
            return true;
        }
        return false;
    }
    
    /**
     * Removes the PlayList with the given playlistID. If there is a week with the
     * ID it will be removed and returns true, else it will return false.
     * @param playlistID ID of PlayList to remove
     * @return true if the PlayList was found and removed.
     */
    public boolean removePlayList(long playlistID){
        return playlistMap.remove(playlistID) != null;
    }
}
