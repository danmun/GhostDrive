package driveghost;

/**
 * @author Daniel Munkacsi
 */
public class Main {
    
    public static void main(String[] args) throws InterruptedException{
        if(args.length == 0){
            new GUI().run();
        }else{
            final String START_CONSOLE = "-cmd";
            String arg1 = args[0];
            if(arg1.equals(START_CONSOLE)){
                new Console().run();
            }else{
                System.out.println("Invalid argument: " + arg1 + "\r\n" + "Acceptable arguments: " + START_CONSOLE);
            }
        }
    }
}