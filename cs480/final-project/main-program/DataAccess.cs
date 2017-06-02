using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DataAccess {
	class Data {
		public string _connectionInfo;

		public Data(string connectionInfo) {
			_connectionInfo = connectionInfo;
		}

		public DataSet ExecuteNonScaler(string sql) {
			SqlConnection db = null;

			try {
				db = new SqlConnection(_connectionInfo);
				db.Open();

				SqlCommand cmd = new SqlCommand();
				cmd.Connection = db;

				SqlDataAdapter adapter = new SqlDataAdapter(cmd);
				DataSet ds = new DataSet();

				cmd.CommandText = sql;
				adapter.Fill(ds);

				return ds;
			} catch {
				throw;
			} finally {
				if (db != null && db.State == ConnectionState.Open) {
					db.Close();
				}
			}
		}

		public object ExecuteScaler(string sql) {
			SqlConnection db = null;
			
			try {
				db = new SqlConnection(_connectionInfo);
				db.Open();

				SqlCommand cmd = new SqlCommand();
				cmd.Connection = db;
				cmd.CommandText = sql;

				return cmd.ExecuteScalar();
			} catch {
				throw;
			} finally {
				if (db != null && db.State == ConnectionState.Open) {
					db.Close();
				}
			}
		}
	}
}
