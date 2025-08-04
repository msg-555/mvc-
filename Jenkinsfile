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
                                remoteDirectory: '/apache-tomcat-10.1.19/webapps',
                                cleanRemote: false,
                                flatten: true,
                                execCommand: '''
                                    echo "=== Server deployment verification ==="
                                    echo "Current working directory:"
                                    pwd
                                    
                                    echo "Checking webapps directory exists..."
                                    if [ ! -d "/apache-tomcat-10.1.19/webapps" ]; then
                                        echo "ERROR: Webapps directory not found!"
                                        exit 1
                                    fi
                                    
                                    echo "Checking uploaded WAR package..."
                                    ls -l /apache-tomcat-10.1.19/webapps/MVC.war || echo "WAR package upload failed!"
                                    
                                    echo "Stopping Tomcat service..."
                                    /apache-tomcat-10.1.19/bin/shutdown.sh
                                    sleep 5
                                    
                                    echo "Cleaning old deployment files..."
                                    rm -rf /apache-tomcat-10.1.19/webapps/MVC*
                                    
                                    echo "Starting Tomcat after confirming WAR exists..."
                                    if [ -f "/apache-tomcat-10.1.19/webapps/MVC.war" ]; then
                                        /apache-tomcat-10.1.19/bin/startup.sh
                                        sleep 10
                                        echo "Webapps directory after deployment:"
                                        ls -l /apache-tomcat-10.1.19/webapps
                                    else
                                        echo "ERROR: MVC.war not found on server, deployment aborted!"
                                        exit 1
                                    fi
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
