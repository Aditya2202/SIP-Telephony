
package cli;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cli implements Runnable{

   int cport,sport,cport1;
   String cadd,sadd;
   static Thread t,t1;
   DatagramSocket socket;
   DatagramPacket pack,pkt1;
    String msg1;
   int i=0,j=0;
   int phone1,phone2;
   static boolean f = true,x=true;
   public Sound son;

    public javax.swing.JTextField jTextField1;
    public Cli() throws SocketException
    {
    cadd="192.168.1.8";    
    //cadd="172.210.120.133";
    sport=5060;
    sadd="192.168.1.8";
    //sadd="172.210.120.133";
    cport=5677;
    cport1=5679;
    phone1=1234;
    phone2=5678;
    socket=new DatagramSocket(cport1);
    
    }
    
    public String Registercli(){

         String str = "REGISTER sip:"+ sadd +" SIP/2.0\r\n" ; 
        str = str + "CSeq: "+ i++ +" REGISTER\r\n" ;
        str = str + "From: <sip:"+ phone2 +"@"+ sadd+">;tag ="+ Math.random() +"\r\n" ;
        str = str + "To: <sip:"+ phone2 +"@"+ sadd +">\r\n" ;
        str = str + "Via: SIP/2.0/UDP "+ cadd +":"+ cport1 +";branch=z9hG4f564515646\r\n" ;
        str = str+  "Contact: <sip:"+ cport1 +"@"+ cadd +":"+cport1+">\r\n" ;
        return str;
        
    }
         public String InviteToClient()
    {
        String str = "INVITE sip:"+phone2+"@"+ sadd +":"+ sport +" SIP/2.0\r\n" ; 
        str = str + "CSeq: "+ j++ +" REGISTER\r\n" ;
        str = str + "From: <sip:"+ phone1 +"@"+ sadd +">;tag ="+ Math.random() +"\r\n" ;
        str = str + "To: <sip:"+ phone2 +"@"+ sadd +">\r\n" ;
        str = str + "Via: SIP/2.0/UDP "+ cadd +":"+ cport1+"f;branch=z9hG4f564515646\r\n" ;
        str = str+  "Contact: <sip:"+ cport1 +"@"+ cadd +":"+sport+">\r\n" ;
       
     
        return str ;
        
    }
     

    
    
     public void senddata(String str) throws UnknownHostException
    {
       try {
           System.out.println("Sending on server.. ");
           byte[] arr=new byte[1024];
           arr=str.getBytes();
           pack=new DatagramPacket(arr,arr.length,InetAddress.getByName("localhost"),sport);
           socket.send(pack);
       } catch (IOException ex) {
           Logger.getLogger(Cli.class.getName()).log(Level.SEVERE, null, ex);
       }
    }
     

// trying it .. 2nd option     
//    public static void main(String[] args) {
//       try {
//           // TODO code application logic here
//           Cli cli=new Cli();
//           t=new Thread(cli);
//           t1=new Thread(cli);
//           t.start();
//          
//       } catch (SocketException ex) {
//           Logger.getLogger(Cli.class.getName()).log(Level.SEVERE, null, ex);
//       }
//    }

    public void par(String str) throws UnknownHostException
    {     
       String str1=null,str2=null; 
            i=0;
                   if(str != null){
                     f=false;
              
               String s[] = str.split("\r\n");
               String s2  = s[0];
               System.out.println(s2);
               int len  = s.length-1;
              
               if(s2.contains("INVITE")){
                   
                   System.out.println("180 ringing packets to clien1");
                   String st1 = "SIP/2.0 180 Ringing\r\n";
                      for(int i=1; i<len; i++){
                      String s3=null,s4=null;
                      if(s[i].contains("From")){
                         s3 = s[i].substring(11, 15);
                         st1 = st1 + "To: <sip:"+ s3 +"@"+ sadd+">\r\n";
                      }else if(s[i].contains("To")){
                          s4 = s[i].substring(9,13); 
                          st1 = st1 + "From: <sip:"+ s4 +"@"+ sadd +">;tag ="+Math.random()+"\r\n" ;    
                      }else 
                          st1 += s[i] + "\r\n";    
                  }
                while(x)  {         
                          senddata(st1);
                 }
                 if(!x){
                     System.out.println("\nCall picked up");
                     i=0;
                String st = "SIP/2.0 200 OK\r\n";
                String s1[] = st1.split("\r\n");
                 len = s1.length-1;
                 while(i++<len){                      
                   st += s1[i] + "\r\n";      
                 }
                 System.out.println("\n"+st);
                 senddata(st);
                 }
               
               } 
               
               
               
               
               
            if(s2.contains("SIP/2.0 200")){
                    while(i++<len){
                        if(s[i].contains("From"))
                            str1 = s[i].substring(11,15);                        
                        if(s[i].contains("To"))    
                            str2 = s[i].substring(9,13);                        

                    }
                    
                    if(str1.equals(str2)){ }
                    else{      
                   String str3 = "Request-Line: ACK sip:"+phone2+"@"+sadd+":"+cport+" SIP/2.0\r\n";
                   str3 = str3 + "From: <sip:"+ phone1 +"@"+ sadd +">;tag ="+ Math.random() +"\r\n" ;
                   str3= str3 + "To: <sip:"+ phone2 +"@"+ sadd +">\r\n" ;
                   for(int i=1;i<len;i++)
                      if(!(s[i].contains("From")||s[i].contains("To")))
                          str3 += s[i] + "\r\n";
                          senddata(str3);
                          son.RTP(1);
                    }
                    
   
                }   
            
                           else if(s2.contains("ACK")){
                                   son = new Sound(this);
                                  son.RTP(0);
                               System.out.println("\n\nAcknowlegment received\n ");
               } 


        }   
    
    }
    
    
    
    
    @Override
    public void run() {
     System.out.println("Client thread started"); 
      while(true)
        {
       
         try {
            // System.out.println("CLient receive Ok msg");
             byte[] ar1=new byte[1024];
             pkt1= new DatagramPacket(ar1,ar1.length);
             socket.receive(pkt1);
             msg1=new String(pkt1.getData());
             System.out.println(msg1+"\n\n");
             par(msg1);
         } catch (IOException ex) {
             Logger.getLogger(Cli.class.getName()).log(Level.SEVERE, null, ex);
         }
            
         
        
        }
    }
    
}
