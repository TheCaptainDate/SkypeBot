/*
 * Copyright (C) 2012 Maksim Fomin (Date) (далее - автор)
 *
 * Данный программа - закрытое програмное обеспечение.
 * Вы НЕ имеете права использовать, изменять, дополнять или хранить ее, без разрешения автора!
 */

package com.thecaptaindate.skypebot;

import com.naef.jnlua.LuaState;
import com.skype.Chat;
import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.SkypeException;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ChatMessageListener extends ChatMessageAdapter {
    
    int x = 0;
    private static final Map<Character, String> charMap = new HashMap<Character, String>();

    static {
        charMap.put('а', "a");
        charMap.put('б', "b");
        charMap.put('в', "v");
        charMap.put('г', "g");
        charMap.put('д', "d");
        charMap.put('е', "e");
        charMap.put('ё', "e");
        charMap.put('ж', "zh");
        charMap.put('з', "z");
        charMap.put('и', "i");
        charMap.put('й', "i");
        charMap.put('к', "k");
        charMap.put('л', "l");
        charMap.put('м', "m");
        charMap.put('н', "n");
        charMap.put('о', "o");
        charMap.put('п', "p");
        charMap.put('р', "r");
        charMap.put('с', "s");
        charMap.put('т', "t");
        charMap.put('у', "u");
        charMap.put('ф', "f");
        charMap.put('х', "h");
        charMap.put('ц', "c");
        charMap.put('ч', "ch");
        charMap.put('ш', "sh");
        charMap.put('щ', "sh");
        charMap.put('ъ', "'");
        charMap.put('ы', "y");
        charMap.put('ь', "'");
        charMap.put('э', "e");
        charMap.put('ю', "u");
        charMap.put('я', "ya");

    }
    
    @Override
    public void chatMessageReceived(ChatMessage msg) throws SkypeException {
	if(msg.getType().equals(ChatMessage.Type.SAID)) {
	    if(x == 0) {
		x = x + 1;
		return;
	    } else if(x == 1) {
		x = 0;
	    }
	    
	    ArrayList<String> c = getCmd(msg.getContent());
	    Chat chat = msg.getChat();
	    
	    if(msg.getContent().length() < 2) {
		return;
	    }
	    
	    String perfix = msg.getContent().substring(0, 1);
	    String cmd = c.get(0).substring(1).toLowerCase();
	    c.remove(0);
	    
	    if(perfix.equals(">")) {
		/*if(!Data.self.isAdmin(msg.getSender().getId())) {
		    return;
		}*/
		
		if(cmd.equals("teach") & c.size() >= 2 & Data.self.isModerator(msg.getSenderId())) {
		    String question = c.get(0).toLowerCase();
		    List<String> l = c.subList(1, c.size());
		    Data.self.addPhrase(question, l.toArray(new String[l.size()]));
		} else if(cmd.equals("rank") & c.size() >= 2 & Data.self.isAdmin(msg.getSenderId())) {
		    if(c.get(1).equals("admin")) {
			Data.self.addAdmin(c.get(1));
		    } else if(c.get(1).equals("moderator")) {
			Data.self.addModerator(c.get(1));
		    }
		} else if(cmd.equals("channel") & c.size() >= 1) {
		    Data.self.setChannel(msg.getChat().getId(), c.get(0));
		} else if(cmd.equals("addcommand") & c.size() >= 1) {
		    Data.self.addCommand(c.get(0));
		}
	    } else if(perfix.equals("!")) {
		if(isRus(cmd)) {
		    transliterate(cmd);
		}
		
		if(Data.self.hasCommand(cmd)) {
		    File l = new File("lua" + File.separator + cmd + ".lua");
		    
		    if(l.exists()) {
			LuaState ls = new LuaState();
			try {
			    ls.load(new FileInputStream(l), cmd);
			    ls.openLibs();
			    ls.call(0, 0);
			    ls.getGlobal("invoke");
			    
			    ls.pushJavaObject(c);
			    ls.pushJavaObject(new LuaSkypeWrapper(msg));
			    ls.call(2, 0);
			    
			} catch (Exception ex) {
			    Logger.getLogger(ChatMessageListener.class.getName()).log(Level.SEVERE, "Some error with lua", ex);
			} finally {
			    ls.close();
			}
		    } else {
			Data.self.addCommand(cmd);
		    }
		}
	    } else if(cmd.equals("от,")) {
		chat.send(Data.self.getPhrase(concat(c, " ")));
	    }
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
    
    public String transliterate(String string) {
	StringBuilder transliteratedString = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            Character ch = string.charAt(i);
            String charFromMap = charMap.get(ch);
            if (charFromMap == null) {
                transliteratedString.append(ch);
            } else {
                transliteratedString.append(charFromMap);
            }
        }
        return transliteratedString.toString();
    }
    
    public boolean isRus(String line) {
	for (Map.Entry<Character, String> entry : charMap.entrySet()) {
	    Character character = entry.getKey();
	    
	    if(line.contains(character.toString())) {
		return true;
	    }
	}
	
	return false;
    }
}
