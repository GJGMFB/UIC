//
// BusinessTier:  business logic, acting as interface between UI and data store.
//

using System;
using System.Collections.Generic;
using System.Data;


namespace BusinessTier
{
    ///
    /// <summary>
    /// Ways to sort the Areas in Chicago.
    /// </summary>
    /// 
    public enum OrderAreas
    {
        ByNumber,
        ByName
    };


    //
    // Business:
    //
    public class Business
    {
        //
        // Fields:
        //
        private string _DBFile;
        private DataAccessTier.Data dataTier;


        ///
        /// <summary>
        /// Constructs a new instance of the business tier.  The format
        /// of the filename should be either |DataDirectory|\filename.mdf,
        /// or a complete Windows pathname.
        /// </summary>
        /// <param name="DatabaseFilename">Name of database file</param>
        /// 
        public Business(string DatabaseFilename)
        {
            _DBFile = DatabaseFilename;

            dataTier = new DataAccessTier.Data(DatabaseFilename);
        }


        ///
        /// <summary>
        ///  Opens and closes a connection to the database, e.g. to
        ///  startup the server and make sure all is well.
        /// </summary>
        /// <returns>true if successful, false if not</returns>
        /// 
        public bool OpenCloseConnection()
        {
            return dataTier.OpenCloseConnection();
        }


        ///
        /// <summary>
        /// Returns overall stats about crimes in Chicago.
        /// </summary>
        /// <returns>CrimeStats object</returns>
        ///
        public CrimeStats GetOverallCrimeStats()
        {
            CrimeStats cs;
            string sql = @"
            Select Min(Year) As MinYear, Max(Year) As MaxYear, Count(*) As Total
            From Crimes;
            ";

            // Execute
            DataSet ds = dataTier.ExecuteNonScalarQuery(sql);

            // we get back exactly one record:
            DataRow row = ds.Tables["TABLE"].Rows[0];

            int minYear = Convert.ToInt32(row["MinYear"]);
            int maxYear = Convert.ToInt32(row["MaxYear"]);
            long total = Convert.ToInt64(row["Total"]);

            cs = new CrimeStats(total, minYear, maxYear);

            return cs;
        }


        ///
        /// <summary>
        /// Returns all the areas in Chicago, ordered by area # or name.
        /// </summary>
        /// <param name="ordering"></param>
        /// <returns>List of Area objects</returns>
        /// 
        public List<Area> GetChicagoAreas(OrderAreas ordering)
        {
            List<Area> areas = new List<Area>();
            string sql;

            if (ordering == OrderAreas.ByName)
            {
                sql = @"
                SELECT * FROM Areas 
                WHERE Area > 0
                ORDER BY AreaName ASC;
                ";
            } else
            {
                sql = @"
                SELECT * FROM Areas 
                WHERE Area > 0
                ORDER BY Area ASC;
                ";
            }

            // Execute
            DataSet ds = dataTier.ExecuteNonScalarQuery(sql);

            foreach (DataRow r in ds.Tables["TABLE"].Rows)
            {
                Area a;
                a = new Area(ds.Tables["TABLE"].Rows.IndexOf(r), Convert.ToString(r["AreaName"]));
                areas.Add(a);
            }

            return areas;
        }


        ///
        /// <summary>
        /// Returns all the crime codes and their descriptions.
        /// </summary>
        /// <returns>List of CrimeCode objects</returns>
        ///
        public List<CrimeCode> GetCrimeCodes()
        {
            List<CrimeCode> codes = new List<CrimeCode>();
            string sql = @"
            SELECT * FROM Codes 
            ORDER BY IUCR;
            ";

            // Execute
            DataSet ds = dataTier.ExecuteNonScalarQuery(sql);

            foreach (DataRow row in ds.Tables["TABLE"].Rows)
            {
                CrimeCode c;
                c = new CrimeCode(Convert.ToString(row["IUCR"]), Convert.ToString(row["PrimaryDesc"]), Convert.ToString(row["SecondaryDesc"]));
                codes.Add(c);
            }

            return codes;
        }


        ///
        /// <summary>
        /// Returns a hash table of years, and total crimes each year.
        /// </summary>
        /// <returns>Dictionary where year is the key, and total crimes is the value</returns>
        ///
        public Dictionary<int, long> GetTotalsByYear()
        {
            Dictionary<int, long> totalsByYear = new Dictionary<int, long>();
            string sql = @"
            Select Year, Count(*) As Total
            From Crimes
            Group By Year
            Order By Year ASC;
            ";

            // Execute
            DataSet ds = dataTier.ExecuteNonScalarQuery(sql);

            foreach (DataRow row in ds.Tables["TABLE"].Rows)
            {
                totalsByYear.Add(Convert.ToInt32(row["Year"]), Convert.ToInt64(row["Total"]));
            }

            return totalsByYear;
        }


        ///
        /// <summary>
        /// Returns a hash table of months, and total crimes each month.
        /// </summary>
        /// <returns>Dictionary where month is the key, and total crimes is the value</returns>
        /// 
        public Dictionary<int, long> GetTotalsByMonth()
        {
            Dictionary<int, long> totalsByMonth = new Dictionary<int, long>();
            string sql = @"
            SELECT DatePart(month, CrimeDate) As Month, COUNT(*) As Total
            FROM Crimes
            GROUP BY DatePart(month, CrimeDate)
            ORDER BY DatePart(month, CrimeDate);
            ";

            // Execute
            DataSet ds = dataTier.ExecuteNonScalarQuery(sql);

            foreach (DataRow row in ds.Tables["TABLE"].Rows)
            {
                totalsByMonth.Add(Convert.ToInt32(row["Month"]), Convert.ToInt64(row["Total"]));
            }

            return totalsByMonth;
        }


