pipeline {
    agent any  // 执行环境（any 表示任意可用节点）
    stages {   // 所有阶段（构建、测试、部署等）
        stage('拉取代码') {  // 阶段 1：拉取代码
            steps {          // 阶段内的步骤
                git url: 'https://github.com/msg-555/mvc-.git', branch: 'main'
            }
        }
        stage('构建项目') {  // 阶段 2：构建（以 Maven 为例）
            steps {
              //  sh 'mvn clean package'  // 执行 shell 命令（Linux/macOS）
                 bat 'E:\\Maven\\apache-maven-3.9.11\\bin\\mvn clean package'  // Windows 用 bat 命令
            }
        }
        stage('运行测试') {  // 阶段 3：测试
            steps {
                bat 'mvn test'
            }
        }
    }
    post {     // 构建后操作（成功/失败处理）
        success {
            echo '构建成功！'
        }
        failure {
            echo '构建失败！'
        }
    }
}
