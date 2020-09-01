#!/usr/bin/env groovy

@Library(["camunda-ci", "zeebe-jenkins-shared-library"]) _

pipeline {

  agent {
    kubernetes {
      cloud getZeebeK8sCloud()
      label getZeebeK8sLabel()
      defaultContainer 'jnlp'
      yaml libraryResource("zeebe/podspecs/${utils.isProdJenkins() ? 'mavenAgent.yml' : 'mavenAgentStage.yml'}")
    }
  }

  options {
    buildDiscarder(logRotator(numToKeepStr: '10'))
    skipDefaultCheckout()
    timestamps()
    timeout(time: 15, unit: 'MINUTES')
  }

  environment {
    NEXUS = credentials("camunda-nexus")
  }

  parameters {
    booleanParam(name: 'RELEASE', defaultValue: false, description: 'Build a release from current commit?')
    string(name: 'RELEASE_VERSION', defaultValue: '0.X.0', description: 'Which version to release?')
    string(name: 'DEVELOPMENT_VERSION', defaultValue: '0.Y.0-SNAPSHOT', description: 'Next development version?')
  }

  stages {
    stage('Prepare') {
      steps {
        checkout scm
        container('maven') {
          configFileProvider([configFile(fileId: 'maven-nexus-settings-zeebe', variable: 'MAVEN_SETTINGS_XML')]) {
            sh 'mvn clean install -B -s $MAVEN_SETTINGS_XML -DskipTests'
          }
        }
      }
    }

    stage('Build') {
      when { not { expression { params.RELEASE } } }
      steps {
        container('maven') {
          configFileProvider([configFile(fileId: 'maven-nexus-settings-zeebe', variable: 'MAVEN_SETTINGS_XML')]) {
            sh 'mvn install -B -s $MAVEN_SETTINGS_XML'
          }
        }
      }

      post {
        always {
          junit testResults: "**/*/TEST-*.xml", keepLongStdio: true
        }
      }
    }

    stage('Upload') {
      when { not { expression { params.RELEASE } } }
      steps {
        container('maven') {
          configFileProvider([configFile(fileId: 'maven-nexus-settings-zeebe', variable: 'MAVEN_SETTINGS_XML')]) {
            sh 'mvn -B -s $MAVEN_SETTINGS_XML generate-sources source:jar javadoc:jar deploy -DskipTests'
          }
        }
      }
    }

    stage('Release') {
      when { expression { params.RELEASE } }

      environment {
        MAVEN_CENTRAL = credentials('maven_central_deployment_credentials')
        GPG_PASS = credentials('password_maven_central_gpg_signing_key')
        GPG_PUB_KEY = credentials('maven_central_gpg_signing_key_pub')
        GPG_SEC_KEY = credentials('maven_central_gpg_signing_key_sec')
        GITHUB_TOKEN = credentials('camunda-jenkins-github')
        RELEASE_VERSION = "${params.RELEASE_VERSION}"
        DEVELOPMENT_VERSION = "${params.DEVELOPMENT_VERSION}"
      }

      steps {
        container('maven') {
          configFileProvider([configFile(fileId: 'maven-nexus-settings-zeebe', variable: 'MAVEN_SETTINGS_XML')]) {
            sshagent(['camunda-jenkins-github-ssh']) {
              sh 'gpg -q --import ${GPG_PUB_KEY} '
              sh 'gpg -q --allow-secret-key-import --import --no-tty --batch --yes ${GPG_SEC_KEY}'
              sh 'git config --global user.email "ci@camunda.com"'
              sh 'git config --global user.name "camunda-jenkins"'
              sh 'mkdir ~/.ssh/ && ssh-keyscan github.com >> ~/.ssh/known_hosts'
              sh 'mvn -B -s $MAVEN_SETTINGS_XML -DskipTests source:jar javadoc:jar release:prepare release:perform -Prelease'
              sh '.ci/scripts/github-release.sh'
            }
          }
        }
      }
    }
  }

  post {
    always {
      // Retrigger the build if the node disconnected
      script {
        if (nodeDisconnected()) {
          build job: currentBuild.projectName, propagate: false, quietPeriod: 60, wait: false
        }
      }
    }
  }
}

boolean nodeDisconnected() {
  return currentBuild.rawBuild.getLog(500).join('') ==~ /.*(ChannelClosedException|KubernetesClientException|ClosedChannelException).*/
}
