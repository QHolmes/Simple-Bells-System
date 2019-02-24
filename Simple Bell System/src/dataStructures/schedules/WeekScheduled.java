/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.schedules;

import dataStructures.PlayList;
import dataStructures.SoundFile;
import dataStructures.Types.WeekType;
import dataStructures.templates.WeekTemplate;
import helperClasses.Helper;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 *
 * @author Quinten Holmes
 */
public class WeekScheduled extends WeekType{
    
    private final DayScheduled[] days = new DayScheduled[7];

    public WeekScheduled(LocalDate date) {
        super();
        this.date = date;
        
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate local = date.with(weekFields.dayOfWeek(), 1);
        
        
        for(int i = 0; i < 7; i++){
            days[i] = new DayScheduled(local);
            local = local.minusDays(-1);
        }
        check();
    }
    
    public WeekScheduled(String weekName, LocalDate date){
        this(date);
        this.name = weekName;
    }
    
    public DayScheduled[] getDays() throws InterruptedException{
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(dayLock);
            DayScheduled[] array = new DayScheduled[7];
            System.arraycopy(days, 0, array, 0, 7);
            return array;
        }finally{
            if(stamp != 0)
                dayLock.unlockRead(stamp);
        }
    }
    
    public void setDay(DayScheduled day) throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(dayLock);
            int index = day.getDay().get(WeekFields.of(Locale.getDefault()).dayOfWeek()) - 1;
            days[index] = day;
        }finally{
            if(stamp != 0)
                dayLock.unlockWrite(stamp);
        }
    }
    
    public DayScheduled getDay(int i) throws InterruptedException{
        long stamp = 0;
        if(i >= 1 && i <= 7){
            try{
                stamp = Helper.getReadLock(dayLock);
                return days[i - 1];
            }finally{
                if(stamp != 0)
                    dayLock.unlockRead(stamp);
            }
        }
        
        throw new java.lang.IndexOutOfBoundsException("Day needs to be between 1-7");
    }    
     
    public LocalDate getDate() throws InterruptedException{
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(timeLock);
            return date;
        }finally{
            if(stamp != 0)
                timeLock.unlockRead(stamp);
        }
    }
    
    public WeekTemplate getTemplate() throws InterruptedException{
        WeekTemplate wTemp = new WeekTemplate();
        
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(dayLock);
            for(int i = 0; i < 7; i++)
                wTemp.setDay(days[i].createTemplate(), i + 1);
        }finally{
            if(stamp != 0)
                dayLock.unlockRead(stamp);
        }
        
        return wTemp;
    }
    
    public void removeFile(SoundFile file) throws InterruptedException{
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(dayLock);
            for(int i = 0; i < 7; i++)
                days[i].removeFile(file);  
        }finally{
            if(stamp != 0)
                dayLock.unlockWrite(stamp);
        }  
    }
    
    public void removePlayList(PlayList list) throws InterruptedException{
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(dayLock);
            for(int i = 0; i < 0; i++)
                days[i].removePlayList(list); 
        }finally{
            if(stamp != 0)
                dayLock.unlockWrite(stamp);
        }  
    }
    
}
