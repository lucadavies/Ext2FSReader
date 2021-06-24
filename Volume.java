import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Represents an ext2 file system volume
 */
public class Volume
{
    private RandomAccessFile f;
    private SuperBlock superBlock;
    private Helper help;
	private int blockSize;

	private static final int BLOCK_SIZE_OFFSET = 1024 + 24;
	private static final int GROUP_DESC_LEN = 32;
    /**
     * Opens the Volume represented by the host Windows/ Linux <code>filename</code>
     * @param fileName the f system image f to open
     */
    public Volume(String fileName)
    {
        help = new Helper();
        try
        {
            f = new RandomAccessFile(fileName, "r");
            f.seek(BLOCK_SIZE_OFFSET);
            blockSize = 1024 * (int)Math.pow(2, f.readInt()); //get FS block size before anything else
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File \"" + fileName + "\" could not be found.");
            System.out.println(e.getMessage());
        }
        catch (SecurityException e)
        {
            System.out.println("File \"" + fileName + "\" could not be accessed: permission denied.");
            System.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        superBlock = new SuperBlock(getBlock(1));
        blockSize = superBlock.getBlockSize();
    }

    /**
     * Creates and returns an instance of <code>Inode</code> created from this volume using it's inode number
     * @param inodeNumber number of inode to be returned
     * @return the inode
     */
    public Inode getInode(int inodeNumber)
    {
        inodeNumber--;
        int groupNum = inodeNumber / superBlock.getInodesPerGroup();
        int tableIndex = (inodeNumber % superBlock.getInodesPerGroup());
        GroupDesc tempDesc = new GroupDesc(getBlock(2, groupNum * GROUP_DESC_LEN, GROUP_DESC_LEN));
        byte[] d = getBlock(tempDesc.getInodeTablePointer(), superBlock.getInodeSize() * tableIndex, superBlock.getInodeSize());
        return new Inode(d);
    }

    /**
     * Returns a byte array from this Volume's file data of <code>length</code> bytes, starting at <code>offset</code> bytes from the start og the file
     * @param offset number of bytes from the start of the file from which to read
     * @param length number of bytes to read beyond the offset
     * @return a byte array of the data between <code>offset</code> and <code>length</code>
     */
    private byte[] getBytes(int offset, int length)
    {
        byte[] data = new byte[length];
        try
        {
            f.seek(offset);
            f.read(data);
        }
        catch (IOException e)
        {
            System.out.println("Error reading file system.");
            System.out.println(e.getMessage());
        }
        return data;
    }

    /**
     * Returns a byte array of a whole block in this volume
     * @param blockNum block number to return
     * @return the block data
     */
    public byte[] getBlock(int blockNum)
    {
        return getBytes(blockNum * blockSize, blockSize);
    }

    /**
     * Returns a byte array of a part of a block in this volume
     * @param blockNum block number to return from
     * @param offset offset from start of block to return data from
     * @param length length of data set to return
     * @return portion of block data
     */
    public byte[] getBlock(int blockNum, int offset, int length)
    {
        if (offset >= blockSize)
        {
            blockNum += offset / blockSize;
            offset = offset % blockSize;
        }
        return getBytes((blockNum * blockSize) + offset, length);
    }

    /**
     * Returns the label of this volume
     * @return the label
     */
    public String getLabel()
    {
        return superBlock.getLabel();
    }

    /**
     * Returns the block size, in bytes, associated with this volume
     * @return the block size
     */
    public int getBlockSize()
    {
        return blockSize;
    }

    /**
     * Returns a string representation of this Volume
     * @return the string
     */
    public String toString()
    {
        return super.toString() + superBlock.toString();
    }
}
