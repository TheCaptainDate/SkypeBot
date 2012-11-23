/*
 * Copyright (C) 2012 Maksim Fomin (Date) (далее - автор)
 *
 * Данный программа - закрытое програмное обеспечение.
 * Вы НЕ имеете права использовать, изменять, дополнять или хранить ее, без разрешения автора!
 */

package com.thecaptaindate.skypebot;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.skype.Chat;
import com.skype.Skype;
import com.skype.SkypeException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkypeBot 
{   
    public static void main( String[] args )
    {	
	// Загружаем конфиг
	LoadConfig();
	
	// Check lua dirrectory
	File f = new File("lua/");
	System.out.println(f.getPath() + ": is path");
	if(!f.exists()) {
	    f.mkdir();
	}
	
	// Создаем листенер сообщений:
	try {
	    Skype.addChatMessageListener(new ChatMessageListener());
	} catch (SkypeException ex) {
	    Logger.getLogger(SkypeBot.class.getName()).log(Level.WARNING, "Can't create message listener", ex);
	}
	// === Сокет-сервер ===
	try {
	    ServerSocket s = new ServerSocket(27015, 0);
	    System.out.println("Socket Server started!");
	    
	    while(true) {
		new SocketQuery(s.accept()).start();
	    }
	} catch (Exception ex) {
	    Logger.getLogger(SocketQuery.class.getName()).log(Level.WARNING, "Can't create socket server!", ex);
	}
    }
    
    private static void LoadConfig() {
	try {
	    if(new File("config.yml").exists()) {
		YamlReader reader = new YamlReader(new FileReader("config.yml"));
		Data.self = (Data) reader.read();
		reader.close();
	    } else {
		CreateConfig();
	    }
	} catch (YamlException ex) {
	    Logger.getLogger(SkypeBot.class.getName()).log(Level.WARNING, "Can't deserialize config.yml!", ex);
	} catch (IOException ex) {
	    Logger.getLogger(SkypeBot.class.getName()).log(Level.WARNING, "Can't open config.yml!", ex);
	}
    }
    
    // Функция создания конфига
    private static void CreateConfig() {
	Data.self = new Data();
	Data.self.save();
    }
    
    public static void SendMessage(String content, String channel) {
	try {
	    Chat[] chats = Skype.getAllChats();
	    
	    for (Chat chat : chats) {
		String cnl = Data.self.getChannel(chat.getId());
		
		if(cnl.equalsIgnoreCase(channel) || cnl.equalsIgnoreCase("all")) {
		    chat.send(content);
		}
	    }
	} catch (SkypeException ex) {
	    Logger.getLogger(SkypeBot.class.getName()).log(Level.SEVERE, "Some error with skype: ", ex);
	}
    }
    
    public static void SocketDecoder(String line, PrintWriter w) {
	if(line.contains("<split>")) {
	    String[] args = line.split("<split>");
	    
	    if(args.length == 3) {
		if(args[0].equals(Data.self.getPassword())) {
		    SendMessage(args[2], args[1]);
		    w.print("OK");
		} else {
		    w.println("Wrong password!");
		}
	    } else {
		w.println("Wrong format");
	    }
	} else {
	    w.println("Wrong password");
	}
    }
}
