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
import java.net.ServerSocket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkypeBot 
{
    
    public static Data data;
    
    public static void main( String[] args )
    {	
	// Загружаем конфиг
	LoadConfig();
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
		data = (Data) reader.read();
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
	data = new Data();
	data.save();
    }
}
