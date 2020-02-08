/* package MachineDemo ; */

import java.io.*;

/* import Machine ; */

/**
 * This class serves as the main driver for the CSC488S compiler.
 * It accepts user options and coordinates overall control flow.
 * The main flow of control includes the following activities:
 * <ul>
 *   <li> Parse user supplied arguments and options.
 *   <li> Open output files.
 *   <li> Parse the input and produce an AST.
 *   <li> Perform semantic Analysis on the AST
 *   <li> Perform code generation on the AST
 *   <li> Invoke the machine interpreter to execute the compiled program
 *   <li> Close output files.
 * </ul>
 * <p>
 * See the compiler man page for a list of Main program options.
 *
 * @author Dave Wortman
 * @version last updated: $Date: 2008-01-28 10:29:54 -0500 (Mon, 28 Jan 2008) $
 */

public class Main {

    /**
     * Version string for -V
     */
    public final static String version =
            " $Revision: 53 $ $Date: 2008-01-28 10:29:54 -0500 (Mon, 28 Jan 2008) $ $Author: dw $ ";

    /**
     * Only constructor is private,
     * Do not allocate any instances of this class.
     */
    private Main() {
    }

    /**
     * public error flag
     * Setting this flag to true will suppress further processing
     * This can be used by parsing, semantic analysis or code generation
     * to quit early if errors have occurred.
     */
    public static boolean errorOccurred = false;

    /**
     * Options set by the user
     */
    /**
     * User option -- supress execution
     */
    public static boolean supressExecution = false;

    /* DUMP Options */
    /**
     * User option -- dump compiled code before execution
     */
    public static boolean dumpCode = true;

    /** TRACE options switches */
    /**
     * User option -- trace lexical analysis
     */
    public static boolean traceExecution = true;

    /** FILE NAMES supplied by the user  */
    /**
     * User option -- alternative file for compiler error messages
     */
    private static String errorFileName = new String();
    /**
     * User option -- alternative file for compiler output
     */
    private static String compilerDumpFileName = new String();
    /**
     * User option -- alternative file for program trace output
     */
    private static String executeTraceFileName = new String();
    /**
     * User option -- alternative file for program execution input
     */
    private static String executeInputFileName = new String();
    /**
     * User option -- assembly input file name
     */
    private static String assemblyInputFileName = new String();

    /**
     * FILES and STREAMS
     */
    /* source for compiler or program input */
    private static File inputFile = null;
    private static FileInputStream inputStream = null;
    /* sink for compiler or program output  */
    private static File outputFile = null;
    private static FileOutputStream outputStream = null;
    /* sink for error messages  */
    private static File errorFile = null;
    private static FileOutputStream errorStream = null;
    /* sink for dumps  */
    private static File dumpFile = null;
    private static FileOutputStream dumpFileStream = null;
    /* sink for assembly input */
    private static File assemblyInputFile = null;
    private static FileInputStream assemblyInputStream = null;

    /**
     * Save settings before tampering
     */
    private static InputStream saveSysIn = System.in;
    private static PrintStream saveSysOut = System.out;
    private static PrintStream saveSysErr = System.err;

    /**
     * stream for doing dumps
     * All implementations of dump should write on this stream
     */

    public static PrintStream dumpStream = System.out;

    /**
     * PrintStream for trace output.
     * All implementations of tracing should write on this stream
     */
    public static PrintStream traceStream = System.out;

    /**
     * printVersion - print version information for compiler components
     */
    private final static void printVersion() {

        System.out.println("compiler488 was constructed from");
        System.out.println("  Main:" + version);
        System.out.println("  Machine:" + Machine.version);
    }

    /**
     * commandLineArgs
     *
     * @param arguments an array of strings containing command line arguments.
     *                  process command line arguments to Main program.
     *                  <p>
     *                  Will accept any name as a file argument,
     *                  if the name is invalid the error  will be caught wher the file is used.
     *                  Sets boolean flags to indicate active options
     */

