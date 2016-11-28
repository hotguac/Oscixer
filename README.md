# Oscixer
**Remote control of DAW**

[Download APK (zipped)](https://github.com/hotguac/Oscixer/releases)

Oscixer is an Android app that allows remote control of a Digital Audio Workstation (DAW). The DAW targeted
during the initial development is the open source [Ardour](https://ardour.org/) v5.4.0.

Other DAW platforms may be included at a later date but my intent for the development is to provide a simple, intuitive,
remote for [Ardour](https://ardour.org/) that will allow me, and others, to engineer recording sessions while performing the reports being 
recorded. For example, placing an Android smartphone or tablet on a music stand while performing the part being recorded
without needing to be able to see the computer screen or reach the computer keyboard.

This project uses the [JavaOSC](https://github.com/hoijui/JavaOSC) library through [Maven Central](mvnrepository.com/artifact/com.illposed.osc/javaosc-core) .

In [Ardour](https://ardour.org/) enable [OSC control with feedback](http://manual.ardour.org/using-control-surfaces/controlling-ardour-with-osc/) .

On the Oscixer startup screen enter the IP address for the host running Ardour and port that Ardour is listening on for OSC commands.

![alt text](https://github.com/hotguac/Oscixer/blob/master/app/src/main/res/drawable/start_screen.png "Startup Screen")

Click send to show the Oscixer control screen.

![alt text](https://github.com/hotguac/Oscixer/blob/master/app/src/main/res/drawable/control_screen.png "Control Screen")

The arrows on the left side control which track (left/right) in [Ardour](https://ardour.org/) is being controlled and which control (fader/pan/trim) is mapped
 to the touch area box. The top of the touch area box is slightly more sensitive at the top than at the bottom and also adjusts responds more to quicker movements
 and less to slower movements.
 
 Across the bottom are transport controls. From left to right are Stop, Play, Loop toggle, Home, End, Previous Mark, and Next Mark.
 
 The title bar show the currently selected track, and has toggles for track record enable and global record enable.
 
 I hope you find this useful,
 Joe Kokosa
 
