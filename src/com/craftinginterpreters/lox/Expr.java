package com.craftinginterpreters.lox;

/**
 * 所有表达式 AST 节点的抽象基类
 * 通过 visitor pattern 执行具体操作
 */
abstract class Expr {
    /**
     * visitor interface
     * 定义各类表达式节点的处理操作
     *
     * @param <R> 处理结果的类型
     */
    interface Visitor<R> {
        R visitBinaryExpr(Binary expr);

        R visitGroupingExpr(Grouping expr);

        R visitLiteralExpr(Literal expr);

        R visitUnaryExpr(Unary expr);
    }

    // Binary (二元运算)
    static class Binary extends Expr {
        /**
         * 创建二院表达式节点
         *
         * @param left     左侧表达式
         * @param operator 运算符
         * @param right    右侧表达式
         */
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    // Grouping (括号分组)
    static class Grouping extends Expr {
        /**
         * 创建分组表达式节点
         *
         * @param expression 括号内的表达式
         */
        Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final Expr expression;
    }

    // Literal (字面量)
    static class Literal extends Expr {
        /**
         * 创建字面量表达式节点
         *
         * @param value 字面量的值
         */
        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }

    // Unary (一元运算)
    static class Unary extends Expr {
        /**
         * 创建一元表达式节点
         *
         * @param operator 运算符
         * @param right    右侧表达式
         */
        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final Expr right;
    }

    /**
     * 将当前节点交给访问者处理
     *
     * @param <R>     处理结果的类型
     * @param visitor 表达式访问者
     * @return 处理结果
     */
    abstract <R> R accept(Visitor<R> visitor);
}
