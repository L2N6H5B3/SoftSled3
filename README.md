# SoftSled3
A Software Media Center Extender, based on Java and properJavaRDP


## Overview
This project is a continuation of the SoftSled2 project, copying the code used for the original C# project into Java and using the properJavaRDP as a test ground to ensure the issues present in the original SoftSled2 project are resolved by properJavaRDP.

## Background of SoftSled2 Issues
The original SoftSled2 project is unfortunately fatally flawed ... issues with truncation within AxMsRdpClient / COM Marshalling (unknown as to the actual root problem) causes the RequestHandle to jump from 215 to 65023 seemingly unexplainedly.  The truncation issue also manifests in the 'devcaps' Device Capabilities strings, with what should be 3-char strings being truncated to 2-char (this is semi-workaroundable, but the RequestHandle issue is not, as it will halt playback.)


## Attribution
All rights to properJavaRDP go to the respective owners.  This project is really just a Proof-of-Concept that the original SoftSled2 code is workable, but the underlying RDP virtual channel communication truncation is the cause of the issues.

## Current Progress
* Audio RTSP communication is working as it was in SoftSled2, RTSP playback not yet implemented but the RequestHandle Issues are now gone.
* Video RTSP communication is mostly done, RTSP playback not working (perhaps because of DRM license failure?)

## Finished Elements
* Device Services Remoting (DSLR)
* Extender Device Capability Queries (DSPA)
* Extender Device Session Communication (DSMN)
* Extender Device Media Control (DMCT)

## Future Requirements
* Build Initial Device Configuration in Java (or a C#-based configuration Wizard)
* Implement Audio Playback
* Implement Video Playback/Overlay
* Create Extender Certificate
* Implement media controls (play/pause/previous/next) media from client
* Create RC6 remote control forwarder


## Possible Features to Add
* Create settings areas (perhaps could be used for dedicated device)
    * WLAN
    * Display
    * Audio

## Installation and configuration
### Prerequisites
* Windows 7

### Configuration
1. Complete main configuration using original SoftSled2 Project (Follow the Configuration steps on SoftSled2 Repo).
2. Import the project into your IDE of choice (Eclipse is preferred).
3. (if using Eclipse) Edit Run Configurations > Arguments and add the arguments as follows: <ip-address-of-wmc-pc>:3390 -u <mcx-username> -p mcxpw123
4. Run the Project and if you're lucky you'll get to the WMC home screen through RDP.

