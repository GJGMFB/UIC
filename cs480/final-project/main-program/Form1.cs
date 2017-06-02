//
// Main application for Chicago Public Library Metrics Analyzer. Uses the online Azure Cloud DB.
//
// References: https://support.microsoft.com/en-us/kb/319401
//
// Dennis Aurelian Leancu
// U. of Illinois, Chicago
// CS480, Summer 2016
// Final Project
//

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace main_program {
	public partial class Form1 : Form {
		DateTime today;
		Business.Business data = new Business.Business();
		private ListViewColumnSorter lvwColumnSorter;

		public Form1() {
			InitializeComponent();

			// Doesn't properly fix HiDPI issues, but makes things fit a little better
			this.AutoSize = true;
			this.AutoSizeMode = AutoSizeMode.GrowAndShrink;

			// Setup listview control
			listView1.MultiSelect = false;

			// Create an instance of a ListView column sorter and assign it 
			// to the ListView control.
			lvwColumnSorter = new ListViewColumnSorter();
			listView1.ListViewItemSorter = lvwColumnSorter;

			// Set correct date
			dateTimePicker1.Value = DateTime.Today;

			// Get selected today's date
			today = dateTimePicker1.Value.Date;
			today = DateTime.Parse(today.Month + "/1/" + today.Year); // Only use first day of the month

			// Update WiFi and computer sessions
			updateSessions();

			// Update library list
			updateLibraries();
		}

		private void dateTimePicker1_ValueChanged(object sender, EventArgs e) {
			// Get selected today's date
			today = dateTimePicker1.Value.Date;
			today = DateTime.Parse(today.Month + "/1/" + today.Year); // Only use first day of the month

			// Update WiFi and computer sessions
			updateSessions();
		}

		private void updateSessions() {
			// Overall This Month
			Business.Sessions s = data.GetSessionsThisMonth(today);

			if (!string.IsNullOrEmpty(s.wifi)) {
				label4.Text = s.wifi;
			} else {
				label4.Text = "No data";
			}

			if (!string.IsNullOrEmpty(s.computer)) {
				label5.Text = s.computer;
			} else {
				label5.Text = "No data";
			}

			// Selected Library This Month
			if (listView1.SelectedItems.Count > 0)
				libMonthCompSess.Text = Convert.ToString(data.GetLibrarySessionsThisMonth(today, listView1.SelectedItems));
		}

		private void updateLibraries() {
			List<Business.Library> l = data.GetLibraries();

			// Clear ListView
			listView1.Clear();

			// Insert data to GUI
			listView1.View = View.Details;
			listView1.FullRowSelect = true;
			listView1.Columns.Add("lid", -1, HorizontalAlignment.Left); // Width of -1 means auto-size of longest data field
			listView1.Columns.Add("Library", -1, HorizontalAlignment.Left);
			listView1.Columns.Add("Total Visitors", -1, HorizontalAlignment.Left);

			foreach (Business.Library lib in l) {
				listView1.Items.Add(new ListViewItem(new[] {
					Convert.ToString(lib.lid),
					lib.name,
					Convert.ToString(lib.visitors)
				}));
			}

			// Auto-resize the column width based on data length
			listView1.AutoResizeColumns(ColumnHeaderAutoResizeStyle.ColumnContent);
			listView1.AutoResizeColumns(ColumnHeaderAutoResizeStyle.HeaderSize);
			listView1.Columns[0].Width = 0; // Hide lid column
		}

		private void updateLibrary(ListView.SelectedListViewItemCollection library) {
			Business.Library l = data.GetLibrary(Convert.ToInt32(library[0].SubItems[0].Text)); // Index 0 == lid

			listView1.Items[listView1.SelectedIndices[0]].SubItems[2].Text = Convert.ToString(l.visitors); // Index 2 == # of visitors
		}

		private void button1_Click(object sender, EventArgs e) {
			listView1.Focus(); // Giving focus back to list view reselects last selected item

			data.AddWifiSession(today);
			updateSessions();
		}

		private void button2_Click(object sender, EventArgs e) {
			if (listView1.SelectedItems.Count == 0) {
				MessageBox.Show("Please select a Library before starting a computer sessions");
				return;
			}

			listView1.Focus(); // Giving focus back to list view reselects last selected item

			data.AddComputerSession(today, listView1.SelectedItems);
			updateSessions();
		}

		private void btnVisit_Click(object sender, EventArgs e) {
			if (listView1.SelectedItems.Count == 0) {
				MessageBox.Show("Please select a Library to visit");
				return;
			}

			data.VisitLibrary(today, listView1.SelectedItems);
			updateLibrary(listView1.SelectedItems);
			listView1.Focus(); // Giving focus back to list view reselects last selected item
		}

		private void listView1_ColumnClick(object sender, ColumnClickEventArgs e) {
			// Determine if clicked column is already the column that is being sorted.
			if (e.Column == lvwColumnSorter.SortColumn) {
				// Reverse the current sort direction for this column.
				if (lvwColumnSorter.Order == SortOrder.Ascending) {
					lvwColumnSorter.Order = SortOrder.Descending;
				} else {
					lvwColumnSorter.Order = SortOrder.Ascending;
				}
			} else {
				// Set the column number that is to be sorted; default to ascending.
				lvwColumnSorter.SortColumn = e.Column;
				lvwColumnSorter.Order = SortOrder.Ascending;
			}

			// Perform the sort with these new sort options.
			this.listView1.Sort();
		}

		private void listView1_SelectedIndexChanged(object sender, EventArgs e) {
			// Update groupbox with library name. Changing items causes SelectedItems array to blank for a few milliseconds.
			try {
				groupBox2.Text = listView1.SelectedItems[0].SubItems[1].Text;
			} catch (Exception) {
				return;
			}

			// Show groupbox if hidden
			if (groupBox2.Visible == false)
				groupBox2.Visible = true;

			updateSessions();
		}
	}
}
