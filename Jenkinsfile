pipeline {
    agent {
        label 'gradle'
    }
    stages {
        stage('Test') {
            steps {
                container('gradle') {
                    sh 'uname -a'
                    sh 'gradle -v'
                }
            }
        }
        stage('Build') {
            steps {
                container('gradle') {
                    sh 'gradle build'
                }
            }
        }
    }
}
