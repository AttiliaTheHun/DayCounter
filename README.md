The original single-counter version (1.1) is still available in [this branch](https://github.com/AttiliaTheHun/DayCounter/tree/version-1.1) and in [this release](https://github.com/AttiliaTheHun/DayCounter/releases/tag/v1.1.0).<br>
# DayCounter
A simple Android app counting remaining days until a given date. It was origianlly meant to give you a precise idea of how many days you have left until you reach a certain age (eg. 60 years), but you can use it for any sort of day counting.<br>
<br>
I have been told that the code is a mess and it is hard to understand how it works (the fact that I already forgot it underlines that I guess), so I decided to make a small docs [here](https://github.com/AttiliaTheHun/DayCounter/blob/master/BRIEF_DOCUMENTATION.md).
## Features
- Unlimited number of counters (RAM is the limit)
- Notification service displaying the amount of remaining days for any counter you choose
- Homescreen widgets displaying the amount of remaining days for any counter you choose
- Starts on boot
- Supports complete translations (except for some error displaying and logging), currently available for 🇨🇿 Czech and partially 🇫🇷 French.
### Notes
- The app is tested to work on Android 10 when the user uses the app as intended, there may be bugs and undesired features which I will appreciate in Issues (and won't ever fix)
- The UI sucks
#### ⚠️ Saved data protection
The application stores the counters as a serialized data stream. Thus if you change the version of the Counter class (update/downgrade), it will be no longer possible for the application to load the data (the counters).<br><br>
You can use the export and import functions to overcome this shortcoming by exporting the data in JSON format. Keep in mind that the byte format (the serialized data) can not be imported if the application uses a differnet version of the Counter class.<br><br>
The JSON import/export option should be always prefered over bytes.
## Items For Mental Consultation
- Yes, I made the app in [Sketchware Pro](https://github.com/Sketchware-Pro/Sketchware-Pro) (on PHONE)
## How to build
Seriously, I have no idea
### Build in Sketchware
See [Releases](https://github.com/AttiliaTheHun/DayCounter/releases) and pick the file with `.swb` extension. It is a Sketchware backup file and you can use it to import this application as a Sketchware project. **Be sure to use __Sketchware Pro__ as I used several features the original Sketchware app does not offer.**
## Credits
I stole the icon from [Icons8](https://icons8.com)
### Examples
#### Example 1: How many days until I reach 60?
When the app asks you for your birth date, be sure to enter the birth date and in the age box type '60'.
![dc_timer_until_sixty_years](https://user-images.githubusercontent.com/37469561/161230935-059f80ff-bc1a-471c-bcf5-c1af127f0f6e.png)
#### Example 2: How many days until it is 2 years from now?
Into the birth date form you enter today's date and into the age box you type '2'.
![Screenshot_20220401-104700](https://user-images.githubusercontent.com/37469561/161231004-1e2d929d-2735-4ef4-83db-38c5171f73ea.png)
## Screenshots
![dc_app_preview](https://user-images.githubusercontent.com/37469561/207144260-34ac0f0a-b1fb-4b0a-9f73-9195d7c6aa2d.png)
![dc_settings_preview](https://user-images.githubusercontent.com/37469561/207144349-6d0b7256-33f1-4651-a6f3-c9ee76ff3a9c.png)
![dc_homescreen_widget](https://user-images.githubusercontent.com/37469561/161231392-d2160ff1-c13c-4566-b36f-d311fe15861b.png)
![dc_notification_counter](https://user-images.githubusercontent.com/37469561/161231437-ac9d461c-7758-4063-bd0b-94d402ed1bc0.png)