

import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;

public class AlertServer
{
    private ServerSocket m_socket;

    public class SListener implements Runnable
    {
	public Socket m_sock;

	public SListener(Socket s)
	{
	    m_sock = s;
	}
	
	public void run()
	{
	    try {
		InputStream is = m_sock.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		OutputStream outs = m_sock.getOutputStream();
		Writer out = new OutputStreamWriter(outs);

		String line;
		while ((line=br.readLine()) != null)
		    {
			FileWriter fw = new FileWriter("c:/alertserver.log", true);
			out.write("PRINTING <" + line + ">");
			System.out.println("Received: " + line);
			fw.write(line + "\n");
			if (line.startsWith("EXIT"))
			    {
				System.out.println("calling System.exit(0);");
				System.exit(0);
				return;
			    }
			else if (line.startsWith("PATH"))
			    {
				line = System.getProperty("user.dir");
			    }
			fw.close();
			JOptionPane.showMessageDialog(null, line, "alert", JOptionPane.ERROR_MESSAGE); 
		    }
		is.close();
	    } catch (IOException iox)
		{
		    iox.printStackTrace();
		}
	}

    }

    public AlertServer()
    {
    }

    public void setup()
    {
	try {
	    m_socket = new ServerSocket(2273);
	} catch (Exception ex)
	    {
		ex.printStackTrace();
	    }
    }
    
    public void listen()
    {
	try {
	    while (true)
		{
		    Socket sock = m_socket.accept();
		    SListener sl = new SListener(sock);
		    new Thread(sl).start();
		}

	} catch (Exception exc)
	    {
		exc.printStackTrace();
	    }
    }

    public void shutdown()
    {
	try {
	    FileWriter fw = new FileWriter("c:/shutdown.log", true);
	    fw.write("END NOW " + System.currentTimeMillis() + "\n");
	    fw.close();
	} catch (IOException iox)
	    {
		iox.printStackTrace();
	    }
    }

    static public void main(String[] args)
    {
	final AlertServer a = new AlertServer();
	Runtime.getRuntime().addShutdownHook(new Thread() {
		public void run()
		{
		    // JOptionPane.showMessageDialog(null, "TERMINATING!", "alert", JOptionPane.ERROR_MESSAGE); 
		    try {
			FileWriter fw = new FileWriter("shutdown1.log", true);
			fw.write("END NOW " + System.currentTimeMillis() + "\n");
			fw.close();
		    } catch (Exception exc)
			{
			    exc.printStackTrace();
			}
		    System.out.println("SHUTDOWN...");
		    a.shutdown();
		}
	    });
	
	a.setup();
	a.listen();
    }
}
