/*
 * Copyright (C) 2012 Maksim Fomin (Date) (далее - автор)
 *
 * Данный программа - закрытое програмное обеспечение.
 * Вы НЕ имеете права использовать, изменять, дополнять или хранить ее, без разрешения автора!
 */

package com.thecaptaindate.skypebot;

import com.esotericsoftware.yamlbeans.YamlWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Data {
    
    private ArrayList<String> admins;
    private HashMap<String, String[]> phrases;
    private Random r;
    public static Data self;
    private HashMap<String, String> channels;
    private String password;
    
    // Создаем новый класс
    public Data() {
	admins = new ArrayList<String>();
	admins.add("max---855");
	phrases = new HashMap<String, String[]>();
	r = new Random();
	channels = new HashMap<String, String>();
	password = "CHANGEIT";
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
	admins.add(login);
	save();
    }
    
    public boolean isAdmin(String login) {
	if(admins.contains(login)) {
	    return true;
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
    
    public void save() {
	try {
	    YamlWriter writer = new YamlWriter(new FileWriter("config.yml"));
	    writer.write(this);
	    writer.close();
	} catch (IOException ex) {
	    Logger.getLogger(Data.class.getName()).log(Level.WARNING, "Can't write config.yml!", ex);
	}
	
    }
    
}
