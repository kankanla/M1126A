package sun.dodofei.e560.m1126a.ToolsClass;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by E560 on 2016/12/04.
 */

public class sun_FTP {
    private String USER, PASSWORD;
    private InetAddress FTPServerIP;
    private BufferedReader FTPCmdreader;
    private BufferedWriter FTPCmdwriter;
    private final String TAG = "sun_FTP";

    public sun_FTP(String FURL, String USER, String PASSWORD) throws Exception {
        Log.i(TAG, "sun_FTP");
        this.FTPServerIP = InetAddress.getByName(FURL);
        this.USER = USER;
        this.PASSWORD = PASSWORD;
    }

    public String FTPconnect() {
        Log.i(TAG, "FTPconnect");
        String errorlevel = "ConnectFTPServer";
        try {
            Socket socket21 = new Socket(FTPServerIP, 21);
            FTPCmdreader = new BufferedReader(new InputStreamReader(socket21.getInputStream()));
            FTPCmdwriter = new BufferedWriter(new OutputStreamWriter(socket21.getOutputStream()));
            FTPcommand(FTPCmdwriter, "USER " + USER);
            FTPcommand(FTPCmdwriter, "PASS " + PASSWORD);
        } catch (Exception e) {
            errorlevel = "ConnectFTPerror";
            e.printStackTrace();
        }
        return errorlevel;
    }

    public void upload(File[] files) throws Exception {
        Log.i(TAG, "upload");
        for (int i = 0; i < files.length; i++) {
            FTPcommand(FTPCmdwriter, "PASV ");
            String[] temp = new String[]{};
            String recode = new String();
            while (true) {
                recode = FTPCmdreader.readLine();
                System.out.println(recode);
                if (recode.substring(0, 3).equals("227")) {
                    temp = get227(recode);
                    break;
                }
            }
            Socket socket20 = new Socket(temp[0], Integer.parseInt(temp[1]));
            OutputStream ftpOutputStream = socket20.getOutputStream();
            FileInputStream upfileInputStream = new FileInputStream(files[i]);
            FTPcommand(FTPCmdwriter, "TYPE " + "I");
            FTPcommand(FTPCmdwriter, "STOR " + files[i].getName());
            byte[] buff = new byte[20408];
            int len = 0;
            while ((len = upfileInputStream.read(buff)) != -1) {
                ftpOutputStream.write(buff, 0, len);
                ftpOutputStream.flush();
                System.out.print(".");
            }
            ftpOutputStream.close();
            upfileInputStream.close();
            System.out.println("");
            Thread.sleep(5 * 1000);
        }
    }

    public void close() {
        Log.i(TAG, "close");
        try {
            FTPcommand(FTPCmdwriter, "QUIT ");
            String temp = null;
            while ((temp = FTPCmdreader.readLine()) != null) {
                System.out.println(temp);
            }
            System.out.println("");
            FTPCmdwriter.close();
            FTPCmdreader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void FTPcommand(BufferedWriter FTPCmdwriter, String FTPcmd) throws Exception {
        Log.i(TAG, "FTPcommand");
        FTPCmdwriter.write(FTPcmd + "\r\n");
        FTPCmdwriter.flush();
    }

    private String[] get227(String string) {
        Log.i(TAG, "get227");
        int start = string.indexOf("(") + 1;
        int end = string.indexOf(")");
        String substring = string.substring(start, end);
        String[] temp = substring.split(",");
        String ip = temp[0] + "." + temp[1] + "." + temp[2] + "." + temp[3];
        int port = Integer.parseInt(temp[4]) * 256 + Integer.parseInt(temp[5]);
        String sport = String.valueOf(port);
        String[] res = {ip, sport};
        return res;
    }
}
