--- 
Services: active-directory
platforms: Android
author: danieldobalian
---

# MSAL Android Sample Microsoft Graph API Sample 

| [Getting Started](https://apps.dev.microsoft.com/portal/register-app?appType=mobileAndDesktopApp&appTech=android)| [Library](https://github.com/AzureAD/microsoft-authentication-library-for-android) | [API Reference](http://javadoc.io/doc/com.microsoft.identity.client/msal) | [Support](README.md#community-help-and-support)
| --- | --- | --- | --- |

The MSAL Android preview gives your app the ability to begin using the [Microsoft Cloud](https://cloud.microsoft.com) by supporting [Microsoft Azure Active Directory](https://azure.microsoft.com/en-us/services/active-directory/) and [Microsoft Accounts](https://account.microsoft.com) in a converged experience using industry standard OAuth2 and OpenID Connect. This sample demonstrates all the normal lifecycles your application should experience, including:

* Get a token for the Microsoft Graph
* Refresh a token
* Call the Microsoft Graph
* Sign out the user

## Example

```Java

PublicClientApplication pApp = new PublicClientApplication(
                this.getApplicationContext(),
                CLIENT_ID);
pApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());

// ...

authenticationResult.getAccessToken();
```

## Optional: Register your App  

The app comes pre-configured for testing.  If you would like to register your own app, please follow the steps below. 

You will need to have a native client application registered with Microsoft using the [Microsoft Identity App Registration Portal](https://apps.dev.microsoft.com/portal/register-app?appType=mobileAndDesktopApp&appTech=android). Make sure to uncheck the *Guided Setup* box and save your app. 

## Steps to Run

1. Clone the code. 
    ```
    git clone https://github.com/Azure-Samples/active-directory-android-native-v2 
    ```
2. Open Android Studio 2, and select *open an existing Android Studio project*. Find the cloned project and open it. 

3. Select *Build* > *Clean Project*. 

4. Select *Run* > *Run 'app'*. Make sure the emulator you're using has Chrome, if it doesn't follow [these steps](https://github.com/Azure-Samples/active-directory-general-docs/blob/master/AndroidEmulator.md). In Android Studio, we recommend using the Pixel image with Android 24. 

## Important Info

1. There's a more complete app that stores global state and has cleaner UI in the full-sample branch. Check it out!
2. Redirect URI format: `msal<YOUR_CLIENT_ID>://auth` is strictly enforced by MSAL at the current time. 

## Community Help and Support

We use [Stack Overflow](http://stackoverflow.com/questions/tagged/msal) with the community to provide support. We highly recommend you ask your questions on Stack Overflow first and browse existing issues to see if someone has asked your question before. 

If you find and bug or have a feature request, please raise the issue on [GitHub Issues](../../issues). 

To provide a recommendation, visit our [User Voice page](https://feedback.azure.com/forums/169401-azure-active-directory).

## Contribute

We enthusiastically welcome contributions and feedback. You can clone the repo and start contributing now. Read our [Contribution Guide](Contributing.md) for more information.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Security Library

This library controls how users sign-in and access services. We recommend you always take the latest version of our library in your app when possible. We use [semantic versioning](http://semver.org) so you can control the risk associated with updating your app. As an example, always downloading the latest minor version number (e.g. x.*y*.x) ensures you get the latest security and feature enhanements but our API surface remains the same. You can always see the latest version and release notes under the Releases tab of GitHub.

## Security Reporting

If you find a security issue with our libraries or services please report it to [secure@microsoft.com](mailto:secure@microsoft.com) with as much detail as possible. Your submission may be eligible for a bounty through the [Microsoft Bounty](http://aka.ms/bugbounty) program. Please do not post security issues to GitHub Issues or any other public site. We will contact you shortly upon receiving the information. We encourage you to get notifications of when security incidents occur by visiting [this page](https://technet.microsoft.com/en-us/security/dd252948) and subscribing to Security Advisory Alerts.



