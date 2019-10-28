# SOFTENG 206 Project / Justin Rojos / Tianren Shen
VARpedia is a Visual Audio Reading encyclopaedia.
This application should be tested in the SOFTENG 206 updated VirtualBox image.

# Libraries
The following libraries must be imported for the source code to work.

JavaFX13

flickr4java-3.0.1

scribejava-apis-6.6.3

scribejava-core-6.2.0

slf4j-api-1.7.25

slf4j-nop-1.7.26

These libraries are included in the libs folder, excluding JavaFX13. 
Along with some other libraries from the 206_FlickrExample ACP project.
This application was compiled with Java13.

# Packages
Make sure the following packages are installed on your linux system.

ffmpeg, ffmplay, soxi, festival, wikit, mp3info, vlc

# Flickr API Keys
The flickr API keys are located in flickr-api-keys.txt, in the same folder as the jar file.

# Runnable jar
This application should be run on the SOFTENG 206 Virtual Image.
Make sure the flickr-api-keys.txt, the jar file and the run.sh are in the same folder.
Make sure the bgmusic.wav file is in the same level as the jar file!
Open up terminal and navigate to that folder in terminal.
You can run the jar file with the included run.sh script(with the command "./run.sh"), make sure its in the same folder as the jar file.
Or from terminal type these two commands:
>PATH=/home/student/Downloads/openjdk-13_linux-x64_bin/jdk-13/bin:$PATH
>
>java --module-path /home/student/Downloads/openjfx-13-rc+2_linux-x64_bin-sdk/javafx-sdk-13/lib --add-modules javafx.base,javafx.controls,javafx.media,javafx.graphics,javafx.fxml -jar 206Assignment3.jar

# Music Attribution:
Make sure the all music files (bgmusic.wav, Techno.mp3, Chill.mp3) are in the same level as the jar file!

>Licensed under a Creative Commons Attribution (3.0) License

Ukulele Vs. Kazoo and Whistle, Too (C) 2013
>http://ccmixter.org/files/mindmapthat/41199 ft: Kara Square

Good Night (C) 2019
>http://ccmixter.org/files/JohnDope/60163 ft: John Dope

ElectricBody (C) 2019
>http://ccmixter.org/files/destinazione_altrove/60185 ft: Dysfunction_AL

# Image Attribution

A Set of Active Kids on A White Background
>Vecteezy.com

Several GIFs were taken from GIPHY
>giphy.com

# Code Attribution

Nasser's Flickr ACP
>https://acp.foe.auckland.ac.nz/

Alert CSS Style Sheet
>https://stackoverflow.com/questions/37354686/customize-javafx-alert-with-css

