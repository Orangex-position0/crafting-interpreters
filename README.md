# Crafting Interpreters 实战

跟随 Robert Nystrom 的《Crafting Interpreters》动手实现 Lox 语言解释器，记录学习过程中的代码。

## 项目结构

```text
.
├── README.md
└── jlox/
    └── Lox.java    # Java 解释器入口
```

`jlox` 对应书中的第二部分 **A Tree-Walk Interpreter（树遍历解释器）**，使用 Java 实现。

## 当前进度

目前处于解释器入口与词法扫描的搭建阶段，尚不能编译运行。

- 已搭建命令行入口，支持脚本文件与交互式输入两种模式。
- 已预留词法扫描调用和 Token 输出流程，尚缺少 `Scanner`、`Token` 实现。
- 已有错误报告方法，`error()` 尚未完成。
- `Lox.java` 中的 `List` 目前误导入为 `java.awt.List`，后续需要改为 `java.util.List`。

## 编译与运行

需要安装 JDK，并确保 `java` 和 `javac` 在命令行中可用。目前没有引入构建工具。

**以下是补齐实现后的编译与运行方式；当前代码尚无法执行这些流程。** 在仓库根目录运行：

```bash
# 编译
javac jlox/*.java

# 交互模式（通过 EOF 结束输入）
java -cp jlox Lox

# 脚本模式：将路径替换为自己的 Lox 源文件
java -cp jlox Lox path/to/script.lox
```

## 参考资料

- [Crafting Interpreters 在线书籍](https://craftinginterpreters.com/contents.html)
- [第二部分：A Tree-Walk Interpreter](https://craftinginterpreters.com/a-tree-walk-interpreter.html)
- [作者的源码仓库](https://github.com/munificent/craftinginterpreters)

## 许可证

本项目采用 [MIT License](LICENSE)。基于书中源码的实现保留原作者 Robert Nystrom 的版权声明，本项目新增内容署名为 Orange-position0。

书籍原文、插图及网站素材不属于本项目 MIT 授权范围，其使用须遵守[原项目许可证](https://github.com/munificent/craftinginterpreters/blob/master/LICENSE)。
