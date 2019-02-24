/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.Types;

import helperClasses.Helper;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.concurrent.locks.StampedLock;

/**
 *
 * @author Quinten Holmes
 */
public abstract class WeekType implements Serializable{
    protected static final long serialVersionUID = 1;
    
    protected String name;
    protected LocalDate date;
    
    protected StampedLock dayLock;
    protected StampedLock varLock;
    protected StampedLock timeLock;
    
    public WeekType(){
        check();
    }
    
    public String getWeekName() throws InterruptedException {
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(varLock);
            return name;
        }finally{
            if(stamp != 0)
                varLock.unlockRead(stamp);
        }
    }

    public void setWeekName(String weekName) throws InterruptedException {
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(varLock);
            this.name = weekName;
        }finally{
            if(stamp != 0)
                varLock.unlockWrite(stamp);
        }
    }
    
    final public void check(){
        if(dayLock == null)
            dayLock = new StampedLock();
        
        if(varLock == null)
            varLock= new StampedLock();
        
        if(timeLock == null)
            timeLock = new StampedLock();
    }
    
}
