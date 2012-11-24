/*
 * Copyright (C) 2012 Maksim Fomin (Date) (далее - автор)
 *
 * Данный программа - закрытое програмное обеспечение.
 * Вы НЕ имеете права использовать, изменять, дополнять или хранить ее, без разрешения автора!
 */

package com.thecaptaindate.skypebot;

import com.skype.ChatMessage;


public class LuaSkypeWrapper {
    
    private ChatMessage msg;
    
    public LuaSkypeWrapper(ChatMessage msg) {
	this.msg = msg;
    }
    
    public void answer(String m) {
	try {
	    msg.getChat().send(m);
	} catch(Exception e) {
	    System.out.println("Skype error: " + e);
	}
    }

}
