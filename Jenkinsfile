pipeline {
    agent any
    
    tools {
        maven 'ceshi1'  // 保持已配置的Maven名称
        jdk 'JDK'       // 保持已配置的JDK名称
    }
    
    environment {
        PROJECT_NAME = 'MVC'
        WAR_FILE = "${PROJECT_NAME}.war"  // 从日志可知实际WAR包名为MVC.war
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
                        configName: 'my-server',  // 与SSH配置名称一致
                        transfers: [
                            sshTransfer(
                                sourceFiles: "target/${WAR_FILE}",  // 上传target/MVC.war
                                remoteDirectory: "${REMOTE_DEPLOY_PATH}",  // 服务器Tomcat目录
                                cleanRemote: false,
                                flatten: true,
                                // 部署后执行重启Tomcat命令
                                execCommand: '''
                                    /root/apache-tomcat-10.1.19/bin/shutdown.sh
                                    sleep 5
                                    /root/apache-tomcat-10.1.19/bin/startup.sh
                                '''
                            )
                        ]
                    )
                ])
            }
        }
    }
    
    post {
        success {
            echo "部署成功！访问地址: http://111.230.94.55:8080/${PROJECT_NAME}"
        }
        failure {
            echo "部署失败，请查看日志"
        }
    }
}
