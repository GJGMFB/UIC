//
// Console app to build & populate the database.
//
// Data source: https://data.cityofchicago.org
// Source data format: CSV (for Excel)
// Target data format: SQL Server Database
//
// References: http://www.codeproject.com/Articles/11698/A-Portable-and-Efficient-Generic-Parser-for-Flat-F
//
// Dennis Aurelian Leancu
// U. of Illinois, Chicago
// CS480, Summer 2016
// Final Project
//

using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using GenericParsing;

namespace database_creator {
	class Program {
		static void Main(string[] args) {
			// Azure SQL db info
			string NetID = "leancu2";
			string databasename = "final_project";
			string username = "leancu2";
			string pwd = "1Password";

			string remoteInfo = string.Format(@"
Server=tcp:{0}.database.windows.net,1433;Initial Catalog={1};Persist Security Info=False;User ID={2};Password={3};MultipleActiveResultSets=False;Encrypt=True;TrustServerCertificate=False;Connection Timeout=30;
", NetID, databasename, username, pwd);

			////////////////////////////////////
			// Clean up database
			string sql = @"
				IF (OBJECT_ID('fk_cslib', 'F') IS NOT NULL)
				BEGIN
					ALTER TABLE ComputerSessions DROP CONSTRAINT fk_cslib;
				END

				IF (OBJECT_ID('fk_vlib', 'F') IS NOT NULL)
				BEGIN
					ALTER TABLE Visitors DROP CONSTRAINT fk_vlib;
				END

				IF (OBJECT_ID('ComputerSessions', 'U') IS NOT NULL)
					DROP TABLE ComputerSessions;

				IF (OBJECT_ID('Libraries', 'U') IS NOT NULL)
					DROP TABLE Libraries;

				IF (OBJECT_ID('Visitors', 'U') IS NOT NULL)
					DROP TABLE Visitors;

				IF (OBJECT_ID('Wifi', 'U') IS NOT NULL)
					DROP TABLE Wifi;
			";

			SqlConnection db = new SqlConnection(remoteInfo);
			db.Open();
			SqlCommand cmd = new SqlCommand();
			cmd.Connection = db;
			cmd.CommandText = sql;
			Console.WriteLine("Dropping tables and constraints...");
			cmd.ExecuteNonQuery();
			db.Close();

			////////////////////////////////////
			// Create tables
			sql = @"
                CREATE TABLE Libraries (
                    lid INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
					name NVARCHAR(128) NOT NULL,
                    address NVARCHAR(128) NOT NULL,
					zip NVARCHAR(5) NOT NULL,
					lat FLOAT NOT NULL,
					lon FLOAT NOT NULL
				);

				CREATE TABLE Visitors (
					lid INT NOT NULL,
					visitors INT NOT NULL,
					date DATE NOT NULL,
					CONSTRAINT fk_vlib FOREIGN KEY (lid) REFERENCES Libraries(lid)
				);

				CREATE TABLE Wifi (
					date DATE NOT NULL,
					sessions INT NOT NULL
				);

				CREATE TABLE ComputerSessions (
					lid INT NOT NULL,
					date DATE NOT NULL,
					sessions INT NOT NULL,
					CONSTRAINT fk_cslib FOREIGN KEY (lid) REFERENCES Libraries(lid)
				);

				CREATE INDEX CSDate on ComputerSessions(date);
				CREATE INDEX WDate on Wifi(date);
				CREATE INDEX VDate on Visitors(date);
            ";

			Console.WriteLine("...Creating tables...");
			db.Open();
			cmd.CommandText = sql;
			cmd.ExecuteNonQuery();
			db.Close();

			////////////////////////////////////
			// Data parse

			// Locations
			Console.WriteLine("...Parsing & storing library locations...");
			using (GenericParser parser = new GenericParser()) {
				parser.SetDataSource(@".\data\Libraries_-_Locations__Hours_and_Contact_Information.csv");
				parser.FirstRowHasHeader = true;
				sql = string.Empty;

				while (parser.Read()) {
					string[] location = parser[10]
						.Trim(new char[] { '(', ')', ' ' }) // remove ( & ) & spaces
						.Split(new char[] { ',' }); // split into 2 coordinates

					string name = Convert.ToString(parser[0]).Trim();
					string address = Convert.ToString(parser[4]).Trim();
					string zip = Convert.ToString(parser[7]).Trim();
					double lat = Convert.ToDouble(location[0]);
					double lon = Convert.ToDouble(location[1]);

					sql += string.Format(@"
				            INSERT INTO Libraries(name, address, zip, lat, lon)
				            VALUES('{0}', '{1}', '{2}', {3}, {4});
                        ", name, address, zip, lat, lon);
				}

				db.Open();
				cmd.CommandText = sql;
				cmd.ExecuteNonQuery();
				db.Close();
			}

			// Wifi
			Console.WriteLine("...WiFi from 2011 to 2014...");
			using (GenericParser parser = new GenericParser()) {
				parser.SetDataSource(@".\data\Libraries_-_WiFi_Usage__2011-2014_.csv");
				parser.FirstRowHasHeader = true;
				sql = string.Empty;

				while (parser.Read()) {
					DateTime date = DateTime.Parse(parser[0] + ' ' + parser[1]); // Month Year
					int num = Convert.ToInt32(parser[2]); // Number of sessions for that month

					sql += string.Format(@"
				            INSERT INTO Wifi(date, sessions)
				            VALUES('{0}', {1});
                        ", date.ToString("s"), num);
				}

				db.Open();
				cmd.CommandText = sql;
				cmd.ExecuteNonQuery();
				db.Close();
			}

			// Wifi
			Console.WriteLine("...WiFi from 2015...");
			using (GenericParser parser = new GenericParser()) {
				parser.SetDataSource(@".\data\Libraries_-_2015_Wi_Fi_Usage.csv");
				parser.FirstRowHasHeader = true;
				sql = string.Empty;

				while (parser.Read()) {
					DateTime date = DateTime.Parse(parser[0] + ' ' + parser[1]); // Month Year
					int num = Convert.ToInt32(parser[2]); // Number of sessions for that month

					sql += string.Format(@"
				            INSERT INTO Wifi(date, sessions)
				            VALUES('{0}', {1});
                        ", date.ToString("s"), num);
				}

				db.Open();
				cmd.CommandText = sql;
				cmd.ExecuteNonQuery();
				db.Close();
			}

			// Wifi
			Console.WriteLine("...WiFi from 2016...");
			using (GenericParser parser = new GenericParser()) {
				parser.SetDataSource(@".\data\Libraries_-_2016_Wi_Fi_Usage.csv");
				parser.FirstRowHasHeader = true;
				sql = string.Empty;

				while (parser.Read()) {
					// Skip missing data
					if (string.IsNullOrEmpty(parser[1])) {
						continue;
					}

					DateTime date = DateTime.Parse(parser[0] + " 2016"); // Month Year
					int num = Convert.ToInt32(parser[1]); // Number of sessions for that month

					sql += string.Format(@"
				            INSERT INTO Wifi(date, sessions)
				            VALUES('{0}', {1});
                        ", date.ToString("s"), num);
				}

				db.Open();
				cmd.CommandText = sql;
				cmd.ExecuteNonQuery();
				db.Close();
			}

			// Computer sessions
			Console.WriteLine("...Computer sessions from 2014...");
			using (GenericParser parser = new GenericParser()) {
				parser.SetDataSource(@".\data\Libraries_-_2014_Computer_Sessions_by_Location.csv");
				parser.FirstRowHasHeader = true;
				sql = string.Empty;

				while (parser.Read()) {
					int jan = Convert.ToInt32(parser[4]);
					int feb = Convert.ToInt32(parser[5]);
					int mar = Convert.ToInt32(parser[6]);
					int apr = Convert.ToInt32(parser[7]);
					int may = Convert.ToInt32(parser[8]);
					int jun = Convert.ToInt32(parser[9]);
					int jul = Convert.ToInt32(parser[10]);
					int aug = Convert.ToInt32(parser[11]);
					int sep = Convert.ToInt32(parser[12]);
					int oct = Convert.ToInt32(parser[13]);
					int nov = Convert.ToInt32(parser[14]);
					int dec = Convert.ToInt32(parser[15]);

					sql += string.Format(@"
				            INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{1}', {13});
							
							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{2}', {14});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{3}', {15});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{4}', {16});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{5}', {17});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{6}', {18});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{7}', {19});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{8}', {20});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{9}', {21});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{10}', {22});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{11}', {23});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{12}', {24});
                        ", parser[0],
					DateTime.Parse("January 2014"),
					DateTime.Parse("February 2014"),
					DateTime.Parse("March 2014"),
					DateTime.Parse("April 2014"),
					DateTime.Parse("May 2014"),
					DateTime.Parse("June 2014"),
					DateTime.Parse("July 2014"),
					DateTime.Parse("August 2014"),
					DateTime.Parse("September 2014"),
					DateTime.Parse("October 2014"),
					DateTime.Parse("November 2014"),
					DateTime.Parse("December 2014"),
					jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec
					);
				}

				db.Open();
				cmd.CommandText = sql;
				cmd.ExecuteNonQuery();
				db.Close();
			}

			// Computer sessions
			Console.WriteLine("...Computer sessions from 2015...");
			using (GenericParser parser = new GenericParser()) {
				parser.SetDataSource(@".\data\Libraries_-_2015_Computer_Sessions_by_Location.csv");
				parser.FirstRowHasHeader = true;
				sql = string.Empty;

				while (parser.Read()) {
					int jan = Convert.ToInt32(parser[4]);
					int feb = Convert.ToInt32(parser[5]);
					int mar = Convert.ToInt32(parser[6]);
					int apr = Convert.ToInt32(parser[7]);
					int may = Convert.ToInt32(parser[8]);
					int jun = Convert.ToInt32(parser[9]);
					int jul = Convert.ToInt32(parser[10]);
					int aug = Convert.ToInt32(parser[11]);
					int sep = Convert.ToInt32(parser[12]);
					int oct = Convert.ToInt32(parser[13]);
					int nov = Convert.ToInt32(parser[14]);
					int dec = Convert.ToInt32(parser[15]);

					sql += string.Format(@"
				            INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{1}', {13});
							
							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{2}', {14});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{3}', {15});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{4}', {16});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{5}', {17});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{6}', {18});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{7}', {19});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{8}', {20});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{9}', {21});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{10}', {22});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{11}', {23});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{12}', {24});
                        ", parser[0],
					DateTime.Parse("January 2015"),
					DateTime.Parse("February 2015"),
					DateTime.Parse("March 2015"),
					DateTime.Parse("April 2015"),
					DateTime.Parse("May 2015"),
					DateTime.Parse("June 2015"),
					DateTime.Parse("July 2015"),
					DateTime.Parse("August 2015"),
					DateTime.Parse("September 2015"),
					DateTime.Parse("October 2015"),
					DateTime.Parse("November 2015"),
					DateTime.Parse("December 2015"),
					jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec
					);
				}

				db.Open();
				cmd.CommandText = sql;
				cmd.ExecuteNonQuery();
				db.Close();
			}

			// Computer sessions
			Console.WriteLine("...Computer sessions from 2016...");
			using (GenericParser parser = new GenericParser()) {
				parser.SetDataSource(@".\data\Libraries_-_2016_Computer_Sessions_by_Location.csv");
				parser.FirstRowHasHeader = true;
				sql = string.Empty;

				while (parser.Read()) {
					int jan = Convert.ToInt32(parser[1]);
					int feb = Convert.ToInt32(parser[2]);
					int mar = Convert.ToInt32(parser[3]);
					int apr = Convert.ToInt32(parser[4]);
					int may = Convert.ToInt32(parser[5]);
					int jun = Convert.ToInt32(parser[6]);

					sql += string.Format(@"
				            INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{1}', {7});
							
							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{2}', {8});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{3}', {9});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{4}', {10});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{5}', {11});

							INSERT INTO ComputerSessions(lid, date, sessions)
				            VALUES({0}, '{6}', {12});
                        ", parser[0],
					DateTime.Parse("January 2016"),
					DateTime.Parse("February 2016"),
					DateTime.Parse("March 2016"),
					DateTime.Parse("April 2016"),
					DateTime.Parse("May 2016"),
					DateTime.Parse("June 2016"),
					jan, feb, mar, apr, may, jun
					);
				}

				db.Open();
				cmd.CommandText = sql;
				cmd.ExecuteNonQuery();
				db.Close();
			}

			// Visitors
			Console.WriteLine("...Visitors from 2014...");
			using (GenericParser parser = new GenericParser()) {
				parser.SetDataSource(@".\data\Libraries_-_2014_Visitors_by_Location.csv");
				parser.FirstRowHasHeader = true;
				sql = string.Empty;
				while (parser.Read()) {
					int jan = Convert.ToInt32(parser[4]);
					int feb = Convert.ToInt32(parser[5]);
					int mar = Convert.ToInt32(parser[6]);
					int apr = Convert.ToInt32(parser[7]);
					int may = Convert.ToInt32(parser[8]);
					int jun = Convert.ToInt32(parser[9]);
					int jul = Convert.ToInt32(parser[10]);
					int aug = Convert.ToInt32(parser[11]);
					int sep = Convert.ToInt32(parser[12]);
					int oct = Convert.ToInt32(parser[13]);
					int nov = Convert.ToInt32(parser[14]);
					int dec = Convert.ToInt32(parser[15]);

					sql += string.Format(@"
				            INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{1}', {13});
							
							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{2}', {14});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{3}', {15});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{4}', {16});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{5}', {17});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{6}', {18});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{7}', {19});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{8}', {20});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{9}', {21});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{10}', {22});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{11}', {23});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{12}', {24});
                        ", parser[0],
					DateTime.Parse("January 2014"),
					DateTime.Parse("February 2014"),
					DateTime.Parse("March 2014"),
					DateTime.Parse("April 2014"),
					DateTime.Parse("May 2014"),
					DateTime.Parse("June 2014"),
					DateTime.Parse("July 2014"),
					DateTime.Parse("August 2014"),
					DateTime.Parse("September 2014"),
					DateTime.Parse("October 2014"),
					DateTime.Parse("November 2014"),
					DateTime.Parse("December 2014"),
					jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec
					);
				}

				db.Open();
				cmd.CommandText = sql;
				cmd.ExecuteNonQuery();
				db.Close();
			}

			// Visitors
			Console.WriteLine("...Visitors from 2015...");
			using (GenericParser parser = new GenericParser()) {
				parser.SetDataSource(@".\data\Libraries_-_2015_Visitors_by_Location.csv");
				parser.FirstRowHasHeader = true;
				sql = string.Empty;

				while (parser.Read()) {
					int jan = Convert.ToInt32(parser[4]);
					int feb = Convert.ToInt32(parser[5]);
					int mar = Convert.ToInt32(parser[6]);
					int apr = Convert.ToInt32(parser[7]);
					int may = Convert.ToInt32(parser[8]);
					int jun = Convert.ToInt32(parser[9]);
					int jul = Convert.ToInt32(parser[10]);
					int aug = Convert.ToInt32(parser[11]);
					int sep = Convert.ToInt32(parser[12]);
					int oct = Convert.ToInt32(parser[13]);
					int nov = Convert.ToInt32(parser[14]);
					int dec = Convert.ToInt32(parser[15]);

					sql += string.Format(@"
				            INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{1}', {13});
							
							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{2}', {14});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{3}', {15});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{4}', {16});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{5}', {17});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{6}', {18});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{7}', {19});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{8}', {20});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{9}', {21});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{10}', {22});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{11}', {23});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{12}', {24});
                        ", parser[0],
					DateTime.Parse("January 2015"),
					DateTime.Parse("February 2015"),
					DateTime.Parse("March 2015"),
					DateTime.Parse("April 2015"),
					DateTime.Parse("May 2015"),
					DateTime.Parse("June 2015"),
					DateTime.Parse("July 2015"),
					DateTime.Parse("August 2015"),
					DateTime.Parse("September 2015"),
					DateTime.Parse("October 2015"),
					DateTime.Parse("November 2015"),
					DateTime.Parse("December 2015"),
					jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec
					);
				}

				db.Open();
				cmd.CommandText = sql;
				cmd.ExecuteNonQuery();
				db.Close();
			}

			// Visitors
			Console.WriteLine("...Visitors from 2016...");
			using (GenericParser parser = new GenericParser()) {
				parser.SetDataSource(@".\data\Libraries_-_2016_Visitors_by_Location.csv");
				parser.FirstRowHasHeader = true;
				sql = string.Empty;

				while (parser.Read()) {
					int jan = Convert.ToInt32(parser[1]);
					int feb = Convert.ToInt32(parser[2]);
					int mar = Convert.ToInt32(parser[3]);
					int apr = Convert.ToInt32(parser[4]);
					int may = Convert.ToInt32(parser[5]);
					int jun = Convert.ToInt32(parser[6]);

					sql += string.Format(@"
				            INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{1}', {7});
							
							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{2}', {8});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{3}', {9});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{4}', {10});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{5}', {11});

							INSERT INTO Visitors(lid, date, visitors)
				            VALUES({0}, '{6}', {12});
                        ", parser[0],
					DateTime.Parse("January 2016"),
					DateTime.Parse("February 2016"),
					DateTime.Parse("March 2016"),
					DateTime.Parse("April 2016"),
					DateTime.Parse("May 2016"),
					DateTime.Parse("June 2016"),
					jan, feb, mar, apr, may, jun
					);
				}

				db.Open();
				cmd.CommandText = sql;
				cmd.ExecuteNonQuery();
				db.Close();
			}
		}
	}
}
