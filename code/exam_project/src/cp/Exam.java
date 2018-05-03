package cp;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exam
{
    static ExecutorService Serv = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); 
    static ConcurrentLinkedDeque<Future<Result>> m1List = new ConcurrentLinkedDeque<>();
    
    static AtomicInteger m1Int = new AtomicInteger();
    
    private static Result calcResult(Path dir)
    {
        return new PathResultMin(dir);
    }
    
    
    
    
    /**
    * This method recursively visits a directory to find all the text
    * files contained in it and its subdirectories.
    * 
    * You must consider only files ending with a ".txt" suffix.
    * You are guaranteed that they will be text files.
    * 
    * You can assume that each text file contains a (non-empty)
    * comma-separated sequence of
    * numbers. For example: 100,200,34,25
    * There won't be any new lines, spaces, etc., and the sequence never
    * ends with a comma.
    * You are guaranteed that each number will be at least or equal to
    * 0 (zero), i.e., no negative numbers.
    * 
    * The search is recursive: if the directory contains subdirectories,
    * these are also searched and so on so forth (until there are no more
    * subdirectories).
    * 
    * This method returns a list of results.
    * The list contains a result for each text file that you find.
    * Each {@link Result} stores the path of its text file,
    * and the lowest number (minimum) found inside of the text file.
    * 
    * @param dir the directory to search
    * @return a list of results ({@link Result}), each giving the lowest number found in a file
    */
    public static List< Result > m1( Path dir )
    {
        m1Int.addAndGet(1);
       // Creates the list to be returned
       List<Result> returnList = new LinkedList();            

       // In case the given path is a directory and not a txt file:
       if (Files.isDirectory(dir))
       {    
           // Creates an array of Files, representing the files in the given irectory
           File[] dirFiles = dir.toFile().listFiles();

           // Loops through each file, and calls this method upon the loop
           for(File file : dirFiles)
           {
               System.out.println("shit");
               m1(file.toPath());
           }
       }

       // In case the given path is a txt file and not a directory:
       else if(dir.toString().toLowerCase().endsWith(".txt"))
       {
           // Adds a PathResultMin for the given file to the lsit
           m1List.add(Serv.submit(()-> calcResult(dir)));
       } 

       if(m1Int.decrementAndGet()==1)
       {
           returnList = consume1();
       }
       
       return returnList;            
    }

    private static List<Result> consume1() {
        LinkedList<Result> returnList = new LinkedList<>();
        boolean bol = true;
        while(bol)
        {
            System.out.println(m1Int.get());
            System.out.println("asd");
            
            try {
                //System.out.println(m1List.peekFirst().get().path());
                returnList.add(m1List.pollFirst().get());
            } catch (InterruptedException ex) {
                Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch(NullPointerException e)
            {
                Thread.sleep(100);
                if(m1List.isEmpty())
                {
                    System.out.println("nuked");
                    bol = false;
                }
            }
            
        }
            
        return returnList;
    }
    
    
    /**
     * This method recursively visits a directory for text files with suffix
     * ".dat" (notice that it is different than the one before)
     * contained in it and its subdirectories.
     * 
     * You must consider only files ending with a .dat suffix.
     * You are guaranteed that they will be text files.
     * 
     * Each .dat file contains some lines of text,
     * separated by the newline character "\n".
     * You can assume that each line contains a (non-empty)
     * comma-separated sequence of
     * numbers. For example: 100,200,34,25
     * 
     * This method looks for a .dat file that contains a line whose numbers,
     * when added together (total), amount to at least (>=) parameter min.
     * Once this is found, the method can return immediately
     * (without waiting to analyse also the other files).
     * The return value is a result that contains:
     *	- path: the path to the text file that contains the line that respects the condition;
     *  - number: the line number, starting from 1 (e.g., 1 if it is the first line, 3 if it is the third, etc.)
     * @param dir the directory to search
     * @param min the int to compare a sum to
     * @return A result that contains the path c:\ and number -1, if no sum is larger than min
     * 
     */
    public static Result m2( Path dir, int min )
    {       
        Result tmp = new PathResultSum(false); 
        // In case the given path is a directory and not a txt file:
        if (Files.isDirectory(dir))
        {    
            // Creates an array of Files, representing the files in the given irectory
            File[] dirFiles = dir.toFile().listFiles();

            // Loops through each file, and calls this method upon the loop
            for(File file : dirFiles)
            {
                int i = m2(file.toPath(), min).number();
                if(i > -1)
                {
                    return m2(file.toPath(), min);
                }
            }
        }

        // In case the given path is a txt file and not a directory:
        else if(dir.toString().toLowerCase().endsWith(".dat"))
        {
            int tmpSum = new PathResultSum(dir, min).number();
            if(tmpSum > -1)
            {
                return new PathResultSum(dir, min);
            }
        }
        return tmp; 
    }

	
    /**
     * Computes overall statistics about the occurrences of numbers in a directory.
     * 
     * This method recursively searches the directory for all numbers in all lines of .txt and .dat files and returns
     * a {@link Stats} object containing the statistics of interest. See the
     * documentation of {@link Stats}.
     */
    public static Stats m3( Path dir )
    {
            throw new UnsupportedOperationException();
    }

    
}
