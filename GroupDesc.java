import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents an ext2 file system group descriptor block
 */
public class GroupDesc
{
    private byte[] data;
    private int inodeTablePointer;

    /**
     * Reads in and creates a new <code>GroupDesc</code> instance
     * @param data data to create instance from
     */
    public GroupDesc(byte[] data)
    {
        this.data = data;
        ByteBuffer buf = initByteBuffer(this.data);
        inodeTablePointer = buf.getInt(8);
    }

    /**
     * Creates and initialises a new <code>ByteBuffer</code> wrapping the given data, in little endian format
     * @param data data to be wrapped
     * @return buffer wrapping <code>data</code>
     */
    private ByteBuffer initByteBuffer(byte[] data)
    {
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.put(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.flip();
        return buf;
    }

    /**
     * Gets this <code>GroupDesc</code>'s inode table pointer
     * @return the inode table pointer
     */
    public int getInodeTablePointer()
    {
        return inodeTablePointer;
    }
}
