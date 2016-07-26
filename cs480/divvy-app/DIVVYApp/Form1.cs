//
// Multi-user GUI app to simulate DIVVY bike operations. Uses exception handling, transactions, and batch SQL to be more efficient and tolerant of multi-user, real-world usage.
//
// Dennis Aurelian Leancu
// U. of Illinois, Chicago
// CS480, Summer 2016
// HW #5
//

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;
using System.Diagnostics;
using System.Data.SqlClient;
using System.Data;

namespace DIVVYApp
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        // #####################################################
        //
        // Helper functions:
        //

        // returns local / remote connection string based on
        // user's selection:
        string getConnectionString()
        {
            if (optLocalConnection.Checked)
                return txtLocalConnection.Text;
            else
            {
                Debug.Assert(optRemoteConnection.Checked);
                return txtRemoveConnection.Text;
            }
        }

        private void ClearStationUI()
        {
            // clear the station UI:
            txtStationLatLong.Clear();
            txtStationLatLong.Refresh();
            txtStationCapacity.Clear();
            txtStationCapacity.Refresh();
            txtStationNumDocked.Clear();
            txtStationNumDocked.Refresh();
            lstStationBikes.Items.Clear();
            lstStationBikes.Refresh();
        }


        private void ClearCustomerUI()
        {
            // clear the customer UI:
            txtCustomerEmail.Clear();
            txtCustomerEmail.Refresh();
            txtCustomerDateJoined.Clear();
            txtCustomerDateJoined.Refresh();
            txtCustomerNumOut.Clear();
            txtCustomerNumOut.Refresh();
            lstCustomerBikes.Items.Clear();
            lstCustomerBikes.Refresh();
        }

        private int GetSelectedStationID()
        {
            Debug.Assert(lstStations.SelectedIndex >= 0);

            // get selected text:
            string txt = this.lstStations.SelectedItem.ToString();

            // grab the station id at the front:
            int pos = txt.IndexOf(':');
            int sid = Convert.ToInt32(txt.Substring(0, pos));

            return sid;
        }

        private int GetSelectedStationBikeID()
        {
            Debug.Assert(lstStationBikes.SelectedIndex >= 0);

            // get selected text:
            string txt = this.lstStationBikes.SelectedItem.ToString();

            // convert id and return:
            int bid = Convert.ToInt32(txt);

            return bid;
        }

        private int GetSelectedCustomerID()
        {
            Debug.Assert(lstCustomers.SelectedIndex >= 0);

            // get selected text:
            string txt = this.lstCustomers.SelectedItem.ToString();

            // grab the customer id at the front:
            int pos = txt.IndexOf(':');
            int cid = Convert.ToInt32(txt.Substring(0, pos));
            return cid;
        }

        private int GetSelectedCustomerBikeID()
        {
            Debug.Assert(lstCustomerBikes.SelectedIndex >= 0);

            // get selected text:
            string txt = this.lstCustomerBikes.SelectedItem.ToString();

            // convert id and return:
            int bid = Convert.ToInt32(txt);

            return bid;
        }

        // #####################################################
        //
        // UI event handlers:
        //

        //
        // Called automatically just before the window appears:
        //
        private void Form1_Load(object sender, EventArgs e)
        {
            string connectionInfo = getConnectionString();

            //
            // Display list of stations & stations:
            //
            string sql = string.Format(@"
                SELECT StationID, CrossStreet1, CrossStreet2 
                FROM Stations
                ORDER BY StationID ASC;

                SELECT CustomerID, LastName, FirstName 
                FROM Customers
                ORDER BY CustomerID ASC;
            ");

            SqlConnection db = new SqlConnection(connectionInfo);

            try
            {
                db.Open();

                SqlCommand cmd = new SqlCommand();
                cmd.Connection = db;
                SqlDataAdapter adapter = new SqlDataAdapter(cmd);
                DataSet ds = new DataSet();

                cmd.CommandText = sql;
                adapter.Fill(ds);

                //
                // Display list of stations:
                //
                foreach (DataRow row in ds.Tables[0].Rows)
                {
                    string msg = string.Format("{0}: {1} and {2}",
                      Convert.ToString(row["StationID"]),
                      Convert.ToString(row["CrossStreet1"]),
                      Convert.ToString(row["CrossStreet2"]));

                    this.lstStations.Items.Add(msg);
                }

                //
                // Display list of customers:
                //
                foreach (DataRow row in ds.Tables[1].Rows)
                {
                    string msg = string.Format("{0}: {1}, {2}",
                      Convert.ToString(row["CustomerID"]),
                      Convert.ToString(row["LastName"]),
                      Convert.ToString(row["FirstName"]));

                    this.lstCustomers.Items.Add(msg);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(string.Format("**Exception: '{0}'", ex.Message));
            }
            finally
            {
                db.Close();
            }
        }

        //
        // user has selected a station, display data:
        //
        private void lstStations_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (lstStations.SelectedIndex < 0)  // nothing selected:
                return;

            ClearStationUI();

            // set and access DB to get station data:
            string connectionInfo = getConnectionString();

            int sid = GetSelectedStationID();

            // 
            // retrieve data about this station, and the 
            // bikes at this station:
            //
            string sql = string.Format(@"
                SELECT *
                FROM Stations
                WHERE StationID = {0};

                SELECT BikeID
                FROM Bikes
                WHERE StationID = {0}
                ORDER BY BikeID ASC;
                ",
            sid);

            SqlConnection db = new SqlConnection(connectionInfo);

            try
            {
                db.Open();

                SqlCommand cmd = new SqlCommand();
                cmd.Connection = db;
                SqlDataAdapter adapter = new SqlDataAdapter(cmd);
                DataSet ds = new DataSet();

                cmd.CommandText = sql;
                adapter.Fill(ds);

                Debug.Assert(ds.Tables[0].Rows.Count == 1);
                DataRow station = ds.Tables[0].Rows[0];

                txtStationLatLong.Text = string.Format("{0}, {1}",
                  Convert.ToDouble(station["Latitude"]),
                  Convert.ToDouble(station["Longitude"]));

                txtStationCapacity.Text = Convert.ToString(
                  station["Capacity"]);

                txtStationNumDocked.Text = Convert.ToString(
                  station["BikeCount"]);

                //
                // Display list of bikes at this station:
                //
                foreach (DataRow row in ds.Tables[1].Rows)
                {
                    string msg = Convert.ToString(row["BikeID"]);
                    this.lstStationBikes.Items.Add(msg);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(string.Format("**Exception: '{0}'", ex.Message));
            }
            finally
            {
                db.Close();
            }
        }

        //
        // user has selected a customer, display data:
        //
        private void lstCustomers_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (lstCustomers.SelectedIndex < 0)  // nothing selected:
                return;

            ClearCustomerUI();

            // set and access DB to get customer data:
            string connectionInfo = getConnectionString();

            int cid = GetSelectedCustomerID();

            // 
            // retrieve data about this customer, and the 
            // bikes checked out to this customer:
            //
            string sql = string.Format(@"
                SELECT *
                FROM Customers
                WHERE CustomerID = {0};

                SELECT BikeID
                FROM Bikes
                WHERE CustomerID = {0}
                ORDER BY BikeID ASC;
                ", cid);

            SqlConnection db = new SqlConnection(connectionInfo);

            try
            {
                db.Open();

                SqlCommand cmd = new SqlCommand();
                cmd.Connection = db;
                SqlDataAdapter adapter = new SqlDataAdapter(cmd);
                DataSet ds = new DataSet();

                cmd.CommandText = sql;
                adapter.Fill(ds);

                Debug.Assert(ds.Tables[0].Rows.Count == 1);
                DataRow customer = ds.Tables[0].Rows[0];

                txtCustomerEmail.Text = Convert.ToString(
                  customer["Email"]);

                txtCustomerDateJoined.Text = Convert.ToDateTime(
                  customer["DateJoined"]).ToShortDateString();

                //
                // Display list of bikes at this station:
                //
                txtCustomerNumOut.Text = ds.Tables[1].Rows.Count.ToString();

                foreach (DataRow row in ds.Tables[1].Rows)
                {
                    string msg = Convert.ToString(row["BikeID"]);
                    this.lstCustomerBikes.Items.Add(msg);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(string.Format("**Exception: '{0}'", ex.Message));
            }
            finally
            {
                db.Close();
            }
        }

        //
        // Refresh the entire UI with data from DB:
        //
        private void cmdRefresh_Click(object sender, EventArgs e)
        {
            int selectedStation = lstStations.SelectedIndex;
            int selectedCustomer = lstCustomers.SelectedIndex;

            //
            // clear stations:
            //
            lstStations.Items.Clear();
            lstStations.Refresh();

            ClearStationUI();

            //
            // clear customers:
            //
            lstCustomers.Items.Clear();
            lstCustomers.Refresh();

            ClearCustomerUI();

            //
            // now let's reload:
            //
            Form1_Load(sender, e);

            if (selectedStation >= 0)  // a station is selected:
                lstStations.SelectedIndex = selectedStation;  // this triggers event to display:

            if (selectedCustomer >= 0)
                lstCustomers.SelectedIndex = selectedCustomer;
        }

        //
        // Customer bike checkout:
        //
        private void cmdBikeCheckout_Click(object sender, EventArgs e)
        {
            // is a customer selected?
            if (lstCustomers.SelectedIndex < 0)
            {
                MessageBox.Show("Please select a customer...");
                return;
            }

            // is a bike selected from a station?
            if (lstStations.SelectedIndex < 0)
            {
                MessageBox.Show("Please select a station...");
                return;
            }
            if (lstStationBikes.SelectedIndex < 0)
            {
                MessageBox.Show("Please select a station bike...");
                return;
            }

            //
            // extract the customer id, station id, and bike id
            // from the selected items...
            //
            int cid = GetSelectedCustomerID();
            int sid = GetSelectedStationID();
            int bid = GetSelectedStationBikeID();

            // Multi-User Awareness:
            // Check if bike is still available at the station
            string connectionInfo = getConnectionString();
            SqlConnection db = new SqlConnection(connectionInfo);

            string sql = string.Format(@"
                SELECT 1
                FROM Bikes
                WHERE StationID = {0} AND BikeID = {1};", sid, bid);

            try
            {
                db.Open();

                SqlCommand cmd = new SqlCommand();
                cmd.Connection = db;

                cmd.CommandText = sql;

                var rowsModified = cmd.ExecuteScalar();
                if (rowsModified == null)
                {
                    MessageBox.Show("Another customer has checked out this bike before you.");
                    int selectedStation = lstStations.SelectedIndex;

                    ClearStationUI();
                    lstStations.SelectedIndex = -1;  // re-select to update:
                    lstStations.SelectedIndex = selectedStation;
                    return;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("**Exception: '{0}'", ex.Message);
            }
            finally
            {
                db.Close();
            }

            //MessageBox.Show(cid.ToString() + ", " + sid.ToString() + ", " + bid.ToString());

            //
            // to checkout the bike, we need to:
            //
            //   1. update bike, setting station id to NULL and
            //        customer id to cid
            //   2. update station, reducing bike count
            //   3. insert history record denoting checkout
            //
            DateTime curDateTime = DateTime.Now;

            string sql1 = string.Format(@"
                UPDATE Bikes
                SET    StationID = NULL,
                       CustomerID = {0}
                WHERE  BikeID = {1};

                UPDATE  Stations
                SET     BikeCount = BikeCount - 1
                WHERE   StationID = {3};

                INSERT INTO 
                  History(CustomerID,BikeID,Checkout,StationIDout,Checkin,StationIDin)
                  Values({0}, {1}, '{2}', {3}, NULL, NULL);
                ", cid, bid, curDateTime.ToString(), sid);

            SqlTransaction tx = null;
            try
            {
                db.Open();

                tx = db.BeginTransaction();
                SqlCommand cmd = new SqlCommand();
                cmd.Connection = db;

                cmd.CommandText = sql1;
                cmd.Transaction = tx;

                int rowsModified = cmd.ExecuteNonQuery();

                tx.Commit();

                //
                // success, update GUI for station and customer:
                //
                int selectedStation = lstStations.SelectedIndex;

                ClearStationUI();
                lstStations.SelectedIndex = -1;  // re-select to update:
                lstStations.SelectedIndex = selectedStation;

                int selectedCustomer = lstCustomers.SelectedIndex;

                ClearCustomerUI();
                lstCustomers.SelectedIndex = -1;  // re-select to update:
                lstCustomers.SelectedIndex = selectedCustomer;
            }
            catch (Exception ex)
            {
                tx.Rollback();
                MessageBox.Show(string.Format("**SQL: '{1}'\n**Exception: '{0}'", ex.Message, sql1));
            }
            finally
            {
                db.Close();
            }
        }

        //
        // Customer bike checkin:
        private void cmdBikeCheckin_Click(object sender, EventArgs e)
        {
            // is a customer selected?  one of customer's bikes?
            if (lstCustomers.SelectedIndex < 0)
            {
                MessageBox.Show("Please select a customer...");
                return;
            }
            if (lstCustomerBikes.SelectedIndex < 0)
            {
                MessageBox.Show("Please select a customer bike...");
                return;
            }

            // is a station selected?
            if (lstStations.SelectedIndex < 0)
            {
                MessageBox.Show("Please select a station...");
                return;
            }

            //
            // is there room at this station to dock the bike?
            //
            int capacity = Convert.ToInt32(txtStationCapacity.Text);
            int count = Convert.ToInt32(txtStationNumDocked.Text);

            if (count == capacity)
            {
                MessageBox.Show("Station is full, please select a different station...");
                return;
            }

            //
            // extract the customer id, station id, and bike id
            // from the selected items...
            //
            int cid = GetSelectedCustomerID();
            int sid = GetSelectedStationID();
            int bid = GetSelectedCustomerBikeID();

            // Multi-User Awareness:
            // Check if the station still has room
            string connectionInfo = getConnectionString();
            SqlConnection db = new SqlConnection(connectionInfo);

            string sql2 = string.Format(@"
                SELECT Capacity, BikeCount
                FROM Stations
                WHERE StationID={0}", sid);

            try
            {
                db.Open();

                SqlCommand cmd = new SqlCommand();
                cmd.Connection = db;
                SqlDataAdapter adapter = new SqlDataAdapter(cmd);
                DataSet ds = new DataSet();

                cmd.CommandText = sql2;
                adapter.Fill(ds);

                Debug.Assert(ds.Tables[0].Rows.Count == 1);
                DataRow station = ds.Tables[0].Rows[0];

                if (Convert.ToInt32(station["BikeCount"]) == Convert.ToInt32(station["Capacity"]))
                {
                    MessageBox.Show("The station you selected is full.");
                    int selectedStation = lstStations.SelectedIndex;

                    ClearStationUI();
                    lstStations.SelectedIndex = -1;  // re-select to update:
                    lstStations.SelectedIndex = selectedStation;
                    return;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("**Exception: '{0}'", ex.Message);
            }
            finally
            {
                db.Close();
            }

            // MessageBox.Show(cid.ToString() + ", " + sid.ToString() + ", " + bid.ToString());

            //
            // to checkin the bike, we need to:
            //
            //   1. update bike, setting station id to sid and
            //        customer id to NULL
            //   2. update station, increasing bike count
            //   3. update history record denoting checkin
            //
            DateTime curDateTime = DateTime.Now;
            
            string sql = string.Format(@"
                UPDATE Bikes
                SET    StationID = {3},
                       CustomerID = NULL
                WHERE  BikeID = {1};

                UPDATE  Stations
                SET     BikeCount = BikeCount + 1
                WHERE   StationID = {3};

                DECLARE @hid AS INTEGER;

                SELECT @hid = HistoryID
                FROM   History
                WHERE  CustomerID = {0} AND
                       BikeID = {1} AND
                       StationIDin IS NULL;

                UPDATE History
                SET CheckIn = '{2}', 
                    StationIDin = {3}
                WHERE HistoryID = @hid;
                ", cid, bid, curDateTime.ToString(), sid);

            SqlTransaction tx = null;
            try
            {
                db.Open();

                tx = db.BeginTransaction();
                SqlCommand cmd = new SqlCommand();
                cmd.Connection = db;
                cmd.CommandText = sql;
                cmd.Transaction = tx;

                cmd.ExecuteNonQuery();

                tx.Commit();

                //
                // success, update GUI for station and customer:
                //
                int selectedStation = lstStations.SelectedIndex;

                ClearStationUI();
                lstStations.SelectedIndex = -1;  // re-select to update:
                lstStations.SelectedIndex = selectedStation;

                int selectedCustomer = lstCustomers.SelectedIndex;

                ClearCustomerUI();
                lstCustomers.SelectedIndex = -1;  // re-select to update:
                lstCustomers.SelectedIndex = selectedCustomer;
            }
            catch (Exception ex)
            {
                tx.Rollback();
                MessageBox.Show(string.Format("**SQL: '{1}'\n**Exception: '{0}'", ex.Message, sql));
            }
            finally
            {
                db.Close();
            }
        }

        //
        // User wants to see Customer history:
        //
        private void cmdCustomerHistory_Click(object sender, EventArgs e)
        {
            // is a customer selected?
            if (lstCustomers.SelectedIndex < 0)
            {
                MessageBox.Show("Please select a customer...");
                return;
            }

            //
            // extract customer id and display their history:
            //
            int cid = GetSelectedCustomerID();

            string connectionInfo = getConnectionString();

            //
            // Get customer's history and display:
            //
            string sql = string.Format(@"
                SELECT *  
                FROM   History
                WHERE CustomerID = {0} AND
                      Checkin IS NOT NULL
                ORDER BY Checkin DESC;
                ", cid);

            SqlConnection db = new SqlConnection(connectionInfo);
            
            try
            {
                db.Open();

                SqlCommand cmd = new SqlCommand();
                cmd.Connection = db;
                SqlDataAdapter adapter = new SqlDataAdapter(cmd);
                DataSet ds = new DataSet();

                cmd.CommandText = sql;
                adapter.Fill(ds);

                FormHistory frm = new FormHistory();
                frm.Text = "History for customer " + lstCustomers.SelectedItem.ToString();

                frm.lstHistory.Items.Add("BikeID\tCheckout\t\t\tStation\tCheckin\t\t\tStation");

                foreach (DataRow row in ds.Tables["TABLE"].Rows)
                {
                    string msg = string.Format("{0}\t{1}\t{2}\t{3}\t{4}",
                      Convert.ToString(row["BikeID"]),
                      Convert.ToDateTime(row["Checkout"]).ToString(),
                      Convert.ToString(row["StationIDout"]),
                      Convert.ToDateTime(row["Checkin"]).ToString(),
                      Convert.ToString(row["StationIDin"]));

                    frm.lstHistory.Items.Add(msg);
                }

                //
                // show form modally so user must dismiss before
                // we continue:
                //
                frm.ShowDialog();
            }
            catch (Exception ex)
            {
                MessageBox.Show(string.Format("**SQL: '{1}'\n**Exception: '{0}'", ex.Message, sql));
            }
            finally
            {
                db.Close();
            }
        }

        //
        // Reset database back to its original state:
        //
        private void cmdReset_Click(object sender, EventArgs e)
        {
            var choice = MessageBox.Show("Do you really want to reset the database by reloading the original data?", "DivvyApp", MessageBoxButtons.YesNo);
            if (choice == DialogResult.No)
                return;

            this.Cursor = Cursors.WaitCursor;

            //
            // first let's clear the UI:
            //
            lstStations.Items.Clear();
            lstStations.Refresh();

            ClearStationUI();

            lstCustomers.Items.Clear();
            lstCustomers.Refresh();

            ClearCustomerUI();

            //
            // now reload the data into the DB:
            //
            string connectionInfo = getConnectionString();

            LoadCSV.ResetData(connectionInfo);

            //
            // now reload into the GUI:
            //
            Form1_Load(sender, e);

            this.Cursor = Cursors.Default;
        }

    }//class
}//namespace
