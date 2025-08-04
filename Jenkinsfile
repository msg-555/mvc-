pipeline {
    agent any
    tools {
        maven 'ceshi1'
        jdk 'JDK'
    }
    stages {
        stage('拉取代码') {
            steps {
                echo "Pulling code from GitHub main branch..."
                git url: 'https://github.com/msg-555/mvc-.git', branch: 'main'
            }
        }
        stage('构建项目') {
            steps {
                echo "Building WAR package with Maven..."
                bat 'mvn clean package -Dmaven.test.skip=true'
                // 检查 WAR 包是否生成
                bat '''
                    if not exist "target/MVC.war" (
                        echo "ERROR: WAR package not generated!"
                        exit 1
                    ) else (
                        echo "WAR package generated successfully: target/MVC.war"
                        dir target (
                            dir MVC.war (
                                echo "WAR package size:"
                                dir /s /b MVC.war
                            )
                        )
                    )
                '''
            }
        }
        
        stage('运行测试') {
            steps {
                echo "Running unit tests..."
                bat 'mvn test'
            }
        }
        
        stage('部署到服务器') {
            steps {
                echo "Deploying WAR package to server Tomcat directory..."
                
                // 先确认本地WAR包存在
                script {
                    def warFile = fileExists('target/MVC.war')
                    if (!warFile) {
                        error("WAR package not found! Cannot deploy.")
                    }
                    echo "Local WAR package confirmed: target/MVC.war"
                }
                
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'my-server',
                        transfers: [
                            sshTransfer(
                                sourceFiles: 'target/MVC.war',
                                remoteDirectory: '/apache-tomcat-9.0.89/webapps',
                                cleanRemote: false,
                                flatten: true,
                                execCommand: '''
                                    # 定义Tomcat webapps目录的实际路径（带root前缀）
                                    TOMCAT_WEBAPPS="/root/apache-tomcat-9.0.89/webapps"
                                    
                                    echo "=== 确认服务器目标目录 ==="
                                    ls -ld $TOMCAT_WEBAPPS || { echo "ERROR: 目标目录 $TOMCAT_WEBAPPS 不存在!"; exit 1; }
                                    
                                    echo "=== 检查WAR包是否上传成功 ==="
                                    ls -l $TOMCAT_WEBAPPS/MVC.war || { echo "ERROR: WAR包未上传到 $TOMCAT_WEBAPPS!"; exit 1; }
                                    
                                    echo "=== 停止Tomcat服务 ==="
                                    /root/apache-tomcat-9.0.89/bin/shutdown.sh
                                    sleep 5
                                    
                                    echo "=== 启动Tomcat ==="
                                    /root/apache-tomcat-9.0.89/bin/startup.sh
                                    sleep 10
                                    echo "=== 部署后目录检查 ==="
                                    ls -l $TOMCAT_WEBAPPS
                                '''
                            )
                        ],
                        // 添加 verbose 模式便于调试
                        verbose: true
                    )
                ])
            }
        }
    }
    
    post {
        success {
            echo "=============================================="
            echo "🎉 Build and deployment completed successfully!"
            echo "Access URL: http://111.230.94.55:8080/MVC"
            echo "=============================================="
        }
        failure {
            echo "=============================================="
            echo "❌ Build or deployment failed. Check console logs for details."
            echo "=============================================="
        }
    }
}
