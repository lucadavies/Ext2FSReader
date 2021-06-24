import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
* Represents a Directory in an Ext2 filesystem
*/
public class Directory
{
    private ArrayList<String> levels;
    private ArrayList<FileInfo> files;
    private Volume vol;
    private String path;

    /**
     * Creates an new Directory using the given inputs.
     * @param vol The volume which this Directory belongs to
     * @param path The path of this Directory
     * @param parentPath The path of the parent Directory to this Directory
     * @throws NoSuchDirectoryException When the path cannot be found within vol
     * @throws RootReachedException When the parent path (..) is passed when the current Directory si the root
     */
    public Directory(Volume vol, String path, String parentPath) throws NoSuchDirectoryException, RootReachedException
    {
        this.vol = vol;
        this.path = path;
        if (parentPath.equals(vol.getLabel()) && path.equals("..")) //if currently at root, and path is ..
        {
            throw new RootReachedException();
        }
        StringTokenizer st = new StringTokenizer(path, "/");
        levels = new ArrayList<>();
        String firstLevel = st.nextToken();
        if (!firstLevel.equals(vol.getLabel()))
        {
            this.path = parentPath + "/" + path;
            st = new StringTokenizer(this.path, "/");
        }
        while (st.hasMoreTokens())
        {
            String t = st.nextToken();
            if (!(t.equals(vol.getLabel())))
            {
                levels.add(t);
            }
        }
        tidyPath();
        getData(2);
        descend();
    }

    /**
     * Traverses down filesystem to <code>path</code> directory by getting each successive directory's files form its data blocks
     * @throws NoSuchDirectoryException thrown when <code>path</code> cannot be found
     */
    private void descend() throws NoSuchDirectoryException
    {
        Iterator levelItr = levels.iterator();
        Iterator fileItr;
        while (levelItr.hasNext()) //loop through each requested directory
        {
            fileItr = files.iterator();
            boolean present = false;
            String next = (String)levelItr.next();
            FileInfo f = null;
            while (!present && fileItr.hasNext()) //attempt to match next directory in path against files found in preceding directory
            {
                f = (FileInfo)fileItr.next();
                if (next.equals(f.getName()))
                {
                    present = true;
                }
            }
            if (present)
            {
                getData(f.getInodeNum());
            }
            else
            {
                throw new NoSuchDirectoryException(path);
            }
        }
    }

    /**
     * Get data from the data blocks of directory found at given <code>inodeNum</code>
     * @param inodeNum the inode number of Directory to get data for
     */
    private void getData(int inodeNum)
    {
        files = new ArrayList<>();
        Inode inode = vol.getInode(inodeNum);
        ByteBuffer buf;
        int i = 0;
        while ( i < 12 && inode.getDataPointers()[i] != 0)
        {
            int nextEntry = 0;
            buf = initByteBuffer(vol.getBlock(inode.getDataPointers()[i]));
            while (nextEntry != 1024)
            {
                byte[] nameB = new byte[buf.get(6 + nextEntry)];
                buf.position(8 + nextEntry);
                buf.get(nameB, 0, buf.get(6 + nextEntry));
                files.add(new FileInfo(buf.getInt(nextEntry), buf.getShort(4 + nextEntry), buf.get(6 + nextEntry), buf.get(7 + nextEntry), new String(nameB)));
                nextEntry += files.get(files.size() - 1).getLength();
                buf.position(nextEntry);
            }
            i++;
        }
    }

    /**
     * Trims occurrences of . and .. from <code>path</code>
     */
    private void tidyPath()
    {
        StringTokenizer st = new StringTokenizer(path, "/");
        ArrayList<String> newPathTokens = new ArrayList<>();
        while (st.hasMoreTokens())
        {
            newPathTokens.add(st.nextToken());
        }
        for (String s : newPathTokens)
        {
            if (s.equals(".."))
            {
                newPathTokens.remove(newPathTokens.indexOf(s) - 1);
                newPathTokens.remove(s);
                break;
            }
            else if (s.equals("."))
            {
                newPathTokens.remove(s);
                break;
            }
        }
        path = "";
        for (String s : newPathTokens)
        {
            path = path + s + "/";
        }
        path = path.substring(0, path.length() - 1);
    }

    public String getPath()
    {
        return path;
    }

    /**
     * Returns contents of a directory in a form suited to being output in Unix like format
     * @return array of <code>FileInfo</code> objects for all files in directory
     */
    public FileInfo[] getFileInfo()
    {
        return files.toArray(new FileInfo[files.size()]);
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
}
