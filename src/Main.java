import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        int commandNumber;
        JarLoader jarLoader = new JarLoader(new Scanner(System.in));
        JarLoader.showJarFiles();
        Scanner reader = new Scanner(System.in);
        Commander cmd = new Commander();
        String command = reader.nextLine();
        while (!command.equals("exit")) {
            if (command.equals("again")) {
                JarLoader.inputURL();
            } else {
                commandNumber = cmd.checkCommand(command);
                switch (commandNumber) {
                    case 1:
                        jarLoader.jarFileName = cmd.getNameFromCommand(commandNumber, command);
                        jarLoader.loadClassesFromJarFile();
                        jarLoader.showInterfaces();
                        jarLoader.showClasses();
                        break;
                    case 2:
                        String interfaceName = cmd.getNameFromCommand(commandNumber, command);
                        jarLoader.showInterfaceClasses(interfaceName);
                        break;
                    case 3:
                        String className = cmd.getNameFromCommand(commandNumber, command);
                        jarLoader.showClassFields(className);
                        jarLoader.showClassMethods(className);
                        break;
                    case 4:
                        System.out.println("******* Help *******");
                        System.out.println("JarExplorer file_name.jar\t\tlist of classes and interfaces in file_name.jar");
                        System.out.println("JarExplorer -i interface_name\tlist of classes which implements interface_name");
                        System.out.println("JarExplorer -c class_name\t\tfields and methods existing in class_name");
                        System.out.println("JarExplorer -h help");
                        System.out.println("exit\t\"exit\"");
                        break;
                    default:
                        System.out.println("\nThis command not allowed");
                        System.out.println("JarExplorer -h help");
                }
            }
            command = reader.nextLine();

        }
    }
}
