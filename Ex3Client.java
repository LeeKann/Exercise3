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
            Socket socket = new Socket("18.221.102.182", 38102);
            System.out.println("Connected to server.");
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            byte[] byteArray = new byte[100];
            for(int i = 0; i < 100; i++) {
                byte upper;
                byte lower;
                byte result;
                int mask = 0xF;
                upper = dis.readByte();
                lower = dis.readByte();
                upper = (byte)(upper & mask);
                lower = (byte)(lower & mask);
                upper = (byte)(upper<<0x4);
                result = (byte)(upper | lower);
                byteArray[i] = result;
            }
            StringBuilder sb = new StringBuilder();
            for(byte b : byteArray) {
                sb.append(String.format("%02X ", b));
            }
            System.out.println("Received bytes:\n[" + sb.toString() + "]");
            CRC32 crc = new CRC32();
            crc.update(byteArray);
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putInt((int)crc.getValue());
            
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            StringBuilder crcsb = new StringBuilder();
            byte[] crcArray = bb.array();
            
            for(byte b : crcArray) {
                crcsb.append(String.format("%02X ", b));
            }
            System.out.println("Generated CRC32: " + crcsb);
            dos.write(crcArray, 0, 4);
            dos.flush();
            
            if(dis.readByte() == 0x1)
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
}
