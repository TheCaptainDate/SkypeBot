/*
 * Copyright (C) 2012 LoneIsland Dev Team
 *
 * Данный программа (игровой режим, далее - "гейммод) - закрытое програмное обеспечение.
 * Вы НЕ имеете права использовать, изменять, дополнять или хранить его (гейммод)!
 * Данный гейммод принадлежит команде LoneIsland Dev Team (https://github.com/TheCaptainDate/LoneIsland)
 * Менеджером проекта является Maksim 'Date' Fomin (hellow68@gmail.com)
 */

package com.thecaptaindate.skypebot.lua;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;
import com.skype.Skype;


public class LuaSend implements NamedJavaFunction {

    public String getName() {
	return "bSend";
    }

    public int invoke(LuaState l) {
	String t = l.checkString(1);
	String m = l.checkString(2);
	
	try {
	    Skype.chat(t).send(m);
	} catch (Exception e) {
	    System.out.println("Error with skype: " + e);
	}
	
	return 1;
    }

}
