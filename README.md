--- 
Services: active-directory
platforms: Android
author: danieldobalian
level: 100
client: Android Mobile App
service: Microsoft Graph
endpoint: AAD V2
---
# MSAL Android app calling Microsoft Graph 

| [Getting Started](https://docs.microsoft.com/azure/active-directory/develop/guidedsetups/active-directory-android)| [Library](https://github.com/AzureAD/microsoft-authentication-library-for-android) | [API Reference](http://javadoc.io/doc/com.microsoft.identity.client/msal) | [Support](README.md#community-help-and-support)
| --- | --- | --- | --- |

![Build Badge](https://identitydivision.visualstudio.com/_apis/public/build/definitions/a7934fdd-dcde-4492-a406-7fad6ac00e17/500/badge)

The MSAL Android preview gives your app the ability to begin using the
[Microsoft identity platform](https://aka,ms/aaddev) by supporting [Microsoft Azure Active Directory](https://azure.microsoft.com/en-us/services/active-directory/) and [Microsoft Accounts](https://account.microsoft.com) in a converged experience using industry standard OAuth2 and OpenID Connect. This sample demonstrates all the normal lifecycles your application should experience, including:

* Get a token for the Microsoft Graph
* Device-wide SSO and Conditional Access suport through the Auth Broker
* Refresh tokens automatically
* Call the Microsoft Graph
* Sign out the user

## Scenario

This app is a multi-tenant app meaning it can be used by any Azure AD tenant or Microsoft Account.  It demonstrates how a developer can build apps to connect with enterprise users and access their Azure + O365 data via the Microsoft Graph.  During the auth flow, end users will be required to sign in and consent to the permissions of the application, and in some cases may require an admin to consent to the app.  The majority of the logic in this sample shows how to auth an end user and make a basic call to the Microsoft Graph.

![Topology](./images/topology1.png)

## Example

```Java
// Initialize your app with MSAL
PublicClientApplication pApp = new PublicClientApplication(
            this.getApplicationContext(),
            R.raw.auth_config);

// Perform authentication requests
pApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());

// ...

// Get tokens to call APIs like the Microsoft Graph
authenticationResult.getAccessToken();
```

## Register your App  

To begin registering your app, begin at the [Azure portal](https://aka.ms/MobileAppReg)

To create an app,  
1. Click `New Registration`. 

2. Name your app, select the audience you're targeting, and click `Register`. 
    - This sample is focused on `Accounts in any organization and personal Microsoft accounts`, but can be modified to support any audience. 
    - Do not register a Redirect URI. 

3. Click `Authentication` > `Add Platform` > `Android`. 
    - Enter the Package Name from your Android studio project. 
    - Generate a Signature Hash. Refer to the portal for instructions.

4. Hit the `Configured` button. Store the ***MSAL Configuration*** for the next steps. 

## Steps to Run

1. Clone the code. 
    ```
    git clone https://github.com/Azure-Samples/active-directory-android-native-v2 
    ```
2. Open Android Studio, and select *open an existing Android Studio project*. Find the cloned project and open it. 

3. Configure the sample code in `auth_config.JSON` and `AndroidManifest.xml`.
    - Copy and paste the ***MSAL Configuration*** JSON from the Azure portal into `auth_config.JSON`. 
    - Inside the `AndroidManifest.xml`, replace `android:host` and `android:path` with the same info registered above.
        - `auth_config.JSON` contains this information as a reference inside the `redirect_uri` field.
        - The Signature Hash should NOT be URL encoded in the `AndroidManifest.xml`. 

5. Select *Build* > *Clean Project*. 

6. Select *Run* > *Run 'app'*. 

## Feedback, Community Help, and Support

We use [Stack Overflow](http://stackoverflow.com/questions/tagged/msal) with the community to 
provide support. We highly recommend you ask your questions on Stack Overflow first and browse 
existing issues to see if someone has asked your question before. 

If you find and bug or have a feature request, please raise the issue 
on [GitHub Issues](../../issues). 

To provide a recommendation, visit 
our [User Voice page](https://feedback.azure.com/forums/169401-azure-active-directory).

## Contribute

We enthusiastically welcome contributions and feedback. You can clone the repo and start 
contributing now. Read our [Contribution Guide](Contributing.md) for more information.

This project has adopted the 
[Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). 
For more information see 
the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact 
[opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Security Library

This library controls how users sign-in and access services. We recommend you always take the 
latest version of our library in your app when possible. We 
use [semantic versioning](http://semver.org) so you can control the risk associated with updating 
your app. As an example, always downloading the latest minor version number (e.g. x.*y*.x) ensures 
you get the latest security and feature enhanements but our API surface remains the same. You 
can always see the latest version and release notes under the Releases tab of GitHub.

## Security Reporting

If you find a security issue with our libraries or services please report it 
to [secure@microsoft.com](mailto:secure@microsoft.com) with as much detail as possible. Your 
submission may be eligible for a bounty through the [Microsoft Bounty](http://aka.ms/bugbounty) 
program. Please do not post security issues to GitHub Issues or any other public site. We will 
contact you shortly upon receiving the information. We encourage you to get notifications of when 
security incidents occur by 
visiting [this page](https://technet.microsoft.com/en-us/security/dd252948) and subscribing 
to Security Advisory Alerts.



