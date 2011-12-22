/*******************************************************************************
 * Copyright (c) 2009 Ale46.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
/*
 * 
 * This code is not tested accurately. The main problems are related to:
 * 
 * 1. The method that check if a proxy is alive. 
 * 2. The method that test the anon level is only theorical so don't give it too importance.
 * 
 * To set all the "geo-information" is used the maxmindgeoip api.
 * 
 * 
 */


import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.JFrame;


import java.awt.Rectangle;


import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;


import java.io.BufferedWriter;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.JToolBar;
import javax.swing.JLabel;
import javax.swing.JDialog;
import java.awt.Dimension;
import javax.swing.JTextField;
import java.awt.Point;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;


public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private JButton cmdAdd = null;
	private JDialog addProxy = null;
	private ImageIcon check = new ImageIcon(getClass().getResource("/check.png"));  //  @jve:decl-index=0:
	private ImageIcon uncheck = new ImageIcon(getClass().getResource("/cross.png"));  //  @jve:decl-index=0:
	private ImageIcon mark = new ImageIcon(getClass().getResource("/mark.png"));  //  @jve:decl-index=0:
	private ArrayList<Proxy> loadedProxy = new ArrayList<Proxy>();  //  @jve:decl-index=0:
	private CheckProxy chkproxy = new CheckProxy();  //  @jve:decl-index=0:
	ProxyEngine p = new ProxyEngine();  //  @jve:decl-index=0:
	private Object[] columnNames =  { "Status","IP", "Port", "Hostname", "City", "Country", "Delay", "Anon Level"};

	private DefaultTableModel model = new DefaultTableModel(null ,columnNames){

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		@Override
		public boolean isCellEditable(int row, int col) {
			//Note that the data/cell address is constant,
			//no matter where the cell appears onscreen.
			if (col == 1 || col == 2) {
				return true;
			} else {
				return false;
			}
		}

		// Returning the Class of each column will allow different
		// renderers to be used based on Class
		@Override
		public Class<?> getColumnClass(int column)
		{
			if (getColumnName(column).equals("Status") || getColumnName(column).equals("Country") ) {
				return new ImageIcon().getClass();
			}
			return new Object().getClass();
		}

	};
	private JButton cmdDel = null;
	private JToolBar jToolBar = null;
	private JButton cmdAddT = null;
	private JButton cmdStart = null;
	private JLabel lblDebug = null;
	private JButton cmdStop = null;
	private JButton cmdUrl = null;
	private JButton cmdExport = null;
	private JDialog jDialog = null;  //  @jve:decl-index=0:visual-constraint="786,70"
	private JPanel jContentPane1 = null;
	private JTextField txtPort = null;
	private JButton cmdAddProxy = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JTextField txtA = null;
	private JTextField txtB = null;
	private JTextField txtC = null;
	private JTextField txtD = null;
	private JButton cmdPause = null;
	private JLabel jLabel2 = null;


	class CheckProxy extends SwingWorker<Void, Void> {
		private boolean pause=false;


		public void setPause(boolean pause) {

			this.pause = pause;


		}

		public boolean isPause() {

			return this.pause;

		}







		@Override
		protected Void doInBackground() throws Exception {

			for (int i = 0;i<loadedProxy.size();i++) {

				if (isCancelled()) {
					lblDebug.setText("Cancelled");
					cmdStop.setEnabled(false);
					break;
				}

				if (pause == true) {

					synchronized(this) {
						lblDebug.setText("Paused..");
						this.wait();
						pause = false;
					}
				}


				lblDebug.setText("Checking "+loadedProxy.get(i).getIp()+":"+loadedProxy.get(i).getPort() + " (" + (((i+1)* loadedProxy.size())/100)+"% )");
				jTable.scrollRectToVisible(jTable.getCellRect(i+1, 0, true));
				jScrollPane.scrollRectToVisible(jTable.getCellRect(i, 0, true));
				jTable.setRowSelectionInterval(i,i);
				try {
					if (loadedProxy.get(i).isAlive()) {
						String hostname = loadedProxy.get(i).getHostName();
						String city  = loadedProxy.get(i).getCity();
						String country = loadedProxy.get(i).getCountry();
						double delay = loadedProxy.get(i).getDelay();
						String icon = "/png/"+country+".png";
						String anon = loadedProxy.get(i).getAnonLevel();
						jTable.setValueAt(check, i, 0);
						jTable.setValueAt(hostname, i, 3);
						jTable.setValueAt(city, i, 4);
						jTable.setValueAt(new ImageIcon(getClass().getResource(icon.toLowerCase())), i, 5);
						jTable.setValueAt(delay, i, 6);
						jTable.setValueAt(anon, i, 7);
					}else {
						jTable.setValueAt(uncheck, i, 0);	
					}
					
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}



			}


			return null;
		}
		@Override
		protected void done() {
			if (!isCancelled()) {
				lblDebug.setText("Completed");
				cmdStop.setEnabled(false);
			}
		}


	}


	private void addRow(Object[] vect) {
		String ip = (String) vect[1];
		String port = (String) vect[2];
		try {
			loadedProxy.add(new Proxy(ip,port));
		} catch (UnknownHostException e1) {
			
			lblDebug.setText(ip+":"+port+"-> "+e1.getMessage());
		}
		model.addRow(vect);   
	}

	private void addRows(ArrayList<Proxy> proxylist) {
		for (int i = 0;i<proxylist.size();i++) {
			String ip = proxylist.get(i).getIp();
			String port = proxylist.get(i).getPort();
			Object[] data =  new Object[]{  mark,ip,port } ;
			addRow(data);
		}
	}

	private String getFilename() {
		File file;
		String filename;
		JFileChooser fc = new JFileChooser();


		int rc = fc.showDialog(null, "Select file");

		if (rc == JFileChooser.APPROVE_OPTION){

			file = fc.getSelectedFile();

			filename = file.getAbsolutePath();
			return filename;

		}else {
			return null;
		}
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable(model){
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public TableCellRenderer getCellRenderer( int row, int col ) {
					TableCellRenderer renderer = super.getCellRenderer(row,col);
					if ( (col == 1 )|| (col == 2) )
						((JLabel)renderer).setHorizontalAlignment( SwingConstants.LEFT);
					
					else
						((JLabel)renderer).setHorizontalAlignment( SwingConstants.CENTER );
					return renderer;
				}
			};

			jTable.setShowGrid(true);
			jTable.getColumnModel().getColumn(0).setPreferredWidth(45);
			jTable.getColumnModel().getColumn(1).setPreferredWidth(120);
			jTable.getColumnModel().getColumn(2).setPreferredWidth(55);
			jTable.getColumnModel().getColumn(3).setPreferredWidth(180);
			jTable.getColumnModel().getColumn(4).setPreferredWidth(130);
			jTable.getColumnModel().getColumn(5).setPreferredWidth(50);
			jTable.getColumnModel().getColumn(6).setPreferredWidth(55);

		}

		return jTable;
	}

	/**
	 * This method initializes cmdAdd	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCmdAdd() {
		if (cmdAdd == null) {
			cmdAdd = new JButton();
			cmdAdd.setText("");
			cmdAdd.setToolTipText("Add single proxy");
			cmdAdd.setIcon(new ImageIcon(getClass().getResource("/add.png")));
			cmdAdd.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addProxy = getJDialog();
					addProxy.pack();
					addProxy.setSize(new Dimension(231, 144));
					addProxy.setLocationRelativeTo(jContentPane);
					addProxy.setVisible(true);
				}

			});

		}
		return cmdAdd;
	}

	/**
	 * This method initializes cmdDel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCmdDel() {
		if (cmdDel == null) {
			cmdDel = new JButton();
			cmdDel.setText("");
			cmdDel.setToolTipText("Delete selected proxy");
			cmdDel.setIcon(new ImageIcon(getClass().getResource("/delete.png")));
			cmdDel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int numRows = jTable.getSelectedRows().length;
					for(int i=0; i<numRows ; i++ ) model.removeRow(jTable.getSelectedRow());
				}
			});
		}
		return cmdDel;
	}

	/**
	 * This method initializes jToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getJToolBar() {
		if (jToolBar == null) {
			jToolBar = new JToolBar();
			jToolBar.setFloatable(false);
			jToolBar.add(getCmdAdd());
			jToolBar.add(getCmdDel());
			jToolBar.add(getCmdAddT());
			jToolBar.add(getCmdUrl());
			jToolBar.add(getCmdStart());
			jToolBar.add(getCmdPause());
			jToolBar.add(getCmdStop());
			jToolBar.add(getCmdExport());
		}
		return jToolBar;
	}

	/**
	 * This method initializes cmdAddT	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCmdAddT() {
		if (cmdAddT == null) {
			cmdAddT = new JButton();
			cmdAddT.setText("");
			cmdAddT.setActionCommand("LoadF");
			cmdAddT.setToolTipText("Import proxy list from file. The list should be ip:port or ip port.");
			cmdAddT.setIcon(new ImageIcon(getClass().getResource("/Import-48.png")));
			cmdAddT.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String filename = getFilename();

					if (filename != null)
						addRows( p.getProxyFromFile(filename) );



				}
			});
		}
		return cmdAddT;
	}

	/**
	 * This method initializes cmdStart	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCmdStart() {
		if (cmdStart == null) {
			cmdStart = new JButton();
			cmdStart.setText("");

			cmdStart.setToolTipText("Start checking the status of all proxy.");
			cmdStart.setIcon(new ImageIcon(getClass().getResource("/start.png")));
			cmdStart.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					
					cmdPause.setEnabled(true);	
					cmdStart.setEnabled(false);

					if (chkproxy.isPause()==true) {
						synchronized(chkproxy) {
							chkproxy.notify();
						}
					}else {
						chkproxy.execute();
						
						
					}
					cmdStop.setEnabled(true);



				}
			});
		}
		return cmdStart;
	}


	/**
	 * This method initializes cmdStop	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCmdStop() {
		if (cmdStop == null) {
			cmdStop = new JButton();
			cmdStop.setText("");
			cmdStop.setIcon(new ImageIcon(getClass().getResource("/Stop-48.png")));
			cmdStop.setToolTipText("Stop all current operation.");
			cmdStop.setEnabled(false);
			cmdStop.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					chkproxy.cancel(true);
					cmdStart.setEnabled(true);
					cmdPause.setEnabled(false);


				}
			});
		}
		return cmdStop;
	}


	/**
	 * This method initializes cmdUrl	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCmdUrl() {
		if (cmdUrl == null) {
			cmdUrl = new JButton();
			cmdUrl.setText("");
			cmdUrl.setToolTipText("Import proxy list from remote file. List should be in ip:port or ip port format.");
			cmdUrl.setIcon(new ImageIcon(getClass().getResource("/remote.png")));
			cmdUrl.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String url = JOptionPane.showInputDialog(null,
							"Import from remote file",
							"Enter the remote url of your proxy list",
							JOptionPane.QUESTION_MESSAGE);
					if (url!=null)
						addRows(p.getProxyFromUrl(url));


				}
			});
		}
		return cmdUrl;
	}


	/**
	 * This method initializes cmdExport	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCmdExport() {
		if (cmdExport == null) {
			cmdExport = new JButton();
			cmdExport.setIcon(new ImageIcon(getClass().getResource("/Export-48.png")));
			cmdExport.setToolTipText("Export current alive proxy.");
			cmdExport.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String filename = getFilename();

					FileWriter fstream = null;
					try {
						fstream = new FileWriter(filename);
					} catch (IOException e2) {
					
						lblDebug.setText(e2.getMessage());
					}
					BufferedWriter out = new BufferedWriter(fstream);

					for (int i = 1;i<jTable.getRowCount();i++) {
						if (jTable.getModel().getValueAt(i, 0).toString().contains("check.png")) {
							String toWrite = ((String)jTable.getModel().getValueAt(i, 1)+":"+jTable.getModel().getValueAt(i, 2));
							try {
								out.write(toWrite);
								out.newLine();


							} catch (IOException e1) {
								
								lblDebug.setText(e1.getMessage());
							}
						}

					}
					try {
						out.close();
					} catch (IOException e1) {
						
						lblDebug.setText(e1.getMessage());
					}
					lblDebug.setText(filename+ " Exported");

				}
			});
		}
		return cmdExport;
	}

	/**
	 * This method initializes jDialog	
	 * 	
	 * @return javax.swing.JDialog	
	 */
	private JDialog getJDialog() {
		if (jDialog == null) {
			jDialog = new JDialog(this);
			jDialog.setSize(new Dimension(227, 154));
			jDialog.setTitle("Add Proxy");

			jDialog.setContentPane(getJContentPane1());

		}
		return jDialog;
	}

	/**
	 * This method initializes jContentPane1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPane1() {
		if (jContentPane1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(161, 29, 36, 13));
			jLabel1.setText("PORT");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(8, 25, 44, 16));
			jLabel.setText("IP");
			jContentPane1 = new JPanel();
			jContentPane1.setLayout(null);
			jContentPane1.add(getTxtPort(), null);
			jContentPane1.add(getCmdAddProxy(), null);
			jContentPane1.add(jLabel, null);
			jContentPane1.add(jLabel1, null);
			jContentPane1.add(getTxtA(), null);
			jContentPane1.add(getTxtB(), null);
			jContentPane1.add(getTxtC(), null);
			jContentPane1.add(getTxtD(), null);
		}
		return jContentPane1;
	}

	/**
	 * This method initializes txtPort	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtPort() {
		if (txtPort == null) {
			txtPort = new JTextField();
			txtPort.setLocation(new Point(162, 43));
			txtPort.setSize(new Dimension(42, 20));
		}
		return txtPort;
	}

	/**
	 * This method initializes cmdAddProxy	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCmdAddProxy() {
		if (cmdAddProxy == null) {
			cmdAddProxy = new JButton();
			cmdAddProxy.setBounds(new Rectangle(85, 79, 57, 22));
			cmdAddProxy.setText("Add");
			cmdAddProxy.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String ip = txtA.getText()+"."+txtB.getText()+"."+txtC.getText()+"."+txtD.getText();
					Object[] data = {mark, ip, txtPort.getText(),"",""};
					addRow(data);
					addProxy.setVisible(false);

				}
			});
		}
		return cmdAddProxy;
	}

	/**
	 * This method initializes txtA	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtA() {
		if (txtA == null) {
			txtA = new JTextField();
			txtA.setLocation(new Point(7, 43));
			txtA.setSize(new Dimension(32, 20));
		}
		return txtA;
	}

	/**
	 * This method initializes txtB	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtB() {
		if (txtB == null) {
			txtB = new JTextField();
			txtB.setBounds(new Rectangle(45, 43, 32, 20));
		}
		return txtB;
	}

	/**
	 * This method initializes txtC	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtC() {
		if (txtC == null) {
			txtC = new JTextField();
			txtC.setLocation(new Point(81, 43));
			txtC.setSize(new Dimension(32, 20));
		}
		return txtC;
	}

	/**
	 * This method initializes txtD	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtD() {
		if (txtD == null) {
			txtD = new JTextField();
			txtD.setLocation(new Point(117, 43));
			txtD.setSize(new Dimension(32, 20));
		}
		return txtD;
	}

	/**
	 * This method initializes cmdPause	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCmdPause() {
		if (cmdPause == null) {
			cmdPause = new JButton();
			cmdPause.setIcon(new ImageIcon(getClass().getResource("/Pause-48.png")));
			cmdPause.setToolTipText("Give you a pause.");
			cmdPause.setEnabled(false);
			cmdPause.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					chkproxy.setPause(true);
					cmdStart.setEnabled(true);
					cmdStop.setEnabled(false);
					cmdPause.setEnabled(false);


				}
			});
		}
		return cmdPause;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		} catch (InstantiationException e) {
			
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Main thisClass = new Main();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public Main() {
		super();
		initialize();

	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(733, 427);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Note: the anon level is defined visiting a php script with the current testing proxy, this can take some time.");
			lblDebug = new JLabel();
			lblDebug.setText("");
			jContentPane = new JPanel();
			GroupLayout gl_jContentPane = new GroupLayout(jContentPane);
			gl_jContentPane.setHorizontalGroup(
				gl_jContentPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_jContentPane.createSequentialGroup()
						.addGap(1)
						.addGroup(gl_jContentPane.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_jContentPane.createSequentialGroup()
								.addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 606, GroupLayout.PREFERRED_SIZE)
								.addContainerGap())
							.addGroup(gl_jContentPane.createSequentialGroup()
								.addComponent(getJToolBar(), GroupLayout.DEFAULT_SIZE, 1383, Short.MAX_VALUE)
								.addGap(10))))
					.addGroup(gl_jContentPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblDebug, GroupLayout.PREFERRED_SIZE, 676, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(28, Short.MAX_VALUE))
					.addComponent(getJScrollPane(), GroupLayout.DEFAULT_SIZE, 717, Short.MAX_VALUE)
			);
			gl_jContentPane.setVerticalGroup(
				gl_jContentPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_jContentPane.createSequentialGroup()
						.addGap(1)
						.addComponent(getJToolBar(), GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(getJScrollPane(), GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
						.addGap(19)
						.addComponent(lblDebug, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
			);
			jContentPane.setLayout(gl_jContentPane);
		}
		return jContentPane;
	}



}  //  @jve:decl-index=0:visual-constraint="10,10"
