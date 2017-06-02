namespace main_program {
	partial class Form1 {
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.IContainer components = null;

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		/// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
		protected override void Dispose(bool disposing) {
			if (disposing && (components != null)) {
				components.Dispose();
			}
			base.Dispose(disposing);
		}

		#region Windows Form Designer generated code

		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent() {
			this.dateTimePicker1 = new System.Windows.Forms.DateTimePicker();
			this.label2 = new System.Windows.Forms.Label();
			this.btnWifiSession = new System.Windows.Forms.Button();
			this.btnComputerSession = new System.Windows.Forms.Button();
			this.listView1 = new System.Windows.Forms.ListView();
			this.btnVisit = new System.Windows.Forms.Button();
			this.groupBox1 = new System.Windows.Forms.GroupBox();
			this.label5 = new System.Windows.Forms.Label();
			this.label4 = new System.Windows.Forms.Label();
			this.label3 = new System.Windows.Forms.Label();
			this.label1 = new System.Windows.Forms.Label();
			this.groupBox2 = new System.Windows.Forms.GroupBox();
			this.libMonthCompSess = new System.Windows.Forms.Label();
			this.label6 = new System.Windows.Forms.Label();
			this.pictureBox1 = new System.Windows.Forms.PictureBox();
			this.groupBox1.SuspendLayout();
			this.groupBox2.SuspendLayout();
			((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
			this.SuspendLayout();
			// 
			// dateTimePicker1
			// 
			this.dateTimePicker1.Format = System.Windows.Forms.DateTimePickerFormat.Short;
			this.dateTimePicker1.Location = new System.Drawing.Point(358, 40);
			this.dateTimePicker1.Name = "dateTimePicker1";
			this.dateTimePicker1.Size = new System.Drawing.Size(200, 31);
			this.dateTimePicker1.TabIndex = 0;
			this.dateTimePicker1.Value = new System.DateTime(2016, 8, 2, 19, 59, 3, 0);
			this.dateTimePicker1.ValueChanged += new System.EventHandler(this.dateTimePicker1_ValueChanged);
			// 
			// label2
			// 
			this.label2.AutoSize = true;
			this.label2.Font = new System.Drawing.Font("Arial", 7.875F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
			this.label2.Location = new System.Drawing.Point(389, 12);
			this.label2.Name = "label2";
			this.label2.Size = new System.Drawing.Size(132, 24);
			this.label2.TabIndex = 2;
			this.label2.Text = "Today\'s Date";
			// 
			// btnWifiSession
			// 
			this.btnWifiSession.Font = new System.Drawing.Font("Arial", 7.875F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
			this.btnWifiSession.Location = new System.Drawing.Point(12, 85);
			this.btnWifiSession.Name = "btnWifiSession";
			this.btnWifiSession.Size = new System.Drawing.Size(189, 70);
			this.btnWifiSession.TabIndex = 6;
			this.btnWifiSession.Text = "Start WiFi Session";
			this.btnWifiSession.UseVisualStyleBackColor = true;
			this.btnWifiSession.Click += new System.EventHandler(this.button1_Click);
			// 
			// btnComputerSession
			// 
			this.btnComputerSession.Font = new System.Drawing.Font("Arial", 7.875F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
			this.btnComputerSession.Location = new System.Drawing.Point(12, 167);
			this.btnComputerSession.Name = "btnComputerSession";
			this.btnComputerSession.Size = new System.Drawing.Size(189, 70);
			this.btnComputerSession.TabIndex = 7;
			this.btnComputerSession.Text = "Start Computer Session";
			this.btnComputerSession.UseVisualStyleBackColor = true;
			this.btnComputerSession.Click += new System.EventHandler(this.button2_Click);
			// 
			// listView1
			// 
			this.listView1.Location = new System.Drawing.Point(207, 85);
			this.listView1.Name = "listView1";
			this.listView1.Size = new System.Drawing.Size(506, 710);
			this.listView1.TabIndex = 8;
			this.listView1.UseCompatibleStateImageBehavior = false;
			this.listView1.View = System.Windows.Forms.View.Details;
			this.listView1.ColumnClick += new System.Windows.Forms.ColumnClickEventHandler(this.listView1_ColumnClick);
			this.listView1.SelectedIndexChanged += new System.EventHandler(this.listView1_SelectedIndexChanged);
			// 
			// btnVisit
			// 
			this.btnVisit.Font = new System.Drawing.Font("Arial", 7.875F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
			this.btnVisit.Location = new System.Drawing.Point(358, 801);
			this.btnVisit.Name = "btnVisit";
			this.btnVisit.Size = new System.Drawing.Size(189, 70);
			this.btnVisit.TabIndex = 9;
			this.btnVisit.Text = "Visit Library";
			this.btnVisit.UseVisualStyleBackColor = true;
			this.btnVisit.Click += new System.EventHandler(this.btnVisit_Click);
			// 
			// groupBox1
			// 
			this.groupBox1.Controls.Add(this.label5);
			this.groupBox1.Controls.Add(this.label4);
			this.groupBox1.Controls.Add(this.label3);
			this.groupBox1.Controls.Add(this.label1);
			this.groupBox1.Location = new System.Drawing.Point(719, 85);
			this.groupBox1.Name = "groupBox1";
			this.groupBox1.Size = new System.Drawing.Size(331, 169);
			this.groupBox1.TabIndex = 11;
			this.groupBox1.TabStop = false;
			this.groupBox1.Text = "Overall This Month";
			// 
			// label5
			// 
			this.label5.AutoSize = true;
			this.label5.Font = new System.Drawing.Font("Arial", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
			this.label5.Location = new System.Drawing.Point(4, 127);
			this.label5.Name = "label5";
			this.label5.Size = new System.Drawing.Size(30, 32);
			this.label5.TabIndex = 13;
			this.label5.Text = "0";
			// 
			// label4
			// 
			this.label4.AutoSize = true;
			this.label4.Font = new System.Drawing.Font("Arial", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
			this.label4.Location = new System.Drawing.Point(4, 55);
			this.label4.Name = "label4";
			this.label4.Size = new System.Drawing.Size(30, 32);
			this.label4.TabIndex = 12;
			this.label4.Text = "0";
			// 
			// label3
			// 
			this.label3.AutoSize = true;
			this.label3.Font = new System.Drawing.Font("Arial", 7.875F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
			this.label3.Location = new System.Drawing.Point(5, 103);
			this.label3.Name = "label3";
			this.label3.Size = new System.Drawing.Size(193, 24);
			this.label3.TabIndex = 11;
			this.label3.Text = "Computer Sessions";
			// 
			// label1
			// 
			this.label1.AutoSize = true;
			this.label1.Font = new System.Drawing.Font("Arial", 7.875F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
			this.label1.Location = new System.Drawing.Point(3, 31);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(147, 24);
			this.label1.TabIndex = 10;
			this.label1.Text = "WiFi Sessions";
			// 
			// groupBox2
			// 
			this.groupBox2.Controls.Add(this.libMonthCompSess);
			this.groupBox2.Controls.Add(this.label6);
			this.groupBox2.Location = new System.Drawing.Point(719, 271);
			this.groupBox2.Name = "groupBox2";
			this.groupBox2.Size = new System.Drawing.Size(331, 98);
			this.groupBox2.TabIndex = 15;
			this.groupBox2.TabStop = false;
			this.groupBox2.Text = "Library Comp. Sess.";
			this.groupBox2.Visible = false;
			// 
			// libMonthCompSess
			// 
			this.libMonthCompSess.AutoSize = true;
			this.libMonthCompSess.Font = new System.Drawing.Font("Arial", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
			this.libMonthCompSess.Location = new System.Drawing.Point(6, 52);
			this.libMonthCompSess.Name = "libMonthCompSess";
			this.libMonthCompSess.Size = new System.Drawing.Size(30, 32);
			this.libMonthCompSess.TabIndex = 14;
			this.libMonthCompSess.Text = "0";
			// 
			// label6
			// 
			this.label6.AutoSize = true;
			this.label6.Font = new System.Drawing.Font("Arial", 7.875F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
			this.label6.Location = new System.Drawing.Point(7, 28);
			this.label6.Name = "label6";
			this.label6.Size = new System.Drawing.Size(301, 24);
			this.label6.TabIndex = 12;
			this.label6.Text = "Computer Sessions This Month";
			// 
			// pictureBox1
			// 
			this.pictureBox1.Image = global::main_program.Properties.Resources.chicago_public_library_logo;
			this.pictureBox1.Location = new System.Drawing.Point(780, 486);
			this.pictureBox1.Name = "pictureBox1";
			this.pictureBox1.Size = new System.Drawing.Size(200, 200);
			this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.AutoSize;
			this.pictureBox1.TabIndex = 16;
			this.pictureBox1.TabStop = false;
			// 
			// Form1
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(12F, 25F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(1062, 925);
			this.Controls.Add(this.pictureBox1);
			this.Controls.Add(this.groupBox2);
			this.Controls.Add(this.groupBox1);
			this.Controls.Add(this.btnVisit);
			this.Controls.Add(this.listView1);
			this.Controls.Add(this.btnComputerSession);
			this.Controls.Add(this.btnWifiSession);
			this.Controls.Add(this.label2);
			this.Controls.Add(this.dateTimePicker1);
			this.Name = "Form1";
			this.Text = "Chicago Public Libraries";
			this.groupBox1.ResumeLayout(false);
			this.groupBox1.PerformLayout();
			this.groupBox2.ResumeLayout(false);
			this.groupBox2.PerformLayout();
			((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
			this.ResumeLayout(false);
			this.PerformLayout();

		}

		#endregion

		private System.Windows.Forms.DateTimePicker dateTimePicker1;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.Button btnWifiSession;
		private System.Windows.Forms.Button btnComputerSession;
		private System.Windows.Forms.ListView listView1;
		private System.Windows.Forms.Button btnVisit;
		private System.Windows.Forms.GroupBox groupBox1;
		private System.Windows.Forms.Label label5;
		private System.Windows.Forms.Label label4;
		private System.Windows.Forms.Label label3;
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.GroupBox groupBox2;
		private System.Windows.Forms.Label libMonthCompSess;
		private System.Windows.Forms.Label label6;
		private System.Windows.Forms.PictureBox pictureBox1;
	}
}

