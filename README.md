# Twilio Verification SDK for Android


You can also have a quick start from an empty project following the steps described [here](https://www.twilio.com/docs/guides/twilio-verification-sdk-integration-guide)

## Setup

### Token server address

You will need to specify where is the server that will provide the JWts. 
The backend sample can be directly deployed from [this repository](https://github.com/authy/authy-sdk-backend). 

Follow the steps described in this tutorial to [easily deploy a sample server](https://www.twilio.com/docs/guides/twilio-verification-sdk-integration-guide/integrating-twilio-verification-sdk-using-sample-backend)

Then you can point your app to that server by providing the url in strings.xml

```
<string name="default_endpoint">https://{YOUR_SERVER}.herokuapp.com/verify/token</string>
```

### Read verification codes without requesting sms read permissions
  
 To setup automatic input of verification codes in your app please refer to [this guide](https://www.twilio.com/docs/guides/twilio-verification-sdk-integration-guide/register-your-app-twilio-verification-api)


For further information please [contact us](https://ahoy.twilio.com/verificationsdk)
