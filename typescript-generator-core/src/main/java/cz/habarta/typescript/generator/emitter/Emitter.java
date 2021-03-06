
package cz.habarta.typescript.generator.emitter;

import cz.habarta.typescript.generator.Settings;
import cz.habarta.typescript.generator.TsType;
import java.io.*;
import java.util.logging.Logger;


public class Emitter {

    private final Logger logger;
    private final Settings settings;
    private final PrintWriter writer;
    private int indent = 0;

    private Emitter(Logger logger, Settings settings, PrintWriter writer) {
        this.logger = logger;
        this.settings = settings;
        this.writer = writer;
    }

    public static void emit(Logger logger, Settings settings, File outputFile, TsModel model) {
        try (PrintWriter printWriter = new PrintWriter(outputFile)) {
            final Emitter emitter = new Emitter(logger, settings, printWriter);
            emitter.emitModule(model);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void emitModule(TsModel model) {
        if (settings.module != null) {
            writeNewLine();
            writeIndentedLine("declare module '" + settings.module + "' {");
            indent++;
            emitNamespace(model, true);
            indent--;
            writeNewLine();
            writeIndentedLine("}");
        } else {
            emitNamespace(model, false);
        }
    }

    private void emitNamespace(TsModel model, boolean ambientContext) {
        if (settings.namespace != null) {
            writeNewLine();
            final String declarePrefix = ambientContext ? "" : "declare ";
            writeIndentedLine(declarePrefix +  "namespace " + settings.namespace + " {");
            indent++;
            emitObjects(model);
            indent--;
            writeNewLine();
            writeIndentedLine("}");
        } else {
            emitObjects(model);
        }
    }

    private void emitObjects(TsModel model) {
        emitInterfaces(model);
        emitTypeAliases(model);
    }

    private void emitInterfaces(TsModel model) {
        for (TsBeanModel bean : model.getBeans()) {
            writeNewLine();
            final String parent = bean.getParent() != null ? " extends " + bean.getParent() : "";
            writeIndentedLine("interface " + bean.getName() + parent + " {");
            indent++;
            for (TsPropertyModel property : bean.getProperties()) {
                emitProperty(property);
            }
            indent--;
            writeIndentedLine("}");
        }
    }

    private void emitProperty(TsPropertyModel property) {
        if (property.getComments() != null) {
            writeIndentedLine("/**");
            for (String comment : property.getComments()) {
                writeIndentedLine("  * " + comment);
            }
            writeIndentedLine("  */");
        }
        final TsType tsType = property.getTsType() instanceof TsType.EnumType ? TsType.String : property.getTsType();
        final String opt = settings.declarePropertiesAsOptional ? "?" : "";
        writeIndentedLine(property.getName() + opt + ": " + tsType + ";");
    }

    private void emitTypeAliases(TsModel model) {
        for (TsType.AliasType alias : model.getTypeAliases()) {
            writeNewLine();
            writeIndentedLine(alias.definition);
        }
    }

    private void writeIndentedLine(String line) {
        for (int i = 0; i < indent; i++) {
            writer.write(settings.indentString);
        }
        writer.write(line);
        writeNewLine();
    }

    private void writeNewLine() {
        writer.write(settings.newline);
    }

}
