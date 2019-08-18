Welcome to the Github Statistics Web Application
==================================================

This project will help find statistics about any public repository of github. You just have to provide a link to the repository and you are ready to go.

About the Application - Internals
==================================================

This Application uses Java-8 with Spring Boot, Maven integrated with Github API.

From the front-end perspective HTML-5 is used as a base with inline styling.

What's Here
-----------

This application includes:

* README.md - this file

* pom.xml - this file is the Maven Project Object Model for the web application
* src/main - this directory contains your Java service source files
* src/test - this directory contains your Java service unit test files

Technologies Used
---------------

* Java
* Maven 
* Spring - Boot
* HTML
* Github API

Getting Started
---------------

These directions assume you want to run the code on your local computer.

1. Clone the repository.

2. Choose the IDE and import the project.

3. Run the Application class in the com.arpan package.

4. The web application is started on port 8080 by default. You can change the port with the configuration -Dserver.port.

5. Application is now ready to go.

Internal Working
------------------

The Project Contains three main sections namely 

1. Home Section.

2. Results Section.

3. Error Section.

Each section has a seperate html page linked to it. Whenever you try to run the project by default it redirects you to the home page. 

In the home page, you can add a github url for any public repository and click on the button.
Now if the url is correct you are forwarded to a results page where you get the results for open - issues stats.
If the url is incorrect, you are forwarded to error page.

From both the results page and error page you can come back to the home page by clicking a link given in that page.

<b>Note</b>: The time to load the results page is directly proportional to the number of issues in that particular repository.

Every request to the git api fetches 100 results at a time. 

So suppose if any repository contains 100 issues will load much more faster than what a repository with 500 issues or more will.

<b>Note</b>: Currently the support is provided for the following types of urls:

1. https://github.com/{git-user-id}/{repo-name}

2. http://github.com/{git-user-id}/{repo-name}

3. www.github.com/{git-user-id}/{repo-name}

Much more will be handled pretty soon...


What Can you Expect Next?
------------------

No matter how good the code is there is always a scope of improvement. This goes on and on. 

The Github Statistics currently is for Open Issues. 

The code now is configurable and can be made working for other types of requests with minimal code changes.

Upcoming Versions Will Include:

1. Support for Closed Issues.
2. Support for Open Pull Requests.
3. Support for Closed Pull Requests.

And much more coming...

Open for any suggestions and advices...
