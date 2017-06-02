using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using DataAccess;
using System.Windows.Forms;

namespace Business {
	class Sessions {
		public string wifi;
		public string computer;

		public Sessions() {
			wifi = null;
			computer = null;
		}

		public Sessions(string wifi, string computer) {
			this.wifi = wifi;
			this.computer = computer;
		}
	}

	class Library {
		public readonly uint lid;
		public readonly string name;
		public readonly uint visitors;

		public Library(uint lid, string name, uint visitors) {
			this.lid = lid;
			this.name = name;
			this.visitors = visitors;
		}
	}

	class Business {
		Data _data;

		public Business() {
			// Azure SQL db info
			string NetID = "leancu2";
			string databasename = "final_project";
			string username = "leancu2";
			string pwd = "1Password";
			string remoteInfo = string.Format(@"
Server=tcp:{0}.database.windows.net,1433;Initial Catalog={1};Persist Security Info=False;User ID={2};Password={3};MultipleActiveResultSets=False;Encrypt=True;TrustServerCertificate=False;Connection Timeout=30;
", NetID, databasename, username, pwd);

			_data = new Data(remoteInfo);
		}

		// Returns list of libraries and corresponding total visitors
		public List<Library> GetLibraries() {
			var l = new List<Library>();

			string sql = string.Format(@"
				SELECT Libraries.lid, name, sum(visitors) as sum FROM Libraries INNER JOIN Visitors ON Libraries.lid = Visitors.lid GROUP BY Libraries.lid, name ORDER BY name;
			");

			DataSet ds = _data.ExecuteNonScaler(sql);

			foreach (DataRow r in ds.Tables[0].Rows) {
				l.Add(new Library(
					Convert.ToUInt32(r["lid"]),
					Convert.ToString(r["name"]),
					Convert.ToUInt32(r["sum"])
				));
			}

			return l;
		}

		public Library GetLibrary(int lid) {
			string sql = string.Format(@"
				SELECT Libraries.lid, name, sum(visitors) as sum FROM Libraries INNER JOIN Visitors ON LIbraries.lid = Visitors.lid WHERE Libraries.lid = {0} GROUP BY Libraries.lid, name;", lid);

			DataSet ds = _data.ExecuteNonScaler(sql);

			return new Library(
				Convert.ToUInt32(ds.Tables[0].Rows[0]["lid"]),
				Convert.ToString(ds.Tables[0].Rows[0]["name"]),
				Convert.ToUInt32(ds.Tables[0].Rows[0]["sum"])
			);
		}

		public void VisitLibrary(DateTime today, ListView.SelectedListViewItemCollection library) {
			string sql;
			SqlConnection db = null;
			SqlTransaction tx = null;
			int retryCount = 0;
			bool success = false;

			// Retry block 3 times before it quits.
			while (!success && retryCount < 3) {
				try {
					sql = string.Format(@"
						SELECT visitors FROM Visitors WHERE lid = {0} AND date = '{1}';",
						library[0].SubItems[0].Text, today.ToShortDateString());

					db = new SqlConnection(_data._connectionInfo);
					db.Open();

					tx = db.BeginTransaction(IsolationLevel.Serializable);

					SqlCommand cmd = new SqlCommand();

					cmd.Connection = db;
					cmd.Transaction = tx;
					cmd.CommandText = sql;

					object result = cmd.ExecuteScalar();

					// Date might not exist in db
					if (result == null || result == DBNull.Value) {
						// Create new
						sql = string.Format(@"
							INSERT INTO Visitors (lid, date, visitors) VALUES ({0}, '{1}', {2});",
							library[0].SubItems[0].Text, today.ToShortDateString(), 1);
					} else {
						uint visitors = Convert.ToUInt32(result);
						visitors++;

						sql = string.Format(@"
						UPDATE Visitors SET visitors = {0} WHERE lid = {1} AND date = '{2}';",
						visitors, library[0].SubItems[0].Text, today);
					}

					cmd.CommandText = sql;
					cmd.ExecuteNonQuery();

					tx.Commit();
					success = true;
				} catch (SqlException sql_ex) when (sql_ex.Number == 1205) {
					retryCount++;
					tx.Rollback();
					Console.WriteLine("Deadlocked. Total retries: " + retryCount);
				} catch (Exception ex) {
					string msg = string.Format("Error: '{0}'",
					  ex.Message);

					MessageBox.Show(msg);
					tx.Rollback();
					success = true; // Do not retry for other exceptions
				} finally {
					if (db != null && db.State == ConnectionState.Open)
						db.Close();
				}
			}
		}

		// Get computer sessions this month for selected library
		public uint GetLibrarySessionsThisMonth(DateTime today, ListView.SelectedListViewItemCollection library) {
			string sql = string.Format(@"
				SELECT sessions FROM ComputerSessions WHERE lid = {0} AND date = '{1}';",
				library[0].SubItems[0].Text, today.ToShortDateString());

			return Convert.ToUInt32(_data.ExecuteScaler(sql));
		}

		// Returns WiFi & computer sessions this month. [0] == wifi, [1] == computer sessions
		public Sessions GetSessionsThisMonth(DateTime today) {
			Sessions s = new Sessions();

			string sql = string.Format(@"
				select sessions from Wifi where date = '{0}';
			", today.ToShortDateString());
			s.wifi = Convert.ToString(_data.ExecuteScaler(sql));

			sql = string.Format(@"
				select sum(sessions) from ComputerSessions where date = '{0}';
			", today.ToShortDateString());
			s.computer = Convert.ToString(_data.ExecuteScaler(sql));

			return s;
		}

		// Adds 1 to number of WiFi sessions for the selected date
		public void AddWifiSession(DateTime today) {
			string sql;
			SqlConnection db = null;
			SqlTransaction tx = null;
			int retryCount = 0;
			bool success = false;

			// Retry block 3 times before it quits.
			while (!success && retryCount < 3) {
				try {
					sql = string.Format(@"
						SELECT sessions FROM Wifi WHERE date = '{0}';
					", today.ToShortDateString());

					db = new SqlConnection(_data._connectionInfo);
					db.Open();

					tx = db.BeginTransaction(IsolationLevel.Serializable);

					SqlCommand cmd = new SqlCommand();

					cmd.Connection = db;
					cmd.Transaction = tx;
					cmd.CommandText = sql;

					object result = cmd.ExecuteScalar();

					if (result == null || result == DBNull.Value) {
						// Create new
						sql = string.Format(@"
							INSERT INTO Wifi (date, sessions) VALUES ('{0}', {1})", today.ToShortDateString(), 1);
					} else {
						uint sessions = Convert.ToUInt32(result);
						sessions++;

						sql = string.Format(@"
							UPDATE Wifi SET sessions = {0} WHERE date = '{1}';
						", sessions, today.ToShortDateString());
					}
					
					cmd.CommandText = sql;
					cmd.ExecuteNonQuery();

					tx.Commit();
					success = true;
				} catch (SqlException sql_ex) when (sql_ex.Number == 1205) {
					retryCount++;
					tx.Rollback();
					Console.WriteLine("Deadlocked. Total retries: " + retryCount);
				} catch (Exception ex) {
					string msg = string.Format("Error: '{0}'",
					  ex.Message);

					MessageBox.Show(msg);
					tx.Rollback();
					success = true; // Do not retry for other exceptions
				} finally {
					if (db != null && db.State == ConnectionState.Open)
						db.Close();
				}
			}
		}

		// Adds 1 to number of computer sessions for the selected date and library
		public void AddComputerSession(DateTime today, ListView.SelectedListViewItemCollection library) {
			string sql;
			SqlConnection db = null;
			SqlTransaction tx = null;
			int retryCount = 0;
			bool success = false;

			// Retry block 3 times before it quits.
			while (!success && retryCount < 3) {
				try {
					sql = string.Format(@"
						SELECT sessions FROM ComputerSessions WHERE date = '{0}' AND lid = {1};
					", today.ToShortDateString(), library[0].SubItems[0].Text);

					db = new SqlConnection(_data._connectionInfo);
					db.Open();

					tx = db.BeginTransaction(IsolationLevel.Serializable);

					SqlCommand cmd = new SqlCommand();

					cmd.Connection = db;
					cmd.Transaction = tx;
					cmd.CommandText = sql;

					object result = cmd.ExecuteScalar();

					if (result == null || result == DBNull.Value) {
						// Create new
						sql = string.Format(@"
							INSERT INTO ComputerSessions (date, sessions, lid) VALUES ('{0}', {1}, {2})",
							today.ToShortDateString(), 1, library[0].SubItems[0].Text);
					} else {
						// Update
						uint sessions = Convert.ToUInt32(result);
						sessions++;

						sql = string.Format(@"
							UPDATE ComputerSessions SET sessions = {0} WHERE date = '{1}' AND lid = {2};",
							sessions, today.ToShortDateString(), library[0].SubItems[0].Text);
					}

					cmd.CommandText = sql;
					cmd.ExecuteNonQuery();

					tx.Commit();
					success = true;
				} catch (SqlException sql_ex) when (sql_ex.Number == 1205) {
					retryCount++;
					tx.Rollback();
					Console.WriteLine("Deadlocked. Total retries: " + retryCount);
				} catch (Exception ex) {
					string msg = string.Format("Error: '{0}'",
					  ex.Message);

					MessageBox.Show(msg);
					tx.Rollback();
					success = true; // Do not retry for other exceptions
				} finally {
					if (db != null && db.State == ConnectionState.Open)
						db.Close();
				}
			}
		}
	}
}
