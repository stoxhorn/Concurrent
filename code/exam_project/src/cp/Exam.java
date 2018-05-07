package cp;

import static cp.Main.startTime;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exam
{
    // an executor that is used for all the curent methods
    // as only one executor should be running at a time
    static ForkJoinPool Serv;
    
    static ExecutorCompletionService Results1;
    
    static ExecutorCompletionService Results2;
    
    static ExecutorCompletionService Results3;
    
    static ConcurrentLinkedDeque<FutureTask<Callable<Result>>> m1Fut = new ConcurrentLinkedDeque<>();
    
    static LinkedList<Result> m1Return = new LinkedList<>();
    
    static Result m2Return = new PathResultSum(false);
    
    static ConcurrentLinkedDeque<statNode> m3Temp = new ConcurrentLinkedDeque<>();
    
    static AtomicInteger m1Int = new AtomicInteger();
    
    static AtomicInteger m2Int = new AtomicInteger();
    
    static AtomicInteger m3Int = new AtomicInteger();
    
    static CountDownLatch m2Latch = new CountDownLatch(1);
    
    static CountDownLatch m3Latch = new CountDownLatch(1);
    
    static StatsExam locals = new StatsExam();
    
    
    
    
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
    
    private static  int incrementM3()
    {
        int x = m3Int.incrementAndGet();
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
       
        if(callBlock == 1)
        {
            Serv = new ForkJoinPool();
            Results1 = new ExecutorCompletionService(Serv);
        }
        
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
            
            
            try {
                if(tmp.get() == null)
                {
                }
                else if(tmp.get().number() != -1)
                {
                    m2Return = tmp.get();
                    m2Latch.countDown();
                }
            } catch (InterruptedException | ExecutionException | NullPointerException ex) {
                Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
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
        if(local == 1)
        {
            Serv = new ForkJoinPool();
            Results2 = new ExecutorCompletionService(Serv);
        }
        
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

    
    // This gets the statNodes for each directory, along with the proper sum accompanied
    // from here on i only need to add them to StatsExam
    private static void add3One()
    {
        

        Future<statNode> tmp;
        try {
            tmp = Results3.take();
            if(tmp == null)
            {
            }
            else
            {
                
                try {
                    m3Temp.add(tmp.get());
                    System.out.println(m3Temp.size());
                } catch (ExecutionException ex) {
                    Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            
        } catch (NullPointerException ex) {
            Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    public static void add3()
    {
        while(m3Temp.size() != 143)
        {
            Serv.execute(()-> add3One());
        }
        
        ArrayList<statNode> temp = new ArrayList<>();
        temp.addAll(m3Temp);
        temp = addNodes(temp);
        locals.setPaths(temp);
        
    }
    
    public static ArrayList<statNode> addNodes(List<statNode> list)
    {
        
        // Adds all the relevant occurences and so one, now i just need to sort in order of sum
        for(statNode x :list)
        {
            IntStream strim = addStream.addStream(x.getPath());
            locals.calcOcc(strim);
            locals.calcOcc();
            strim.close();
        }
        
        return addNode(list);
        
    }
    
    public static ArrayList<statNode> addNode(List<statNode> old)
    {
        ArrayList<statNode> newb = new ArrayList<statNode>();
        ArrayList<Integer> mid = new ArrayList<Integer>();

        statNode min = null;
        
        int index = 0;
        
        for(statNode x : old)
        {
            // the index of x is alread in the list, don't store// 
            if(!contains(mid, x.getInd()))
            {
                min = x;
                min.setInd(x.getInd());
                System.out.println(min.getInd());
            }
                
            // the lowest y, below x
            for(statNode y : old)
            {
                // if min.sum is larger then the next y
                if(min.getSum() > y.getSum()) 
                {
                    // the index of y is alread in the list, don' store
                    if(!contains(mid, y.getInd()))
                    {
                        // set min to be y
                        min = y;
                        min.setInd(y.getInd());   
                    }
                }
            }
            // Store the index
            //System.out.println(min.getInd());
            mid.add(min.getInd());
            newb.add(min);
        }

        
        
        
        return newb;
    }
    
    private static boolean contains(List<Integer> list, int x)
    {
        for(int z : list)
        {
            if(x == z)
            {
                return true;
            }
        }
        
        return false;
        
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
        int local = incrementM3();
        File[] dirFiles = dir.toFile().listFiles();
        
        if(local == 1)
        {
            Serv = new ForkJoinPool();
            Results3 = new ExecutorCompletionService(Serv);
        }
        
        for(File x : dirFiles)
        {
            String tmp = x.getAbsolutePath();
            if(tmp.toLowerCase().endsWith(".txt") || tmp.toLowerCase().endsWith(".dat"))
            {
                // I shubmit a result to the completion service
                Results3.submit(() -> {
                    return getNode(Paths.get(tmp));
                });
            }
            else if(x.isDirectory())
            {
                m3(Paths.get(tmp));
            }   
        }
        if(local == 1)
        {
            // I start the method that gets the current futures
            Serv.execute(() -> add3());
            try {
                m3Latch.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
            }
            return locals; 
        }
            
        return null;
    }
    
    // each time i meet a txdt/ dat file i need to run following sequence:
    /*
        with only a list of paths, i am now able to create a list of statNodes without proper indexing
        this can be achieved in it's local methods using calcOcc on for each path traversed
        
    idea:
        i make a fuckton of statNodes, and at the end i add them to statsExam
    
    */
    
    // Takes a list of directories, and spits out a list of gamenodes without proper indexing
    private static List<statNode> getNodes(List<Path> dirs)
    {
        ArrayList<statNode> tmp = new ArrayList<>();
        for(Path x : dirs)
        {
            tmp.add(getNode(x));
        }
        
        ArrayList<statNode> temp = new ArrayList<>();
        int y = 0;
        for(statNode x : tmp)
        {
            x.setInd(y);
            y++;
            temp.add(x);   
        }
        
        return temp;
        
    }
    
    
    // Takes a path, and a list of Nodes. and returns a list with one new Node
    private static statNode getNode(Path dir)
    {
        IntStream loco;
        loco = addStream.addStream(dir);
        
        int newSum;
        newSum = loco.sum();
        
        loco.close();
        
        statNode newNode = new statNode(newSum, dir, -1);
        
        
        return newNode;

    }
    
    
    
    
   
}
