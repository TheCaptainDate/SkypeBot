/*
 * Copyright (C) 2012 Maksim Fomin (Date) (далее - автор)
 *
 * Данный программа - закрытое програмное обеспечение.
 * Вы НЕ имеете права использовать, изменять, дополнять или хранить ее, без разрешения автора!
 */

package com.thecaptaindate.skypebot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;




public class SocketQuery extends Thread {

    private Socket s;
    
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public SocketQuery(Socket s) {
	super("SocketQuery");
	this.s = s;
    }
    
    @Override
    public void run() {
	try {
	    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

	    String input = in.readLine();
	    
	    SkypeBot.SocketDecoder(input, out);
	    
	    out.close();
	    in.close();
	    s.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
}
