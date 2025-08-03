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
                git url: 'https://github.com/msg-555/mvc-.git', branch: 'dev'
            }
        }
        stage('安装依赖') {
            steps {
                bat 'npm install'  // Windows 用 bat 代替 sh
            }
        }
        stage('构建') {
            steps {
                bat 'npm run build'  // Windows 用 bat 代替 sh
            }
        }
        stage('部署到服务器') {
            steps {
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'my-server',
                        transfers: [
                            sshTransfer(
                                sourceFiles: 'dist/**',
                                remoteDirectory: '/root',
                                cleanRemote: true,
                                flatten: false
                            )
                        ]
                    )
                ])
            }
        }
    }
}
