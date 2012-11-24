/*
 * Copyright (C) 2012 Maksim Fomin (Date) (далее - автор)
 *
 * Данный программа - закрытое програмное обеспечение.
 * Вы НЕ имеете права использовать, изменять, дополнять или хранить ее, без разрешения автора!
 */

package com.thecaptaindate.skypebot;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.naef.jnlua.LuaState;
import com.skype.Chat;
import com.skype.Skype;
import com.skype.SkypeException;
import com.thecaptaindate.skypebot.lua.LuaSend;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkypeBot 
{   
    public static void main( String[] args ) throws IOException, IOException, SkypeException
    {	
	// Загружаем конфиг
	LoadConfig();
	
	// Check lua dirrectory
	File f = new File("lua");
	if(!f.exists()) {
	    f.mkdir();
	}
	
	 // Create a Lua state 
                LuaState luaState = new LuaState(); 
                try { 
			Chat test = Skype.chat("sevendarkos");
			test.send("Hello from java!");
                        // Define a function 
                        luaState.load(new FileInputStream("lua" + File.separator + "test.lua"), "=test");
			luaState.openLibs();

                        // Evaluate the chunk, thus defining the function 
                        luaState.call(0, 0); // No arguments, no returns 

                        // Prepare a function call 
                        luaState.getGlobal("add"); // Push the function on the stack 
                        luaState.pushInteger(1); // Push argument #1 
                        luaState.pushInteger(1); // Push argument #2 
			luaState.pushJavaObject(test);
			luaState.register(new LuaSend());
			
                        // Call 
                        luaState.call(3, 1); // 2 arguments, 1 return 
			

                        // Get and print result 
                        int result = luaState.toInteger(1); 
                        luaState.pop(1); // Pop result 
                        System.out.println("According to Lua, 1 + 1 = " + result); 
                } finally { 
                        luaState.close(); 
                } 
	
	System.out.println("System library path: " + System.getProperty("java.library.path"));
	
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
