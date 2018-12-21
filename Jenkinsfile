node {
  PROJECT_NAME = 'mafia'
  FE_PROJECT_NAME = 'mafia-fe'
  env.TZ = 'Asia/Shanghai'
  checkout scm

  stage('Build') {
    configFileProvider([configFile(fileId: '68be7343-3694-4f84-8af0-149bbf7921a4', variable: 'MY_SETTINGS')]) {
      docker.withRegistry('https://804775010343.dkr.ecr.cn-north-1.amazonaws.com.cn', 'ecr:cn-north-1:9f34c613-7c6d-4132-baf8-a14d8689ed4a') {
        docker.image('maven:jdk11-0.0.2').inside("-v /home/jenkins/.m2/:/home/jenkins/.m2") {
          sh 'mvn clean package -s $MY_SETTINGS'
        }
      }
    }
  }

  stage('Deploy') {
    if (env.BRANCH_NAME == 'master') {
      DEPLOY_ENV = 'prod'
    }

    if (env.BRANCH_NAME == 'master') {
      GIT_COMMIT_HASH = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
      GIT_COMMIT_MESSAGE = sh(script: "git log -1 --pretty=%s", returnStdout: true).trim()

      docker.withRegistry('https://804775010343.dkr.ecr.cn-north-1.amazonaws.com.cn', 'ecr:cn-north-1:9f34c613-7c6d-4132-baf8-a14d8689ed4a') {
        image = docker.build("${PROJECT_NAME}:${env.BRANCH_NAME}-${GIT_COMMIT_HASH}", "./service")
        image.push()
      }

      HOST = "http://service-proxy.ruguoapp.com/service-proxy/jkdpy-dashboard-svc.infra:8000/api/kube/${DEPLOY_ENV}/dpymts/${PROJECT_NAME}/"
      DATA_JSON = "{\"images\":{\"${PROJECT_NAME}\":\"804775010343.dkr.ecr.cn-north-1.amazonaws.com.cn/${PROJECT_NAME}:${env.BRANCH_NAME}-${GIT_COMMIT_HASH}\"},\"notes\":\"${GIT_COMMIT_MESSAGE}\"}"
      sh "curl -XPUT '${HOST}' -H 'Content-Type:application/json' --data '${DATA_JSON}' --fail"

      docker.withRegistry('https://804775010343.dkr.ecr.cn-north-1.amazonaws.com.cn', 'ecr:cn-north-1:9f34c613-7c6d-4132-baf8-a14d8689ed4a') {
        image = docker.build("${FE_PROJECT_NAME}:${env.BRANCH_NAME}-${GIT_COMMIT_HASH}", "./mafia-frontend")
        image.push()
      }

      HOST = "http://service-proxy.ruguoapp.com/service-proxy/jkdpy-dashboard-svc.infra:8000/api/kube/${DEPLOY_ENV}/dpymts/${FE_PROJECT_NAME}/"
      DATA_JSON = "{\"images\":{\"${FE_PROJECT_NAME}\":\"804775010343.dkr.ecr.cn-north-1.amazonaws.com.cn/${FE_PROJECT_NAME}:${env.BRANCH_NAME}-${GIT_COMMIT_HASH}\"},\"notes\":\"${GIT_COMMIT_MESSAGE}\"}"
      sh "curl -XPUT '${HOST}' -H 'Content-Type:application/json' --data '${DATA_JSON}' --fail"
    }

    sh 'echo congrats'
  }
}