    private static void commandLineArgs(String arguments[]) {
        int length = arguments.length; //number of command line arguments passed
        int optionLen = 0;        //number of arguments passed
        //for current option

        int i, j, k;        //just a counter
        String argTmp;        //temp argument strings for -D and -T
        final String badUsage = "Incorrect usage of command line arguments."
                + " Please refer to the man page.";

        if (length > 0)
            try {        // catch arrayOutOfBoundsException for bad argument list
                for (i = 0; i < length; i++) {
                    if (arguments[i].equals("-X"))
                        supressExecution = true;
                    else if (arguments[i].equals("-D")) {
                        i++;    // advance to next argument
                        argTmp = arguments[i];
                        dumpCode = argTmp.indexOf('x') >= 0;
                        k = argTmp.length();
                        for (j = 0; j < k; j++)
                            if ("abxy".indexOf(argTmp.charAt(j)) < 0)
                                System.err.println("Invalid flag '" +
                                        argTmp.charAt(j) + "' for -D option (ignored)");
                    } else if (arguments[i].equals("-T")) {
                        i++;   // advance to next argument
                        argTmp = arguments[i];
                        traceExecution = argTmp.indexOf('x') >= 0;
                        k = argTmp.length();
                        for (j = 0; j < k; j++)
                            if ("lpasycx".indexOf(argTmp.charAt(j)) < 0)
                                System.err.println("Invalid flag '" +
                                        argTmp.charAt(j) + "' for -T option (ignored)");
                    } else if (arguments[i].equals("-E")) {
                        i++;   // advance to next argument
                        errorFileName = new String(arguments[i]);
                    } else if (arguments[i].equals("-S")) {
                        i++;   // advance to next argument
                        executeTraceFileName = new String(arguments[i]);
                    } else if (arguments[i].equals("-U")) {
                        i++;   // advance to next argument
                        compilerDumpFileName = new String(arguments[i]);
                    } else if (arguments[i].equals("-I")) {
                        i++;
                        executeInputFileName = new String(arguments[i]);
                    } else if (arguments[i].equals("-A")) {
                        i++;
                        assemblyInputFileName = new String(arguments[i]);
                    } else if (arguments[i].equals("-V"))
                        printVersion();

                    else    // unrecognized command flag
                    {
                        System.err.println(badUsage);
                        errorOccurred = true;
                        return;
                    }
                }  // end for length loop
            } catch (ArrayIndexOutOfBoundsException arrayBounds) {
                System.err.println(badUsage);
                errorOccurred = true;
                return;
            }
    }

    /**
     * Optionally set System.in to be the specified file
     *
     * @param fileName - the input file
     */
    private static void setInputSource(String fileName) {
        if (fileName.length() == 0)
            return;    // use existing System.in

        /* set System.in to point at specifed file */
        try {
            inputFile = new File(fileName);
        } catch (Exception e) {
            System.err.println("Unable to open file " + fileName);
            System.err.println(e.getClass().getName()
                    + ": " + e.getMessage());
            return;  // continue with System.in  unchanged
        }
        try {
            inputStream = new FileInputStream(inputFile);
            System.setIn(inputStream);
        } catch (Exception e) {
            System.err.println("Unable to set input stream to  file " + fileName);
            System.err.println(e.getClass().getName()
                    + ": " + e.getMessage());
            return;  // continue with System.in  unchanged
        }
    }

    private static void setAssemblyInput(String fileName) {
        System.out.println(fileName);
    	if (fileName.length() == 0)
    	    return;
    	try {
    	    assemblyInputFile = new File(fileName);
    	    assemblyInputStream = new FileInputStream(assemblyInputFile);
        } catch (Exception e) {
            System.err.println("Unable to open file " + fileName);
            System.err.println(e.getClass().getName()
                    + ": " + e.getMessage());
        }
    }

