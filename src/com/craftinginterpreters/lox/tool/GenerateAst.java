package com.craftinginterpreters.lox.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * 工具类，生成抽象语法树（AST）
 */
public class GenerateAst {
    /**
     * 读取输出目录参数，生成表达式节点文件
     *
     * @param args 命令行参数，指定输出目录
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }

        String outputDir = args[0];

        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"));
    }

    /**
     * @param outputDir 输出目录
     * @param baseName  基类名
     * @param types     节点定义列表
     * @throws IOException 创建输出文件失败
     */
    private static void defineAst(
            String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.craftinginterpreters.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        // 生成 visitor interface
        defineVisitor(writer, baseName, types);

        // 生成 AST 节点子类
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        // 生成基类的抽象 accept() 方法
        writer.println();
        writer.println("  abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    /**
     * 生成 visitor interface
     *
     * @param writer
     * @param baseName
     * @param types
     */
    private static void defineVisitor(
            PrintWriter writer,
            String baseName,
            List<String> types) {
        writer.println("  interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" +
                    typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("  }");
    }

    /**
     * 生成单个节点子类的构造方法和字段。
     *
     * @param writer    代码输出流
     * @param baseName  基类名
     * @param className 节点子类名
     * @param fieldList 字段声明列表
     */
    private static void defineType(
            PrintWriter writer,
            String baseName,
            String className,
            String fieldList) {

        // 定义 AST 子类
        writer.println(
                "  static class " + className + " extends " + baseName + " {");

        // Constructor
        writer.println("    " + className + "(" + fieldList + ") {");

        // 初始化字段
        String[] fields = fieldList.split(", ");

        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("      this." + name + " = " + name + ";");
        }

        writer.println("    }");

        // 新增：为每个 AST 子类生成 accept() 方法
        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println(
                "      return visitor.visit"
                        + className + baseName + "(this);");
        writer.println("    }");

        // 定义字段
        writer.println();

        for (String field : fields) {
            writer.println("    final " + field + ";");
        }

        writer.println("  }");
    }
}
