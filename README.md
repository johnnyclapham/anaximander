## RSSI Crowd-source Data Collection Tool: Anaximander

CSCI 619 Course Project &amp; Research Topic. This repository hosts the Android application source code for the back-end data collection procedure of Anaximander. The data collected is stored in an online realtime database and is the input to the data visualization tool that is accessible at this link: 

https://johnnyclapham.github.io/dist/tool.html

### Abstract 
Network users must have reliable connection quality to the local network infrastructure in order to meet the requirements of daily modern life. The Wi-Fi Received Signal Strength Indicator (RSSI) is the measurement of this connection quality between the user device, and network Access Point (AP). Unfortunately, it is currently difficult for users to locate physical area zones with strong or weak RSSI. This means that users are left with limited options when they find themselves in an area with a weak network connection.

In order to mitigate this problem, we propose “Anaximander”, a novel data visualization application that can take mobile phone sensor data as an input and produce an output of informative heatmaps of RSSI over a traveled area. Anaximander uses crowdsourcing techniques in order to enable future scaling and deployment to student devices.

### Note about Android APKs:

Starting in August 2021, new apps will need to:
- Publish with the Android App Bundle format.
- Use Play Asset Delivery or Play Feature Delivery to deliver assets or features that exceed download size of 150MB. Expansion files (OBBs) will no longer be supported for new apps.
- Target API level 30 (Android 11) or above and adjust for behavioral changes; except Wear OS apps, which must continue to target API level 28 or higher.
