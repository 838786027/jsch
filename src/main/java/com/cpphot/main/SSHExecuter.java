package com.cpphot.main;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHExecuter implements Closeable {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(SSHExecuter.class);
	private JSch jsch = null;
	private Session session = null;

	/**
	 * 连接方式一：通过密码登陆
	 * @param userName
	 * @param password
	 * @param host
	 * @param port
	 */
	public SSHExecuter(String userName, String password, String host, int port) throws JSchException {
		this.jsch = new JSch(); // 创建JSch对象
		this.session = this.jsch.getSession(userName, host, port); // 根据用户名，主机ip，端口获取一个Session对象
		this.session.setPassword(password); // 设置密码
		
		// 设置连接属性
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		this.session.setConfig(config); // 为Session对象设置properties
		this.session.setTimeout(60*1000); // 设置timeout时间
		LOGGER.info("创建远程连接："+host);
	}

	public String cmd(String order,String args) throws JSchException, IOException{
		if(!this.session.isConnected()){
			this.session.connect();
		}
		
		ChannelExec channelExec = (ChannelExec) this.session.openChannel("exec");
		channelExec.setCommand(order);
		// note：暂时不设置输入
        channelExec.setInputStream(null);
        channelExec.setErrStream(System.err);
        channelExec.connect();
        
        // 执行命令获取输出
        InputStream in = channelExec.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
        String buf = null;
        StringBuilder sb = new StringBuilder();
        while ((buf = reader.readLine()) != null) {
            sb.append(buf+"\r\n");
        }
        reader.close();
        channelExec.disconnect();
        
        return sb.toString();
	}
	
    /**
     * 上传文件
     *
     * @param directory  上传的目录，带/后缀
     * @param uploadFile 要上传的文件
     * @param otherName 重命名
     */
    public void upload(String directory, String src,String otherName) throws JSchException {
		if(!this.session.isConnected()){
			this.session.connect();
		}
		
        try {
        	ChannelSftp sftp = (ChannelSftp) this.session.openChannel("sftp");
        	sftp.connect();
            File file = new File(src);
            sftp.put(src, directory+(otherName==null?file.getName():otherName));
            sftp.disconnect();
            LOGGER.info("上传文件"+src+"成功");
        } catch (Exception e) {
        	LOGGER.error("上传文件异常",e);
        }
        
    }
    
    /**
     * 下载文件
     * 
     * @param src 要下载的文件
     * @param target 存储到哪个文件
     * @throws JSchException
     */
    public void download(String src,String target) throws JSchException {
		if(!this.session.isConnected()){
			this.session.connect();
		}
		
        try {
        	ChannelSftp sftp = (ChannelSftp) this.session.openChannel("sftp");
        	sftp.connect();
            File file = new File(src);
            sftp.get(src, target);
            sftp.disconnect();
            LOGGER.info("下载文件"+src+"成功");
        } catch (Exception e) {
        	LOGGER.error("下载文件异常",e);
        }
    }

	public void close() throws IOException {
		session.disconnect();
	}
}
