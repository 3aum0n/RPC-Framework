package util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author 3aum0n
 */
public class ReflectUtil {

    public static String getStackTrace() {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        return stack[stack.length - 1].getClassName();
    }

    public static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件，并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是 jar 包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取 jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从 jar 包中获取一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取 jar 里的一个实体 （目录、jar包中的其他文件）
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以 / 开头
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以 "/" 结尾（代表一个包）
                                if (idx != -1) {
                                    // 获取包名 把'/'替换成'.'
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                //如果可以继续迭代，且还是包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class"获取真正的包名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // classes.add(Class.forName(packageName + '.' + className));
                                            classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
                                                         final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在 或 不是目录 直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在（即找到） 就获取包下的所有文件 包括目录
        File[] dirFiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 可以循环获取目录(包含子目录) 或 以.class结尾的文件(编译好的java类文件)
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirFiles) {
            // 如果是目录，则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是Java类的文件，去掉后面的.class只留类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中
                    // classes.add(Class.forName(packageName + '.' + className));
                    // forName会触发static方法，没有使用classLoader的load干净
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }
}
