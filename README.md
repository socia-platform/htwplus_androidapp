# HTWplus Android app

This Android app is currently still a prototype to demonstrate the communication with the [HTWplus RESTful API](https://github.com/roechi/htwplus_restAPI).
Currently the prototype supports a login communication based on OAuth2, a display for contacts of the currently logged in user, a display for all postings
which are related of the currently logged in user and the possibility to create simple postings.  

**Attention:** There are uses currently no https!

## Installation

### Requirements

* Android Studio (min v1.3) or other technology to compile a android app
* JDK 8
* Git
* Working HTWPlus application with RESTful API add-on
* Valid user account
* Minimum Android Jelly Bean 4.3

### Setup

* Clone this repository
* Edit *app/src/main/java/OAuth2Preferences.java*
 * Search for the function *setInitialPreferences()*
 * There enter your *clientId*, *clientSecret* and *authenticationCallBackURI*
   which are defined in HTWplus platform
* Compile the app

### First run

* Open settings (*Einstellungen*)
* Enter the url to your HTWplus RESTful API (such as *http://htwplus.de/api*) and press OK 
* After successfull enter of api url press LOGIN (*Anmelden*)
* It pop ups the login page of HTWplus, here log in and allow access to your account

