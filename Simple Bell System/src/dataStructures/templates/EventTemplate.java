/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.templates;

import audioSystem.ScheduledEvent;
import dataStructures.SoundFile;
import exceptions.StartDateInPast;
import exceptions.TimeOutOfBounds;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author Quinten Holmes
 */
public class EventTemplate implements Serializable{
    private static final long serialVersionUID = 1;
    
    private int hour;
    private int minute;
    private int second;
    private boolean playPreBell;
    private boolean playPostBell;
    private boolean playSong;
    private SoundFile preBell;
    private SoundFile postBell;
    private SoundFile song;
    private String eventName;
    private double songDuration;

    public EventTemplate(int hour, int minute, int second, boolean playPreBell, boolean playPostBell, boolean playSong, SoundFile preBell, SoundFile postBell, SoundFile song, double songDuration) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.playPreBell = playPreBell;
        this.playPostBell = playPostBell;
        this.playSong = playSong;
        this.preBell = preBell;
        this.postBell = postBell;
        this.song = song;
        this.songDuration = songDuration;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) throws TimeOutOfBounds {
        if(hour > 23 || hour < 0)
            throw new TimeOutOfBounds("Hour needs to be between 0-23");
        
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) throws TimeOutOfBounds {
        if(minute > 59 || minute < 0)
            throw new TimeOutOfBounds("Minutes needs to be between 0-59");
        
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) throws TimeOutOfBounds {        
         if(second > 59 || second < 0)
            throw new TimeOutOfBounds("Seconds needs to be between 0-59");
        
        this.second = second;
    }

    public boolean isPlayPreBell() {
        return playPreBell;
    }

    public void setPlayPreBell(boolean playPreBell) {
        this.playPreBell = playPreBell;
    }

    public boolean isPlayPostBell() {
        return playPostBell;
    }

    public void setPlayPostBell(boolean playPostBell) {
        this.playPostBell = playPostBell;
    }

    public boolean isPlaySong() {
        return playSong;
    }

    public void setPlaySong(boolean playSong) {
        this.playSong = playSong;
    }

    public SoundFile getPreBell() {
        return preBell;
    }

    public void setPreBell(SoundFile preBell) {
        this.preBell = preBell;
    }

    public SoundFile getPostBell() {
        return postBell;
    }

    public void setPostBell(SoundFile postBell) {
        this.postBell = postBell;
    }

    public SoundFile getSong() {
        return song;
    }

    public void setSong(SoundFile song) {
        this.song = song;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public double getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(double songDuration) {
        if(song == null)
            throw new NullPointerException("No song set");
        
        if(songDuration > 0 && songDuration < song.getPlayTime())
            this.songDuration = songDuration;
    }
    
    public double getEventDuration(){
        double length = 0;
        
        if(playPreBell && preBell != null)
            length += preBell.getPlayTime();
        
        if(playPostBell && postBell != null)
            length += postBell.getPlayTime();
        
        if(playSong && song != null){
            if(songDuration <= 0)
                length += song.getPlayTime();
            else
                length += songDuration;
        }
        
        return length;
    }
    
    /**
     * Creates a ScheduledEvent for today's date at the given time.
     * @return 
     * @throws exceptions.StartDateInPast 
     */
    public ScheduledEvent createScheduledEvent() throws StartDateInPast{
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        
        return new ScheduledEvent(calendar.getTime(), 
                playPreBell, 
                preBell, 
                playPostBell, 
                postBell, 
                playSong, 
                songDuration, 
                song);
    }
    
    public int compareTo(EventTemplate e){
        return ((hour * 100) + minute) - ((e.getHour() * 100) + e.getMinute());
    }
    
    public boolean checkOverlap(EventTemplate e){
        double start = (hour * 100) + minute;
        double end = start + getEventDuration();
        double time = e.getHour() * 100 + e.getMinute();
        
        return time >= start && time <= end;            
    }
    
}
