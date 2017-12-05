/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cli;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
/**
 *
 * @author Ananya
 */
public class Sound implements Runnable{
    
     static Thread t3, t4;
     float sampleRate;
    int sampleSizeInBits;
    int channels;
    boolean signed;
    boolean bigEndian;
    File file;
    AudioInputStream ais;
    TargetDataLine line;
    SourceDataLine srcLine;
    DatagramSocket sock;
    Cli cli;
    DatagramPacket outPacket, inPacket;
   
    InetAddress clientAddress ;
    AudioFormat format;
    int cport;

   
    public Sound (Cli cli)
    {
        this.cli=cli;
     sampleRate = 8000;
     sampleSizeInBits = 8;
     channels = 1;
     clientAddress = InetAddress.getLoopbackAddress();
     cport=1502;
     signed=true;
     bigEndian=true;

    }
    
    
    public void openSrcDataLine() {

      try {
            sock = new DatagramSocket(cport);

        } catch (SocketException ex) {
            Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            srcLine = (SourceDataLine) AudioSystem.getLine(info);
            srcLine.open(format);
            srcLine.start();

        } catch (LineUnavailableException ex) {
            Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        public void RTP(int i){
        //Sound s = new Sound()
        
       
        t3 = new Thread(cli.son);
        t4 = new Thread(cli.son);
  
      //  this.sock = sock;
     //   clientPort = cp;
        openSrcDataLine();
        t3.start();
   
        t4.start();
    } 
    public void close(){
        t3.stop();
        t4.stop();
    } 

    @Override
    public void run() {
        if (Thread.currentThread() == t3) {
    
             format = new AudioFormat(8000,8, 1, true, true);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                try {
                    line = (TargetDataLine) AudioSystem.getLine(info);

                    line.open(format);
                    line.start();

                    int numBytesRead;

                    byte[] buff1 = new byte[12];
                    byte[] buff2 = new byte[172];
                    byte[] buff = new byte[172];
                    while (true) {
                     
                        numBytesRead = line.read(buff, 0, buff.length);
                        System.out.println("Bytes Read:" + numBytesRead);
                        System.out.println("Buffer: \n" + buff);
                        try {

                            outPacket = new DatagramPacket(buff, 0, buff.length, clientAddress, cport);
                            sock.send(outPacket);

                        } catch (Exception ex) {
                        }

                    }

                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        } else if(Thread.currentThread() == t4) {

            while (true) {
                byte[] receive = new byte[172];

                inPacket = new DatagramPacket(receive, receive.length);
                try {
                  
                     sock.receive(inPacket);
                } catch (IOException ex) {
                    Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
                }
               // System.out.println("Hello");
                for (int i = 0; i < receive.length; i++) {
                    System.out.print(receive[i] + " ");

                }
                ByteArrayInputStream bals = new ByteArrayInputStream(receive);
                ais = new AudioInputStream(bals, format, receive.length);

                System.out.println(" ");
                srcLine.write(receive, 0, receive.length);
            }
        }
    
    }
}
