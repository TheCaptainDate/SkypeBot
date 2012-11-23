/*
 * Copyright (C) 2012 Maksim Fomin (Date) (далее - автор)
 *
 * Данный программа - закрытое програмное обеспечение.
 * Вы НЕ имеете права использовать, изменять, дополнять или хранить ее, без разрешения автора!
 */

package com.thecaptaindate.skypebot;

import com.skype.Chat;
import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.SkypeException;
import java.util.ArrayList;
import java.util.List;


public class ChatMessageListener extends ChatMessageAdapter {
    
    int x = 0;
    
    @Override
    public void chatMessageReceived(ChatMessage msg) throws SkypeException {
	if(msg.getType().equals(ChatMessage.Type.SAID)) {
	    if(x == 1) {
		x = 0;
		return;
	    }
	    
	    String cmd = msg.getContent().toLowerCase();
	    ArrayList<String> c = getCmd(cmd);
	    Chat chat = msg.getChat();
	    
	    if(cmd.startsWith(">")) {
		if(!SkypeBot.data.isAdmin(msg.getSender().getId())) {
		    return;
		}
		
		c.set(0, c.get(0).substring(1));
		
		if(c.get(0).equals("teach") & c.size() >= 3) {
		    String question = c.get(1);
		    c.remove(0);
		    c.remove(1);
		    SkypeBot.data.addPhrase(question, c.toArray(new String[c.size()]));
		}
		
		if(c.get(0).equals("admin"))
	    } else if(cmd.startsWith("!")) {
		c.set(0, c.get(0).substring(1));
		
	    } else if(c.get(0).equals("бот,")) {
		c.remove(0);
		chat.send(SkypeBot.data.getPhrase(concat(c, " ")));
	    }
	    
	    x = x + 1;
	}
    }
    
    private ArrayList<String> getCmd(String line) {
	char[] chars = line.toCharArray();
	boolean a = false;
	boolean b = false;
	ArrayList<String> args = new ArrayList<String>();
	String t = "";
	
	
	for (int i = 0; i < chars.length; i++) {
	    Character c = Character.valueOf(chars[i]);
	    
	    if(c.toString().equals(" ") && !a) {
		if(!b) {
		    args.add(t);
		    t = "";
		} else {
		    b = false;
		}
	    } else if(c.toString().equals("\"")) {
		if(a) {
		    args.add(t);
		    a = false;
		    b = true;
		    t = "";
		} else {
		    a = true;
		}
	    } else {
		t = t + c.toString();
		
		if(i + 1 == chars.length) {
		    args.add(t);
		}
	    }
	}
	
	if(args.isEmpty()) {
	    args.add(line);
	}
	
	return args;
    }
    
    public static String concat(List<String> array, String separator) {
	String result = "";
	
	for (int i = 0; i < array.size(); i++) {
	    result = result.concat(array.get(i));
	    
	    if(i + 1 != array.size()) {
		result = result.concat(" ");
	    }
	}
	
	return result;
    }
}
