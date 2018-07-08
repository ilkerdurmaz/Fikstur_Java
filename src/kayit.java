import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class kayit extends JFrame implements ActionListener{
	Connection conn=null; 
	JComboBox<String> jcb[];
	JTextField jtf[];
	DefaultListModel<Integer> dlm_id;
	public kayit()
	{
		baglan();
		this.setSize(400,185);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setTitle("KAR�ILA�MA EKLEME");
		this.setLayout(new FlowLayout());
		
		jcb=new JComboBox[2];
		jtf=new JTextField[2];
		
		for(int i=0;i<2;i++)
		{
			JPanel jpn=new JPanel();
			jpn.setPreferredSize(new Dimension(400, 35));
			
			JLabel jlab=new JLabel("Tak�m "+(i+1)+":");
			jlab.setPreferredSize(new Dimension(50, 25));
			JLabel jlab1=new JLabel("Tak�m "+(i+1)+" Skoru:");
			jlab1.setPreferredSize(new Dimension(85, 25));
			
			jcb[i]=new JComboBox<String>();
			jcb[i].setPreferredSize(new Dimension(200, 25));
			jtf[i]=new JTextField();
			jtf[i].setPreferredSize(new Dimension(25, 25));
		
			jpn.add(jlab);
			jpn.add(jcb[i]);
			jpn.add(jlab1);
			jpn.add(jtf[i]);
			this.add(jpn);
		}
		comboDoldur();
		JButton jbtn=new JButton("KAR�ILA�MA EKLE");
		jbtn.setPreferredSize(new Dimension(375, 50));
		jbtn.addActionListener(this);
		this.add(jbtn);
		
		this.setVisible(true);
	}
	
	public void baglan()
	{
		try {
			Class.forName("org.sqlite.JDBC");
			conn =DriverManager.getConnection("jdbc:sqlite:odev2.db");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this,"BA�LANTI BA�ARISIZ");
		}
	}
	
	public void comboDoldur()
	{
		try 
		{
			String sql="select id,adi from takimlar";
			PreparedStatement ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			dlm_id=new DefaultListModel<Integer>();
			while(rs.next())
			{
				jcb[0].addItem(rs.getString("adi"));
				jcb[1].addItem(rs.getString("adi"));
				dlm_id.addElement(rs.getInt("id"));
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,"DOLDURMA BA�ARISIZ");
		}
	}
	
	public void karsilasmaEkle()
	{
		try
		{
			if(jtf[0].getText().length()>0&&jtf[1].getText().length()>0)
			{
				if(jcb[0].getSelectedIndex()!=jcb[1].getSelectedIndex())
				{
						try //DAHA �NCE AYNI KAR�ILA�MA EKLENM�� �SE BU SQL SORGUSU POZ�T�F B�R ID D�ND�RECEKT�R,
						{	//EGER DAHA �NCE EKLENMEM�� KAR�ILA�MA �SE, EXCEPTION �RETECEKT�R, BU DURUMDA VER�LER� DB'YE YAZACAKTIR.
							String kontrol="select id from skorlar where takim1=? and takim2=?";
							PreparedStatement ps = conn.prepareStatement(kontrol);
							ps.setInt(1, dlm_id.getElementAt(jcb[0].getSelectedIndex()));
							ps.setInt(2, dlm_id.getElementAt(jcb[1].getSelectedIndex()));
							ResultSet rs=ps.executeQuery();
							if(rs.getInt("id")>-1)
							JOptionPane.showMessageDialog(this,"BU KAR�ILA�MA DAHA �NCE EKLEND�!");
						} 
						catch (Exception e) {
							String sql="insert into skorlar(takim1,takim2,skor1,skor2) values(?,?,?,?)";
							PreparedStatement ps = conn.prepareStatement(sql);
							ps.setInt(1, dlm_id.getElementAt(jcb[0].getSelectedIndex()));
							ps.setInt(2, dlm_id.getElementAt(jcb[1].getSelectedIndex()));
							ps.setInt(3, Integer.parseInt(jtf[0].getText()));
							ps.setInt(4, Integer.parseInt(jtf[1].getText()));
							ps.executeUpdate();
							JOptionPane.showMessageDialog(this,"EKLEME BA�ARILI!");
						}
				}
				else
					JOptionPane.showMessageDialog(this,"FARKLI TAKIMLAR SE��N�Z!");
			}
			else
				JOptionPane.showMessageDialog(this,"SKOR ALANLARINI BO� BIRAKMAYINIZ!");		
		} 
		catch (Exception e) {
			JOptionPane.showMessageDialog(this,"EKLEME BA�ARISIZ");
		}
	}
	public void actionPerformed(ActionEvent e) {
		karsilasmaEkle();
	}
}
