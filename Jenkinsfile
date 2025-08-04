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
                bat '''
                    if not exist "target/MVC.war" (
                        echo "ERROR: WAR package not generated!"
                        exit 1
                    ) else (
                        echo "WAR package generated successfully: target/MVC.war"
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
                bat 'dir "target\\MVC.war"'
                
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'my-server',
                        transfers: [
                            sshTransfer(
                                sourceFiles: 'target/MVC.war',
                                // 用相对路径，基于 SSH 登录默认目录 /root
                                remoteDirectory: 'apache-tomcat-10.1.19/webapps', 
                                cleanRemote: false,
                                flatten: true,
                                execCommand: '''
                                    echo "=== Server deployment verification ==="
                                    echo "Checking WAR package in webapps directory..."
                                    ls -l /root/apache-tomcat-10.1.19/webapps/MVC.war || echo "WAR package upload failed!"
                                    
                                    echo "Stopping Tomcat service..."
                                    /root/apache-tomcat-10.1.19/bin/shutdown.sh
                                    sleep 5
                                    
                                    echo "Cleaning old deployment files..."
                                    rm -rf /root/apache-tomcat-10.1.19/webapps/MVC*
                                    
                                    echo "Starting Tomcat after confirming WAR exists..."
                                    if [ -f "/root/apache-tomcat-10.1.19/webapps/MVC.war" ]; then
                                        /root/apache-tomcat-10.1.19/bin/startup.sh
                                        sleep 10
                                        echo "Webapps directory after deployment:"
                                        ls -l /root/apache-tomcat-10.1.19/webapps
                                    else
                                        echo "ERROR: MVC.war not found on server, deployment aborted!"
                                        exit 1
                                    fi
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
