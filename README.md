Chameleon - Color Palette Management Tool
=========================================

What is it?
-----------
The Chameleon is a Color Palette Management Tool.
It keeps palette in snapshot format. Each change commits 
only once and stored in database. Commit contains 
color information (variable, color, opacity)
User can create palettes and variables. Each palette save
will create new commit. You can compare two different versions of palette.

Download
--------

Download binaries of the latest release here:

<a href='https://bintray.com/devexperts/Maven/chameleon/_latestVersion'><img src='https://api.bintray.com/packages/devexperts/Maven/chameleon/images/download.svg'></a>.
 
Using Chameleon
---------------

Start tool:

	chameleon-<version>.jar
	
or

	java -jar chameleon-<version>.jar
	
Open the link:

	http://localhost:8080/index.html
	
Configuration
-------------

By default application listening localhost on 8080 port and
application use file database, default credentials is 'sa' without password. 

You can change it in application.properties file which has
the same location as **chameleon-< version>.jar**

* application.properties

    	server.port=8080
    	spring.datasource.username=sa
    	spring.datasource.password=
	
---

In file chameleon-< version> .conf you can change jvm options, format of config:

* chameleon-< version>.conf 

		JAVA_HOME=
    	JAVA_OPTS=
    	
    	
    	
Licensing
---------

Chameleon.

Copyright (C) 2016-2017 Devexperts LLC

This product is developed at Devexperts LLC (http://devexperts.com/).
Licensed under the GNU General Public License, Version 3.
Full text of the licence can be found in the LICENSE file.
Source code and documentation are available at <http://code.devexperts.com/>.


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
    	