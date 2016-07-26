//
// N-tier C# and SQL program to analyze Chicago crime data from 
// a Microsoft SQL Server database file
//
// Dennis Aurelian Leancu
// U. of Illinois, Chicago
// CS341, Spring 2016
// Homework 8
//

using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Data;
using CHART = System.Windows.Forms.DataVisualization.Charting;


namespace ChicagoCrimeGUI
{
    public partial class Form1 : Form
    {
        //
        // private data members accessible to all functions:
        //
        private string ConnectionInfo;


        public Form1()
        {
            InitializeComponent();

            // initialize database connection string:
            string version = "MSSQLLocalDB";
            string filename = this.txtDBFile.Text;

            ConnectionInfo = String.Format(@"Data Source=(LocalDB)\{0};AttachDbFilename={1};Integrated Security=True;",
              version, filename);
        }


        //
        // Called when window is about to appear for the first time:
        //
        private void Form1_Load(object sender, EventArgs e)
        {
            string filename = this.txtDBFile.Text;
            BusinessTier.CrimeStats stats;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            stats = biztier.GetOverallCrimeStats();
            int minYear = stats.MinYear;
            int maxYear = stats.MaxYear;
            long total = stats.TotalCrimes;

            string title;
            title = String.Format("Chicago Crime Analysis from {0}-{1}, Total of {2:#,##0} crimes", stats.MinYear, stats.MaxYear, stats.TotalCrimes);

            this.Text = title;

            //
            // While we're here, let's also fill the drop-down list of
            // Chicago areas...
            //
            List<BusinessTier.Area> areas;

            areas = biztier.GetChicagoAreas(BusinessTier.OrderAreas.ByName);

            foreach (BusinessTier.Area area in areas)
            {
                this.dropAreas.Items.Add(area.AreaName);
            }

            this.dropAreas.SelectedIndex = 0; // Pre-select first area

            //
            // and config the slider controls for min/max years:
            //
            this.tbarMinYear.Minimum = minYear;
            this.tbarMinYear.Maximum = maxYear;
            this.tbarMinYear.Value = minYear;
            this.lblMinYear.Text = minYear.ToString();

            this.tbarMaxYear.Minimum = minYear;
            this.tbarMaxYear.Maximum = maxYear;
            this.tbarMaxYear.Value = maxYear;
            this.lblMaxYear.Text = maxYear.ToString();
        }

        private void exitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void ResetListBox(string newTitle)
        {
            this.lblListboxTitle.Text = newTitle;
            this.lblListboxTitle.Refresh();

            this.listBox1.Items.Clear();
            this.listBox1.Refresh();
        }


        //
        // Display total crimes and arrest percentages by year in the
        // listbox:
        //
        private void totalAndPercentagesByYearToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.ResetListBox("Totals and Arrest Percentages by Year");

            string filename = this.txtDBFile.Text;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            Dictionary<int, double> percents;
            Dictionary<int, long> totals;
            percents = biztier.GetArrestPercentagesByYear();
            totals = biztier.GetTotalsByYear();

            foreach (int year in percents.Keys)
            {
                string msg = string.Format("{0}:  {1:#,##0} crimes, {2:0.00}% arrested",
                  year,
                  totals[year],
                  percents[year]);

                this.listBox1.Items.Add(msg);
            }
        }


        //
        // Display all the crime codes in the listbox:
        //
        private void allCrimeCodesToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.ResetListBox("All Crime Codes");

            string filename = this.txtDBFile.Text;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            List<BusinessTier.CrimeCode> codes;

            codes = biztier.GetCrimeCodes();

