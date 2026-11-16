package com.craftinginterpreters.lox;

import static com.craftinginterpreters.lox.TokenType.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * scanner
 */
class Scanner {
    // raw source code string
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    // Index of the first character in the current lexeme
    private int start = 0;
    // Index of the character currently being considered.
    private int current = 0;
    // Line number of the character at current.
    private int line = 0;

    // 关键字与 tokentype 的映射表
    private static final Map<String, TokenType> keywords;

    // 初始化保留字映射
    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    /**
     * 扫描全部源代码，生成 token 列表，并在末尾添加 EOF 标记
     *
     * @return 包含 EOF 标记的 token 列表
     */
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    /**
     * 从当前位置扫描一个词素，为有效词素生成 token，跳过空白和行注释
     */
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            // operators
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd())
                        advance();
                } else {
                    addToken(SLASH);
                }
                break;
            // whitespace
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            // String Literal
            case '"':
                string();
                break;
            default:
                // handle Number Literal
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    /**
     * 条件消费：仅当下一个字符符合预期时才消费
     *
     * @param expected
     * @return
     */
    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    /**
     * 扫描字符串字面量，提取字面量值并生成对应 token
     */
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // 消费结束引号
        advance();

        // 去掉收尾引号，提取字面量值
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    /**
     * 扫描数字字面量，提取字面量值并生成对应 token
     */
    private void number() {
        while (isDigit(peek()))
            advance();

        // 查看小数部分
        if (peek() == '.' && isDigit(peekNext())) {
            // 消费 '.' 字符
            advance();

            while (isDigit(peek()))
                advance();
        }

        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    /**
     * 扫描标识符，并生成对应 token
     */
    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null)
            type = IDENTIFIER;
        addToken(type);
    }

    // ========== helper methods ==========

    /**
     * 判断 scanning 是否可以结束
     *
     * @return
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * 查看并消费下一个字符
     *
     * @return 被消费的字符
     */
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * 只查看下一个未消费字符，不推进 current
     *
     * @return 下一个未消费字符
     */
    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    /**
     * 查看下下一个字符，不消费
     *
     * @return
     */
    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    /**
     * 添加不含字面量的 token
     *
     * @param type Token type
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * 添加 token
     *
     * @param type    Token type
     * @param literal
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    /**
     * 判断字符是否为 ASCII 数字（0-9）
     *
     * @param c 待检查的字符
     * @return 如果是数字则返回 true，否则返回 false
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * 判断字符是否为 ASCII 字母（a-z、A-Z）或下划线
     *
     * @param c 待检查的字符
     * @return 如果是字母或下划线则返回 true，否则返回 false
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    /**
     * 判断字符是否为 ASCII 字母、数字或下划线
     *
     * @param c 待检查的字符
     * @return 如果符合条件则返回 true，否则返回 false
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

}
