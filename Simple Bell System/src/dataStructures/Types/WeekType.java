/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.Types;

import exceptions.IncorrectVersionException;
import helperClasses.Helper;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.concurrent.locks.StampedLock;

/**
 *
 * @author Quinten Holmes
 */
public abstract class WeekType implements Serializable{
    protected static final long serialVersionUID = 3;
    
    protected String name;
    protected LocalDate date;
    protected int version = 0;
    protected StampedLock lock;
    protected final long ID;
    
    public WeekType(long ID){
        this.ID = ID;
        check();
    }
    
    public String getWeekName() throws InterruptedException {
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(lock);
            return name;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public void setWeekName(int version, String weekName) throws InterruptedException, IncorrectVersionException {
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.name = weekName;
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
        final WeekType other = (WeekType) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }
    
    
    
}
