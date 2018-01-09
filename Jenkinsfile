prepare = 10
unitTests = 15
archivingArtifacts = 10

future_release = 'future_release'
master = 'master'

body = """FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'. Check console output at "${env.BUILD_URL}"""
subject = "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"

properties([
  buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '25')),
  pipelineTriggers([
    upstream(
      threshold: 'SUCCESS',
      upstreamProjects: "TwilioVerification_Android_SDK/${env.BRANCH_NAME}"
    )
  ])
])
node('appium_ventspils_node') {
  try{
    if (env.BRANCH_NAME == future_release || env.BRANCH_NAME == master || currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause)){
      stage 'Prepare'
        timeout(prepare) {
          checkout scm
      }
      stage 'Run Unit Tests'
        timeout(unitTests) {
          try{
            sh "./gradlew :sample:connectedDebugAndroidTest :sample:testDebugUnitTest"
          } catch (e) {
            throw e
          } finally {
            publishUnitTests()
          }
      }
      stage 'Artifacts'
        timeout(archivingArtifacts) {
          archiveArtifacts artifacts: '**/*.apk'
      }
    }
    else {
        currentBuild.result = "NOT_BUILT"
    }
  } catch (e) {
    notifyFailed(env.APP_TEAM_EMAIL)
    currentBuild.result = "FAILED"
    throw e
  }
}

def notifyFailed(emailList) {
  if (env.BRANCH_NAME == future_release || env.BRANCH_NAME == master) {
    mail body: body, subject: subject, to: emailList
  }
}

def publishUnitTests() {
  publishHTML target: [
    allowMissing: false,
    alwaysLinkToLastBuild: true,
    keepAll: true,
    reportDir: 'sample/build/reports/androidTests/connected',
    reportFiles: 'index.html',
    reportName: 'Android Unit Tests'
  ]

publishHTML target: [
    allowMissing: false,
    alwaysLinkToLastBuild: true,
    keepAll: true,
    reportDir: 'sample/build/reports/tests/testDebugUnitTest',
    reportFiles: 'index.html',
    reportName: 'Debug Unit Tests'
  ]
}
