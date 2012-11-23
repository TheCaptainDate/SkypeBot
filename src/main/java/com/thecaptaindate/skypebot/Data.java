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
    
    // Создаем новый класс
    public Data() {
	admins = new ArrayList<String>();
	phrases = new HashMap<String, String[]>();
	r = new Random();
    }
    
    public void addPhrase(String question, String[] answer) {
	System.out.println("Save:" + question + ";");
	phrases.put(question, answer);
    }
    
    public String getPhrase(String question) {
	System.out.println("Get:" + question + ";");
	
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
