pipeline {
    agent {
        label 'docker'
    }
    tools {
        gradle 'gradle'
    }
    stages {
        stage ('Read Project Version') {
            steps {
                // read the project version from Gradle and store it as a variable
                script {
                    def projectVersion = sh(script: "gradle version | grep Version: | awk '{print \$2}'", returnStdout: true).trim()
                    env.PROJECT_VERSION = projectVersion
                }
                echo "The project version is ${env.PROJECT_VERSION}"
            }
        }
        stage ('Run Tests') {
            steps {
                sh 'gradle test'
            }
        }
        stage ('Build Jar') {
            steps {
                sh 'gradle bootJar'
            }
        }
        stage('Build Docker Image') {
            steps {
                // Run commands inside the Docker container
                sh "docker build -t localhost:5000/nidaljaafar/mock-object-server:${env.PROJECT_VERSION} ."
            }
        }
        stage('Push Docker Image') {
            steps {
                sh "docker push localhost:5000/nidaljaafar/mock-object-server:${env.PROJECT_VERSION}"
            }
        }
    }
}
