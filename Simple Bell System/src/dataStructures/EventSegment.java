/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

/**
 *
 * @author Quinten Holmes
 */
public class EventSegment {
    
    public final SegmentType type;
    private SoundFile file;
    private double duration;
    private boolean running;

    public EventSegment(SegmentType type, SoundFile file, double duration) {
        this.type = type;
        this.file = file;
        this.duration = duration;            
    }

    public SoundFile getFile() {
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
        if(duration > 0)
            return duration;
        
        if(file != null)
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
     */
    public boolean setDuration(double duration) {
        if(running)
            return false;
        
        if(duration <= 0 && type == SegmentType.SILENCE)
            return false;
        
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
        return cl;
    }
    
}
