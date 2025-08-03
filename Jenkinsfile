pipeline {
    agent any
    tools {
        nodejs 'NodeJS 16'  // 与全局工具配置的 NodeJS 名称一致
    }
    environment {
        NODE_ENV = 'production'
    }
    stages {
        stage('拉取代码') {
            steps {
                git url: 'https://github.com/msg-555/mvc-.git', branch: 'dev'  // 替换为你的仓库地址
            }
        }
        stage('安装依赖') {
            steps {
                bat 'npm install'  // 现在会使用 Jenkins 配置的 NodeJS 环境
            }
        }
        stage('构建') {
            steps {
                bat 'npm run build'  // 生成 dist 目录
            }
        }
        stage('部署到服务器') {
            steps {
                // 使用 Publish Over SSH 插件部署
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'my-server',  // 与 SSH 服务器配置的 Name 一致
                        transfers: [
                            sshTransfer(
                                sourceFiles: 'dist/**',  // 上传 dist 目录下所有文件
                                remoteDirectory: '/root',  // 服务器目标目录
                                cleanRemote: true,  // 可选：上传前清空服务器目标目录
                                flatten: false  // 保留 dist 内的目录结构
                            )
                        ]
                    )
                ])
            }
        }
    }
}
