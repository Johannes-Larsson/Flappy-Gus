package com.JLarsson.FlappyGus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class HighscoreHandler {
	
	Preferences prefs;
	final String key = "highscore";
	
	public HighscoreHandler(){
		prefs = Gdx.app.getPreferences("GusPrefs");
	}
	
	public void Set(int highscore) {
		prefs.putInteger(key, highscore);
		prefs.flush();
	}
	
	public int Get() {
		if(prefs.contains(key)){
		return prefs.getInteger(key);
		}
		else return 0;
	}
}
