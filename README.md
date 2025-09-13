# Current Status 
This software is published as Version 1.0. It is only testet on my machine, so use it with care. I'm not reponsible for data loss.

# What is this software?
This software is used to make a copy of your Tonies on disk and to manage them in a library. The software is a java desktop application and should run on Windows/Linux and Mac. You need access to the internal data of your Toniebox (access to the Micro-SD card).

With this softare you can copy your Tonies to disk and from disk back to your Toniebox. Therefore you can backup the downloaded Tonie-Audiofiles and restore them to your box. It is also possible to rearrange them, that means to connect an Audiofile to another Tonie.

You can save the audiofile associated with a Tonie on disk and write it back to the Toniebox. You dont have to write back the associated audiofile, you can save another audiofile. In the current version its not possible to write own mp3/ogg or similar or to backup the Tonie-audiofile to a readable and usable format. You can only change the associated audiofile.
I.e. you save Tonie T1 with audiofile A1 and Tonie T2 with audiofile A2 and you can write back T1 with audiofile A2 so the audiofile A2 is played when you put Tonie T1 on the Toniebox. This also works for Creative Tonies and for any SLIX-L RFID-Tag.

# What is not supported
Currently you can only save the Audiofile and restore them back. But the Audiofile is in the Toniebox format, not MP3 or simmilar, so it is not possible to play them with an Audioplayer.
It is not possible to decode the Tonie Audiofile to MP3 or simmilar (but planned for a later Version). Also it is not possible to save some Audiofiles to a Tonie-Audiofile (also planned, but not implemented).

# What othe Software exists
I know there is a software called TeddyBench (https://github.com/toniebox-reverse-engineering/teddy) which allow the audio encode/decode. I had my problem with this software because I do not use Windows and this software is Windows only. And this software allows to save the Audiofiles as MP3s and to save MP3s back on the Box. It dont manage a library of the Toniebox as my software will do.

I know there is a software called Teddycloud (https://github.com/toniebox-reverse-engineering/teddycloud) which replaced the whole Toniecloud and allow fancy stuff. But to use it you have to extract the key pair from the Toniebox which is a pain in the a**. Also you have to be very very firm with servers etc (not a problem for me, but for may other).

# What else?
Maybe I will upload a video on Youtibe where i show all functions of my software. Or at least a classic tutorial. I mainly wrote it for me, so many functions may not be as intuitive as they could be. 
