/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.schedules;

import dataStructures.EventSegment;
import exceptions.StartDateInPast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;

/**
 *
 * @author Quinten Holmes
 */
public class ScheduledEvent implements Serializable{
    private static final long serialVersionUID = 1;
    
    private Date startTime;
    private ArrayList<EventSegment> segments;
    private StampedLock lock;
    private boolean cancelled = false;
    private boolean running = false;

    /**
     * A bell event is a sequence of files to be played.When the start time is reached
     * it will play or skip the pre bell, then it will play or skip the song, lastly it will 
     * play or skip the post bell.No files are required... however that seems silly
     * @param startTime This is when the event should start. Cannot be null or in the past.
     * @throws exceptions.StartDateInPast
     */
    public ScheduledEvent(Date startTime ) throws StartDateInPast {
        if(startTime == null)
            throw new NullPointerException("Start time was not specified");
        
        if(startTime.before(new Date()))
            throw new StartDateInPast();
                    
        this.startTime = startTime;
        segments = new ArrayList();
        
        lock = new StampedLock();
    }

    public Date getStartTime() {
        return new Date(startTime.getTime());
    }
    
    public Date getStopTime(){
        Calendar ca = Calendar.getInstance();
        ca.setTime(startTime);
        ca.add(Calendar.SECOND, (int) Math.ceil(getDuration()));
        return ca.getTime();
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
    
    /**
     * Tries to add the given segment to the list at the given spot. If the event
     * is running, the segment list cannot be modified.
     * @param seg
     * @param order
     * @return True if the segment was added, else false
     */
    public boolean addSegment(EventSegment seg, int order){
        if(!running){
            long stamp = lock.writeLock();
            segments.add(order, seg);
            lock.unlockWrite(stamp);
        }
        
        return !running;
    }
    
    /**
     * Returns a shallow copy of all the segments.
     * @return list of all segments for this event
     */
    public ArrayList<EventSegment> getSegments(){
        ArrayList<EventSegment> segs = new ArrayList();
        {
            long stamp = lock.readLock();
            segs.addAll(segments);
            lock.unlockRead(stamp);
        }
        return segs;
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
            long stamp = lock.readLock();
            boolean rt = segments.remove(seg);
            lock.unlockRead(stamp);
            return rt;
        }
        else
            return false;
    }
    
    @Override
    public boolean equals(Object o){
        if( o instanceof ScheduledEvent)
            return this.startTime.compareTo(((ScheduledEvent) o).startTime) == 0;
        else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.startTime);
        return hash;
    }
    
    public int compareTo(ScheduledEvent o){
        return startTime.compareTo(o.startTime);
    }
    
    public boolean checkOverlap(ScheduledEvent e){
        Date start = new Date(startTime.getTime());
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.SECOND, (int) Math.ceil(getDuration()));
        
        Date end = calendar.getTime();
        Date time = e.getStartTime();
        
        
        return time.after(start) && time.before(end);            
    }
    
    public long readLockSegments(){
        return lock.readLock();
    }
    
    public void unlockReadLockSegments(long stamp){
        lock.unlockRead(stamp);
    }
    
}
