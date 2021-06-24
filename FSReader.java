import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FSReader
{
    public static void main(String[] args)
    {
        Helper h = new Helper();
        String input = "";
        Scanner sc = new Scanner(System.in);
        Volume vol = new Volume("./res/ext2fs");
        Directory prevDir = getDir(vol, vol.getLabel(), null);
        Directory workingDir = prevDir;
        Ext2File file;
        while (!input.equals("exit"))
        {
            System.out.print("~" + workingDir.getPath() + "\n$ ");
            String arg;
            input = sc.next();
            switch (input)
            {
                case "cd":
                    arg = sc.nextLine();
                    if (Pattern.matches(" [^ ].*", arg))
                    {
                        String path = arg.substring(1).replace("\"", ""); //remove leading space and quotes
                        prevDir = workingDir;
                        workingDir = getDir(vol, path, prevDir);
                    }
                    break;
                case "cat":
                    arg = sc.nextLine();
                    int start = 0;
                    int len = -1;
                    String fileName;
                    if (Pattern.matches(" [^ ].*", arg))
                    {
                        arg = arg.substring(1).replace("\"", ""); //remove leading space and quotes
                        List<String> argList = Arrays.asList(arg.split(" "));
                        for (String s : argList)
                        {
                            if (s.equals(""))
                            {
                                argList.remove(s);
                            }
                        }
                        fileName = argList.get(0);
                        try
                        {
                            if (argList.size() == 3)
                            {
                                start = Integer.parseInt(argList.get(1));
                                len = Integer.parseInt(argList.get(2));
                            }
                            else if (argList.size() == 2)
                            {
                                len = Integer.parseInt(argList.get(1));
                            }
                        }
                        catch (NumberFormatException e)
                        {
                            System.out.println(e.getMessage());
                        }
                        boolean fileExists = false;
                        for (FileInfo fi : workingDir.getFileInfo())
                        {
                            if (fi.getName().equals(fileName))
                            {
                                fileExists = true;
                                file = new Ext2File(vol, fi);
                                System.out.println(new String(file.read(start, (len == -1 ? file.size() : len)), StandardCharsets.UTF_8).trim());
                                System.out.println();
                                break;
                            }
                        }
                        if (!fileExists)
                        {
                            System.out.println("cat: " + fileName + ": No such file\n");
                        }
                    }
                    break;
                case "ls":
                    for (FileInfo f : workingDir.getFileInfo())
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd hh:mm ");
                        Inode i = vol.getInode(f.getInodeNum());
                        System.out.println(i.getFileMode() + " " + String.format("%2d ", i.getHardLinks()) + String.format("%6d ", i.getUID()) + String.format("%6d ", i.getGID()) + String.format("%8d ", i.getSize()) + " " + sdf.format(i.getLastModTime()) + f.getName());
                    }
                    System.out.println();
                    break;
                case "exit":
                    break;
                default:
                    System.out.println("Syntax / command error.\n");
            }
        }
    }

    /**
     * Gets a Directory object in a Volume from a given path. If the requested directory cannot be found / accessed, <code>prevDir</code> is returned, so no change is observed
     * @param vol volume in which directory resides
     * @param path path of the directory to get
     * @param prevDir last directory to be accessed
     * @return the requested directory or the same directory as last accessed if an exception was raised
     */
    private static Directory getDir(Volume vol, String path, Directory prevDir)
    {
        try
        {
            return new Directory(vol, path, (prevDir == null ? "" : prevDir.getPath()));
        }
        catch (NoSuchDirectoryException e)
        {
            System.out.println(e.getMessage());
            System.out.println();
            return prevDir;
        }
        catch (RootReachedException e)
        {
            System.out.println(e.getMessage());
            System.out.println();
            return prevDir;
        }
    }
}