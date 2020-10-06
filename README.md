# PandemyTracker
A project aimed to get a better understanding for Spring. This project is an attempt at creating a tracker for the ongoing covid pandemy.

REST support:
GET
POST
PUT

Before use:
Add databse information to the application.yml file. As this program was made with postgresql as the main database, this is what suggest you to use.

How to use:
This program currently supports fetching data regarding cases, deaths and intense nursed for any given day.
It is also possible in the same regard to fetch data regarding Region + Week data, containing for Region: region_name, total_cases, cases_per_hundredthousand, total_deaths, total_intense_nursed. As for Week, this contains data linked to Region: week_number, cases, cumulative_cases, deaths, cumulative_deaths, intense_nursed, cumulative_intense_nursed, cases_per_hundredthousand, cumulative_cases_per_hundredthousand.

DAY:
(GET): To get day data use the url: "{PATH}:{PORT}/api/v1/day". (I.e., for testing purposes i used localhost:8081/api/v1/day). (GET)
(GET): It's also possible to check one day at a time by using previous mentioned URL followed by a date in the format "YEAR-MONTH-DAY" (I.e., localhost:8081/api/v1/day/2020-01-02).

(PUT): With previously mentioned PATH, using the REST method PUT, will connect to "Folkhälsomyndighetens" data source and update the data based on it.
(PUT): It's also possibble to update a specific date by using the PATH above, followed by a date, and then in the body specify any changes to the data in the following format:
{
	"test_date": "2020-12-28", 
	"cases": 1,
	"deaths": 1,
	"intense_nursed": 1
}
This will update the given date to the information given above.

(POST): See PUT section, as it is the same principe.

REGION + WEEK:

(GET): To get day data use the url: "{PATH}:{PORT}/api/v1/region". (I.e., for testing purposes i used localhost:8081/api/v1/region). (GET)
(GET): To check one region at a time, use previous mentioned URL followed by a region name (I.e., localhost:8081/api/v1/region/Stockholm, NOTE: cases sensitive for now).

(PUT): With previously mentioned PATH, using the REST method PUT, will connect to "Folkhälsomyndighetens" data source and update the data based on it.
(PUT): It's also possibble to update a specific date by using the PATH above, followed by a reion name, and then in the body specify any changes to the data in the following format:
{
	"region_name": "Stockholm", 
	"total_cases": 100000,
	"cases_per_hundredthousand": 1000,
	"total_deaths": 100,
	"total_intense_nursed": 10,
	"week_data": []
}
This will update the given region to the information given above.

(POST): See PUT section, as it is the same principe.

Todo List:
Finish backend
- Fix so that dates are handled in the backend, rather than reading from data (as dates are sometimes not specified). (DONE)
- Fix so that when downloading data it is stored in cache so that there is no need to download again (for that day for day data, for that week for week/region data). (DONE)
- Fix region part, add anything that it currently isn't fetching. (DONE)
- comment code (NOT DONE)
- write test cases (NOT DONE)

Finish frontend (any html, css, javascript) (NOT DONE)
