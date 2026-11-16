package com.craftinginterpreters.lox;

/**
 * 词法分析生成的词法单元，记录类型、源代码文本、字面量值和所在行号。
 */
class Token {
    // 词法单元类型，如标识符、关键字或运算符
    final TokenType type;
    // 词素在源代码中的原始文本
    final String lexeme;
    // 字符串或数字字面量的解析值，其他类型为 null
    final Object literal;
    // 扫描完该词法单元时所在的行号
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
