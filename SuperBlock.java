import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents an ext2 file system Super block
 */
public class SuperBlock
{
    private byte[] data;
    private int numInodes;
    private int numBlocks;
    private int blockSize;
    private int blocksPerGroup;
    private int inodesPerGroup;
    private int inodeSize;
    private String label = "";

    private static final short EXT2_MAGIC_NUM = (short)0xef53;
    private static final int MAGIC_NUM_OFFSET = 56;

    /**
     * Reads in and creates a new <code>SuperBlock</code> instance
     * @param data data to create instance from
     */
    public SuperBlock(byte[] data)
    {
        this.data = data;
        blockSize = data.length;
        ByteBuffer buf = initByteBuffer(this.data);
        if (buf.getShort(MAGIC_NUM_OFFSET) == EXT2_MAGIC_NUM) //if magic number matches required value for Ext2 (0xef53)
        {
            numInodes = buf.getInt(0);
            numBlocks = buf.getInt(4);
            blocksPerGroup = buf.getInt(32);
            inodesPerGroup = buf.getInt(40);
            inodeSize = buf.getInt(88);
            for (int i = 0; i < 16; i++)
            {
                label += (char)buf.get(120 + i);
            }
        }
        else
        {
            System.out.println("Error: file may not be a valid ext2 file system volume.");
        }
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
     * Returns number of inodes in the Volume this super block belongs to
     * @return the number of inodes
     */
    public int getNumInodes() {
        return numInodes;
    }

    /**
     * Returns number of blocks in the Volume this super block belongs to
     * @return the number of blocks
     */
    public int getNumBlocks() {
        return numBlocks;
    }

    /**
     * Returns block size, in bytes, of the blocks in the Volume this super block belongs to
     * @return the block size
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * Returns number of blocks in each block group in the Volume this super block belongs to
     * @return the number blocks per block group
     */
    public int getBlocksPerGroup() {
        return blocksPerGroup;
    }

    /**
     * Returns number of inodes in each block group in the Volume this super block belongs to
     * @return the number of inodes per group
     */
    public int getInodesPerGroup() {
        return inodesPerGroup;
    }

    /**
     * Returns size, in bytes, of inodes in the Volume this super block belongs to
     * @return the size of inodes
     */
    public int getInodeSize() {
        return inodeSize;
    }

    /**
     * Returns the label of the Volume this super block belongs to
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns a string representation of this <code>SuperBlock</code>
     * @return the string
     */
    public String toString()
    {
        return super.toString() + " [numInodes: " + getNumInodes() + ", numBlocks: " + getNumBlocks() + ", blocksPerGroup: " + getBlocksPerGroup() + ", inodesPerGroup: " + getInodesPerGroup() + ", inodeSize: " + getInodeSize() + ", label: \"" + getLabel() + "\"]";
    }
}
