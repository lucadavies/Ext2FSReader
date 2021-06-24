import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a file within an Ext2 file system volume
 */
public class Ext2File
{
    private byte[] fileBytes;
    private Volume vol;
    private int position;
    private FileInfo info;
    private Inode inode;

    /**
     * Reads in data from a given file in the given the volume
     * @param vol Ext2 file system volume to source the file data from
     * @param info <code>FIleInfo</code> object sourced from the Directory containing this file
     */
    public Ext2File(Volume vol, FileInfo info)
    {
        this.vol = vol;
        this.info = info;
        inode = vol.getInode(info.getInodeNum());
        int[] dirPointers = inode.getDataPointers();
        fileBytes = new byte[(inode.getSize() / vol.getBlockSize() + 1) * vol.getBlockSize()];
        if (inode.getSize() < 12 * vol.getBlockSize())
        {
            for (int i = 0; i < 12 && i < inode.getSize() / vol.getBlockSize() + 1; i++)
            {
                if (dirPointers[i] != 0)
                {
                    getData(0, fileBytes, dirPointers[i], i);
                }
            }
        }
        if (inode.getIndirPointer() != 0)
        {
            getData(1, fileBytes, inode.getIndirPointer(), inode.getDataPointers().length);
        }
        if (inode.getIndir2Pointer() != 0) {
            getData(2, fileBytes, inode.getIndir2Pointer(), inode.getDataPointers().length + (vol.getBlockSize() / 4));
        }
        if (inode.getIndir3Pointer() != 0)
        {
            getData(3, fileBytes, inode.getIndir3Pointer(), inode.getDataPointers().length + (vol.getBlockSize() / 4) + ((vol.getBlockSize() / 4) * 2));
        }

    }

    /**
     * Recursively reads data blocks belonging to this file
     * @param level maximum number of levels of indirection
     * @param data byte array to hold read data
     * @param pointer pointer to first block
     * @param blockOffset offset to store data in <code>data</code> array
     */
    private void getData(int level, byte[] data, int pointer, int blockOffset)
    {
        byte[] block = vol.getBlock(pointer);
        ByteBuffer buf = initByteBuffer(block);
        if (level == 0)
        {
            buf.get(data, vol.getBlockSize() * blockOffset, vol.getBlockSize());
        }
        else
        {
            ArrayList<Integer> pointers = new ArrayList<>();
            for (int i = 0; i < vol.getBlockSize(); i += 4)
            {
                pointers.add(buf.getInt(i));
            }
            for (int i = 0; i < pointers.size(); i++)
            {
                if (pointers.get(i) != 0)
                {
                    getData(level - 1, data, pointers.get(i), inode.getDataPointers().length + (i * (int)Math.pow(256, level - 1)));
                }
            }
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
     * Reads at most <code>length</code> bytes starting at byte offset <code>startByte</code> from start of file. Byte 0 is the first byte in the file.
     * <code>startByte</code> must be such that, 0 ≤ <code>startByte</code> < <code>file.size</code> or an exception should be raised.
     * If there are fewer than <code>length</code> bytes remaining these will be read and a smaller number of bytes than requested will be returned.
     * @param startByte first byte to read
     * @param length number of bytes to read
     * @return array of bytes read
     */
    public byte[] read(int startByte, int length)
    {
        return Arrays.copyOfRange(fileBytes, startByte, startByte + length);
    }

    /**
     * Reads at most <code>length</code> bytes starting at current position in the file.
     * If the current position is set beyond the end of the file, and exception should be raised.
     * If there are fewer than <code>length</code> bytes remaining these will be read and a smaller number of bytes than requested will be returned.
     * @param length number of bytes to read
     * @return array of byte read
     */
    public byte[] read(int length)
    {
        return Arrays.copyOfRange(fileBytes, position, length);
    }

    /**
     * Move to byte <code>position</code> in file.
     * Setting position to 0L will move to the start of the file. Note, it is legal to seek beyond the end of the file; if writing were supported, this is how holes are created.
     * @param position byte position to move to
     */
    public void seek(int position)
    {
        if (position < inode.getSize())
        {
            this.position = inode.getSize();
        }
        else
        {
            this.position = position;
        }
    }

    /**
     * Returns current <code>position</code> in file, i.e. the byte offset from the start of the file. The file position will be zero when the file is first opened and will advance by the number of bytes read with every call to one of the read( ) routines.
     * @return
     */
    public long position()
    {
        return position;
    }

    /**
     * Returns <code>size</code> of file as specified in filesystem.
     * @return
     */
    public int size()
    {
        return inode.getSize();
    }

}