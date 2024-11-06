pipeline {
    agent {
        node {
            label 'node_name'
        }
    }
    options {
        timeout(time: 3600, unit: 'SECONDS')
    }
    stages {
        stage('Clean my_repo_one/master') {
            steps {
                script {
                    build(job: 'Repo-scavenger', wait: true, parameters:
		        		[[$class: 'StringParameterValue', name: 'REPO_NAME', value: 'my_repo_one/master'],
				        [$class: 'StringParameterValue', name: 'DELETE_TIMESTAMP', value: '45'],
				    ])
                }
            }
        }
        stage('Clean my_repo_two/dev') {
            steps {
                script {
                    build(job: 'Repo-scavenger', wait: true, parameters:
		        		[[$class: 'StringParameterValue', name: 'REPO_NAME', value: 'my_repo_two/dev'],
				        [$class: 'StringParameterValue', name: 'DELETE_TIMESTAMP', value: '45'],
				    ])
                }
            }
        }
        stage('Clean my_repo_old') {
            steps {
                script {
                    build(job: 'Repo-scavenger', wait: true, parameters:
		        		[[$class: 'StringParameterValue', name: 'REPO_NAME', value: 'my_repo_old'],
				        [$class: 'StringParameterValue', name: 'DELETE_TIMESTAMP', value: '14'],
				    ])
                }
            }
        }
    }
    post {
        always {
            script {
                currentBuild.result = currentBuild.result ?: 'SUCCESS'
            }
            cleanWs()
        }
    }
}
