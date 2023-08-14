#!groovy

// gitflow helpers

def isMaster() {
    return (env.EFFECTIVE_BRANCH_NAME == 'master')
}

def isDevelop() {
    return (env.EFFECTIVE_BRANCH_NAME == 'develop')
}

def isRelease() {
    return (((String) env.EFFECTIVE_BRANCH_NAME).startsWith('release/'))
}

def isFeature() {
    return (((String) env.EFFECTIVE_BRANCH_NAME).startsWith('feature/'))
}

def isBugfix() {
    return (((String) env.EFFECTIVE_BRANCH_NAME).startsWith('bugfix/'))
}

def isHotfix() {
    return (((String) env.EFFECTIVE_BRANCH_NAME).startsWith('hotfix/'))
}

def isReleasableBranch() {
    return isMaster() || isRelease()
}

def shouldRunSmokeTests() {
    return isFeature() || isBugfix() || isHotfix()
}

def shouldPublishToDevelopRepo() {
    return ! ( isMaster() || isDevelop() )
}

pipeline {
    agent {
        node {
            label 'docker-builder'
        }
    }
    options {
        disableConcurrentBuilds()
    }
    triggers {
        pollSCM('*/2 * * * *')
    }

    environment {
        JAVA_HOME = "/usr/lib/jvm/java-1.8.0/"
        GIT_REMOTE = sh(returnStdout: true, script: "git remote").trim()
        EFFECTIVE_BRANCH_NAME = "${env.CHANGE_BRANCH?:env.BRANCH_NAME}"
        GIT_BRANCH = "${env.GIT_REMOTE}/${env.EFFECTIVE_BRANCH_NAME}".replaceAll("[^\\w\\-_/]+", "_")
        GIT_COMMIT = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%H'").trim()
        GIT_URL = sh(returnStdout: true, script: "git config remote.origin.url").trim()
        GRADLEW_DIR = "."
        SONAR_REPO_FILE = "${WORKSPACE}/${GIT_REPO_NAME}.scannerwork/scanner-report"
        SONAR_PROJECT_KEY = "uidm-sdk-java"
        SONAR_SCANNER_HOME = tool "sonarscanner_base"
    }
    stages {
        stage('Clean') {
            steps {
                dir(path: "$GRADLEW_DIR") {
                    ansiColor('xterm') {
                        sh 'printenv'
                        sh '''
                          ./gradlew clean --refresh-dependencies
                        '''
                    }
                }
            }
        }
        stage('Run UNIT tests') {
            steps {
                dir(path: "$GRADLEW_DIR") {
                    ansiColor('xterm') {
                        sh '''
                          ./gradlew test
                        '''
                    }
                }
            }
        }
        stage('Gitleaks Analysis') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'be012fb5-bdcb-4383-96d3-d4e5d7bbaf53', passwordVariable: 'NEXUSPASSWORD', usernameVariable: 'NEXUSUSER')]) {
                    sh 'docker login -u $NEXUSUSER -p $NEXUSPASSWORD repo2.rooxcloud.com:8084'
                    script {
                        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                            sh 'docker run -v $PWD:/path  repo2.rooxcloud.com:8084/gitleaks:v8.17.0 detect --source "/path" --verbose --no-git'
                        }
                    }
                }
            }
        }
        stage('OWASP Dependency-Check Vulnerabilities') {
            steps {
                dir(path: "$GRADLEW_DIR") {
                    ansiColor('xterm') {
                        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                            timeout(time: 10, unit: 'MINUTES') {
                                sh './gradlew :dependencyCheckAggregate'
                                dependencyCheckPublisher pattern: 'build/reports/dependency-check-report.xml'
                            }
                        }
                    }
                }
            }
        }
        stage('SonarQube Analysis') {
            when {
                expression { isMaster() }
            }
            tools {
                jdk "JDK 11"
            }
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    withSonarQubeEnv('SonarQube v3') {
                        // sonarscanner_base is declared at 'Manage Jenkins'->'Global Tool Configuration'->'SonarQube Scanner'
                        sh """
                            $SONAR_SCANNER_HOME/bin/sonar-scanner -X \
                                -Dsonar.projectKey=$SONAR_PROJECT_KEY \
                                -Dsonar.dependencyCheck.jsonReportPath=$GRADLEW_DIR/build/reports/dependency-check-report.json \
                                -Dsonar.dependencyCheck.htmlReportPath=$GRADLEW_DIR/build/reports/dependency-check-report.html
                        """
                    }
                }
            }
        }
        stage('Quality Gate') {
            when {
                expression { isMaster() }
            }
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    timeout(time: 5, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }
        }
        stage('Build/Package/Publish') {
            steps {
                dir(path: "$GRADLEW_DIR") {
                    ansiColor('xterm') {
                        sh """
                        ./gradlew publish -i -s -ProoxDevelopOn=${String.valueOf( shouldPublishToDevelopRepo() )}
                        """
                    }
                }
            }
        }
        stage('Update S3 static') {
            when {
                expression { isMaster() }
            }
            steps {
                dir(path: 'auth-lib-common/build/docs/javadoc') {
                    withCredentials([string(credentialsId: 'Yandex-s3cmd-uploader', variable: 'yc_obj_s3cmd_cfg')]) {
                        ansiColor('xterm') {
                            sh """
                            s3cmd --config="${yc_obj_s3cmd_cfg}" --no-mime-magic --guess-mime-type -v --recursive --acl-public --delete-removed --add-header="Cache-Control:max-age=3600"  sync * s3://uidm.ru/javasdk/latest/apidocs/
                            """
                        }
                    }
                }
            }
        }
        stage('Tag') {
            when {
                expression { return isReleasableBranch() }
            }
            steps {
                sshagent(credentials: ['cd58ea32-f170-45d7-b7c4-714fa06fc5d9']) {
                    sh 'git config user.email "jenkins@jenkins-builder2a.roox.ru"'
                    sh 'git config user.name "jenkins"'
                    sh "git tag -a buildId-$env.EFFECTIVE_BRANCH_NAME.$env.BUILD_NUMBER -m buildId-$env.EFFECTIVE_BRANCH_NAME.$env.BUILD_NUMBER"
                    sh "git push origin --tags"
                }
            }
        }
    }
    post {
        aborted {
            echo "Sending message to Telegram"
            script {
                def authors = currentBuild.changeSets.collectMany {
                    it.toList().collect { it.author }
                }.unique().toString()
                withCredentials([string(credentialsId: 'TELEGRAM_BOT', variable: 'TELEGRAM_BOT')]) {
                    sh "curl https://api.telegram.org/${TELEGRAM_BOT}/sendMessage?chat_id=-1001715766664\\&parse_mode=Markdown\\&text=" +
                        java.net.URLEncoder.encode("*ABORTED*: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER} by ${authors}\nMore info at: ${env.BUILD_URL}", "UTF-8")
                }
            }
        } // aborted

        failure {

            echo "Sending message to Telegram"
            script {
                def authors = currentBuild.changeSets.collectMany {
                    it.toList().collect { it.author }
                }.unique().toString()
                withCredentials([string(credentialsId: 'TELEGRAM_BOT', variable: 'TELEGRAM_BOT')]) {
                    sh "curl https://api.telegram.org/${TELEGRAM_BOT}/sendMessage?chat_id=-1001715766664\\&parse_mode=Markdown\\&text=" +
                        java.net.URLEncoder.encode("*FAILED*: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER} by ${authors}\nMore info at: ${env.BUILD_URL}", "UTF-8")
                }
            }
        } // failure

        success {
            script {

                if (env.EFFECTIVE_BRANCH_NAME ==~ /^(master|release|hotfix|feature|bugfix|develop).*$/) {
                    echo "Sending message to Telegram"
                    def commits = currentBuild.changeSets.collectMany {
                        it.toList().collect {
                            it.msg + " - _" + it.author + "_"
                        }
                    }.unique().join('\n')

                    def authors = currentBuild.changeSets.collectMany {
                        it.toList().collect { it.author }
                    }.unique().join(',')

                    withCredentials([string(credentialsId: 'TELEGRAM_BOT', variable: 'TELEGRAM_BOT')]) {
                        sh "curl https://api.telegram.org/${TELEGRAM_BOT}/sendMessage?chat_id=-1001715766664\\&parse_mode=Markdown\\&text=" +
                            java.net.URLEncoder.encode("\u26A1 *${currentBuild.currentResult}* \u26A1\n*uidm-sdk-java собран*\nBranch: *${env.EFFECTIVE_BRANCH_NAME}* Build: ${env.BUILD_ID}\n[GitLab](https://gitlab.rooxintra.net/ucsdk/uidm-sdk-java) | [Jenkins](${env.BUILD_URL})\n\n${commits}\n", "UTF-8")
                    }
                } else {
                    echo 'I not executed elsewhere'
                }
            }
        } // success
    } // post
}