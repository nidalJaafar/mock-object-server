pipeline {
    agent docker

    stages {
        stage('Docker') {
            steps {
                sh '''
                docker build -t registry:5000/nidaljaafar/mock-object-server:3.0
                docker push registry:5000/nidaljaafar/mock-object-server:3.0
                '''
            }
        }
    }
}
