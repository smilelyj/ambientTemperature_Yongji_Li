# ambientTemperature_Yongji_Li
Problem Description
 
Design and implement an android application that would display 5 days(Mon-Fri) and temperature for each day Fahrenheit/Celsius on activity as well as the ambient temperature of the phone.  Application will involves accessing activity UI, JNI components, and SensorManager.
 
Features needed for the Activity
 
·         By default, App to generate a random list of temperature in Celsius for five days (Mon-Fri) and display it on activity
·         Activity to allow user (via button) to change the temperature from Celsius to Fahrenheit for each day
·         Activity to display the phone’s ambient temperature via sensor at the top of the activity
 
JNI
·         Conversion of temperature from Celsius to Fahrenheit or from Fahrenheit to Celsius shall be done through JNI native C/C++ code.
·         List of temperature either in Celsius or Fahrenheit shall be passed to JNI component and correctly converted temperature list shall be returned back from the native code.
 
Ambient Temperature
·         Assumption here is that device has temperature Sensor.
·         At the top of the activity UI, display the ambient temperature of the phone utilizing SensorManager.
 
Instructions
·         Code is expected to be developed in android studio
·         JNI code is expected to build through ndk-build command in NDK to a .so that shall be linked to the android application.
·         Project expected to use minimum Android SDK version 21.
·         Candidate expected to add comments in the code where valid.
