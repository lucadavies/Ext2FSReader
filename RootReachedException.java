public class RootReachedException extends Exception
{
    /**
     * Signals that there is no parent directory to the root
     */
    public RootReachedException()
    {
        super();
    }

    public String getMessage()
    {
        return "Root directory reached - cannot go up a level";
    }
}
