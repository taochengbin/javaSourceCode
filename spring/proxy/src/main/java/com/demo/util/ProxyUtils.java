package com.demo.util;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ProxyUtils {
    public static Object newInstance(Object target){
        // 1.生成代理类的java文件
        // 1.1拼接一个代理类内容的字符串
        Object proxy=null;
        Class targetInf = target.getClass().getInterfaces()[0];
        Method methods[] =targetInf.getDeclaredMethods();
        String line="\n";
        String tab ="\t";
        String infName = targetInf.getSimpleName();
        String content ="";
        String packageContent = "package com.google;"+line;
        String importContent = "import "+targetInf.getName()+";"+line;
        String clazzFirstLineContent = "public class $Proxy implements "+infName+"{"+line;
        String filedContent  =tab+"private "+infName+" target;"+line;
        String constructorContent =tab+"public $Proxy ("+infName+" target){" +line
            +tab+tab+"this.target =target;"
            +line+tab+"}"+line;
        String methodContent = "";
        for (Method method : methods) {
            String returnTypeName = method.getReturnType().getSimpleName();
            String methodName =method.getName();
            // Sting.class String.class
            Class args[] = method.getParameterTypes();
            String argsContent = "";
            String paramsContent="";
            int flag =0;
            for (Class arg : args) {
                String temp = arg.getSimpleName();
                //String
                //String p0,Sting p1,
                argsContent+=temp+" p"+flag+",";
                paramsContent+="p"+flag+",";
                flag++;
            }
            if (argsContent.length()>0){
                argsContent=argsContent.substring(0,argsContent.lastIndexOf(",")-1);
                paramsContent=paramsContent.substring(0,paramsContent.lastIndexOf(",")-1);
            }

            // 1.2对目标对象进行方法的增强，打印Method is enhanced。拼接字符串
            methodContent+=tab+"public "+returnTypeName+" "+methodName+"("+argsContent+") {"+line
                +tab+tab+"System.out.println(\"Method is enhanced\");"+line
                +tab+tab+"target."+methodName+"("+paramsContent+");"+line
                +tab+"}"+line;

        }

        content=packageContent+importContent+clazzFirstLineContent+filedContent+constructorContent+methodContent+"}";

        //1.3 写一个$Proxy.java文件 —— 代理对象的java文件
        File file =new File("d:\\com\\google\\$Proxy.java");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            fw.write(content);
            fw.flush();
            fw.close();

            // 2.调用java 的编译器编译成class文件
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileMgr = compiler.getStandardFileManager(null, null, null);
            Iterable units = fileMgr.getJavaFileObjects(file);
            JavaCompiler.CompilationTask t = compiler.getTask(null, fileMgr, null, null, null, units);
            t.call();
            fileMgr.close();

            //3. 通过反射生成代理类
            URL[] urls = new URL[]{new URL("file:D:\\\\")};
            URLClassLoader urlClassLoader = new URLClassLoader(urls);
            Class clazz = urlClassLoader.loadClass("com.google.$Proxy");
            Constructor constructor = clazz.getConstructor(targetInf);
            proxy = constructor.newInstance(target);
            //clazz.newInstance();
            //Class.forName()
        }catch (Exception e){
            e.printStackTrace();
        }

        return proxy;
    }
}
