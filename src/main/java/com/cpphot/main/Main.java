package com.cpphot.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Main {
	public static void main(String[] args) throws JSchException, IOException {
        String userName = "cxp";// 用户名
        String password = "123456";// 密码
        String host = "192.168.12.80";// 服务器地址
        int port = 22;// 端口号
        String cmd = "echo $SHELL";// 要运行的命令
        SSHExecuter sshExecuter=new SSHExecuter(userName, password, host, port);
        //sshExecuter.download("/home/cxp/test.txt", "D:\\456.txt");
        System.out.println(sshExecuter.cmd(cmd, null));
        sshExecuter.close();
    }
}
