import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
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

    public JarLoader(Scanner url) {
        JarLoader.url = url.nextLine();
        Pattern pattern = Pattern.compile("^[^C][^:][^\\\\][A-Za-z0-9[^:*?\"<>|]]+(\\\\[A-Za-z0-9[^:*?\"<>|]])*");
        Matcher matcher = pattern.matcher(JarLoader.url);
        if(matcher.find()) JarLoader.url = "C:\\" + JarLoader.url;
    }

    public void loadClassesFromJarFile() throws IOException, ClassNotFoundException {
        classes.clear();
        List<String> classNames;
        classNames = loadClassNames();

        File jarFile = new File(url + "\\" + jarFileName);
        ClassLoader classLoader;
        classLoader = this.getClass().getClassLoader();

        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, classLoader);
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

    public void showClasses() {
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

    private List<String> loadClassNames() throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(url + "\\" + jarFileName));
        List<String> classNames = new ArrayList<>();

        for (ZipEntry entry = zipInputStream.getNextEntry(); entry != null; entry = zipInputStream.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                String className = entry.getName().replace('/', '.');
                classNames.add(className.substring(0, className.length() - ".class".length()));
            }
        }
        return classNames;
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
        boolean exist = false;
        Class cls;

        for (Map.Entry<String, Class> entry : classes.entrySet()) {
            cls = entry.getValue();
            if (cls.isInterface()) exist = true;

        }
        if (exist) {
            for (Map.Entry<String, Class> entry : classes.entrySet()) {
                for (Class i : entry.getValue().getInterfaces())
                    if (interfaceName.equals(i.getName())) {
                        empty = false;
                        System.out.println(entry.getValue().getName());
                    }
            }
            if (empty) System.out.println("No classes is implementing by " + interfaceName + " interface");
        } else
            System.out.println("Interface " + interfaceName + " not exist");
    }

    public void getConstructor(String className) {
        Class cls;
        try {
            cls = this.classes.get(className);
        } catch (NullPointerException e) {
            System.out.println("Try input another class:");
            cls = null;
        }
        if (cls != null) {
            Constructor[] constructors = cls.getDeclaredConstructors();
            System.out.println("Constructors:");
            for (Constructor c : constructors) {
                Class[] paramTypes = c.getParameterTypes();
                String name = c.getName();
                System.out.print("- " + Modifier.toString(c.getModifiers()));
                System.out.print(" " + name + "(");
                for (int j = 0; j < paramTypes.length; j++) {
                    if (j > 0)
                        System.out.print(", ");
                    System.out.print(paramTypes[j].getCanonicalName());
                }
                System.out.println(");");
            }
        }
    }

    public void showClassFields(String className) {
        Class cls;
        try {
            cls = this.classes.get(className);
        } catch (NullPointerException e) {
            System.out.println("Try input another class:");
            cls = null;
        }
        if (cls != null) {
            ArrayList<Field> protectedFields = (ArrayList<Field>) getProtectedFields(cls);
            Field[] declaredFields = cls.getDeclaredFields();
            System.out.println("Class " + className + " fields:");
            for (Field fld : declaredFields) {
                System.out.println("- " + Modifier.toString(fld.getModifiers()) + " " + fld.getGenericType() + " " + fld.getName());
            }
            for (Field fld : protectedFields) {
                System.out.println("- " + Modifier.toString(fld.getModifiers()) + " " + fld.getGenericType() + " " + fld.getName());
            }
        } else {
            System.out.println(className + " class not exist.");
        }
    }

    private List<Field> getProtectedFields(Class cls) {
        Class superClass = null;
        try {
            superClass = cls.getSuperclass();
        } catch (NullPointerException e) {
            System.out.println();
        }
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

    public void showClassMethods(String className) {
        Class cls = null;
        try {
            cls = this.classes.get(className);
        } catch (NullPointerException e) {
            System.out.println("Try input another class:");
        }
        if (cls != null) {
            ArrayList<Method> protectedMethods = (ArrayList<Method>) getProtectedMethods(cls);
            Method[] declaredMethods = cls.getDeclaredMethods();
            System.out.println("Class " + className + " methods");
            for (Method mtd : declaredMethods) {
                System.out.println("- " + Modifier.toString(mtd.getModifiers()) + " " + mtd.getGenericReturnType() + " " + mtd.getName() + "() {");
                for (Parameter p : mtd.getParameters()) {
                    System.out.println("\t" + Modifier.toString(p.getModifiers()) + " " + p.getType().getSimpleName() + " " + p.getName());
                }
                System.out.println("  }");
            }
            for (Method mtd : protectedMethods) {
                System.out.println("- " + Modifier.toString(mtd.getModifiers()) + " " + mtd.getGenericReturnType() + " " + mtd.getName() + "() {");
                for (Parameter p : mtd.getParameters()) {
                    System.out.println("\t" + Modifier.toString(p.getModifiers()) + " " + p.getType().getSimpleName() + " " + p.getName());
                }
                System.out.println("  }");
            }
        } else {
            System.out.println(className + " class not exist.");
        }
    }

    private List<Method> getProtectedMethods(Class cls) {
        Class superClass = null;
        try {
            superClass = cls.getSuperclass();
        } catch (NullPointerException e) {
            System.out.println();
        }
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
}
