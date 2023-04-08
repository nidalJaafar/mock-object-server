pipeline {
    agent any

    stages {
        stage('Docker') {
            steps {
                sh '''
                docker build -t localhost:5000/nidaljaafar/mock-object-server:3.0
                docker push localhost:5000/nidaljaafar/mock-object-server:3.0
                '''
            }
        }
    }
}
