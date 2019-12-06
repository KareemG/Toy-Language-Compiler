//package compiler488.runtime;

/**
 * Exception subclass for reporting machine run time  errors
 *
 * @author Danny House
 * @version $Revision: 7 $  $Date: 2010-01-08 17:40:36 -0500 (Fri, 08 Jan 2010) $
 */
public class ExecutionException extends Exception {
    public ExecutionException(String msg) {
        super(msg);
    }
}
