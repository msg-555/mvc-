pipeline {
    agent any
    
    // 配置构建工具：Maven和JDK（需在Jenkins全局工具中预先配置）
    tools {
        maven 'Maven 3.9'  // 与全局工具配置中的Maven名称一致
        jdk 'JDK 17'       // 与服务器JDK 17版本匹配
    }
    
    // 定义构建过程中需要的环境变量
    environment {
        // 项目相关配置
        PROJECT_NAME = 'MVC'
        PROJECT_VERSION = '1.0'
        WAR_FILE = "${PROJECT_NAME}-${PROJECT_VERSION}.war"
        
        // 服务器部署路径（Tomcat的webapps目录）
        REMOTE_DEPLOY_PATH = '/root/apache-tomcat-10.1.19/webapps'
    }
    
    stages {
        stage('拉取代码') {
            steps {
                echo "从Git仓库拉取最新代码..."
                git url: 'https://github.com/msg-555/mvc-.git', branch: 'dev'
            }
        }
        
        stage('代码检查') {
            steps {
                echo "执行代码质量检查..."
                bat 'mvn checkstyle:checkstyle'  // 需在pom.xml中配置checkstyle插件
            }
            post {
                always {
                    junit '**/target/checkstyle-result.xml'  // 展示检查结果
                }
            }
        }
        
        stage('编译与测试') {
            steps {
                echo "编译代码并执行单元测试..."
                bat 'mvn clean compile test'  // 编译并运行测试用例
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'  // 收集测试报告
                }
            }
        }
        
        stage('打包构建') {
            steps {
                echo "构建WAR包..."
                bat 'mvn package -Dmaven.test.skip=true'  // 跳过测试，生成WAR包
            }
            post {
                success {
                    echo "WAR包构建成功: target/${WAR_FILE}"
                }
            }
        }
        
        stage('部署到服务器') {
            steps {
                echo "部署WAR包到服务器..."
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'my-server',  // Jenkins中配置的SSH服务器名称
                        transfers: [
                            sshTransfer(
                                sourceFiles: "target/${WAR_FILE}",  // 明确指定WAR包路径
                                remoteDirectory: "${REMOTE_DEPLOY_PATH}",  // 服务器部署目录
                                cleanRemote: false,  // 不删除服务器其他文件
                                flatten: true        // 仅保留本地目录结构
                            )
                        ],
                        // 部署后执行的命令：重启Tomcat
                        postCommands: '''
                            echo "停止Tomcat服务..."
                            /root/apache-tomcat-10.1.19/bin/shutdown.sh
                            sleep 5
                            
                            echo "清理旧部署文件（可选）..."
                            rm -rf /root/apache-tomcat-10.1.19/webapps/${PROJECT_NAME}*
                            
                            echo "启动Tomcat服务..."
                            /root/apache-tomcat-10.1.19/bin/startup.sh
                            
                            echo "部署完成，Tomcat已重启"
                        '''
                    )
                ])
            }
        }
    }
    
    // 构建结果处理
    post {
        success {
            echo "=============================================="
            echo "构建部署成功！"
            echo "访问地址: http://111.230.94.55:8080/${PROJECT_NAME}-${PROJECT_VERSION}"
            echo "=============================================="
        }
        failure {
            echo "=============================================="
            echo "构建或部署失败，请查看控制台日志排查问题"
            echo "=============================================="
        }
        always {
            echo "构建流程结束"
        }
    }
}
