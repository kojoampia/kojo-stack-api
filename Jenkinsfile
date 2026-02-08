pipeline {
    agent any

    triggers {
        pollSCM('H H * * *')
    }

    environment {
        REGISTRY = "docker.jojoaddison.net"
        IMAGE_NAME = "kojo-stack-api"
        // Jenkins credentials ID for your Docker registry
        REGISTRY_CREDENTIALS_ID = "docker-jojoaddison-net-credentials"
        // Jenkins credentials ID for your Git repository
        GIT_CREDENTIALS_ID = "kojo-git-ssh-priv-key"
        // Version
        BUILD_NUMBER = "2026.01.${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/kojoampia/kojo-stack.git',
                branch: 'main',
                credentialsId: GIT_CREDENTIALS_ID
            }
        }
        stage('Build and Push Docker Image') {
            steps {
                script {
                    def imageName = "${REGISTRY}/${IMAGE_NAME}"
                    sh "docker build -t ${imageName}:${env.BUILD_NUMBER} ."
                    withCredentials([usernamePassword(credentialsId: REGISTRY_CREDENTIALS_ID, passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin https://${REGISTRY}"
                        sh "docker push ${imageName}:${env.BUILD_NUMBER}"
                        sh "docker tag ${imageName}:${env.BUILD_NUMBER} ${imageName}:latest"
                        sh "docker push ${imageName}:latest"
                    }
                }
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
