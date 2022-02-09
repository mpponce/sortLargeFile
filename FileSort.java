package paulaponce;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class has the main method to sort a file -
 * Esta clase principal tiene la invocación a los métodos para realizar el ordenamiento
 * 
 * @author M.Paula Ponce
 */
public class FileSort {

    /**
     * @param args 
     * First param is the path -
     * Second param inputFileName -
     * Third Param outPutFilename -
     * Both input and output files has to be at the same path
     */
    public static void main(String[] args) {
        try{
            String path = args[0];
            String inputfile = args[1];
            String outputfile = args[2];  
            
            File f = new File(path+inputfile);
            if (!f.exists()) {
                System.out.println("No se encuentra el archivo de entrada, verificar nombre o path");
            }else{
                try {
                    FileUtils.mergedFiles(FileUtils.proceso(path,inputfile),path,outputfile);
                } catch (IOException ex) {
                    Logger.getLogger(FileSort.class.getName()).log(Level.SEVERE, null, ex);
                }                  
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("You must enter the parameters");
        } 
    }
    
}
