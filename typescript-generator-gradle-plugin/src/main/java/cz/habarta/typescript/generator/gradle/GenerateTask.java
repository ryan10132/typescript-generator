
package cz.habarta.typescript.generator.gradle;

import cz.habarta.typescript.generator.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.gradle.api.*;
import org.gradle.api.tasks.*;


public class GenerateTask extends DefaultTask {

    public String outputFile;
    public List<String> classes;
    public JsonLibrary jsonLibrary;
    public String namespace;
    public String module;
    public boolean declarePropertiesAsOptional;
    public String removeTypeNameSuffix;
    public DateMapping mapDate;


    @TaskAction
    public void generate() throws Exception {
        if (outputFile == null) {
            throw new RuntimeException("Please specify 'outputFile' property.");
        }
        System.out.println("outputFile: " + outputFile);
        if (classes == null) {
            throw new RuntimeException("Please specify 'classes' property.");
        }
        System.out.println("classes: " + classes);

        // class loader
        final List<URL> urls = new ArrayList<>();
        for (Task task : getProject().getTasksByName("compileJava", false)) {
            for (File file : task.getOutputs().getFiles()) {
                urls.add(file.toURI().toURL());
            }
        }
        for (File file : getProject().getConfigurations().getAt("compile").getFiles()) {
            urls.add(file.toURI().toURL());
        }
        URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());

        // classes
        final List<Class<?>> classList = new ArrayList<>();
        for (String className : classes) {
            classList.add(classLoader.loadClass(className));
        }

        final Settings settings = new Settings();
        settings.jsonLibrary = jsonLibrary;
        settings.namespace = namespace;
        settings.module = module;
        settings.declarePropertiesAsOptional = declarePropertiesAsOptional;
        settings.removeTypeNameSuffix = removeTypeNameSuffix;
        settings.mapDate = mapDate;
        TypeScriptGenerator.generateTypeScript(classList, settings, getProject().file(outputFile));
    }

}
