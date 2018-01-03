import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarLoader {
    public String jarFileName;
    private final HashMap<String, Class> classes = new HashMap<>();
    private final HashSet<String> packages = new HashSet<>();
    private static String url;
    private String aPackage;
    boolean fileExists = false;

    private Class getClassFromClasses(String className){
        Class cls;
        cls = this.classes.get(className);

        return cls;
    }

    private Class getSuperClass(Class cls){
        Class superClass = null;
        superClass = cls.getSuperclass();

        return superClass;
    }

    public JarLoader(Scanner url) {
        JarLoader.url = url.nextLine();
        Pattern pattern = Pattern.compile("^[^C][^:][^\\\\][A-Za-z0-9[^:*?\"<>|]]+(\\\\[A-Za-z0-9[^:*?\"<>|]])*");
        Matcher matcher = pattern.matcher(JarLoader.url);
        if (matcher.find()) JarLoader.url = "C:\\" + JarLoader.url;
    }

    public void loadClassesFromJarFile() {
        classes.clear();
        List<String> classNames;
        classNames = loadClassNames();

        File jarFile;
        jarFile = new File(url + "\\" + jarFileName);
        ClassLoader classLoader;
        classLoader = this.getClass().getClassLoader();

        Path file = Paths.get(url + "\\" + jarFileName);
        boolean isRegularExecutableFile = Files.isRegularFile(file) &
                Files.isReadable(file) & Files.isExecutable(file);
        System.out.println(isRegularExecutableFile);
        URLClassLoader urlClassLoader = null;
        try {
            urlClassLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, classLoader);
            if (classNames == null) {
                fileExists = false;
            } else {
                fileExists = true;
                for (String className : classNames) {
                    classes.put(className, urlClassLoader.loadClass(className));
                    try {
                        packages.add(urlClassLoader.loadClass(className).getPackage().getName());
                    } catch (Exception e) {
                        packages.add("no package");
                    }
                    findClassesAndInterfaces(urlClassLoader.loadClass(className));
                }
            }
        } catch (MalformedURLException e) {
            System.out.println("Pathname of jar file is wrong.");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not exist.");
        }


    }

    public static void showJarFiles() throws InterruptedException, IOException {
        File dir = new File(url);
        File[] listOfFiles = dir.listFiles();

        System.out.println("List of .jar files:");
        assert listOfFiles != null;
        try {
            for (File element : listOfFiles) {
                System.out.println(element.getName());
            }
            System.out.println("In catalog: ");
            System.out.println(dir.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Try input another path..");
            inputPath();
        }
    }

    public static void inputPath() throws IOException, InterruptedException {
        new JarLoader(new Scanner(System.in));
        JarLoader.showJarFiles();
    }

    public void showInterfaces() {
        if (fileExists) {
            Class cls;
            System.out.println("Interfaces:");
            for (String pck : packages) {
                for (Map.Entry<String, Class> entry : classes.entrySet()) {
                    cls = entry.getValue();
                    try {
                        aPackage = cls.getPackage().getName();
                    } catch (Exception e) {
                        aPackage = "no package";
                    }
                    if (cls.isInterface() && (Objects.equals(aPackage, pck))) {

                        System.out.println("\t" + cls.getName());
                    }
                }
            }
        }
    }

    public void showClasses() {
        if (fileExists) {
            Class cls;
            System.out.println("Classes:");
            for (String pck : packages) {
                for (Map.Entry<String, Class> entry : classes.entrySet()) {
                    cls = entry.getValue();
                    try {
                        aPackage = cls.getPackage().getName();
                    } catch (Exception e) {
                        aPackage = "no package";
                    }

                    if (!cls.isInterface() && (Objects.equals(aPackage, pck))) {

                        System.out.println("\t" + cls.getName());
                    }
                }
            }
        }
    }

    private List<String> loadClassNames() {
        ZipInputStream zipInputStream = null;
        try {
            zipInputStream = new ZipInputStream(new FileInputStream(url + "\\" + jarFileName));
        } catch (Exception e) {
            System.out.println("Jar file " + jarFileName + " not exists");
            new Scanner(System.in);
        }
        if (zipInputStream != null) {
            List<String> classNames = new ArrayList<>();

            try {
                for (ZipEntry entry = zipInputStream.getNextEntry(); entry != null; entry = zipInputStream.getNextEntry()) {
                    if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                        String className = entry.getName().replace('/', '.');
                        classNames.add(className.substring(0, className.length() - ".class".length()));
                    }
                }
            } catch (IOException e) {
                System.out.println("Zip input stream not exist.");
            }
            return classNames;
        }
        return null;
    }

    private void findClassesAndInterfaces(Class parentClass) {
        Class[] classes = parentClass.getDeclaredClasses();

        for (Class cls : classes) {
            this.classes.put(cls.getName(), cls);
        }
    }

    public void showInterfaceClasses(String interfaceName) {
        Class cls = classes.get(interfaceName);
        if (cls == null)
            System.out.println(interfaceName + " interface is not exist");
        else
            readInterfaceClasses(interfaceName);
    }

    private void readInterfaceClasses(String interfaceName) {
        boolean empty = true;
        boolean interfaceExists = false;
        Class cls;

        for (Map.Entry<String, Class> entry : classes.entrySet()) {
            cls = entry.getValue();
            if (cls.isInterface()) interfaceExists = true;

        }
        if (interfaceExists) {
            printInterfaceClasses(interfaceName, empty);
        } else
            System.out.println("Interface " + interfaceName + " not exist");
    }

    public void getConstructor(Class cls) {
        if (cls != null) {
            Constructor[] constructors = cls.getDeclaredConstructors();
            System.out.println("Constructors:");
            for (Constructor c : constructors) {
                Class[] paramTypes = c.getParameterTypes();
                String name = c.getName();
                System.out.print("- " + Modifier.toString(c.getModifiers()));
                System.out.print(" " + name + "(");
                printConstructorParam(paramTypes);
                System.out.println(");");
            }
        }
    }

    public void showClassFields(Class cls) {
        if (cls != null) {
            ArrayList<Field> protectedFields = (ArrayList<Field>) getProtectedFields(cls);
            Field[] fields = cls.getDeclaredFields();
            ArrayList<Field> declaredFields = new ArrayList<>();
            for (int i = 0; i < fields.length; i++) {
                declaredFields.add(fields[i]);
            }
            System.out.println("Class " + cls.getSimpleName() + " fields:");
            printFields(declaredFields);
            printFields(protectedFields);
        } else {
            System.out.println(cls.getSimpleName() + " class not exist.");
        }
    }

    public void showClassMethods(Class cls) {

        if (cls != null) {
            ArrayList<Method> protectedMethods = (ArrayList<Method>) getProtectedMethods(cls);
            Method[] methods = cls.getDeclaredMethods();
            ArrayList<Method> declaredMethods = new ArrayList<>();
            for (int i = 0; i < methods.length; i++) {
                declaredMethods.add(methods[i]);
            }

            System.out.println("Class " + cls.getSimpleName() + " methods");
            printMethods(declaredMethods);
            printMethods(protectedMethods);
        } else {
            System.out.println(cls.getSimpleName() + " class not exist.");
        }
    }

    private List<Field> getProtectedFields(Class superClass) {
        ArrayList<Field[]> fields = new ArrayList<>();
        List<Field> protectedFields = new ArrayList<>();
        if (superClass != null) {
            fields.add(superClass.getDeclaredFields());
        }
        for (Field[] fld : fields)
            for (Field f : fld)
                if (Modifier.PROTECTED == f.getModifiers() || Modifier.PUBLIC == f.getModifiers())
                    protectedFields.add(f);

        return protectedFields;
    }

    private List<Method> getProtectedMethods(Class superClass) {
        ArrayList<Method[]> methods = new ArrayList<>();
        List<Method> outerMethods = new ArrayList<>();
        if (superClass != null) {
            methods.add(superClass.getDeclaredMethods());
        }
        getProtectedFields(superClass);
        for (Method[] mtd : methods)
            for (Method m : mtd)
                if (Modifier.PROTECTED == m.getModifiers() || Modifier.PUBLIC == m.getModifiers())
                    outerMethods.add(m);

        return outerMethods;
    }

    private void printFields(ArrayList<Field> declaredFields){
        for (Field fld : declaredFields) {
            System.out.println("- " + Modifier.toString(fld.getModifiers()) + " " + fld.getGenericType() + " " + fld.getName());
        }
    }

    private void printMethods(ArrayList<Method> methods) {
        for (Method mtd : methods) {
            System.out.println("- " + Modifier.toString(mtd.getModifiers()) + " " + mtd.getGenericReturnType() + " " + mtd.getName() + "() {");
            for (Parameter p : mtd.getParameters()) {
                System.out.println("\t" + Modifier.toString(p.getModifiers()) + " " + p.getType().getSimpleName() + " " + p.getName());
            }
            System.out.println("  }");
        }
    }
    private void printConstructorParam(Class[] paramTypes){
        for (int j = 0; j < paramTypes.length; j++) {
            if (j > 0)
                System.out.print(", ");
            System.out.print(paramTypes[j].getCanonicalName());
        }
    }

    private void printInterfaceClasses(String interfaceName, boolean empty) {
        for (Map.Entry<String, Class> entry : classes.entrySet()) {
            for (Class i : entry.getValue().getInterfaces())
                if (interfaceName.equals(i.getName())) {
                    empty = false;
                    System.out.println(entry.getValue().getName());
                }
        }
        if (empty) System.out.println("No classes is implementing by " + interfaceName + " interface");
    }

    public void printInterfacesAndClasses(Commander cmd, int commandNumber, String command) {
        jarFileName = cmd.getNameFromCommand(commandNumber, command);
        loadClassesFromJarFile();
        showInterfaces();
        showClasses();
    }

    public void printClassesOfInterface(Commander cmd, int commandNumber, String command){
        String interfaceName = cmd.getNameFromCommand(commandNumber, command);
        showInterfaceClasses(interfaceName);
    }

    public void printElementsOfClass(Commander cmd, int commandNumber, String command){
        String className = cmd.getNameFromCommand(commandNumber, command);
        Class cls = getClassFromClasses(className);
        if(cls == null){
            System.out.println(className + " class is not exist.");
        }
        else {
            showClassFields(cls);
            showClassMethods(cls);
            getConstructor(cls);
        }
    }
}
