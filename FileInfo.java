/**
 * Represents the information about a file that is stored in an ext2 file system directory file
 */
public class FileInfo {
    private int inodeNum;
    private int length;
    private int nameLen;
    private int fileType;
    private String name;

    /**
     * Creates a new <code>FileInfo</code> instance
     * @param inodeNum the inode number of the associated file
     * @param length the length, in bytes, of the directory entry storing the data in this <code>FileInfo</code> instance
     * @param nameLen the length, in bytes, of the name of the associated file
     * @param fileType the file type of the associated file
     * @param name
     */
    public FileInfo(int inodeNum, int length, int nameLen, int fileType, String name) {
        this.inodeNum = inodeNum;
        this.length = length;
        this.nameLen = nameLen;
        this.fileType = fileType;
        this.name = name;
    }

    /**
     * Returns the inode number of the associated file
     * @return the inode number
     */
    public int getInodeNum() {
        return inodeNum;
    }

    /**
     * Returns the length, in bytes, of the directory entry storing the data in this <code>FileInfo</code> instance
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the length of the name, in bytes, of the name of the associated file
     * @return the length
     */
    public int getNameLen() {
        return nameLen;
    }

    /**
     * Returns the file type of the associated file
     * @return the file type
     */
    public int getFileType() {
        return fileType;
    }

    /**
     * Returns the name of the associated file
     * @return the name
     */
    public String getName() {
        return name;
    }
}