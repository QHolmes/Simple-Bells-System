/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import exceptions.IncorrectVersionException;
import helperClasses.Helper;
import java.io.Serializable;
import java.util.concurrent.locks.StampedLock;

/**
 *
 * @author Quinten Holmes
 */
public class EventSegment implements Serializable{
    private static final long serialVersionUID = 3;
    
    private SegmentType type;
    private long fileID;
    private double duration;
    private boolean running;
    private long playListID;
    private int version = 0;
    private final long segID;
    private StampedLock lock;
    

    public EventSegment(SegmentType type, long segID, long fileID, double duration) {
        this.type = type;
        this.fileID = fileID;
        this.duration = duration;   
        this.segID = segID;
        lock = new StampedLock();
    }

    public SoundFile getFile(RegionDataCore data) throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            if(type == SegmentType.PLAYLIST){
                PlayList pl = data.getPlayList(playListID);
                return pl.getRandomSoundFile();
            }

            SoundFile file = data.getMusicFile(fileID);
            
            if(file == null)
                file = data.getBellFile(fileID); 
            
            return file;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    /**
     * Tries to change the given file.Will return false if the event segment is 
     * running.
     * @param version
     * @param fileID
     * @return True if the file was changed
     * @throws java.lang.InterruptedException
     * @throws exceptions.IncorrectVersionException
     */
    public boolean setFile(int version, long fileID) throws InterruptedException, IncorrectVersionException {
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            if(!running)
                this.fileID = fileID;
            this.version++;
            return !running;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }

    /**
     * Gives the length of time, in seconds, the given segment is set to be played.
     * @param data
     * @return time in seconds
     * @throws java.lang.InterruptedException
     */
    public double getDuration(RegionDataCore data) throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            if(duration > 0.0)
                return duration;

            if(type == SegmentType.SOUND){
                SoundFile file = data.getMusicFile(fileID);
            
                if(file == null)
                    file = data.getBellFile(fileID);
                
                if(file != null)
                    return file.getPlayTime();
                else
                    return 0;
            }
            else
                return 0;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    /**
     * Sets the duration of the given segment.Duration can not be changed if
 the event is running.If the duration is zero or less and the type of segment 
 is Sound the duration will become the length of the sound clip.If the type is
 silence the duration cannot be 0 or less.
     * @param version
     * @param duration 
     * @param data 
     * @return
     * @throws exceptions.IncorrectVersionException
     * @throws java.lang.InterruptedException
     */
    public boolean setDuration(int version, double duration, RegionDataCore data) throws IncorrectVersionException, InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.version++;
            if(running)
                return false;

            if(duration <= 0 && (type == SegmentType.SILENCE || type == SegmentType.PLAYLIST))
                return false;

            if(duration <= 0.0){
                SoundFile file = data.getMusicFile(fileID);
            
                if(file == null)
                    file = data.getBellFile(fileID);
                
                this.duration = file.getPlayTime();
            }
            else
                this.duration = duration;

            return true;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    public boolean isRunning() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return running;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    public void setRunning(int version, boolean b) throws InterruptedException, IncorrectVersionException{
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.version++;
            running = b;
        }finally{
            if(stamp != 0);
            lock.unlockWrite(stamp);
        }
    }
    
    public EventSegment clone(long newID){
        EventSegment cl = new EventSegment(type, newID, fileID, duration);
        
        while(true){
                try {
                    cl.setPlayList(cl.getVersion(), playListID);
                    break;
                } catch (InterruptedException | IncorrectVersionException ex) {}
            }
        
        
        return cl;
    }

    public SegmentType getType() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return type;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public void setType(int version, SegmentType type) throws InterruptedException, IncorrectVersionException {
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.version++;
            if(!running)
                this.type = type;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    /**
     * Removes the given sound file from the segment.If this segment is using the
     * removed file, the type will be changed to SILENCE.Will not remove the file if 
     * the segment is running.
     * @param version
     * @param fileID file to remove
     * @throws java.lang.InterruptedException
     * @throws exceptions.IncorrectVersionException
     */
    public void removeFile(int version, long fileID) throws InterruptedException, IncorrectVersionException{
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.version++;
            
            if(running)
                return;

            if(type == SegmentType.SOUND && this.fileID == fileID){

                this.fileID = 0;
                type = SegmentType.SILENCE;            
            }
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    public long getPlayListID() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return playListID;   
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    /**
     * Removes the given PlayList from the segment.If this segment is using the
 removed play list, the type will be changed to SILENCE.Will not remove the PlayList
 if the segment is running.
     * @param version
     * @param listID PlayList to try to remove
     * @throws exceptions.IncorrectVersionException
     * @throws java.lang.InterruptedException
     */
    public void removePlayList(int version, long listID) throws IncorrectVersionException, InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.version++;
            
            if(running)
                return;

            if(type == SegmentType.PLAYLIST && playListID == listID){
                playListID = 0;
                type = SegmentType.SILENCE;
            }
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
        
    }

    public boolean setPlayList(int version, long playListID) throws IncorrectVersionException, InterruptedException { long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            if(!running)
                this.playListID = playListID;
            return !running;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    public int getVersion() throws InterruptedException {
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(lock);
            return version;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    protected void checkVersion(int version) throws IncorrectVersionException{
        if(version != this.version)
                throw new IncorrectVersionException(String.format("The given version %d does not match the current version %d.%n", 
                        version, this.version));
    }
    
    public long getID(){
        return segID;
    }
    
    
}
