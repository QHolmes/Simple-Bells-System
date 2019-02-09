/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.io.Serializable;

/**
 *
 * @author Quinten Holmes
 */
public class EventSegment implements Serializable{
    private static final long serialVersionUID = 1;
    
    private SegmentType type;
    private SoundFile file;
    private double duration;
    private boolean running;
    private PlayList playList;

    public EventSegment(SegmentType type, SoundFile file, double duration) {
        this.type = type;
        this.file = file;
        this.duration = duration;            
    }

    public SoundFile getFile() {
        if(type == SegmentType.PLAYLIST)
            return playList.getRandomSoundFile();
        
        return file;
    }

    /**
     * Tries to change the given file. Will return false if the event segment is 
     * running.
     * @param file
     * @return True if the file was changed
     */
    public boolean setFile(SoundFile file) {
        if(!running)
            this.file = file;
        return !running;
    }

    /**
     * Gives the length of time the given segment will be played.
     * @return 
     */
    public double getDuration() {
        if(duration > 0.0)
            return duration;
        
        if(file != null && type == SegmentType.SOUND)
            return file.getPlayTime();
        else
            return 0;
    }

    /**
     * Sets the duration of the given segment. Duration can not be changed if
     * the event is running. If the duration is zero or less and the type of segment 
     * is Sound the duration will become the length of the sound clip. If the type is
     * silence the duration cannot be 0 or less.
     * @param duration 
     * @return
     */
    public boolean setDuration(double duration) {
        if(running)
            return false;
        
        if(duration <= 0 && (type == SegmentType.SILENCE || type == SegmentType.PLAYLIST))
            return false;
        
        if(duration <= 0.0)
            this.duration = file.getPlayTime();
        else
            this.duration = duration;
        
        return true;
    }
    
    public boolean isRunning(){
        return running;
    }
    
    public void setRunning(boolean b){
        running = b;
    }
    
    public EventSegment clone(){
        EventSegment cl = new EventSegment(type, file, duration);
        
        if(playList != null)
            cl.setPlayList(playList);
        
        return cl;
    }

    public SegmentType getType() {
        return type;
    }

    public void setType(SegmentType type) {
        if(!running)
            this.type = type;
    }
    
    /**
     * Removes the given sound file from the segment. If this segment is using the
     * removed file, the type will be changed to SILENCE. Will not remove the file if 
     * the segment is running.
     * @param file file to remove
     */
    public void removeFile(SoundFile file){
        if(running)
            return;
        
        if(type == SegmentType.SOUND && this.file.equals(file)){
            if(duration <= 0.0)
                duration = file.getPlayTime();
            
            this.file = null;
            type = SegmentType.SILENCE;            
        }
    }
    
    public PlayList getPlayList(){
        return playList;              
    }
    
    /**
     * Removes the given PlayList from the segment. If this segment is using the
     * removed play list, the type will be changed to SILENCE. Will not remove the PlayList
     * if the segment is running.
     * @param list PlayList to try to remove
     */
    public void removePlayList(PlayList list){
        if(running)
            return;
        
        if(type == SegmentType.PLAYLIST && playList.equals(list)){
            playList = null;
            type = SegmentType.SILENCE;
        }
        
    }

    public boolean setPlayList(PlayList playList) {
        if(!running)
            this.playList = playList;
        return !running;
    }
    
    
}
