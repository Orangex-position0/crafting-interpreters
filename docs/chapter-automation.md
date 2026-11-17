# 章节自动化配置

## 工作流程

1. 从更新后的主分支创建 `part-<篇号>/chapter-<章节号>-<名称>` 分支。
2. 推送章节分支，自动创建中文 Draft PR；后续推送沿用该 PR。
3. 开发期间保持 Draft。用户确认本章完成后，补充改动摘要和验证结果，将 PR 转为 Ready，并启用自动合并。
4. 必需检查通过后，GitHub 使用 merge commit 合并，保留章节分支。
5. 确认远端合并后更新本地主分支，再创建下一章分支。

编号和英文名称以书本目录为准。PR 标题中的英文名称来自分支名，创建后可按书本标题修正。

## GitHub 仓库设置

以下设置须由有权限的维护者启用；仅添加工作流文件不会自动更改仓库设置。

- **Settings → Actions → General → Workflow permissions**：允许 GitHub Actions 创建和批准 Pull Request。工作流本身仅创建 PR，不批准或合并 PR；权限在工作流内限定为 `contents: read` 与 `pull-requests: write`。
- **Settings → General → Pull Requests**：启用 Allow auto-merge 和 Allow merge commits，关闭 Automatically delete head branches。
- **Settings → Branches**：保护默认分支，要求通过 Pull Request 合并，设置 `chapter-ci` 为必需状态检查，并要求分支在合并前保持最新。不要强制删除分支或要求线性历史。
- 个人学习项目不强制他人审批，以免只有一位维护者时无法完成审批；有协作者后可按需增加审批要求。
- 首次上线顺序：先让工作流进入默认分支，并产生一次成功的 CI 运行，再配置 `chapter-ci` 必需检查。启用保护后，后续维护也走 PR。

当前默认分支为 `master`。Draft PR 工作流动态读取仓库默认分支，不写死 `main` 或 `master`。

## 为什么 CI 同时监听 push 和 pull_request

使用仓库自带的 `GITHUB_TOKEN` 创建 PR，不会触发新的 `pull_request` 工作流运行。因此 CI 同时监听所有分支的 push，使机器人创建的 PR 也有对应提交的检查结果，无须额外保存个人访问令牌。

人工创建或更新 PR 时，可能同时看到 push 和 pull_request 两次运行，这是预期行为。若 GitHub 为测试合并提交生成了检查结果，也需要该结果通过。转为 Ready 时会再次触发 PR 检查。

自动创建工作流只调用 GitHub API，不检出或执行章节代码。分支名通过环境变量传入并校验，不直接拼接到 shell 源代码中。

## 本地验证

```bash
bash scripts/check-java.sh
```

目前验证 Java 源码编译、AST 打印示例，以及生成的 Expr.java 能否编译；这不是完整的解释器测试。后续新增节点或调整示例时同步维护验证脚本。进入 Part III 前须补充 C 编译和测试，不能把仅 Java 检查通过当成 C 代码验证通过。

## 常见阻碍

- **没有出现 Draft PR**：先确认章节分支已推送、分支名符合约定、分支包含工作流文件，检查 Actions 运行及 PR 创建权限。
- **该章已有关闭的 PR**：工作流不会重复创建；确认是否需要重新打开原 PR。
- **章节与默认分支没有待合并差异**：跳过创建 PR，不为已合并章节创建空 PR。
- **PR 一直是 Draft**：用户确认完成后才转为 Ready；CI 通过不代表章节内容完成。
- **Ready 后仍未自动合并**：确认已单独启用 auto-merge、必需检查通过、分支已更新且没有冲突。转为 Ready 本身不会开启自动合并。
- **旧章节分支缺少工作流**：旧分支不会自动获得新配置；保留其快照即可。需要继续开发时，先确认如何引入基础设施，避免无意混入后续章节代码。
- **GitHub 已合并但本地未变化**：自动合并只更新远端，仍需获取远端状态并更新本地主分支。
