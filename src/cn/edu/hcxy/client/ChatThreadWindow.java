package cn.edu.hcxy.client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.sql.*;

/**
 * �����߳�
 */
public class ChatThreadWindow {
    private String name;//��ǰҳ����û���
    JComboBox cb;
    private JFrame f;
    JTextArea ta;
    private JTextField tf;
    private static int total;// ��������ͳ��
    DatagramSocket ds;

    public ChatThreadWindow(String name, DatagramSocket ds) {
        this.ds = ds;
        this.name=name;
        /*
         * ���������Ҵ��ڽ���
         */
        f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 400);
        f.setTitle("������" + " - " + name + "     ��ǰ��������:" + ++total);
        f.setLocation(300, 200);
        ta = new JTextArea();
        JScrollPane sp = new JScrollPane(ta);
        ta.setEditable(false);
        tf = new JTextField();
        cb = new JComboBox();
        cb.addItem("All");
        JButton jb = new JButton("˽�Ĵ���");
        JPanel pl = new JPanel(new BorderLayout());
        pl.add(cb);
        pl.add(jb, BorderLayout.WEST);
        JPanel p = new JPanel(new BorderLayout());
        p.add(pl, BorderLayout.WEST);
        p.add(tf);
        f.getContentPane().add(p, BorderLayout.SOUTH);
        f.getContentPane().add(sp);
        f.setVisible(true);
        GetMessageThread getMessageThread = new GetMessageThread(this);//���ڴ��Ĳ������࣬��ֱ����this
        getMessageThread.start();

        /*
        ��ʾXXX����������
         */
        showXXXIntoChatRoom();
    }

    public void showXXXIntoChatRoom() {
        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
        String username_db = "opts";
        String password_db = "opts1234";
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username_db, password_db);
            String sql = "SELECT username,ip,port FROM users WHERE status='online'";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username=rs.getString("USERNAME");//��ȡsql���û���
                String ip = rs.getString("IP");
                int port = rs.getInt("PORT");
                System.out.println(ip);
                System.out.println(port);
                byte[] ipB = new byte[4];

                String ips[] = ip.split("\\.");
                for (int i = 0; i < ips.length; i++) {
                    ipB[i] = (byte)Integer.parseInt(ips[i]);
                }
                if(!username.equals(name))//���������������ȡ���Ĳ�һ��  ������������Ϣ
                {
                    String message = name+"������������";
                    byte[] m = message.getBytes();
                    DatagramPacket dp = new DatagramPacket(m, m.length);
                    dp.setAddress(InetAddress.getByAddress(ipB));
                    dp.setPort(port);
                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);//Ͷ��
                }else if(username.equals(name))//���������������ȡ���Ĳ�һ��  ������������Ϣ
                {
                    String message = username+"����������";
                    byte[] m = message.getBytes();
                    DatagramPacket dp = new DatagramPacket(m, m.length);
                    dp.setAddress(InetAddress.getByAddress(ipB));
                    dp.setPort(port);

                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);//Ͷ��
                }
            }
        } catch (SQLException | UnknownHostException | SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}