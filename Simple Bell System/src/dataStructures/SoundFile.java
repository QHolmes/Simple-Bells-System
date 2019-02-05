/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import javafx.scene.media.Media;

/**
 *
 * @author Quinten Holmes
 */
public class SoundFile implements Serializable{
    private static final long serialVersionUID = 1;
    
    private Double playtime;
    private String filePath;
    private String fileName;
    
    public SoundFile(String filePath, String fileName){
        this.fileName = fileName;
        File file = new File(filePath);
        this.filePath = file.toURI().toString();
        Media md = new Media(this.filePath);
        playtime = md.getDuration().toSeconds();
    }
    
    public SoundFile(URL url, String fileName){
        this.fileName = fileName;
        this.filePath = url.toExternalForm();
        Media md = new Media(url.toExternalForm());
        playtime = md.getDuration().toSeconds();
    }
    
    public SoundFile(File file){
        this.fileName = file.getName();
        this.filePath = file.toURI().toString();
        Media md = new Media(this.filePath);
        playtime = md.getDuration().toSeconds();
    }
    
    /**
     * Returns the playtime of the given file in seconds.
     * @return 
     */
    public Double getPlayTime(){
        return playtime;
    }
    
    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }
    
    public Media getMedia(){
        return new Media(filePath);
    }
    
    @Override
    public String toString(){
        return fileName;
    }
}
