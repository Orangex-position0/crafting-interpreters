package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    // error signal
    static boolean hadError = false;

    /**
     * 程序入口：有一个参数时执行指定脚本，无参数时进入交互模式
     * 参数超过一个时输出用法说明，并以状态码 64 退出
     *
     * @param args 命令行参数，可包含一个脚本路径
     * @throws IOException 读取脚本文件或标准输入失败时抛出
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    /**
     * Interpreter 运行方式之一: 执行指定的文件
     *
     * @param path
     * @throws IOException
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code
        if (hadError)
            System.exit(65);
    }

    /**
     * Interpreter 运行方式之一: 交互式运行，REPL
     *
     * @throws IOException
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.println("> ");
            String line = reader.readLine();
            if (line == null)
                break;
            run(line);
            hadError = false;
        }
    }

    /**
     * core function: 运行源代码
     *
     * @param source
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    /**
     * 行号错误报告入口
     *
     * @param line    错误所在的行号
     * @param message 错误信息
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * 向标准错误输出错误信息，并标记本次运行发生了错误
     *
     * @param line    错误所在的行号
     * @param where   错误位置的补充说明，无补充说明时传入空字符串
     * @param message 错误信息
     */
    public static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}
