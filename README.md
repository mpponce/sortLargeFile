# sortLargeFile
sorts a big inputFile, using the natural order for long numbers with a limited amount of memory(100Mb)

To run from command line:

java -jar "PaulaPonce.jar" path inputFileName outputFileName

ubuntu example: 

java -Xmx100M -jar "PaulaPonce.jar" "/home/novakorp/sortLargeFile-main/" fileNumbers2 fileOutSorted

windows example:

java -Xmx100M -jar "PaulaPonce.jar" "C:\\prueba\\" fileNumbers2 fileOutSorted

A tener en cuenta que en el path indicado debe estar el archivo a leer, y en el mismo path dejará el archivo nuevo ordenado generado
In the path that you indicate must be the file to be read, and the output file will be generated to the same path 

To see more examples of execution view on this repository runUbuntuExample.png for ubuntu or  EjemploEjecucionWindows.jpg for windows

fileNumbers2 is an example of a file to sort.

FileUtils.java and FileSort.java are the classes that contain the code.

PaulaPonce.jar is the code to be executed by command line.
