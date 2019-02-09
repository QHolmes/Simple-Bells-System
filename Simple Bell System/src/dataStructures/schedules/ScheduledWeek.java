/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.schedules;

import dataStructures.PlayList;
import dataStructures.SoundFile;
import dataStructures.templates.WeekTemplate;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Quinten Holmes
 */
public class ScheduledWeek implements Serializable{
    private static final long serialVersionUID = 1;
    
    private ScheduledDay[] days = new ScheduledDay[7];
    private String weekName;
    private Date date;

    public ScheduledWeek(Date date) {
        this.date = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
        int year = cal.get(Calendar.YEAR);
        cal.clear();
        cal.setWeekDate(year, weekOfYear, 1);
        
        for(int i = 0; i < 7; i++){
            days[i] = new ScheduledDay(cal.getTime());
            cal.add(Calendar.DATE, 1);
        }
    }
    
    public ScheduledWeek(String weekName, Date date){
        this(date);
        this.weekName = weekName;
    }

    public String getWeekName() {
        return weekName;
    }

    public void setWeekName(String weekName) {
        this.weekName = weekName;
    }
    
    public ScheduledDay[] getDays(){
        ScheduledDay[] array = new ScheduledDay[7];
        System.arraycopy(days, 0, array, 0, 7);
        return array;
    }
    
    public void setDay(ScheduledDay day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(day.getDay());
        int index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        days[index] = day;
    }
    
    public ScheduledDay getDay(int i){
        if(i >= 1 && i <= 7)
            return days[i - 1];
        
        throw new java.lang.IndexOutOfBoundsException("Day needs to be between 1-7");
    }    
     
    public Date getDate(){
        return date;
    }
    
    public WeekTemplate getTemplate(){
        WeekTemplate wTemp = new WeekTemplate();
        
        for(int i = 0; i < 7; i++)
            wTemp.setDay(days[i].createTemplate(), i + 1);
        
        return wTemp;
    }
    
    public void removeFile(SoundFile file){
        for(int i = 0; i < 7; i++)
            days[i].removeFile(file);    
    }
    
    public void removePlayList(PlayList list){
        for(int i = 0; i < 0; i++)
            days[i].removePlayList(list);
    }
    
}
