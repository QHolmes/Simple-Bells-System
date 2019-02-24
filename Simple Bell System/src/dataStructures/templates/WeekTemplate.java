/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.templates;

import dataStructures.PlayList;
import dataStructures.SoundFile;
import dataStructures.Types.WeekType;
import dataStructures.schedules.WeekScheduled;
import helperClasses.Helper;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Quinten Holmes
 */
public class WeekTemplate extends WeekType{
    
    private DayTemplate[] days = new DayTemplate[7];

    public WeekTemplate() {
        super();
    }
    
    public WeekTemplate(String weekName){
        this();
        this.name = weekName;
    }
    
    public DayTemplate[] getDays() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(dayLock);
            
            DayTemplate[] array = new DayTemplate[7];
            System.arraycopy(days, 0, array, 0, 7);
            return array;
        }finally{
            if(stamp != 0)
                dayLock.unlockRead(stamp);
        }
    }

    public void setDay(DayTemplate day, int i){
        if(i >= 1 && i <= 7)
            days[i - 1] = day;
        else
            throw new java.lang.IndexOutOfBoundsException("Day needs to be between 1-7");
    }
    
    public DayTemplate getDay(int i){
        if(i >= 1 && i <= 7)
            return days[i - 1];
        
        throw new java.lang.IndexOutOfBoundsException("Day needs to be between 1-7");
    } 
    
    /**
     * Creates a scheduledWeek using the given template for the given range
     * @param weekOfYear the number of the week of the given year
     * @param year Year of the week
     * @return 
     * @throws java.lang.InterruptedException 
     */
    public WeekScheduled getScheduledWeek(int weekOfYear, int year) throws InterruptedException{
        
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate local = LocalDate.now()
                            .withYear(year)
                            .with(weekFields.weekOfYear(), weekOfYear)
                            .with(weekFields.dayOfWeek(), 1);
        
        WeekScheduled week = new WeekScheduled(name, local);
        
        for(int i = 0; i < 7; i++){
            if(days[i] != null)
                week.setDay(days[i].getScheduledDay(local));
            local = local.minusDays(-1);
        }
        
        return week;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
    public void removeFile(SoundFile file) throws InterruptedException{
        for(int i = 0; i < 0; i++)
            days[i].removeFile(file);    
    }
    
    public void removePlayList(PlayList list) throws InterruptedException{
        for(int i = 0; i < 0; i++)
            days[i].removePlayList(list);
    }
    
}
