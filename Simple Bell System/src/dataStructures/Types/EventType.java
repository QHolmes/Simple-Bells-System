/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.Types;

import dataStructures.EventSegment;
import dataStructures.PlayList;
import dataStructures.SoundFile;
import helperClasses.Helper;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.locks.StampedLock;

/**
 *
 * @author Quinten Holmes
 */
public abstract class EventType implements Serializable{
    protected static final long serialVersionUID = 1;
    
    protected ArrayList<EventSegment> segments;
    protected StampedLock segmentLock;
    protected StampedLock varLock;
    protected StampedLock timeLock;
    protected boolean cancelled = false;
    protected boolean running = false;
    
    public EventType(){
        check();
    }
    
    /**
     * Tries to add the given segment to the list at the given spot. If the event
     * is running, the segment list cannot be modified.
     * @param seg
     * @param order
     * @return True if the segment was added, else false
     */
    public boolean addSegment(EventSegment seg, int order){
        if(!running){
            long stamp = segmentLock.writeLock();
            segments.add(order, seg);
            segmentLock.unlockWrite(stamp);
        }
        
        return !running;
    }
    
    /**
     * Tries to add the given segment to the list at the given spot. If the event
     * is running, the segment list cannot be modified.
     * @param seg
     * @return True if the segment was added, else false
     */
    public boolean addSegment(EventSegment seg){
        if(!running){
            long stamp = segmentLock.writeLock();
            segments.add(seg);
            segmentLock.unlockWrite(stamp);
        }
        
        return !running;
    }
    
    /**
     * Returns a shallow copy of all the segments.
     * @return list of all segments for this event
     */
    public ArrayList<EventSegment> getSegments() throws InterruptedException{
        ArrayList<EventSegment> segs = new ArrayList();
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(segmentLock);
            segs.addAll(segments);
        }finally{
            if(stamp != 0)
                segmentLock.unlockRead(stamp);
        }
        
        return segs;
    }
    /**
     * Returns the total length of time, in seconds, the event will take to 
     * complete.
     * @return length of time in seconds
     */
    public double getDuration(){
        double length = 0;
        
        length = segments.stream().map((s) -> s.getDuration())
                .reduce(length, (accumulator, _item) -> accumulator + _item);
        
        return length;
    }
    
    
    /**
     * Tries to remove the given segment from the event. Will return false is the
     * segment could not be removed. Segments cannot be removed if the event is 
     * running.
     * @param seg
     * @return 
     */
    public boolean removeSegment(EventSegment seg){
        
        if(!running){
            long stamp = segmentLock.readLock();
            boolean rt = segments.remove(seg);
            segmentLock.unlockRead(stamp);
            return rt;
        }
        else
            return false;
    }
    
    public abstract boolean checkOverlap(EventType ev) throws InterruptedException;
    public abstract int compareTo(EventType o);
    
    
    public long readLockSegments() throws InterruptedException{
        return Helper.getReadLock(segmentLock);
    }
    
    public void unlockReadLockSegments(long stamp){
        segmentLock.unlockRead(stamp);
    }
    
    public void removeFile(SoundFile file){
        segments.forEach(s -> s.removeFile(file));
    }
    
    public void removePlayList(PlayList list){
        segments.forEach(s -> s.removePlayList(list));
    }
    
    public void check(){
        if(segmentLock == null)
            segmentLock = new StampedLock();
        
        if(varLock == null)
            varLock= new StampedLock();
        
        if(timeLock == null)
            timeLock = new StampedLock();
    }
    
}
