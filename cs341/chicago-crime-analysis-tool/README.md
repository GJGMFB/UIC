# Chicago Crime Data Analysis Tool
Taking public Chicago crime data, this program will display relevant statistics and plots according to the user's selections in the GUI. Some examples include:
  - Top crime areas, types, or years
  - Total crimes by year, type, or area
  - Plotting crimes by year, area, or month

This application utilizes an N-Tiered design philosophy with the following layers:

| | |
| --- | --- |
| 1. Presentation | Interacts with the user. |
| 2. Business     | Supports business rules and data processing. |
| 3. Data Access  | Interface between business tier and data store. |
| 4. Data Store   | Actual data repository. |

Languages:
  - C#
  - SQL
