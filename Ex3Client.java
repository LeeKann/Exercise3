/*
*Eric Kannampuzha
*Exercise 3
*Class Ex3Client.java
*CS 380
*Nima
*/

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.zip.CRC32;
import java.util.Arrays;
import java.nio.ByteBuffer;

public final class Ex3Client {

    public static void main(String[] args) throws Exception {
        try {
            Socket socket = new Socket("18.221.102.182", 38103);
            System.out.println("Connected to server.");
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            
            int streamLength = dis.readUnsignedByte();
            //System.out.println(streamLength);
            byte[] byteArray = new byte[streamLength];
            for(int i = 0; i < streamLength; i++) {
                byte result = (byte)dis.readUnsignedByte();
                //System.out.println(result);
                byteArray[i] = result;
            }
            
            StringBuilder sb = new StringBuilder();
            for(byte b : byteArray) {
                sb.append(String.format("%02X ", b));
            }
            System.out.println("Received bytes:\n[" + sb.toString() + "]");
            


            short checkSum = checksum(byteArray);
            dos.writeByte((byte)(checkSum>>8));            
            dos.writeByte((byte)checkSum);
            dos.flush();
            
            int check = dis.readByte();
            //System.out.println(check);
            if(check == 0x1)
                System.out.println("Response good.");
            else
                System.out.println("Response bad.");
                
            socket.close();
            System.out.println("Disconnected from server.");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static short checksum(byte[] b) {
        int sum = 0;
        //System.out.println(b.length);
        
        for(int i = 0; i < b.length; i++) {
            byte upper = b[i++];
            byte lower;
            if(i < b.length)
                lower = b[i];
            else
                lower = 0;
            //if(i + 1 == b.length)
                //lower = 0;
           // else
                //lower = b[i + 1];
            //int mask = 0xF;
            //upper = (byte)(upper & mask);
            //lower = (byte)(lower & mask);
            int result = 0;
            result = (result | (upper<<0x8 & 0xFF00));
            result = (result | (lower & 0x00FF));
            //result = result & 0x0000FFFF;
            sum += result;
            if((sum & 0xFFFF0000) != 0) {
                sum = sum & 0xFFFF;
                sum += 1;
            }
        }
        //System.out.println("The sum " + sum);
        return (short)~(sum & 0xFFFF);
    }
}
