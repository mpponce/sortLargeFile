package paulaponce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * This class has the necesary methods to sort a big inputFile, 
 * using the natural order for long numbers with a limited amount of memory(100Mb)-
 * Tiene los metodos necesarios para realizar el ordenamiento de un archivo 
 * de entrada de gran tamaño,no superando en ningún momento los 100MB de memoria
 * @author M.Paula Ponce
 */
public class FileUtils {
    private static final Comparator<String> cmp = (String s1, String s2)-> Long.compare(Long.parseLong(s1), Long.parseLong(s2));
    
    /**
     * agrega al TreeMap una tupla,
     * si la key existe agrega el BufferedReader br a la lista obtenida del value
     * caso contrario crea la lista con el unico valor de br
     * @param map
     * @param key
     * @param br
     * @return TreeMap
     */
    public static TreeMap<String, List<BufferedReader>> addBufferedReader(TreeMap<String, List<BufferedReader>> map,String key,BufferedReader br){
        List<BufferedReader> l;
        if (map.containsKey(key)) {
            l=((List)map.get(key));
        }else{
            l = new ArrayList<>();
        }
        l.add(br);
        map.put(key,l);   
        return map;
    }
    /**
     * Given a list of sorted files, 
     * return the merge with each other as a final sorted output file
     * @param files
     * @param path 
     * @param outFileName
     * @throws IOException 
     */
    public static void mergedFiles(final List<File> files, final String path,
                                final String outFileName) throws IOException{

	File f = new File(path+outFileName);
	if (f.exists()) {
            f.delete();
	}
        //to write the final sorted file
	BufferedWriter bw = new BufferedWriter(new FileWriter(path+outFileName, true)); 
        
        List<BufferedReader> bReaders = new ArrayList<>();
	TreeMap<String, List<BufferedReader>> map = new TreeMap<>(cmp);
        //uso tree map con Lista de BufferedRader como valor, porque si uso solo  
        //BufferedReader me elmininaba los duplicados, treeMap no los permite
        //para misma clave, entonces los guardo en la lista
        
        
	try{
            //put in TreeMap the first element of each sorted temp file
            //this are the lowest values
            //int totalLines=0;
            for (File file : files) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                bReaders.add(br);
                String line = br.readLine();
                map = addBufferedReader(map,line,br);
            //    totalLines++;
            }
            /** 
             * Map.Entry almacena la clave y el valor juntos en una clase, 
             * los obtenemos en una sola operación
             * pollFirstEmpty returns the removed first entry of this sorted map 
             * or null if it's empty
             * */    
            while (!map.isEmpty()) {
                Map.Entry<String, List<BufferedReader>> tuple = map.pollFirstEntry();
                List<BufferedReader> l = (List)tuple.getValue();
                //tengo que guardar el valor de la clave por cada elemento de la lista
                //significa que hay ocurrencias para el mismo key
                l.forEach(br->{try {
                    bw.write(tuple.getKey());bw.write("\r\n");
                    } catch (IOException ex) {
                        Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                    } });
                
                //as i remove the first entry, 
                //i have to put the next value of the br
                //(the next value of the temp sorted file)
                for (BufferedReader br : l) {
                    String line = br.readLine();
                    if (line != null) {
                        map = addBufferedReader(map,line,br);
                    }                    
                }

            }
            System.out.println("Archivo ordenado generado:"+path+outFileName);
        }
        //close things i used
        finally {
            if (bReaders != null) {
                //foreach lambda
                bReaders.forEach((br) -> {try {
                    br.close();
                    } catch (IOException ex) {
                        Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }});
            }
            if (bw != null) {
                bw.close();
            }
	}           
    }    
    /**
     * Given a large input file the process splits it into 
     * smaller sorted 100mB files and returns this list
     * @param path
     * @param fileName
     * @return List
     * @throws IOException 
     */
    public static List<File> proceso(String path, String fileName) throws IOException{
        
        List<File> files = new ArrayList<>();
        
        long sizeTmpFile = 10485760 ; //bytes equals to 100mB
        List<String> tmplist = new ArrayList<>();
        int totalLines=0;        
        
        //if i use a try-with-resources block the close method is done automatically 
        System.out.println(path+fileName);
        try(BufferedReader br = Files.newBufferedReader(Paths.get(path+fileName))){
            String line = "";   
            int nroFile = 0;
             while (line != null) {
                long currentSizeFile = 0;// in bytes
                while ((currentSizeFile< sizeTmpFile) && 
                       ((line = br.readLine()) != null)) {
                //int cant=0; //para probar con pocos registros
                //while ((cant< 3) && 
                //       ((line = br.readLine()) != null)) {  
                    tmplist.add(line);  
                    currentSizeFile += line.length(); 
                    //cant++;
                }
                //totalLines+=cant;
                files.add(fileSorted(tmplist,path,nroFile++));
                tmplist.clear();
             }
         }
        //System.out.println(Integer.toString(totalLines));
        return files;
    }

    private static File fileSorted(List<String> tmplist, String path, int nroFile) throws IOException {
        Collections.sort(tmplist, cmp);
        File f = null;
        File tmpFile = File.createTempFile(path+"temp_"+nroFile, null, f);
        tmpFile.deleteOnExit();
        OutputStream out = new FileOutputStream(tmpFile);
        
        //if i use a try-with-resources block the close method is done automatically 
        try (BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(
               out, Charset.defaultCharset()))) {
            /*for (String r : tmplist) {
                fbw.write(r);
                fbw.newLine();
            } */    
            tmplist.forEach(s->{try {
                fbw.write(s);fbw.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                } });            
        }
        return tmpFile;  
    }
        
}