    private static void setOutputSink(String fileName) {
        /* set System.out to point at specifed file */
        try {
            outputFile = new File(fileName);
        } catch (Exception e) {
            System.err.println("Unable to open file " + fileName);
            System.err.println(e.getClass().getName()
                    + ": " + e.getMessage());
            return;  // continue with System.out  unchanged
        }
        try {
            outputStream = new FileOutputStream(outputFile);
            // use autoflush for more accurate output
            System.setOut(new PrintStream(outputStream, true));
        } catch (Exception e) {
            System.err.println("Unable to set output stream to  file " + fileName);
            System.err.println(e.getClass().getName()
                    + ": " + e.getMessage());
            return;  // continue with System.out  unchanged
        }
    }

    private static void setErrorSink(String fileName) {
        /* set System.err to point at specifed file */
        try {
            errorFile = new File(fileName);
        } catch (Exception e) {
            System.err.println("Unable to open file " + fileName);
            System.err.println(e.getClass().getName()
                    + ": " + e.getMessage());
            return;  // continue with System.err  unchanged
        }
        try {
            errorStream = new FileOutputStream(errorFile);
            // use autoflush for more precise output
            System.setErr(new PrintStream(errorStream, true));
        } catch (Exception e) {
            System.err.println("Unable to set error stream to  file " + fileName);
            System.err.println(e.getClass().getName()
                    + ": " + e.getMessage());
            return;  // continue with System.err  unchanged
        }
    }

    /**
     * set traceStream to point at specifed file
     */
    private static void setTraceStream(String fileName) {
        File traceFile = null;
        FileOutputStream traceFileStream;

        if (fileName.length() == 0) {
            traceStream = saveSysOut;   // trace to System.out
            return;
        }
        // otherwise set up the file
        try {
            traceFile = new File(fileName);
        } catch (Exception e) {
            System.err.println("Unable to open trace file " + fileName);
            System.err.println(e.getClass().getName()
                    + ": " + e.getMessage());
        }
        try {
            traceFileStream = new FileOutputStream(traceFile);
            // use autoFlush for more accurate output
            traceStream = new PrintStream(traceFileStream, true);
        } catch (Exception e) {
            System.err.println("Unable to set trace stream to  file " + fileName);
            System.err.println(e.getClass().getName()
                    + ": " + e.getMessage());
            traceStream = saveSysOut;
        }
    }

    /*-----------------------------------------------------------*/
    /*--- Main Program ------------------------------------------*/
    /*-----------------------------------------------------------*/

    /**
     * The main driver for the system.
     *
     * @param argv an array of strings containing command line arguments.
     */
    public static void main(String argv[]) {

        /* process user options and arguments */
        try {
            commandLineArgs(argv);
        } catch (Exception e) {
            System.err.println("Exception during command line argument processing");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(90);
        }

        if (errorOccurred) {
            System.out.println("Processing Terminated due to command line errors");
            return;
        }

        /* Setup files for compilation  */
        if (errorFileName.length() > 0)
            setErrorSink(errorFileName);

        setAssemblyInput(assemblyInputFileName);

        // Initialize Machine before code generation
        try {
            Machine.powerOn();
        } catch (Exception e) {
            System.err.println("Exception during Machine initialization");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(140);
        }

        try {
            AssemblyReader.readAssemblyFromStream(assemblyInputStream);
        } catch (Exception e) {
            System.err.println("Exception during reading assembly input.");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            errorOccurred = true;
        }

        if (errorOccurred) {
            System.out.println("Processing Terminated due to errors");
            return;
        } else
            System.out.println("End of Compilation - Begin Execution");

        setTraceStream(executeTraceFileName);
        setInputSource(executeInputFileName);

        try {
            Machine.run();
        } catch (ExecutionException e) {
            System.err.println("Exception during Machine Execution"
                    + e.getMessage());
            // Run error has already dumped machine state.
            return;
        } catch (Exception e) {
            System.err.println("Unexpected Exception during Machine Execution");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            System.exit(200);
        }

        if (traceStream != null && traceStream != saveSysOut)
            traceStream.close();    // finish exceution trace

        System.out.println("End of Execution");

    }    // end main function

}  // end Main class
