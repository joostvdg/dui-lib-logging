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
            options {
                timeout(time: 90, unit: 'SECONDS')
            }
            steps {
                container('gradle') {
                    sh 'gradle build'
                }
            }
        }
    }
}
