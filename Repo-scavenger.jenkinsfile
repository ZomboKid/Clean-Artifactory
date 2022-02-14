pipeline {
    agent {
        node {
            label 'node_name'
        }
    }
parameters {
        string(defaultValue: "my_repo", description: 'The name of the repository to remove the artifacts from', name: 'REPO_NAME')
        string(defaultValue: "90", description: 'Older than how many days will we delete artifacts', name: 'DELETE_TIMESTAMP')
    }	
    stages {
        stage('Run shell-script'){
            steps {
                script{
                    currentBuild.displayName = "${params.REPO_NAME} older ${params.DELETE_TIMESTAMP}"
                }
                cleanArtifactory ("username:password",REPO_NAME,DELETE_TIMESTAMP)
            }
        }
    }
}
def cleanArtifactory (CREDENTIALS, REPO_NAME, DELETE_TIMESTAMP) {
    withEnv(["""CREDENTIALS=$CREDENTIALS""","""REPO_NAME=$REPO_NAME""","""DELETE_TIMESTAMP=$DELETE_TIMESTAMP"""]) {
        sh(script: '''
            echo $REPO_NAME $DELETE_TIMESTAMP
        
            IFS=$'\n'

            declare -a heap_arr
            declare -a arr
            declare -a arr_old

            get_Artifact_array () {
                if [ $(curl -s -u $1 -X GET "http://artifactory.mydomain.local:8081/artifactory/$2/" | grep -oP '(?<=<pre><a href=")..') != ".." ]; then
                    heap_arr=($(curl -s -u $1 -X GET "http://artifactory.mydomain.local:8081/artifactory/$2/" | awk '/(<a href=)/{print $0}'))
                    arr[0]=$(echo "${heap_arr[0]}" | grep -oP '(?<=<pre><a href=").*(?=">)')" "$(echo "${heap_arr[${j}]}" | awk '/^(<pre><a href=")/{print $3}')
                    j=1
                else
                    heap_arr=($(curl -s -u $1 -X GET "http://artifactory.mydomain.local:8081/artifactory/$2/" | awk '/^(<a href=)/{print $0}'))
                    j=0
                fi

                for (( j ; j < ${#heap_arr[@]} ; j=$j+1 ));
                do
                    arr[$j]=$(echo "${heap_arr[${j}]}" | grep -oP '(?<=<a href=").*(?=">)')" "$(echo "${heap_arr[${j}]}" | awk '/^(<a href=")/{print $3}')
                done
            }

            get_OldArtifacts_array () {
                i=0
                get_Artifact_array $1 $2
                old_d=$(date +%d-%h-%Y --date="-$3 day")

                for (( j = 0 ; j < ${#arr[@]} ; j=$j+1 ));
                do
                    current_d=$(echo "${arr[${j}]}" | awk -F " " '{print $2}')
                    if ! [ $(date -d $current_d +%s) -ge $(date -d $old_d +%s) ]; then
                        arr_old[$i]=$(echo "${arr[${j}]}" | awk -F " " '{print $1}')
                        i=$i+1
                    fi
                done
            }

            delete_OldArtifacts () {
                get_OldArtifacts_array $1 $2 $3
                if [ ${#arr_old[@]} == 0 ]; then echo "No old artifacts found. Nothing to delete."; fi
                for (( j = 0 ; j < ${#arr_old[@]} ; j=$j+1 ));
                do
                    curl -s -u $1 -X  DELETE "http://artifactory.mydomain.local:8081/artifactory/$2/${arr_old[${j}]}"
                done
            }

            delete_OldArtifacts $CREDENTIALS $REPO_NAME $DELETE_TIMESTAMP

            unset IFS
        ''', returnStdout: false)
    }
}
