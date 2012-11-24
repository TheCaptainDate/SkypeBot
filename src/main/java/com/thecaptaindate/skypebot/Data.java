/*
 * Copyright (C) 2012 Maksim Fomin (Date) (далее - автор)
 *
 * Данный программа - закрытое програмное обеспечение.
 * Вы НЕ имеете права использовать, изменять, дополнять или хранить ее, без разрешения автора!
 */

package com.thecaptaindate.skypebot;

import com.esotericsoftware.yamlbeans.YamlWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Data {
    
    public HashMap<String, String> ranks;
    public HashMap<String, String[]> phrases;
    private Random r;
    public static Data self;
    public HashMap<String, String> channels;
    public String password;
    public ArrayList<String> commands;
    
    // Создаем новый класс
    public Data() {
	ranks = new HashMap<String, String>();
	ranks.put("max---855", "admin");
	ranks.put("dark_measures", "admin");
	phrases = new HashMap<String, String[]>();
	r = new Random();
	channels = new HashMap<String, String>();
	password = "CHANGEIT";
	commands = new ArrayList<String>();
    }
    
    public void addPhrase(String question, String[] answer) {
	phrases.put(question, answer);
    }
    
    public String getPhrase(String question) {
	if(phrases.containsKey(question)) {
	    return phrases.get(question)[r.nextInt(phrases.size())];
	} else {
	    return "none";
	}
    }
    
    public void addAdmin(String login) {
	ranks.put(login, "admin");
	save();
    }
    
    public void addModerator(String login) {
	ranks.put(login, "moderator");
	save();
    }
    
    public boolean isModerator(String login) {
	if(ranks.containsKey(login)) {
	    if(ranks.get(login).equals("admin") || ranks.get(login).equals("moderator")) {
		return true;
	    }
	}
	
	return false;
    }
    
    public boolean isAdmin(String login) {
	if(ranks.containsKey(login)) {
	    if(ranks.get(login).equals("admin")) {
		return true;
	    }
	}
	
	return false;
    }
    
    public String getChannel(String chatID) {
	if(channels.containsKey(chatID)) {
	    return channels.get(chatID);
	}
	
	return "none";
    }
    
    public void setChannel(String key, String channel) {
	channels.put(key, channel);
	save();
    }
    
    public String getPassword() {
	return password;
    }
    
    public boolean hasCommand(String cmd) {
	if(commands.contains(cmd)) {
	    return true;
	}
	
	return false;
    }
    
    public void addCommand(String cmd) {
	if(!commands.contains(cmd)) {
	    commands.add(cmd);
	}
	File f = new File("lua" + File.separator + cmd + ".lua");
	
	if(!f.exists()) {
	    try {
		System.out.println("Can't find file for " + cmd + "... So, let's create it!");
		FileWriter w = new FileWriter(f);
		BufferedWriter l = new BufferedWriter(w);
		
		l.write("function invoke(args, msg)");
		l.newLine();
		l.write("   -- args - array of arguments");
		l.newLine();
		l.write("   -- msg - java message object");
		l.newLine();
		l.write("   msg:answer(\"Yo, it's '"+cmd+"'! And this message sended directly from lua script... So, it's cool! \")");
		l.write("end");
		l.close();
	    } catch (IOException ex) {
		Logger.getLogger(Data.class.getName()).log(Level.SEVERE, "Can't create lua file for: " + cmd, ex);
	    }
	}
	
	save();
    }
    
    public void save() {
	try {
	    YamlWriter writer = new YamlWriter(new FileWriter("config.yml"));
	    writer.write(self);
	    writer.close();
	} catch (IOException ex) {
	    Logger.getLogger(Data.class.getName()).log(Level.WARNING, "Can't write config.yml!", ex);
	}
	
    }
    
}
