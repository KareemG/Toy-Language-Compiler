import java.io.InputStream;
import java.lang.reflect.Field;

public class AssemblyReader {
    public static void readAssemblyFromStream(InputStream stream) throws NoSuchFieldException,
            IllegalAccessException, MemoryAddressException {
        TextReader reader = new TextReader(stream);
        short counter = 0;
        while (reader.ready()) {
            String world = reader.readWord();
            if (world.isBlank()) continue;
            Field field = Machine.class.getField(world);
            Short opCode = field.getShort(null);
            Machine.writeMemory(counter++, opCode);
            if (opCode < 0 || opCode >= Machine.instructionLength.length) {
                throw new IllegalAccessException();
            }
            for (short i = 0; i < Machine.instructionLength[opCode] - 1; i++) {
                short parameter = (short) reader.readInt();
                Machine.writeMemory(counter++, parameter);
            }
        }
        Machine.setPC((short) 0);
        Machine.setMSP(counter);
        Machine.setMLP(Machine.memorySize);
    }
}
