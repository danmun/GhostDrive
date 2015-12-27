package driveghost;

import java.awt.Toolkit;
import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 *
 * @author Daniel Munkacsi
 */
public class Console {
    private final String QUIT_COMMAND = "quit";
    private final String GHOST_COMMAND = "ghost";
    private final DriveGhost ghost;
    private final Scanner reader;
    
    public Console(){
        ghost = new DriveGhost();
        reader = new Scanner(System.in);
    }
    
    /**
     * Ask for inputs and create ghost. 
     * Print progress to console.
     */
    public void run(){
        System.out.println("Please enter 'quit' to exit the program during any of the input requests.\n");    
        String input = "";
        boolean moreGhostsWanted = true;
        while(moreGhostsWanted){
            boolean inputValid = false;
            while(!inputValid){
                input = getInput("Please enter the location of the drive or folder to ghost:");
                inputValid = ghost.isValidLocation(input) && new File(input).exists() && new File(input).isDirectory();
            }
            ghost.setSource(new File(input));
            System.out.println("Source of drive/folder to be ghosted: " + input);

            inputValid = false;
            while(!inputValid){
                input = getInput("Please enter the location to save the ghost to:");
                File tmp = new File(input);
                inputValid = ghost.isValidLocation(input) && ( tmp.exists() || (!tmp.exists() && tmp.mkdir()) );
            }
            ghost.setDestination(new File(input));
            System.out.println("Ghost will be saved to: " + input);

            while(true){
                input = getInput("Please enter \"" + GHOST_COMMAND + "\" to start the ghosting process:");
                if(input.equals(GHOST_COMMAND)) break;
            }

            System.out.println();
            try {
                getFileCount();
            } catch (InterruptedException | ExecutionException ex) {
                System.err.println("Failed to get number of files. Reason: ");
                System.err.print(ex.getMessage());
                return;
            }
            System.out.println("Starting ghosting process ...");
            ghost.createGhost();
            Toolkit.getDefaultToolkit().beep();
            ghost.saveGhostInfo();
            ghost.reset();
            System.out.println();
            System.out.println("Would you like to create another ghost ? (y/n): ");
            if(!reader.nextLine().equals("y")) moreGhostsWanted = false;
        }
        System.out.println("Exiting ...");
    }
   
    /**
     * Get the number of files at the location represented by the source in the DriveGhost object.
     * Display progress of the counting process.
     * 
     * @throws InterruptedException if 'sleep' was interrupted by any other threads
     * @throws ExecutionException if the current thread was interrupted while waiting (.get())
     */
    private void getFileCount() throws InterruptedException, ExecutionException {
        SwingWorker fileCounter;
        fileCounter = new SwingWorker<Integer, Integer>() {
            @Override
            protected Integer doInBackground() {
                Integer fileCount = ghost.getFileCount();
                return fileCount;
            }

            @Override
            protected void done() {
            }
        };
        fileCounter.execute();

        while(!fileCounter.isDone()){
            String info = "Counting files ";
            System.out.print("\r" + info);
            for(int i = 0; i < 4; i++){
                if(i != 0) System.out.print(".");
                Thread.sleep(650);
            }
            System.out.print("\r" + info + "   ");
        }
        System.out.println("\r" + "Number of files: " + fileCounter.get());
    }

    /**
     * Get input from user.
     * @param message string to display to user
     * @return the string the user has entered
     */
    private String getInput(String message) {
        System.out.print(message);
        String input = reader.nextLine();
        if(input.equals(QUIT_COMMAND)){
            ghost.reset();
            System.exit(0);
            return null;
        }else{
            return input;
        }        
    }
}