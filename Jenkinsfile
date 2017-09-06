prepare = 3
unitTests = 8

future_release = 'future_release'
master = 'master'

body = """FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'. Check console output at "${env.BUILD_URL}"""
subject = "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
emailList = 'team-authy-apps@twilio.com'

properties([
  buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')),
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
            sh "./gradlew :sample:connectedDebugAndroidTest"
          } catch (e) {
            throw e
          } finally {
            publishUnitTests()
          }
      }
    }
  } catch (e) {
    notifyFailed()
    currentBuild.result = "FAILED"
    throw e
  }
}

def notifyFailed() {
  if (env.BRANCH_NAME == future_release || env.BRANCH_NAME == master) {
    mail body: body, subject: subject, to: emailList
  }
}

def publishUnitTests() {
  publishHTML target: [
    allowMissing: true,
    alwaysLinkToLastBuild: true,
    keepAll: true,
    reportDir: 'sample/build/reports/androidTests/connected',
    reportFiles: 'index.html',
    reportName: 'Android Unit Tests'
  ]
}
