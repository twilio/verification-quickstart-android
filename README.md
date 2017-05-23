# Twilio Verification SDK for Android


You can also have a quick start from an empty project following the steps described [here](https://www.twilio.com/docs/guides/twilio-verification-sdk-integration-guide)

## Setup

### Token server address

You will need to specify where is the server that will provide the JWts. 
The backend sample can be directly deployed from [this repository](https://github.com/authy/authy-sdk-backend) 
Then you can point your app to that server by providing the url in strings.xml

```
<string name="default_endpoint">https://{YOUR_SERVER}.herokuapp.com/verify/token</string>
```

### Google Play Services
Temporarily and until Google releases its library, you will need to set a local Google Play Services SDK. 
[Contact us](https://ahoy.twilio.com/verificationsdk) to recieve it.

Once obtained you will need to specify the path to that library by editing your ~/.gradle/gradle.properties file
```
PARTNER_SDK_DIR=/Users/{USER_NAME}/workspace/partner-sdk
```

We already linked that variable in the build.gradle file, and set it as a local maven repository.
```
allprojects {
    repositories {
        maven {
            url PARTNER_SDK_DIR
        }
        jcenter()
    }
}
```
