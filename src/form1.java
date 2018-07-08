import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class form1 extends JFrame implements ActionListener{

	Connection conn=null; 
	public JPanel jpn_adi;
	DefaultListModel<String> dlm_adi;
	DefaultListModel<String> dlm_adi2;
	DefaultListModel<Integer> dlm[];
	ListModel<Integer> dlm2[];
	DefaultListModel<Integer> dlm_id;
	JList<String> jlist_adi;
	JList<Integer> jlist[];
	
	public form1()
	{
	this.setTitle("Fikstür");
	this.setDefaultCloseOperation(3);
	this.setSize(750, 550);
	this.setLayout(new FlowLayout());
	jpn_adi= new JPanel();
	jpn_adi.setPreferredSize(new Dimension(200, 440));
	
	dlm_adi= new DefaultListModel<String>();
	dlm_adi2= new DefaultListModel<String>();
	jlist_adi= new JList<String>(dlm_adi2);
	JScrollPane jscp_adi= new JScrollPane(jlist_adi);
	jscp_adi.setPreferredSize(new Dimension(200, 400));
	JLabel jlab_adi = new JLabel("Takým Ýsmi:");
	jlab_adi.setPreferredSize(new Dimension(200, 25));
	jpn_adi.add(jlab_adi);
	jpn_adi.add(jscp_adi);
	this.add(jpn_adi);
	
	String[] lab= {"Maç Sayýsý:","Attýðý Gol:","Yediði Gol:","Averaj:","Puan:"};
	dlm2= new DefaultListModel[5];
	dlm= new DefaultListModel[5];
	jlist= new JList[5];
	
	
	for(int i=0;i<5;i++)
	{
		JPanel jPanel= new JPanel();
		jPanel.setPreferredSize(new Dimension(100, 440));
		JLabel jLabel= new JLabel(lab[i]);
		jLabel.setPreferredSize(new Dimension(100, 25));
		jPanel.add(jLabel);
		
		dlm[i]= new DefaultListModel<Integer>();
		dlm2[i]= new DefaultListModel<Integer>();
		jlist[i]= new JList<Integer>(dlm2[i]);
		JScrollPane jscp= new JScrollPane(jlist[i]);
		jscp.setPreferredSize(new Dimension(100, 400));
		jPanel.add(jscp);
		this.add(jPanel);
	}

		JButton jbtn2= new JButton("TÜM KARÞILAÞMALARI SÝL");
		jbtn2.setPreferredSize(new Dimension(360, 50));
		jbtn2.addActionListener(this);
		jbtn2.setActionCommand("sil");
		this.add(jbtn2);
	
		JButton jbtn= new JButton("KARÞILAÞMA EKLEME FORMUNU AÇ");
		jbtn.setPreferredSize(new Dimension(360, 50));
		jbtn.addActionListener(this);
		jbtn.setActionCommand("ekle");
		this.add(jbtn);

	listDoldur();
	puanHesapla();
	this.setVisible(true);
	}
	public void baglan()
	{
		try {
			Class.forName("org.sqlite.JDBC");
			conn =DriverManager.getConnection("jdbc:sqlite:odev2.db");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this,"BAÐLANTI BAÞARISIZ");
		}
	}
	public void baglantiKes()
	{
		try {
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("ekle"))
		{
			new kayit();
			
		}
		else if(arg0.getActionCommand().equals("sil"))
		{
			baglan();
			try 
			{
				String sql="delete from skorlar";
				PreparedStatement ps= conn.prepareStatement(sql);
				ps.execute();
				JOptionPane.showMessageDialog(this,"TÜM KARÞILAÞMALAR SÝLÝNDÝ!");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this,"SÝLME BAÞARISIZ");
			}
			baglantiKes();
		}
	}
	public static void main(String[] args) {
		new form1();
	}
	
	
	
	public void listDoldur()
	{
		baglan();
		try 
		{
			String takimlar="select id,adi from takimlar";
			PreparedStatement ps=conn.prepareStatement(takimlar);
			ResultSet rs=ps.executeQuery();
			dlm_id=new DefaultListModel<Integer>();
			while(rs.next())
			{
				dlm_id.addElement(rs.getInt("id"));
				dlm_adi.addElement(rs.getString("adi"));
			}
		} 
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "TAKIMLARI YÜKLEME BAÞARISIZ!");
		}
		
		try 
		{
			for(int i=0;i<dlm_adi.getSize();i++)
			{
				String skor="select count(*) from skorlar where takim1=? or takim2=?";
				PreparedStatement ps=conn.prepareStatement(skor);
				ps.setInt(1, dlm_id.getElementAt(i));
				ps.setInt(2, dlm_id.getElementAt(i));
				ResultSet rs=ps.executeQuery();
				dlm[0].addElement(rs.getInt("count(*)"));
			}	
		} 
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "MAÇ SAYISI YÜKLEME BAÞARISIZ!");
		}
		
		try 
		{
			for(int i=0;i<dlm_adi.getSize();i++)
			{
				int sonuc1,sonuc2;
				String gol="select sum(skor1) from skorlar where takim1=?";
				PreparedStatement ps=conn.prepareStatement(gol);
				ps.setInt(1, dlm_id.getElementAt(i));
				ResultSet rs=ps.executeQuery();
				sonuc1=rs.getInt("sum(skor1)");
				
				gol="select sum(skor2) from skorlar where takim2=?";
				ps=conn.prepareStatement(gol);
				ps.setInt(1, dlm_id.getElementAt(i));
				rs=ps.executeQuery();
				sonuc2=rs.getInt("sum(skor2)");
				
				dlm[1].addElement(sonuc1+sonuc2);
			}		
		} 
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "ATTIÐI GOL SAYISI YÜKLEME BAÞARISIZ!");
		}
		
		try 
		{
			for(int i=0;i<dlm_adi.getSize();i++)
			{
				int sonuc1,sonuc2;
				String gol="select sum(skor1) from skorlar where takim2=?";
				PreparedStatement ps=conn.prepareStatement(gol);
				ps.setInt(1, dlm_id.getElementAt(i));
				ResultSet rs=ps.executeQuery();
				sonuc1=rs.getInt("sum(skor1)");
				
				gol="select sum(skor2) from skorlar where takim1=?";
				ps=conn.prepareStatement(gol);
				ps.setInt(1, dlm_id.getElementAt(i));
				rs=ps.executeQuery();
				sonuc2=rs.getInt("sum(skor2)");
				int yedigiGol=sonuc1+sonuc2;
				
				dlm[2].addElement(yedigiGol);
				dlm[3].addElement(dlm[1].getElementAt(i)-yedigiGol); //AVERAJ HESAPLAMA
			}		
		} 
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "YEDÝÐÝ GOL SAYISI YÜKLEME BAÞARISIZ!");
		}
		baglantiKes();
	}
	
	public void puanHesapla()
	{
		baglan();
		try 
		{
			for(int i=0;i<dlm_adi.getSize();i++)
			{
				int puan=0;
				String takim1="select skor1,skor2 from skorlar where takim1=?";
				PreparedStatement ps=conn.prepareStatement(takim1);
				ps.setInt(1, dlm_id.getElementAt(i));
				ResultSet rs=ps.executeQuery();
				while(rs.next())
				{
					if(rs.getInt("skor1")>rs.getInt("skor2"))
					{
						puan+=3;
					}
					else if(rs.getInt("skor1")==rs.getInt("skor2"))
					{
						puan+=1;
					}
				}
				
				String takim2="select skor1,skor2 from skorlar where takim2=?";
				ps=conn.prepareStatement(takim2);
				ps.setInt(1, dlm_id.getElementAt(i));
				rs=ps.executeQuery();
				while(rs.next())
				{
					if(rs.getInt("skor2")>rs.getInt("skor1"))
					{
						puan+=3;
					}
					else if(rs.getInt("skor2")==rs.getInt("skor1"))
					{
						puan+=1;
					}
				}
				dlm[4].addElement(puan);
			}
		} 
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "PUAN HESAPLAMA BAÞARISIZ!");
		}
		baglantiKes();
		sýrala();
	}
	
	public void sýrala()
	{
		ArrayList<Integer> list = Collections.list(dlm[4].elements()); 
		Collections.sort(list);
		Collections.reverse(list);
		((DefaultListModel<Integer>) dlm2[4]).clear(); 
		for(Object o:list)
		{ 
			((DefaultListModel<Integer>) dlm2[4]).addElement((Integer) o);
			int puan=((Integer) o);
			for(int i=0;i<dlm[4].getSize();i++)
			{
				if(puan==dlm[4].getElementAt(i)&&!dlm_adi2.contains(dlm_adi.getElementAt(i)))
				{
					dlm_adi2.addElement(dlm_adi.getElementAt(i));
					((DefaultListModel<Integer>) dlm2[0]).addElement(dlm[0].getElementAt(i));
					((DefaultListModel<Integer>) dlm2[1]).addElement(dlm[1].getElementAt(i));
					((DefaultListModel<Integer>) dlm2[2]).addElement(dlm[2].getElementAt(i));
					((DefaultListModel<Integer>) dlm2[3]).addElement(dlm[3].getElementAt(i));
				}
			}
		} 
	}
	
}
