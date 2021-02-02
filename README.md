![icon](assets/ic_app_logo.svg)

Tune-MyTask
==========

![License](https://img.shields.io/badge/License-GPLv3-red.svg)
[![GitHub release](https://img.shields.io/github/v/release/saptarshidash/Tune-MyTask)](https://github.com/saptarshidash/Tune-MyTask/releases/tag/)

Task management application aimed to have both a <b>simple interface</b> but keeping <b>smart</b> behavior.

This Android application aims to provide an attractive simple look and follow the most recent design guidelines of the Google. It is developed using 
MVVM design pattern along with Android architechture components like LiveData, ViewModel, Lifecycle Observers ..

<a href="" target="_blank">
<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="90"/></a>

## Setup 
You have to add your own google-services.json (removed mine for security purpose) file in the project. To add the file open Android studio then
click on Tools -> Firebase -> and then add <b>Realtime Database</b> and <b>Authentication</b>.

## Features

Currently the following functions are implemented in Tune-MyTask:

* Material Design interface
* Store your task data in cloud as well as offline
* Basic create, modify, delete tasks
* Set reminder alarm for your tasks
* Change completion status of your tasks by marking/unmarking them
* Calendar view to know about the dates on which you have tasks
* One tap on calendar date and get all your tasks for that date in sorted time order
* Google Firebase integration to manage registration and login and storing of data securely

Further developments will include:
* Google now integration to create tasks with voice assistance. Just tell "Create task" followed
with other details to create a task
* Implement Dependency Injection
* Implement Data Binding

## See below <b>screenshots</b> to get an idea about the workflow of the app<br>

![](https://github.com/saptarshidash/Tune-MyTask/blob/master/assets/registration_screen.svg)
![](https://github.com/saptarshidash/Tune-MyTask/blob/master/assets/login_scr.svg)
![](https://github.com/saptarshidash/Tune-MyTask/blob/master/assets/home_scr1.svg)
![](https://github.com/saptarshidash/Tune-MyTask/blob/master/assets/home_scr2.svg)
![](https://github.com/saptarshidash/Tune-MyTask/blob/master/assets/home_scr3.svg)
![](https://github.com/saptarshidash/Tune-MyTask/blob/master/assets/crud1.svg)
![](https://github.com/saptarshidash/Tune-MyTask/blob/master/assets/crud2.svg)
![](https://github.com/saptarshidash/Tune-MyTask/blob/master/assets/crud3.svg)

## Dependencies

All dependencies are listed into [build.gradle](https://github.com/saptarshidash/Tune-MyTask/blob/master/app/build.gradle) file but due to the fact that some of the dependences have been customized by me I'd like to say thanks here to the original developers of these great libraries:

* https://github.com/shrikanth7698/Collapsible-Calendar-View-Android
* https://github.com/thyrlian/AwesomeValidation
* https://github.com/florent37/SingleDateAndTimePicker
* https://github.com/Chivorns/SmartMaterialSpinner
* https://github.com/Manabu-GT/ExpandableTextView

## Developed with love and passion by


* Saptarshi Das - [![Linkedin](https://i.stack.imgur.com/gVE0j.png) LinkedIn](https://www.linkedin.com/)

## License


    Copyright 2013-2020 Saptarshi Das
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
