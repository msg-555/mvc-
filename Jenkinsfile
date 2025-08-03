pipeline {
    agent any
    
    // 使用Jenkins中实际配置的工具名称
    tools {
        maven 'ceshi1'  // 替换为实际的Maven配置名称（如错误提示中的ceshi1）
        jdk 'JDK'       // 替换为实际的JDK配置名称（如错误提示中的JDK）
    }
    
    environment {
        PROJECT_NAME = 'MVC'
        PROJECT_VERSION = '1.0'
        WAR_FILE = "${PROJECT_NAME}-${PROJECT_VERSION}.war"
        REMOTE_DEPLOY_PATH = '/root/apache-tomcat-10.1.19/webapps'
    }
    
    stages {
        stage('拉取代码') {
            steps {
                echo "从Git仓库拉取最新代码..."
                git url: 'https://github.com/msg-555/mvc-.git', branch: 'main'
            }
        }
        
        stage('编译构建') {
            steps {
                echo "构建WAR包..."
                bat 'mvn clean package -Dmaven.test.skip=true'
            }
        }
        
        stage('部署到服务器') {
            steps {
                echo "部署WAR包到服务器..."
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'my-server',
                        transfers: [
                            sshTransfer(
                                sourceFiles: "target/${WAR_FILE}",
                                remoteDirectory: "${REMOTE_DEPLOY_PATH}",
                                cleanRemote: false,
                                flatten: true
                            )
                        ],
                        postCommands: '''
                            /root/apache-tomcat-10.1.19/bin/shutdown.sh
                            sleep 5
                            /root/apache-tomcat-10.1.19/bin/startup.sh
                        '''
                    )
                ])
            }
        }
    }
    
    post {
        success {
            echo "部署成功！访问地址: http://111.230.94.55:8080/${PROJECT_NAME}-${PROJECT_VERSION}"
        }
        failure {
            echo "部署失败，请查看日志"
        }
    }
}
