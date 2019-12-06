//package compiler488.runtime;

/**
 * Exception subclass for reporting machine address errors
 *
 * @author Danny House
 * @version $Revision: 7 $  $Date: #
 */
public class MemoryAddressException extends Exception {
    public MemoryAddressException(String msg) {
        super(msg);
    }
}
