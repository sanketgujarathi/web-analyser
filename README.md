# HTML Analyser

Web application that analyses the HTML tags and hyperlinks in web page for a given url and returns the result in the form of a tabular summary.	


## Getting Started
	
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

JDK 1.8
Maven 3.0.3

### Build
To build the project run the following maven command from the base project directory - 
mvn clean package

### Installing

The zip file contains a runnable jar called "html-analyser-1.0-RELEASE". 
To run the application simply run the java command from the directory where the zip file was extracted
java -jar html-analyser-1.0-RELEASE.jar

## Usage
Open the following link in a web browser - http://localhost:8080/htmlAnalyser
It will open a simple web page with a text field and two buttons
Enter the url to be analysed in the text field provided
Hit button "Analyse HTML" to view results for first part of coding challenge
Hit button "Analyse Links" to view results for optinal part of coding challenge


## Assumptions
Input urls of only http and https protocols will be allowed
Max size length of input url allowed is 2000
Pages that have a form with exactly one password input field (<input type='password'>) would be assumed to have a login form




