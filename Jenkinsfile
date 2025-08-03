pipeline {
    agent any
    // 全局环境变量，指定编码为 GBK（Windows 系统默认编码）
    environment {
        LANG = 'zh_CN.GBK'
        JAVA_TOOL_OPTIONS = '-Dfile.encoding=GBK'
    }
    tools {
        maven 'ceshi1'
        jdk 'JDK'
    }
    stages {
        stage('拉取代码') {
            steps {
                // Windows 下 bat 命令强制使用 GBK 编码输出
                bat 'chcp 936 & echo 从 GitHub 拉取 main 分支代码...'
                git url: 'https://github.com/msg-555/mvc-.git', branch: 'main'
            }
        }
        
        stage('构建项目') {
            steps {
                bat 'chcp 936 & echo 使用 Maven 构建 WAR 包...'
                // Maven 命令添加编码参数
                bat 'chcp 936 & mvn clean package -Dmaven.test.skip=true -Dfile.encoding=GBK'
                // 中文提示强制指定编码
                bat 'chcp 936 & if not exist "target/MVC.war" (echo "ERROR: WAR 包未生成！" && exit 1) else (echo "WAR 包生成成功: target/MVC.war")'
            }
        }
        
        stage('运行测试') {
            steps {
                bat 'chcp 936 & echo 执行单元测试...'
                bat 'chcp 936 & mvn test -Dfile.encoding=GBK'
            }
        }
        
        stage('部署到服务器') {
            steps {
                bat 'chcp 936 & echo 部署 WAR 包到服务器 Tomcat 目录...'
                bat 'chcp 936 & dir target/MVC.war'
                
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
                                    # 服务器端（Linux）使用 UTF-8 编码
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
            bat 'chcp 936 & echo =============================================='
            bat 'chcp 936 & echo 🎉 构建部署成功！'
            bat 'chcp 936 & echo 访问地址：http://111.230.94.55:8080/MVC'
            bat 'chcp 936 & echo =============================================='
        }
        failure {
            bat 'chcp 936 & echo =============================================='
            bat 'chcp 936 & echo ❌ 构建或部署失败，请查看控制台日志排查问题'
            bat 'chcp 936 & echo =============================================='
        }
    }
}
