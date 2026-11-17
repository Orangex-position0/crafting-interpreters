#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
build_dir=$(mktemp -d)
trap 'rm -rf "$build_dir"' EXIT

sources=()
while IFS= read -r -d '' source; do
  sources+=("$source")
done < <(find src -name '*.java' -type f -print0)

if [[ ${#sources[@]} -eq 0 ]]; then
  echo '未找到 Java 源文件。' >&2
  exit 1
fi

javac -encoding UTF-8 -d "$build_dir/classes" "${sources[@]}"
echo 'Java 编译通过。'

if [[ -f src/com/craftinginterpreters/lox/AstPrinter.java ]]; then
  actual=$(java -cp "$build_dir/classes" com.craftinginterpreters.lox.AstPrinter)
  expected='(* (- 123) (group 45.67))'
  if [[ "$actual" != "$expected" ]]; then
    printf 'AST 打印结果不符。\n预期：%s\n实际：%s\n' "$expected" "$actual" >&2
    exit 1
  fi
  echo 'AST 打印示例验证通过。'
fi

if [[ -f src/com/craftinginterpreters/lox/tool/GenerateAst.java ]]; then
  mkdir -p "$build_dir/generated"
  java -cp "$build_dir/classes" com.craftinginterpreters.lox.tool.GenerateAst "$build_dir/generated"
  javac -encoding UTF-8 -cp "$build_dir/classes" -d "$build_dir/generated-classes" \
    "$build_dir/generated/Expr.java"
  echo 'AST 生成代码编译通过。'
fi
