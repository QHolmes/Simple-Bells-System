/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helperClasses;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Quinten Holmes
 */
public class Helper {
    
    public static boolean isSameWeek(Date d1, Date d2){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) 
                && c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR));
    }
    
    public static String dayOfWeekName(int i){
        switch(i){
            case(1):
                return "Sunday";
            case(2):
                return "Monday";
            case(3):
                return "Tuesday";
            case(4):
                return "Wednesday";
            case(5):
                return "Thursday";
            case(6):
                return "Friday";
            case(7):
                return "Saturday";
            default:
                return "Not a day";
        }
    }
}
