# 校园二手交易平台

## 项目环境

Windows11

Python 3.12.11 （uv）

Go 1.24.4（go modules）

MySQL

MongoDB

## 初始化环境

以下命令都在powershell下运行（cmd可以自行尝试）

### 拉取文件

```shell
git pull
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
//go build,go test等命令目前应该用不太到（）
```

