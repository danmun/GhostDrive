package driveghost;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.swing.JProgressBar;

/**
 * 
 * @author Daniel Munkacsi
 */
public class DriveGhost {
    
    private File source;
    private File destination;
    private int fileCount;
    private int progressCount;
    private JProgressBar progress;
    private ListWriter saveMaker;
    
    public DriveGhost(){
        source = null;
        destination = null;
        progressCount = 0;
        fileCount = 0;
        progress = null;
        saveMaker = null;
    }    

    public void setSource(File f){
        source = f;
    }
    
    public File getSource(){
        return source;
    }
    
    public void setDestination(File f){
        destination = f;
        saveMaker = new ListWriter(destination.getAbsolutePath() + File.separator + "GHOST_CREATION_INFO.txt");
    }
    
    public File getDestination(){
        return destination;
    }
    
    /**
     * Start the ghosting process.
     */
    public void createGhost(){
        walk(source.getAbsolutePath(),destination);
    }
    
    /**
     * Recursively iterate through the files and directories represented by the given path.
     * For each file and directory, create the same entity at the location specified by the ghost argument.
     * @param path the path representing the source location
     * @param ghost the file representing the target directory
     */
    private void walk(String path, File ghost) {
        File root = new File(path);
        File[] list = root.listFiles();
        
        if (list == null) return;

        for (File f : list) {
            String filename = f.getName();
            String ghostpath = ghost.getAbsolutePath() + File.separator + filename;
            if ( f.isDirectory() ) {
                String abspath = f.getAbsolutePath();
                File tmp = new File(ghostpath);
                tmp.mkdir();
                walk(abspath,tmp);
                //System.out.println( "Dir:" + f.getAbsoluteFile() );
            } else {
                progressCount += 1;
                File tmp = new File(ghostpath);
                try{
                    tmp.createNewFile();
                } catch (IOException ex) {
                    System.err.println("Failed to create file.");
                    System.err.println(ex.getMessage());
                }
                updateProgress();
            }
        }
    }
    
    /**
     * Update the progress of the ghosting process.
     * If ran from command line, the progress will be displayed textually in the command line.
     * If ran from GUI, the progress will be displayed by a painted progress bar.
     */
    private void updateProgress(){
        int percent = getProgress();
        if(progress == null){
            String progr = "Progress: " + percent + "%";
            if(percent == 100){
                progr += " - FINISHED!";
                System.out.print("\r" + progr);
                System.out.println();
            }else{
                System.out.print("\r" + progr);
            }
        }else{
            progress.setValue(percent);
        }        
    }
    
    public void setProgressBar(JProgressBar jp){
        this.progress = jp;
    }
    
    /**
     * Get the total number of files at the source location.
     * @return the number of files
     */
    public int getFileCount(){
        fileCount = count(source);
        return fileCount;
    }
    
    /**
     * Get current progress of the ghost operation.
     * @return progress in percent
     */
    public int getProgress(){
        double percent = (progressCount*100) / fileCount;
        return (int) percent;
    }
    
    /**
     * Recursively count the number of files in the given directory.
     * @param file the file to count or directory to enter
     * @return the total number of files in the directory initially supplied
     */
    private int count(File file) {
        File[] files = file.listFiles();
        int count = 0;
        if(files == null) return count;
        for (File f : files)
            if (f.isDirectory()){
                count += count(f);
            }else{
                count++;
            }
        return count;
    }
    
    /**
     * Reset this DriveGhost object by re-initalising its instance variables.
     */
    public void reset(){
        source = null;
        destination = null;
        progressCount = 0;
        fileCount = 0;
        progress = null;
        saveMaker = null;
    }
    
    /**
     * Determine if the given string represents a valid file path.
     * @param loc the string to check
     * @return true if the file path is valid
     */
    public boolean isValidLocation(String loc){
        File f = new File(loc);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Get some information about the source drive/folder such as size and free space, and also 
     * include a timestamp indicating the last ghosting process.
     * @return the information about the source drive/folder
     */
    private String ghostData(){
        String date = new Date().toString();
        long totalspace = source.getTotalSpace();
        long freespace = source.getFreeSpace();
        long usedspace = totalspace - freespace;
        String totalspace_GB = String.format("%.2f", (double)((double)totalspace/1024.00/1024.00/1024.00)) + " gb";
        String freespace_GB = String.format("%.2f", (double)((double)freespace/1024.00/1024.00/1024.00)) + " gb";
        String usedspace_GB = String.format("%.2f", (double)((double)usedspace/1024.00/1024.00/1024.00)) + " gb";
        String data = date 
                + "\r\n" + "Total space: " + totalspace_GB 
                + "\r\n" + "Used space: "  + usedspace_GB 
                + "\r\n" + "Free space: " + freespace_GB;
        return data;
    }
    
    /**
     * Save data to text file.
     */
    public void saveGhostInfo(){
        saveMaker.writeString(ghostData(),ListWriter.WRITE);
    }
}