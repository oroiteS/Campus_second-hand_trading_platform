# 校园二手交易平台

## 项目环境

Windows11

Python 3.12.11（uv）

Go 1.24.4（go modules）

MySQL

MongoDB

## 初始化环境

以下命令都在powershell下运行（cmd可以自行尝试）

### 拉取文件

```shell
git pull
// 或者更安全、具体的git fetch + git merge
```

### backend/python

```shell
uv sync //同步依赖（环境更新后随时同步）
```

### backend/go

```shell
go mod tidy
```

## 修改项目依赖

请注意在修改项目依赖后及时提交，避免不同人修改不同的依赖导致冲突


### backend/python

```shell
uv add requests //以requests为例
//或者 uv pip install requests，在熟悉的pip命令前面加上uv即可
//uv add会自动更新toml配置文件，uv pip不会更新，所以尽量使用uv add
```

### backend/go

```shell
go get -u github.com/gin-gonic/gin  //-u表示最新版
```

## 运行环境

### backend/python

```shell
.venv/scripts/activate //类似于Anaconda的conda activate env_name
python main.py
```

### backend/go

```shell
go run main.go //运行main.go
//go build,go test等命令目前应该用不太到
```

### 项目开发规范
1. 每次开发都要创建一个新分支
2. 分支名请参考：feature/xxx（功能）、fix/xxx（修复bug）、hotfix/xxx（紧急修复bug）、docs/xxx（文档）、refactor/xxx（重构）、style/xxx（样式）、test/xxx（测试）
3. 分支名请使用英文，不要使用中文
4. 分支名请使用短横杠-分隔，不要使用下划线_（例如：feature/add-user）
5. 分支名请使用小写字母（例如：feature/add-user，而不是Feature/Add-User）
#### 具体流程（以开发login为例）

*如果出现了指令错误请告知*

```shell
git checkout main //确保自己在main分支
git pull origin main //拉取最新代码
git checkout -b feature/login //创建新分支
git status //检查状态，查看切换分支是否成功
//修改文件
git add . //或者指定文件
git commit -m "feature(login): 增加用户认证接口"  # 类型+模块+描述
git push origin feature/login
git checkout main
git pull origin main
git merge feature/login 
```
如果出现了merge的错误:
- 可能是有多个人同时修改了某个文件，这时候需要手动更新；
- 也可能是你在编译器或者其他工具中打开了这个文件导致无法更新，此时关闭文件即可
- 请注意：
```shell
# 合并冲突时，使用工具解决冲突后标记已解决
git add .  # 标记冲突已解决
git commit -m "解决合并冲突"  # 提交冲突修复
```
```shell
git push origin main
git branch -d feature/login
git push origin --delete feature/login
```

过程中可以随时前往仓库查看是否`完成`了正确的指令！！！

