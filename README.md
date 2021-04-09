# Scadroid

Open Source Android Application developed in a party of three in 2014-2015 to a scientific research on the Information Technology Center (CTI), Campinas. The main objective of this project was to use obsolete computers in order to reuse them to control and monitor the IoT resourses in a house. That is, we boot those obsolete computers with an extremely light operating system (such as Linux Lubuntu), we installed Apache TomCat with ScadaBR on them, and we used the PC as a Database to control and monitor a residence. However, to easy things to the user, we developed a Android application used to communicate with the ScadaBR server through the usage of a Soap API. Therefore, we could manage the Iot devices in our house using our Android Applications from any place connected to the internet. All those details can be found on the [contribution to the ScadaBR forum](http://www.scadabr.com.br/index.php/2016/01/22/aplicativo-android-scadroid/) that we did. Also, this project was presented at the Feria Nacional de Educación, Artes, Ciencias y Tecnología, held in PuertoIguazú, Argentina, September 21-25, 2015.

**Technologies used:**

* We used **Arduino** to control **IoT devices** in our house. Also, we used **ModBus Protocol** to allow Arduino and ScadaBR to communicate.
* We used **KSOAP2** library to establish the connection between our Android App and the **Apache TomCat + ScadaBR server**. Some details regarding the ScadaBR API can be found [here](https://sites.google.com/a/certi.org.br/certi_scadabr/home/minicursos/scadabr).
* [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) to develop the Charts of resource usage on the Android Application.


![Alt text](images/screenshots.png?raw=true "Mobile APP Screenshots")
