def utils;
def artifactVersion;
def packaging;
def groupId;
def artifactId;
def finalName;
def target_file;

pipeline {
    agent any
    

    environment {

        REPOSTYPE = 'bscash-api'
        REPOSNAME = 'efinanceira-api'
    }
    
    tools {
        jdk 'java21'
    }
    
    stages {
        stage('Checkout') {
            steps {
                script{
                    checkout changelog: true, poll: true, scm: 
                    [
                        $class: 'GitSCM', 
                        branches: 
                            [[name: 'refs/heads/' + BRANCH_NAME]], 
                        doGenerateSubmoduleConfigurations: false, 
                        extensions: [
                            [$class: 'CloneOption', reference: '', noTags: true],
                            [$class: 'SubmoduleOption',disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false],
                            [$class: 'CleanBeforeCheckout', deleteUntrackedNestedRepositories: true]
                        ],
                        submoduleCfg: [], 
                        userRemoteConfigs: 
                        [
                            [
                                credentialsId: 'Github-Apierro', 
                                url: "https://github.com/BScash/${REPOSNAME}.git"
                            ]
                        ]
                    ]
                    
                }
            }
        }
        stage('Update Submodule') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'gitlab-login', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                sh '''
                    git config --global credential.helper "store --file=$HOME/.git-credentials"
                    git config --global user.email "jenkins@bscash.com.br"
                    git config --global user.name "jenkins.bscash"

                    git submodule init
                    git submodule update
                    git submodule update --remote --merge
                '''
        }
      }
    }
        stage('Load Scripts'){

            steps{
                script{
                    utils = load('groovyscripts/functions.groovy')
                    println "O valor de Utils é ${utils}"
                }
            }
        }
        stage('Use POM Info') {
            steps {
                 script {
                    def pomPath = "${env.WORKSPACE}/pom.xml"
                    echo "Caminho do arquivo pom.xml: ${pomPath}"
                    artifactId = utils.getArtifactId()
                    artifactVersion = utils.getArtifactVersion(artifactId)
                    //packaging= utils.getPackaging()
                    groupId = utils.getGroupId()
                    finalName= utils.getFinalName()

                    echo "artifactId == ${artifactId}"
                    echo "artifactVersion == ${artifactVersion}"
                    //echo "packaging == ${packaging}"
                    echo "groupId == ${groupId}"
                    echo "finalName == ${finalName}"
                }

            }

        }

        stage('Build application') {
            steps {
                script {
            target_file = utils.mavenInstall(artifactId, artifactVersion,finalName, 'jar' )
            echo "Valor da variável target_file: ${target_file}"
                }
            }
        }

        stage('Deploy to Nexus'){
            steps {
                    script {    
                        utils.deployToNexus(groupId, artifactId, artifactVersion, env.BRANCH_NAME, target_file, 'jar', finalName)
                    }
            }
        }


    }
}
