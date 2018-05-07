package cp;

import static cp.Main.startTime;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exam
{
    // an executor that is used for all the curent methods
    // as only one executor should be running at a time
    static ForkJoinPool Serv = new ForkJoinPool();
    
    static ExecutorCompletionService Results1 = new ExecutorCompletionService(Serv);
    
    static ExecutorCompletionService Results2 = new ExecutorCompletionService(Serv);
    
    static ConcurrentLinkedDeque<FutureTask<Callable<Result>>> m1Fut = new ConcurrentLinkedDeque<>();
    
    static LinkedList<Result> m1Return = new LinkedList<>();
    
    static Result m2Return = new PathResultSum(false);
    
    static AtomicInteger m1Int = new AtomicInteger();
    
    static AtomicInteger m2Int = new AtomicInteger();
    
    static CountDownLatch m2Latch = new CountDownLatch(1);
    
    
    
    
    
    private static  int incrementM1()
    {
        int x = m1Int.incrementAndGet();
        return x;
    }
    
    private static  int incrementM2()
    {
        int x = m2Int.incrementAndGet();
        return x;
    }
    
    

    
    public static void add()
    {
        Future<Result> tmp = Results1.poll();
        while((Serv.hasQueuedSubmissions()) || tmp != null || Serv.getActiveThreadCount() > 0)
        {
            if(tmp == null)
            {
            }
            else{
            try {
                m1Return.add(tmp.get());
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
            }
                
            }
            tmp = Results1.poll();
        }
        
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
        // Integer to keep track of call number
        int callBlock = incrementM1();
       
        // In case the given path is a txt file and not a directory:
        if(dir.toString().toLowerCase().endsWith(".txt"))
        {
            
            // Adds a PathResultMin for the given file to the future list
            
            Results1.submit(() ->
            {
                return new PathResultMin(dir);
            });
        }  
       
        // In case the given path is a directory and not a txt file:
        else if (Files.isDirectory(dir))
        {    
            // Creates an array of Files, representing the files in the given irectory
            File[] dirFiles = dir.toFile().listFiles();

            // Loops through each file, and calls this method upon the loop
            for(File file : dirFiles)
            {
               m1(file.toPath());
            }
        }
       
       

        // Returns the build List
        // If the given path was neither a txt file or directory,
        // this will be empty, and thus cause no chaos
       
        // if this is first call, return the list of futures
        if(callBlock == 1)
        {
            // Start the creation
            add();
            try{
                return m1Return;
            }finally{
                Serv.shutdown();
            }
            
        }
        else
        {
            
        }
        return null;
    }

    public static void add2One()
    {
        Future<Result> tmp = Results2.poll();
        if(tmp == null)
            {
            }
            else{
            try {

                if(tmp.get().number() != -1)
                {
                    m2Return = tmp.get();
                    m2Latch.countDown();
                }
            } catch (InterruptedException | ExecutionException | NullPointerException ex) {
                Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
            }
                
            }
    }
    
    public static void add2()
    {
        
        
        while((Serv.hasQueuedSubmissions()) || Serv.getActiveThreadCount() > 0)
        {
            Serv.execute(()-> add2One());
        }
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
        
        int local = incrementM2();
        
        // In case the given path is a directory and not a txt file:
        if (Files.isDirectory(dir))
        {    
            // Creates an array of Files, representing the files in the given irectory
            File[] dirFiles = dir.toFile().listFiles();

            // Loops through each file, and calls this method upon the loop
            for(File file : dirFiles)
            {
                m2(file.toPath(), min);
            }
        }

        // In case the given path is a txt file and not a directory:
        else if(dir.toString().toLowerCase().endsWith(".dat"))
        {
            int tmpSum = new PathResultSum(dir, min).number();
            if(tmpSum > -1)
            {
                Results2.submit(() ->
                {
                    Result tmp = new PathResultSum(dir, min);
                    if(tmp.number() != -1)
                    {
                        m2Return = tmp;
                        m2Latch.countDown();
                        return null;
                    }
                    return new PathResultSum(dir, min);
                });
                return null;
            }
        }
        if(local == 1)
        {
            Serv.execute(() -> add2());
            try {
                m2Latch.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
            }
            return m2Return; 
        }
        return null;
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
        File[] dirFiles = dir.toFile().listFiles();

       for(File x : dirFiles)
       {
        String tmp = x.getAbsolutePath();
        if(tmp.toLowerCase().endsWith(".txt") || tmp.toLowerCase().endsWith(".dat"))
        {
            locals.add(Paths.get(tmp));
        }
        else if(x.isDirectory())
        {
            m3(Paths.get(tmp));
        }   
       }
        
    
            
        return locals;
    }
}
