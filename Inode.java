import java.util.Date;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents an ext2 file system inode structure
 */
public class Inode
{

    private byte[] data;
    private String fileMode = "";
    private int UID;
    private int size;
    private Date lastAccessTime;
    private Date creationTime;
    private Date lastModTime;
    private Date deletedTime;
    private int GID;
    private short hardLinks;
    private int[] dataPointers = new int[12];
    private int indirPointer;
    private int indir2Pointer;
    private int indir3Pointer;
    private Helper h;

    private static final int IFREG = 0x8000;      // Regular File
    private static final int IFDIR = 0x4000;      // Directory
    private static final int IRUSR = 0x0100;      // User read
    private static final int IWUSR = 0x0080;      // User write
    private static final int IXUSR = 0x0040;      // User execute
    private static final int IRGRP = 0x0020;      // Group read
    private static final int IWGRP = 0x0010;      // Group write
    private static final int IXGRP = 0x0008;      // Group execute
    private static final int IROTH = 0x0004;      // Others read
    private static final int IWOTH = 0x0002;      // Others write
    private static final int IXOTH = 0x0001;      // Others execute

    /**
     * Reads and creates and new <code>Inode</code> instance
     * @param data data to create new instance from
     */
    public Inode(byte[] data)
    {
        h = new Helper();
        this.data = data;
        ByteBuffer buf = initByteBuffer(this.data);
        readFileMode(buf.getShort(0));
        short UIDL = buf.getShort(2);
        short UIDU = buf.getShort(120);
        UID = UIDU | UIDL;
        lastAccessTime = new Date(buf.getLong(8) * 1000);
        creationTime = new Date(buf.getLong(12) * 1000);
        lastModTime = new Date(buf.getLong(16) * 1000);
        deletedTime = new Date(buf.getLong(20) * 1000);
        short GIDL = buf.getShort(24);
        short GIDU = buf.getShort(122);
        GID = GIDU | GIDL;
        GID = buf.getShort(24);
        hardLinks = buf.getShort(26);
        for (int i = 0; i < 12; i++)
        {
            dataPointers[i] = buf.getInt(40 + 4 * i);
        }
        indirPointer = buf.getInt(88);
        indir2Pointer = buf.getInt(92);
        indir3Pointer = buf.getInt(96);

        int sizeL = buf.getInt(4);
        int sizeU = buf.getInt(108);
        sizeU <<= 32;
        size = sizeU | sizeL;
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
     * Reads this <code>Inode</code>'s file mode according to ORing with predefined constants
     */
    private void readFileMode(Short fm)
    {
        if ((fm & IFDIR) == IFDIR)
        {
            fileMode += "d";
        }
        else if ((fm & IFREG) == IFREG)
        {
            fileMode += "-";
        }
        else
        {
            fileMode += " ";
        }
        if ((fm & IRUSR) == IRUSR)
        {
            fileMode += "r";
        }
        else
        {
            fileMode += "-";
        }
        if ((fm & IWUSR) == IWUSR)
        {
            fileMode += "w";
        }
        else
        {
            fileMode += "-";
        }
        if ((fm & IXUSR) == IXUSR)
        {
            fileMode += "x";
        }
        else
        {
            fileMode += "-";
        }
        if ((fm & IRGRP) == IRGRP)
        {
            fileMode += "r";
        }
        else
        {
            fileMode += "-";
        }
        if ((fm & IWGRP) == IWGRP)
        {
            fileMode += "w";
        }
        else
        {
            fileMode += "-";
        }
        if ((fm & IXGRP) == IXGRP)
        {
            fileMode += "x";
        }
        else
        {
            fileMode += "-";
        }
        if ((fm & IROTH) == IROTH)
        {
            fileMode += "r";
        }
        else
        {
            fileMode += "-";
        }
        if ((fm & IWOTH) == IWOTH)
        {
            fileMode += "w";
        }
        else
        {
            fileMode += "-";
        }
        if ((fm & IXOTH) == IXOTH)
        {
            fileMode += "x";
        }
        else
        {
            fileMode += "-";
        }
    }

    /**
     * Returns the file mode of the file pointed to by this <code>Inode</code>
     * @return the file mode
     */
    public String getFileMode()
    {
        return fileMode;
    }

    /**
     * Returns the UID associated with the file pointed to by this <code>Inode</code>
     * @return the UID
     */
    public int getUID() {
        return UID;
    }

    /**
     * Returns the size, in bytes, of the file pointed to by this <code>Inode</code>
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the last accessed time of the file pointed to by this <code>Inode</code>
     * @return the last accessed time
     */
    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    /**
     * Returns the creation time of the file pointed to by this <code>Inode</code>
     * @return the creation time
     */
    public Date getCreationTime() {
        return creationTime;
    }

    /**
     * Returns the last modified time of the file pointed to by this <code>Inode</code>
     * @return the last modified time
     */
    public Date getLastModTime() {
        return lastModTime;
    }

    /**
     * Returns the deletion time of the file pointed to by this <code>Inode</code>
     * @return the deletion time
     */
    public Date getDeletedTime() {
        return deletedTime;
    }

    /**
     * Returns the GID associated with the file pointed to by this <code>Inode</code>
     * @return the GID
     */
    public int getGID() {
        return GID;
    }

    /**
     * Returns the number of hard links the file pointed to by this <code>Inode</code> has
     * @return the number of hard links
     */
    public short getHardLinks()
    {
        return hardLinks;
    }

    /**
     * Returns the an array of the direct pointers to the data of the file pointed to by this <code>Inode</code>
     * @return the the pointers
     */
    public int[] getDataPointers()
    {
        return dataPointers;
    }

    /**
     * Returns the singly indirect pointer to the data of the file pointed to by this <code>Inode</code>
     * @return the pointer
     */
    public int getIndirPointer() {
        return indirPointer;
    }

    /**
     * Returns the doubly indirect pointer to the data of the file pointed to by this <code>Inode</code>
     * @return the pointer
     */
    public int getIndir2Pointer() {
        return indir2Pointer;
    }

    /**
     * Returns the triply indirect pointer to the data of the file pointed to by this <code>Inode</code>
     * @return the pointer
     */
    public int getIndir3Pointer() {
        return indir3Pointer;
    }
}
