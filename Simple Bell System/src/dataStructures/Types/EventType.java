/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.Types;

import dataStructures.RegionDataCore;
import dataStructures.EventSegment;
import dataStructures.PlayList;
import exceptions.IncorrectVersionException;
import helperClasses.Helper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quinten Holmes
 */
public abstract class EventType implements Serializable{
    protected static final long serialVersionUID = 3;
    
    protected ArrayList<Long> segments;
    protected StampedLock lock;
    protected boolean cancelled = false;
    protected boolean running = false;
    protected int version = 0;
    protected final long ID;
    
    public EventType(long ID){
        this.ID = ID;
        check();
    }
    
    /**
     * Tries to add the given segment to the list at the given spot.If the event
     * is running, the segment list cannot be modified.
     * @param version the last known version of the event
     * @param segID ID of segment to add
     * @param order index segment should be added at, starts at 0
     * @return True if the segment was added, else false
     * @throws exceptions.IncorrectVersionException
     */
    public boolean addSegment(int version, long segID, int order) throws IncorrectVersionException{
        if(!running){
            long stamp = lock.writeLock();
            checkVersion(version);
            segments.add(order, segID);
            this.version++;
            lock.unlockWrite(stamp);
        }
        
        return !running;
    }
    
    /**
     * Tries to add the given segment to the list at the end of the list.If the event
     * is running, the segment list cannot be modified.
     * @param version
     * @param segID
     * @return True if the segment was added, else false
     * @throws exceptions.IncorrectVersionException
     */
    public boolean addSegment(int version, long segID) throws IncorrectVersionException{
        if(!running){
            long stamp = lock.writeLock();
            checkVersion(version);
            segments.add(segID);
            this.version++;
            lock.unlockWrite(stamp);
        }
        
        return !running;
    }
    
    /**
     * Returns a shallow copy of all the segments.
     * @return list of all segments for this event
     * @throws java.lang.InterruptedException
     */
    public ArrayList<Long> getSegments() throws InterruptedException{
        ArrayList<Long> segs = new ArrayList();
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            segs.addAll(segments);
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
        
        return segs;
    }
    /**
     * Returns the total length of time, in seconds, the event will take to 
     * complete.
     * @param data
     * @return length of time in seconds
     */
    public double getDuration(RegionDataCore data){
        double length = 0;
        
        EventSegment s;
        
        for(long e: segments){
            while(true){
                try {
                    s = data.getSegment(e);
                    
                    if(s != null)
                        length += s.getDuration(data);
                    break;
                } catch (InterruptedException ex) {}
            }
        }
        
        return length;
    }
    
    
    /**
     * Tries to remove the given segment from the event. Will return false is the
     * segment could not be removed. Segments cannot be removed if the event is 
     * running.
     * @param seg
     * @return 
     */
    public boolean removeSegment(int version, EventSegment seg) throws IncorrectVersionException{
        
        if(!running){
            long stamp = lock.writeLock();
            checkVersion(version);
            boolean rt = segments.remove(seg);
            this.version++;
            lock.unlockWrite(stamp);
            return rt;
        }
        else
            return false;
    }
    
    public abstract boolean checkOverlap(EventType ev, RegionDataCore data) throws InterruptedException;
    public abstract int compareTo(EventType o);
    
    public void removeFile(int version, long fileID, RegionDataCore data) throws InterruptedException, IncorrectVersionException{
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            segments.forEach(id -> {
                while(true)
                    try {
                        EventSegment s = data.getSegment(id);
                        if(s != null)
                            s.removeFile(s.getVersion(), fileID);
                        return;
                    } catch (InterruptedException | IncorrectVersionException ex) {
                        Logger.getLogger(EventType.class.getName()).log(Level.SEVERE, null, ex);
                    } 
            });
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    public void removePlayList(int version, long listID, RegionDataCore data) throws IncorrectVersionException, InterruptedException{
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            EventSegment s;
            
            for(long e: segments){                
                while(true)
                    try{
                        s = data.getSegment(e);
                        if(s != null)
                            s.removePlayList(s.getVersion(), listID);
                        break;
                    }catch(IncorrectVersionException | InterruptedException ex){}
            }
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    final public void check(){
        if(lock == null)
            lock = new StampedLock();
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
        return ID;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (this.ID ^ (this.ID >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventType other = (EventType) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }
    
    
    
}
