pipeline {
    agent any
    // 全局编码环境变量（Windows 专用）
    environment {
        LANG = 'zh_CN.GBK'
        JAVA_TOOL_OPTIONS = '-Dfile.encoding=GBK'
        // 解决 Jenkins 控制台解码问题
        BUILD_DISPLAY_NAME = "${env.BUILD_NUMBER}"
    }
    tools {
        maven 'ceshi1'
        jdk 'JDK'
    }
    stages {
        stage('拉取代码') {
            steps {
                // 使用 powershell 替代 bat，原生支持 UTF-8 输出
                powershell 'Write-Host "从 GitHub 拉取 main 分支代码..."'
                git url: 'https://github.com/msg-555/mvc-.git', branch: 'main'
            }
        }
        
        stage('构建项目') {
            steps {
                powershell 'Write-Host "使用 Maven 构建 WAR 包..."'
                // Maven 命令指定编码
                bat 'mvn clean package -Dmaven.test.skip=true -Dfile.encoding=GBK'
                // 检查 WAR 包是否存在（中文提示）
                bat '''
                    chcp 936
                    if not exist "target/MVC.war" (
                        echo "ERROR: WAR 包未生成！"
                        exit 1
                    ) else (
                        echo "WAR 包生成成功: target/MVC.war"
                    )
                '''
            }
        }
        
        stage('运行测试') {
            steps {
                powershell 'Write-Host "执行单元测试..."'
                bat 'mvn test -Dfile.encoding=GBK'
            }
        }
        
        stage('部署到服务器') {
            steps {
                powershell 'Write-Host "部署 WAR 包到服务器 Tomcat 目录..."'
                // 修正 dir 命令语法（Windows 下查看文件需用 /b 参数）
                bat 'chcp 936 & dir "target/MVC.war" /b'
                
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'my-server',
                        transfers: [
                            sshTransfer(
                                sourceFiles: 'target/MVC.war',
                                remoteDirectory: '/root/apache-tomcat-10.1.19/webapps',
                                cleanRemote: false,
                                flatten: true,
                                execCommand: '''
                                    export LANG=zh_CN.UTF-8
                                    echo "=== 服务器部署验证 ==="
                                    echo "检查 webapps 目录中的 WAR 包..."
                                    ls -l /root/apache-tomcat-10.1.19/webapps/MVC.war || echo "WAR 包上传失败！"
                                    
                                    echo "停止 Tomcat 服务..."
                                    /root/apache-tomcat-10.1.19/bin/shutdown.sh
                                    sleep 5
                                    
                                    echo "清理旧部署文件..."
                                    rm -rf /root/apache-tomcat-10.1.19/webapps/MVC*
                                    
                                    echo "确认 WAR 包存在后启动 Tomcat..."
                                    if [ -f "/root/apache-tomcat-10.1.19/webapps/MVC.war" ]; then
                                        /root/apache-tomcat-10.1.19/bin/startup.sh
                                        echo "Tomcat 已启动，等待应用部署..."
                                        sleep 10
                                        echo "部署后 webapps 目录内容："
                                        ls -l /root/apache-tomcat-10.1.19/webapps
                                    else
                                        echo "ERROR: 服务器上未找到 MVC.war，部署终止！"
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
            powershell '''
                Write-Host "=============================================="
                Write-Host "🎉 构建部署成功！"
                Write-Host "访问地址：http://111.230.94.55:8080/MVC"
                Write-Host "=============================================="
            '''
        }
        failure {
            powershell '''
                Write-Host "=============================================="
                Write-Host "❌ 构建或部署失败，请查看控制台日志排查问题"
                Write-Host "=============================================="
            '''
        }
    }
}
