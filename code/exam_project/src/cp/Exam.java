package cp;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * 
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exam
{
    // ForkjoinPool, used by all methods, 
    static ForkJoinPool Serv;
    
    // 3 completion Services for the 3 different methods
    static ExecutorCompletionService Results1;
        
    static ExecutorCompletionService Results2;
    
    static ExecutorCompletionService Results3;
    
    // The list added to from method m1, and used as return
    static LinkedList<Result> m1Return = new LinkedList<>();
    
    // The object added to from method m1, and used as return
    static Result m2Return = new PathResultSum(false);
    
    // Used to add nodes to, for later usage when adding to the StatsExam
    static ConcurrentLinkedDeque<statNode> m3Temp = new ConcurrentLinkedDeque<>();
    
    // Atmoci integers used to make sure the first call returns the right return
    static AtomicInteger m1Int = new AtomicInteger();
    
    static AtomicInteger m2Int = new AtomicInteger();
    
    static AtomicInteger m3Int = new AtomicInteger();
    
    
    // Latches used to have the first method wait for the result 
    static CountDownLatch m2Latch = new CountDownLatch(1);
    
    static CountDownLatch m3Latch = new CountDownLatch(1);
    
    // The statsExam object used to retunr in the m3 method
    static StatsExam locals = new StatsExam();
    
    
    
    // Used to differ between depth calls in method one
    private static  int incrementM1()
    {
        int x = m1Int.incrementAndGet();
        return x;
    }
    
    // Used to differ between depth calls in method two
    private static  int incrementM2()
    {
        int x = m2Int.incrementAndGet();
        return x;
    }
    
    // Used to differ between depth calls in method three
    private static  int incrementM3()
    {
        int x = m3Int.incrementAndGet();
        return x;
    }
    
    

    // The final method that sequantially waits for the results of Result1 to finish
    public static void add()
    {
        Future<Result> tmp = Results1.poll();
        while((Serv.hasQueuedSubmissions()) || tmp != null || Serv.getActiveThreadCount() > 0 )
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
       
        // initializing the completionService and the executor
        if(callBlock == 1)
        {
            Serv = new ForkJoinPool();
            Results1 = new ExecutorCompletionService(Serv);
        }
        
        // Submits a future result to the completionist service
        if(dir.toString().toLowerCase().endsWith(".txt"))
        {
            Results1.submit(() ->
            {
                return new PathResultMin(dir);
            });
        }  
       
        // if the files is a directory, the method will make recusrive calls, to make sure every result has been submitted
        else if (Files.isDirectory(dir))
        {    
            File[] dirFiles = dir.toFile().listFiles();
            for(File file : dirFiles)
            {
               m1(file.toPath());
            }
        }
       
       

        // The first depth calls the add() method, once it has submitted every future.
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
        // if not first depth, returns nulil
        else
        {
            return null;    
        }
        
    }

    //------------------------------------------------- helper Methods for second method
    
    
    // the call used every loop once, the method wants to calculates it's return
    public static void add2One()
    {
        Future<Result> tmp = Results2.poll();
            
            
            try {
                if(tmp == null)
                {
                }
                else if(tmp.get() == null)
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
    
    // addition loop
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
        
        // checker for call depth
        int local = incrementM2();
        
        // initializer for first depth
        if(local == 1)
        {
            Serv = new ForkJoinPool();
            Results2 = new ExecutorCompletionService(Serv);
        }
        
        // In case the given path is a directory and not a txt file, makes a recursive call
        if (Files.isDirectory(dir))
        {    
            File[] dirFiles = dir.toFile().listFiles();
            for(File file : dirFiles)
            {
                m2(file.toPath(), min);
            }
        }

        // In case the given path is a .dat file and not a directory, add to completionservice
        else if(dir.toString().toLowerCase().endsWith(".dat"))
        {
            
            // will countdown the latch for the second method if the result is above the given parameter
            Results2.submit(() -> {
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
        
        // if depth 1 do the return call
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

    
    // -------------------------------------------------------------- Helper methods for the third method
    
    
    
    // the method used in the add3() loop, to complete futures
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
                    if(tmp.get().getSum() == 1)
                    {
                    }
                    m3Temp.add(tmp.get());
                    
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
    
    // a loop that gives the executor runnables to complete
    // will when the submission count growth is faster than it can compute, meaning it's waiting for tasks
    public static void add3()
    {
        while(Serv.getQueuedSubmissionCount() <100)
        {

            Serv.execute(()-> add3One());
        }
        m3Latch.countDown();
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
