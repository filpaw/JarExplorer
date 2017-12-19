import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commander {
    private final Pattern filePattern = Pattern.compile("^JarExplorer.*\\.jar");
    private final Pattern interfacePattern = Pattern.compile("^JarExplorer.*-i.*");
    private final Pattern fieldsAndMethodsPattern = Pattern.compile("^JarExplorer.*-c.*");
    private final Pattern helpPattern = Pattern.compile("^JarExplorer.*-h.*");

    public int checkCommand(String command) {
        command = command.replace(" ", "");
        Matcher matcher = filePattern.matcher(command);
        if(matcher.find())
            return 1;
        matcher = interfacePattern.matcher(command);
        if(matcher.find())
            return 2;
        matcher = fieldsAndMethodsPattern.matcher(command);
        if(matcher.find())
            return 3;
        matcher = helpPattern.matcher(command);
        if(matcher.find())
            return 4;

        return 0;
    }

    public String getNameFromCommand(int x, String command) {
        String name;
        command = command.replace(" ", "");
        switch(x) {
            case 1:
                name = command.replace("JarExplorer", "");
                break;
            case 2:
                name = command.replace("JarExplorer-i", "");
                break;
            case 3:
                name = command.replace("JarExplorer-c", "");
                break;
            case 4:
                name = command.replace("JarExplorer-h", "");
                break;
            default:
                name = "";
        }
        return name;
    }
}