            foreach (BusinessTier.CrimeCode code in codes)
            {
                string msg = string.Format("{0}: {1}:{2}",
                  code.IUCR,
                  code.PrimaryDescription,
                  code.SecondaryDescription);

                this.listBox1.Items.Add(msg);
            }
        }


        //
        // Display all the Chicago areas by name in the listbox:
        //
        private void allChicagoAreasToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.ResetListBox("All Chicago Areas by Name");

            string filename = this.txtDBFile.Text;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            List<BusinessTier.Area> areas;

            areas = biztier.GetChicagoAreas(BusinessTier.OrderAreas.ByName);

            foreach (BusinessTier.Area area in areas)
            {
                string msg = string.Format("{0}: #{1}",
                  area.AreaName,
                  area.AreaNumber);

                this.listBox1.Items.Add(msg);
            }
        }

        //
        // Display all the Chicago areas by number in the listbox:
        //
        private void allChicagoAreasByNumberToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.ResetListBox("All Chicago Areas by Number");
            string filename = this.txtDBFile.Text;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            List<BusinessTier.Area> areas;

            areas = biztier.GetChicagoAreas(BusinessTier.OrderAreas.ByNumber);

            foreach (BusinessTier.Area area in areas)
            {
                string msg = string.Format("{0}: #{1}",
                  area.AreaName,
                  area.AreaNumber);

                this.listBox1.Items.Add(msg);
            }
        }


        //
        // Plot total crimes by year:
        //
        private void plotTotalCrimesToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //
            // let's use a separate window to show the graph:
            //
            FormPlot plot = new FormPlot();

            //
            // Compute and plot total # of crimes each year:
            //
            string filename = this.txtDBFile.Text;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            Dictionary<int, long> totals;

            totals = biztier.GetTotalsByYear();
            
            // 
            // We have the data, now let's setup and plot in the chart
            // on the other form:
            //
            plot.chart1.Series.Clear();

            // configure chart: 
            var series1 = new CHART.Series
            {
                Name = "Total Crimes",
                Color = System.Drawing.Color.Blue,
                IsVisibleInLegend = true,
                ChartType = CHART.SeriesChartType.Line
            };

            plot.chart1.Series.Add(series1);

            // now plot (x, y) coordinates: 
            foreach (int year in totals.Keys)
            {
                int x = year;
                long y = totals[year];

                series1.Points.AddXY(x, y);
            }

            //
            // All set, show the window to the user --- note that if user
            // does not close window, it will be closed automatically when
            // this main window exits.
            //
            plot.Text = "** Total Crimes by Year **";
            plot.Location = new System.Drawing.Point(0, 0);  // top-left:

            plot.Show();
        }


        //
        // Plot total crimes by area:
        //
        private void plotTotalCrimesByAreaToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //
            // let's use a separate window to show the graph:
            //
            FormPlot plot = new FormPlot();

            //
            // Compute and plot total # of crimes by each area:
            //
            string filename = this.txtDBFile.Text;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            Dictionary<int, long> totals;

            totals = biztier.GetTotalsByArea();

            // 
            // We have the data, now let's setup and plot in the chart
            // on the other form:
            //
            plot.chart1.Series.Clear();

            // configure chart: 
            var series1 = new CHART.Series
            {
                Name = "Total Crimes",
                Color = System.Drawing.Color.Blue,
                IsVisibleInLegend = true,
                IsXValueIndexed = true,
                ChartType = CHART.SeriesChartType.Line
            };

            plot.chart1.Series.Add(series1);
            plot.chart1.ChartAreas[0].AxisX.Interval = 5;

            // now plot (x, y) coordinates: 
            foreach (int area in totals.Keys)
            {
                int x = area;
                long y = totals[area];

                series1.Points.AddXY(x, y);
            }

            //
            // All set, show the window to the user --- note that if user
            // does not close window, it will be closed automatically when
            // this main window exits.
            //
            plot.Text = "** Total Crimes by Area **";
            plot.Location = new System.Drawing.Point(20, 20);  // top-left:

            plot.Show();
        }


        //
        // Plot total crimes by month:
        //
        private void plotTotalCrimesByMonthToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //
            // let's use a separate window to show the graph:
            //
            FormPlot plot = new FormPlot();

            //
            // Compute and plot total # of crimes by each area:
            //
            string filename = this.txtDBFile.Text;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            Dictionary<int, long> totals;

            totals = biztier.GetTotalsByMonth();
            // 
            // We have the data, now let's setup and plot in the chart
            // on the other form:
            //
            plot.chart1.Series.Clear();

            // configure chart: 
            var series1 = new CHART.Series
            {
                Name = "Total Crimes",
                Color = System.Drawing.Color.Blue,
                IsVisibleInLegend = true,
                ChartType = CHART.SeriesChartType.Line
            };

            plot.chart1.Series.Add(series1);

            // now plot (x, y) coordinates: 
            string[] months = { "Jan", "Feb", "Mar", "Apr", "May",
      "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            foreach (int month in totals.Keys)
            {
                int x = month;
                long y = totals[month];

                var point = new CHART.DataPoint(x, y);
                point.Label = months[x - 1];

                series1.Points.Add(point);
            }

            //
            // All set, show the window to the user --- note that if user
            // does not close window, it will be closed automatically when
            // this main window exits.
            //
            plot.Text = "** Total Crimes by Month **";
            plot.Location = new System.Drawing.Point(40, 40);  // top-left:

            plot.Show();
        }


        //
        // This is the button that searches based on 3 checkboxes:
        //
        private void cmdTotalCrimes_Click(object sender, EventArgs e)
        {
            //
            // make sure at least 1 check box is checked:
            //
            if (chkYear.Checked == false &&
              chkIUCR.Checked == false &&
              chkArea.Checked == false)
            {
                MessageBox.Show("Please check at least one search criteria...");
                return;
            }

            string filename = this.txtDBFile.Text;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            // 
            // Okay, at least one check box is checked, now build 
            // WHERE clause based on user selection...
            //
            bool[] check = new bool[3];
            int year;
            int area;
            Int32.TryParse(this.txtYear.Text, out year);
            Int32.TryParse(this.txtArea.Text, out area);

            if (chkYear.Checked)
            {
                check[0] = true;

                if (Int32.TryParse(this.txtYear.Text, out year) == false)
                {
                    MessageBox.Show("Year must be numeric...");
                    return;
                }
            }

            if (chkIUCR.Checked)
            {
                check[1] = true;
            }

            if (chkArea.Checked)
            {
                check[3] = true;
                if (Int32.TryParse(this.txtArea.Text, out area) == false)
                {
                    MessageBox.Show("Area must be numeric...");
                    return;
                }
            }
            
            object result = biztier.GetTotalFromChecks(year, this.txtIUCR.Text.Replace("'", "''"), area, check);

            // NOTE: we always get a result back, worst-case it's 0.

            long total = System.Convert.ToInt64(result);
            this.lblTotalCrimes.Text = total.ToString("#,##0");
        }

        private void chkYear_CheckedChanged(object sender, EventArgs e)
        {
            this.lblTotalCrimes.Text = "?";
        }

        private void chkIUCR_CheckedChanged(object sender, EventArgs e)
        {
            this.lblTotalCrimes.Text = "?";
        }

        private void chkArea_CheckedChanged(object sender, EventArgs e)
        {
            this.lblTotalCrimes.Text = "?";
        }


        //
        // Top N Areas in the city for crime:
        //
        private int GetN()
        {
            int N;

            if (Int32.TryParse(this.txtTopN.Text, out N) == false)
            {
                MessageBox.Show("N must be numeric...");
                return -1;
            }

            if (N < 1)
            {
                MessageBox.Show("N must be > 0...");
                return -1;
            }

            return N;
        }

        private void cmdTopAreas_Click(object sender, EventArgs e)
        {
            this.listBox2.Items.Clear();

            //
            // First, get the N value for our top N:
            //
            int N = GetN();

            if (N < 1)
                return;

            //
            // Okay, execute query to retrieve top N areas for crime...
            //
            string filename = this.txtDBFile.Text;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            Dictionary<string, int> areas;

            areas = biztier.GetTopNByAreaName(N);

            int i = 1;

            foreach (string area in areas.Keys)
            {
                string msg = string.Format("{0}. {1}: {2:#,##0}",
                  i,
                  area,
                  areas[area]);

                this.listBox2.Items.Add(msg);

                i++;
            }
        }


        //
        // Top N types of crime:
        //
        private void cmdTopCrimeTypes_Click(object sender, EventArgs e)
        {
            this.listBox2.Items.Clear();

            //
            // First, get the N value for our top N:
            //
            int N = GetN();

            if (N < 1)
                return;

            //
            // Okay, execute query to retrieve top N types of crime...
            //
            string filename = this.txtDBFile.Text;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            DataSet ds = biztier.GetTopNByCrime(N);

            int i = 1;

            foreach (DataRow row in ds.Tables["TABLE"].Rows)
            {
                string msg = string.Format("{0}. {1}",
                  i,
                  Convert.ToString(row["PrimaryDesc"]));

                this.listBox2.Items.Add(msg);

                msg = string.Format("    {0}",
                  Convert.ToString(row["SecondaryDesc"]));

                this.listBox2.Items.Add(msg);

                msg = string.Format("    {0:#,##0}",
                  Convert.ToInt32(row["Total"]));

                this.listBox2.Items.Add(msg);

                i++;
            }
        }


        //
        // Top N areas for a particular type of crime:
        //
        private void cmdTopAreasForThisCrimeType_Click(object sender, EventArgs e)
        {
            this.listBox2.Items.Clear();

            //
            // First, get the N value for our top N:
            //
            int N = GetN();

            if (N < 1)
                return;

            //
            // Now retrieve the crime code the user is interested in:
            //
            string iucr = this.txtIUCR2.Text.Replace("'", "''");

            //
            // Okay, execute query to retrieve top N areas for this
            // type of crime:
            //
            string filename = this.txtDBFile.Text;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            DataSet ds = biztier.GetTopNByIucr(N, iucr);

            int i = 1;

            foreach (DataRow row in ds.Tables["TABLE"].Rows)
            {
                string msg = string.Format("{0}. {1}: {2:#,##0}",
                  i,
                  Convert.ToString(row["AreaName"]),
                  Convert.ToInt32(row["Total"]));

                this.listBox2.Items.Add(msg);

                i++;
            }
        }

        private void tbarMinYear_Scroll(object sender, EventArgs e)
        {
            this.lblMinYear.Text = tbarMinYear.Value.ToString();
        }

        private void tbarMaxYear_Scroll(object sender, EventArgs e)
        {
            this.lblMaxYear.Text = tbarMaxYear.Value.ToString();
        }


        //
        // Top N crimes in a given area across a range of years:
        //
        private void cmdTopCrimesGivenAreaAndYears_Click(object sender, EventArgs e)
        {
            this.listBox2.Items.Clear();

            //
            // First, get the N value for our top N:
            //
            int N = GetN();

            if (N < 1)
                return;

            //
            // Second, what area did the user select?
            //

            if (this.dropAreas.SelectedIndex < 0)
            {
                MessageBox.Show("Please select an area...");
                return;
            }

            string areaname = this.dropAreas.SelectedItem.ToString();
            areaname = areaname.Replace("'", "''");

            //
            // Third, what year range?
            //
            int minyear = this.tbarMinYear.Value;
            int maxyear = this.tbarMaxYear.Value;

            if (minyear > maxyear)
            {
                MessageBox.Show("Please select a non-empty range of years...");
                return;
            }

            //
            // Okay, we have the input we need, so now let's execute a
            // query to retrieve the top N crimes in this area over
            // this range of years:
            //
            string filename = this.txtDBFile.Text;
            BusinessTier.Business biztier;
            biztier = new BusinessTier.Business(filename);

            if (biztier.OpenCloseConnection() == false)
            {
                MessageBox.Show("Error, unable to connect to database.");
                return;
            }

            DataSet ds = biztier.GetTopNByYear(N, minyear, maxyear, areaname);

            int i = 1;

            foreach (DataRow row in ds.Tables["TABLE"].Rows)
            {
                string msg = string.Format("{0}. {1}",
                  i,
                  Convert.ToString(row["PrimaryDesc"]));

                this.listBox2.Items.Add(msg);

                msg = string.Format("    {0}",
                  Convert.ToString(row["SecondaryDesc"]));

                this.listBox2.Items.Add(msg);

                msg = string.Format("    {0:#,##0}",
                  Convert.ToInt32(row["Total"]));

                this.listBox2.Items.Add(msg);

                i++;
            }
        }

    }//class
}//namespace
