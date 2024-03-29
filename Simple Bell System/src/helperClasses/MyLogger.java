/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helperClasses;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JFileChooser;

/**
 * http://www.vogella.com/tutorials/Logging/article.html
 * @author Lars Vogel
 */
public class MyLogger {
    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;

    static private FileHandler fileHTML;
    static private Formatter formatterHTML;

    static public void setup() throws IOException {

        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        // suppress the logging output to the console
        
        Handler[] hands = logger.getHandlers();
        
        if(hands.length > 0)
            for (Handler h: hands) 
                h.close();
        
        /*
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }
        */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
            String date = simpleDateFormat.format(new Date());
        
        String locateTxt = System.getProperty("user.home") + "/Library/JavaSimpleBells/Logs/Log-" + date + ".txt";
        String locateHTML = System.getProperty("user.home") + "/Library/JavaSimpleBells/Logs/Log-" + date + ".html";
        
        String fileLocation = System.getProperty("user.home") + "/Library/JavaSimpleBells/Logs";
        
        File directory = new File(fileLocation);
        
        if(! directory.exists())
            directory.mkdir();
            
        logger.setLevel(Level.FINE);
        try{
            fileTxt = new FileHandler(locateTxt, true);
            fileHTML = new FileHandler(locateHTML, true);
        }catch(NoSuchFileException e){
            directory.mkdir();
            fileTxt = new FileHandler(locateTxt, true);
            fileHTML = new FileHandler(locateHTML, true);
        }

        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);

        // create an HTML formatter
        formatterHTML = new MyHtmlFormatter();
        fileHTML.setFormatter(formatterHTML);
        logger.addHandler(fileHTML);
    }
}

class MyHtmlFormatter extends Formatter {
    // this method is called for every log records
    public String format(LogRecord rec) {
        StringBuffer buf = new StringBuffer(1000);
        buf.append("<tr>\n");

        // colorize any levels
        
        if (rec.getLevel().intValue() == Level.SEVERE.intValue()) {
            buf.append("\t<td style=\"color:red\">");
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("</b>");
        }else if (rec.getLevel().intValue() == Level.WARNING.intValue()) {
            buf.append("\t<td style=\"color:yellow\">");
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("</b>");
        }else if(rec.getLevel().intValue() == Level.INFO.intValue()){
            buf.append("\t<td style=\"color:green\">");
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("</b>");
        }else if(rec.getLevel().intValue() == Level.FINE.intValue()){
            buf.append("\t<td style=\"color:blue\">");
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("</b>");
        }else if(rec.getLevel().intValue() == Level.FINER.intValue()){
            buf.append("\t<td style=\"color:orange\">");
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("</b>");
        }else if(rec.getLevel().intValue() == Level.FINEST.intValue()){
            buf.append("\t<td style=\"color:purple\">");
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("</b>");
        }else{
            buf.append("\t<td>");
            buf.append(rec.getLevel());
        }

        buf.append("</td>\n");
        buf.append("\t<td>");
        buf.append(calcDate(rec.getMillis()));
        buf.append("</td>\n");
        buf.append("\t<td>");
        buf.append(formatMessage(rec));
        buf.append("</td>\n");
        buf.append("</tr>\n");

        return buf.toString();
    }

    private String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }

    // this method is called just after the handler using this
    // formatter is created
    public String getHead(Handler h) {
        return "<!DOCTYPE html>\n<head>\n<style>\n"
            + "table { width: 100% }\n"
            + "th { font:bold 10pt Tahoma; }\n"
            + "td { font:normal 10pt Tahoma; }\n"
            + "h1 {font:normal 11pt Tahoma;}\n"
            + "</style>\n"
            + "</head>\n"
            + "<body>\n"
            + "<h1>" + (new Date()) + "</h1>\n"
            + "<table border=\"0\" cellpadding=\"5\" cellspacing=\"3\">\n"
            + "<tr align=\"left\">\n"
            + "\t<th style=\"width:10%\">Loglevel</th>\n"
            + "\t<th style=\"width:15%\">Time</th>\n"
            + "\t<th style=\"width:75%\">Log Message</th>\n"
            + "</tr>\n";
      }

    // this method is called just after the handler using this
    // formatter is closed
    public String getTail(Handler h) {
        return "</table>\n</body>\n</html>";
    }
}
