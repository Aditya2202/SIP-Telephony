
package serv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Serv implements Runnable {

    
    int sport, cport, Client, cport1;
    String sadd, ret, cadd;
    static Thread t;
    DatagramSocket sock;
    DatagramPacket pk;

    RegCli det, info, info1;
    ArrayList<RegCli> list;

    public Serv() throws SocketException {
        //sadd="192.168.1.8";
        sadd = "localhost";
        cport = 5677;
        sport = 5060;
        cport1 = 5679;
        sock = new DatagramSocket(sport);
        list = new ArrayList<RegCli>();
    }

    public void sendingback(String m, int p) {
        try {
            System.out.println("Registered .. ");
            byte[] ana = new byte[1024];
            System.out.println(m);
            ana = m.getBytes();
            pk = new DatagramPacket(ana, ana.length, InetAddress.getByName(sadd), p);
            sock.send(pk);
        } catch (IOException ex) {
            Logger.getLogger(Serv.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void parse(String msg) {

        det = new RegCli();
        info = new RegCli();
        info1 = new RegCli();
        String arr[] = msg.split("\r\n");
        String s = arr[0];
        int len = arr.length - 1;
        int i = 0, j = 0;

        ret = "SIP/2.0 200 OK\r\n";

        if (s.contains("REGISTER")) {
            while (i++ < len) {
                if (arr[i].contains("Via")) {
                    String s1 = arr[i].substring(17, 33).trim();
                    String[] s2 = s1.split(":");
                    det.setAddress(s2[0]);
                    det.setPort(Integer.parseInt(s2[1]));

                }

                if (arr[i].contains("From")) {
                    String s4 = arr[i].substring(11, 15);
                    det.setPhone(Integer.parseInt(s4));

                }
            }
            list.add(det);
            System.out.println("\nCLIENT\nIP ADDRESS : " + (list.get(j)).getAddress() + " Port :" + (list.get(j)).getPort() + " Phone :" + (list.get(j)).getPhone() + "\n");

            String s6 = ret;
            for (i = 1; i <= len; i++) {
                s6 += arr[i] + "\r\n";
            }

            sendingback(s6, det.getPort());

        } else if (s.contains("INVITE")) {
            while (i++ < len) {
                if (arr[i].contains("To")) {
                    String s4 = arr[i].substring(9, 13).trim();
                    int pqr = Integer.parseInt(s4);
                    //System.out.println(pqr);
                    info = list.get(0);
                    //System.out.println(info);
                    info1 = list.get(1);
                    //System.out.println(info1);
                    if (info.getPhone() == pqr) {
                        Client = info.getPort();
                       // System.out.println("inviting");

                    } else if (info1.getPhone() == pqr) {
                        Client = info1.getPort();
                        // System.out.println("inviting"+Client);
                    } else {
                        System.out.println("\nNot Registered\n");
                    }

                }
            }

            sendingback(msg, Client);
        } else if (s.contains("180 Ringing")) {
            while (i++ < len) {
                if (arr[i].contains("To")) {
                    String s4 = arr[i].substring(9, 13).trim();
                    int pqr = Integer.parseInt(s4);
                    info = list.get(0);
                    info1 = list.get(1);
                    if (info.getPhone() == pqr) {
                        Client = info.getPort();
                    } else if (info1.getPhone() == pqr) {
                        Client = info1.getPort();
                    } else {
                        System.out.println("\nNot Registered\n");
                    }

                }
            }

            sendingback(msg, Client);

        } else if (s.contains("ACK")) {
            while (i++ < len) {
                if (arr[i].contains("To")) {
                    String s4 = arr[i].substring(9, 13).trim();
                    int pqr = Integer.parseInt(s4);
                    info = list.get(0);
                    info1 = list.get(1);
                    if (info.getPhone() == pqr) {
                        Client = info.getPort();
                    } else if (info1.getPhone() == pqr) {
                        Client = info1.getPort();
                    } else {
                        System.out.println("\nNot Registered\n");
                    }

                }
            }

            sendingback(msg, Client);
        } else if (s.contains("200 OK")) {

            while (i++ < len) {
                if (arr[i].contains("To")) {
                    String s4 = arr[i].substring(9, 13).trim();
                    int pqr = Integer.parseInt(s4);
                    info = list.get(0);
                    info1 = list.get(1);
                    if (info.getPhone() == pqr) {
                        Client = info.getPort();
                    } else if (info1.getPhone() == pqr) {
                        Client = info1.getPort();
                    } else {
                        System.out.println("\nNot Registered\n");
                    }

                }
            }

          sendingback(msg, Client);

        }
    }

    public static void main(String[] args) {
        // TODO code application logic here
        Serv s;
        try {
            s = new Serv();
            t = new Thread(s);
            t.start();
        } catch (SocketException ex) {
            Logger.getLogger(Serv.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {
        System.out.println("server Thread started");
        while (true) {
            try {
                //System.out.println("Server rec fun started");
                byte[] ar = new byte[1024];
                DatagramPacket pkt = new DatagramPacket(ar, ar.length);
                sock.receive(pkt);
                String msg = new String(pkt.getData());
                System.out.println(msg + "\n\n");
                parse(msg);
            } catch (IOException ex) {
                Logger.getLogger(Serv.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
