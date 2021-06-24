public class NoSuchDirectoryException extends Exception
{
    String path;

    /**
     * Signals that there is no such directory as attempted to access
     * @param path the path that could not be found
     */
    public NoSuchDirectoryException(String path)
    {
        super();
        this.path = path;
    }

    public String getMessage()
    {
        return "cd: " + path + ": No such directory";
    }
}
