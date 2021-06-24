/**
 * A class to provide methods to print byte arrays to screen in readable hexadecimal format or as raw binary
 */
public class Helper
{

    public Helper(){}

    /**
     * Outputs an array of bytes as returned by read() in a readable hexadecimal format, perhaps with printable ASCII codes by the side.
     * @param bytes bytes to print in dump to screen
     */
    public void dumpHexBytes(byte[] bytes)
    {
        for (int i = 0; i < bytes.length; i += 16)
        {
            System.out.print(String.format("Byte %08d | ", i));
            for (int j = 0; j < 16; j++)
            {
                if (i + j < bytes.length)
                {
                    System.out.print(String.format("%02x ", bytes[ i + j]) + (j == 7 || j == 15 ? " | " : " "));
                }
                else
                {
                    System.out.print("XX " +  (j == 7 || j == 15 ? " | " : " "));
                }
            }
            for (int j = 0; j < 16; j++)
            {
                if (i + j < bytes.length)
                {
                    System.out.print((bytes[i + j] >= 33 && bytes[i + j] <= 122 ? String.format("%c", bytes[i + j]) : " ") + (j == 7 || j == 15 ? " | " : " "));
                }
                else
                {
                    System.out.print(" " +  (j == 7 || j == 15 ? " | " : " "));
                }
            }
            System.out.println();
        }

    }

    /**
     * Outputs an array of bytes as returned by read() in a readable hexadecimal format, up to <code>length</code> bytes long
     * @param bytes bytes to print in dump to screen
     * @param length the number of bytes to dump
     */
    public void dumpHexBytes(byte[] bytes, int length)
    {
        for (int i = 0; i < length; i += 16)
        {
            for (int j = 0; j < 16; j++)
            {
                System.out.print(String.format("%02x ", bytes[ i + j]) + (j == 7 || j == 15 ? " | " : " "));
            }
            for (int j = 0; j < 16; j++)
            {
                System.out.print((bytes[i + j] >= 33 && bytes[i + j] <= 122 ? String.format("%c", bytes[i + j]) : " ") + (j == 7 || j == 15 ? " | " : " "));
            }
            System.out.println();
        }

    }

    /**
     * Outputs an array of bytes as returned by read() in a raw binary format, perhaps with printable ASCII codes by the side.
     * @param bytes bytes to print in dump to screen
     */
    public void dumpRawBytes(byte[] bytes)
    {
        for (byte b : bytes)
        {
            System.out.println(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(" ", "0"));
        }
    }
}