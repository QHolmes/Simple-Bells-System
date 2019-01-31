/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import exceptions.StartDateInPast;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Quinten Holmes
 */
public class BellEvent {
    private final Date startTime;
    private final boolean playPreBell;
    private final BellFile preBell;
    private final boolean playPostBell;
    private final BellFile postBell;
    private final boolean playSong;
    private final int songDuration;
    private final SongFile song;
    private int playTime;
    private boolean cancelled = false;
    private boolean running = false;

    /**
     * A bell event is a sequence of files to be played.When the start time is reached
     * it will play or skip the pre bell, then it will play or skip the song, lastly it will 
     * play or skip the post bell. No files are required... however that seems silly
     * @param startTime This is when the event should start. Cannot be null or in the past.
     * @param playPreBell If the pre bell should be played
     * @param preBell Only needed if playPreBell is true. 
     * @param playPostBell True if the post bell should play
     * @param postBell Only needed if playPostBell is ture
     * @param playSong True if the song should be played
     * @param songDuration The length of time to play the song, if the value is less then zero
     * then the song will play until finished.
     * @param song Only needed if playSong is true.
     * @throws exceptions.StartDateInPast
     */
    public BellEvent(Date startTime, boolean playPreBell, BellFile preBell, boolean playPostBell, BellFile postBell, boolean playSong, int songDuration, SongFile song) throws StartDateInPast {
        if(startTime == null)
            throw new NullPointerException("Start time was not specified");
        
        if(startTime.before(new Date()))
            throw new StartDateInPast();
                    
        this.startTime = startTime;
        
        if(playPreBell && preBell == null)
            throw new NullPointerException("Pre bell BellFile missing");
        
        this.playPreBell = playPreBell;
        this.preBell = preBell;
        
        if(playPostBell && postBell == null)
            throw new NullPointerException("Post bell BellFile missing");
        this.playPostBell = playPostBell;
        this.postBell = postBell;
        
        if(playSong && song == null)
            throw new NullPointerException("Music file missing");
        
        if(playSong){
            if(songDuration == 0)
                this.playSong = false;
            else
                this.playSong = playSong;
            
            if(songDuration > 0)
                this.songDuration = songDuration;
            else
                this.songDuration = song.getPlayTime();
        }else{
            this.playSong = false;
            this.songDuration = 0;
        }
        
        this.song = song;
        
        playTime = 0;
        
        if(playPreBell)
            playTime += preBell.getPlayTime();
        
        if(playPostBell)
            playTime += postBell.getPlayTime();
        
        if(playSong)
            playTime += this.songDuration;
    }

    public Date getStartTime() {
        return startTime;
    }

    public int getSongDuration() {
        return songDuration;
    }

    public SongFile getSong() {
        return song;
    }

    public boolean isPlayPreBell() {
        return playPreBell;
    }

    public BellFile getPreBell() {
        return preBell;
    }

    public boolean isPlayPostBell() {
        return playPostBell;
    }

    public BellFile getPostBell() {
        return postBell;
    }

    public boolean isPlaySong() {
        return playSong;
    }
    
    public int totalPlayTime(){
        return playTime;
    }
    
    public boolean isCancelled(){
        return cancelled;
    }
    
    public void cancelEvent(){
        cancelled = true;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
    
    @Override
    public boolean equals(Object o){
        if( o instanceof BellEvent)
            return this.startTime.compareTo(((BellEvent) o).startTime) == 0;
        else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.startTime);
        return hash;
    }
    
    public int compareTo(BellEvent o){
        return startTime.compareTo(o.startTime);
    }
    
}
