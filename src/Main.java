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
                JarLoader.inputPath();
            } else {
                commandNumber = cmd.checkCommand(command);
                switch (commandNumber) {
                    case 1:
                        jarLoader.printInterfacesAndClasses(cmd, commandNumber, command);
                        break;
                    case 2:
                        jarLoader.printClassesOfInterface(cmd, commandNumber, command);
                        break;
                    case 3:
                        jarLoader.printElementsOfClass(cmd, commandNumber, command);
                        break;
                    case 4:
                        printHelper();
                        break;
                    default:
                        printErrorOfCommand();
                }
            }
            command = reader.nextLine();

        }
    }

    private static void printHelper(){
        System.out.println("******* Help *******");
        System.out.println("JarExplorer file_name.jar\t\tlist of classes and interfaces in file_name.jar");
        System.out.println("JarExplorer -i interface_name\tlist of classes which implements interface_name");
        System.out.println("JarExplorer -c class_name\t\tfields and methods existing in class_name");
        System.out.println("JarExplorer -h help");
        System.out.println("exit\t\"exit\"");
    }

    private static void printErrorOfCommand() {
        System.out.println("\nThis command not allowed");
        System.out.println("JarExplorer -h help");
    }
}