        ///
        /// <summary>
        /// Returns a hash table of areas, and total crimes each area.
        /// </summary>
        /// <returns>Dictionary where area # is the key, and total crimes is the value</returns>
        ///
        public Dictionary<int, long> GetTotalsByArea()
        {
            Dictionary<int, long> totalsByArea = new Dictionary<int, long>();
            string sql = @"
            Select Area, Count(*) As Total
            From Crimes
            Where Area > 0
            Group By Area
            Order By Area ASC;
            ";

            // Execute
            DataSet ds = dataTier.ExecuteNonScalarQuery(sql);

            foreach (DataRow row in ds.Tables["TABLE"].Rows)
            {
                totalsByArea.Add(Convert.ToInt32(row["Area"]), Convert.ToInt64(row["Total"]));
            }

            return totalsByArea;
        }


        ///
        /// <summary>
        /// Returns a hash table of years, and arrest percentages each year.
        /// </summary>
        /// <returns>Dictionary where the year is the key, and the arrest percentage is the value</returns>
        /// 
        public Dictionary<int, double> GetArrestPercentagesByYear()
        {
            Dictionary<int, double> percentagesByYear = new Dictionary<int, double>();
            string sql = @"
            Select Year, Count(*) As Total, Avg(Convert(float,Arrested))*100.0 As ArrestPercentage
            From Crimes
            Group By Year
            Order By Year;
            ";

            // Execute
            DataSet ds = dataTier.ExecuteNonScalarQuery(sql);

            foreach (DataRow row in ds.Tables["TABLE"].Rows)
            {
                percentagesByYear.Add(Convert.ToInt32(row["Year"]), Convert.ToDouble(row["ArrestPercentage"]));
            }

            return percentagesByYear;
        }

        public object GetTotalFromChecks(int year, string iucr, int area, bool[] checks)
        {
            string where = "";

            if (checks[0])
            {
                where = string.Format("Year = {0}", year);
            }

            if (checks[1])
            {
                where += string.Format("IUCR = '{0}'", iucr);
            }

            if (checks[2])
            {
                where += string.Format("Area = {0}", area);
            }

            string sql = string.Format(@"
            SELECT Count(*) As Total 
            FROM Crimes 
            WHERE {0};
            ",
                  where);

            return dataTier.ExecuteScalarQuery(sql);
        }

        public Dictionary<string, int> GetTopNByAreaName(int n)
        {
            Dictionary<string, int> areas = new Dictionary<string, int>();
            string sql = string.Format(@"
            SELECT TOP {0} AreaName, Count(*) AS Total
            FROM Crimes
            INNER JOIN
              (SELECT * FROM AREAS WHERE Area > 0) AS T
            ON T.Area = Crimes.Area
            GROUP BY T.AreaName
            ORDER BY Total DESC;
            ",
                  n);

            // Execute
            DataSet ds = dataTier.ExecuteNonScalarQuery(sql);

            foreach (DataRow row in ds.Tables["TABLE"].Rows)
            {
                areas.Add(Convert.ToString(row["AreaName"]), Convert.ToInt32(row["Total"]));
            }

            return areas;
        }

        public DataSet GetTopNByCrime(int N)
        {
            string sql = string.Format(@"
            SELECT TOP {0} T.PrimaryDesc, T.SecondaryDesc, Count(*) AS Total
            FROM Crimes
            INNER JOIN
              (SELECT * FROM Codes) AS T
            ON T.IUCR = Crimes.IUCR
            GROUP BY T.PrimaryDesc, T.SecondaryDesc
            ORDER BY Total DESC;
            ",
                  N);

            // Execute
            return dataTier.ExecuteNonScalarQuery(sql);
        }

        public DataSet GetTopNByIucr(int N, string iucr)
        {
            string sql = string.Format(@"
            SELECT TOP {0} AreaName, Count(*) AS Total
            FROM Crimes
            INNER JOIN
              (SELECT * FROM AREAS WHERE Area > 0) AS T
            ON T.Area = Crimes.Area
            WHERE Crimes.IUCR = '{1}'
            GROUP BY T.AreaName
            ORDER BY Total DESC;
            ",
                  N,
                  iucr);

            // Execute
            return dataTier.ExecuteNonScalarQuery(sql);
        }

        public DataSet GetTopNByYear(int N, int minyear, int maxyear, string areaname)
        {
            string sql = string.Format(@"
            SELECT TOP {0} T.PrimaryDesc, T.SecondaryDesc, Count(*) AS Total
            FROM Crimes
            INNER JOIN
              (SELECT * FROM Codes) AS T
            ON T.IUCR = Crimes.IUCR
            WHERE Crimes.Year >= {1} AND 
                  Crimes.Year <= {2} AND
                  Crimes.Area = (SELECT Area FROM AREAS WHERE AreaName = '{3}')
            GROUP BY T.PrimaryDesc, T.SecondaryDesc
            ORDER BY Total DESC;
            ",
                  N,
                  minyear,
                  maxyear,
                  areaname);

            // Execute
            return dataTier.ExecuteNonScalarQuery(sql);
        }

    }//class
}//namespace
