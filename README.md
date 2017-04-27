---
Services: active-directory
platforms: Android
author: danieldobalian
---

# Sign in Azure AD + MSA Users using MSAL Android

## Steps to Run

Clone the code and follow Android Studio prompts. Build and run!

## Important Identity Code

1. AndroidManifest - Configure intent filter for system browser
2. Gradle - We're using MSAL from source so we specify that in here
3. MainActivity.java - Bulk of identity code
4. activity_main.java - UI for the app

## Important info

1. There's a more complete app that stores global state and has cleaner UI in the full-sample branch. Check it out!
2. Redirect URI format: `msal[clientID]://auth`.  The app may be using a different schema that is out of date with the current MSAL source (but not out-of-date with the one included in this repo)
3. File issues directly to the repo for sample problems.  For SDK bugs, file them in the [MSAL Source](https://github.com/AzureAD/microsoft-authentication-library-for-android). 

# Acks

Big thanks to the following folks:
- [Wei Jia](https://github.com/weijjia)
- [Brian Melton](https://github.com/iambmelt)
- Simone Giaccio
- [Brandon Werner](https://github.com/xerners)
