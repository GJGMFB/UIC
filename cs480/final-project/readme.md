# Chicago Public Library Metrics Program

You can "visit" Chicago's libraries and see which ones have the most/least visitors of all time. Additionally you can "start a WiFi session" or "computer session" at a particular library and see how many sessions there were for a chosen month. Fully ACID compliant, uses built in .NET transaction objects. This assignment uses Microsoft Azure for the SQL database, but that account is deactivated. If you want to run the solution yourself, you will need to modify the source to use your own database.

Note about data: The library data was relatively messy so I had to modify a few things with Excel so that it would be easy to import into a database. Some libraries were missing between datasets, so I removed those. Chicago's data portal team decided to give a monthly granularity for these datasets, so it might make the program a little boring to use.

Note about database-creator program: It will drop tables and constraints at the start to create a fresh database.

Languages:
  - C#
  - SQL
