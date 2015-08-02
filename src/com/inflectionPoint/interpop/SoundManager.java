package com.inflectionPoint.interpop;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundManager {
	
	static final int BUBBLE_POP1 = 47001;
	static final int BUBBLE_POP2 = 47002;
	static final int BUBBLE_POP3 = 47003;
	static final int BUBBLE_POP4 = 47004;
	static final int GAMES = 47005;
	static final int POWERUP = 47006;
	static final int POWERDOWN = 47007;
	static final int POWERHIT = 47008;
	static final int LEVELUP = 47009;
	static final int GAMEOVER = 47010;
	static final int METAL = 47011;
	static final int MENU1 = 47012;
	static final int MENU2 = 47013;
	static final int RESET = 47014;
	
	private Context context;
	
	private static SoundPool soundPool;
	
	private static int bubblePop1ID;
	private static int bubblePop2ID;
	private static int bubblePop3ID;
	private static int bubblePop4ID;
	private static int gamesID;
	private static int powerUpID;
	private static int powerDownID;
	private static int powerHitID;
	private static int levelUpID;
	private static int gameOverID;
	private static int metalID;
	private static int menu1ID;
	private static int menu2ID;
	private static int resetID;
	
	public SoundManager(Context context, int channels) {
		this.context = context;
		SoundManager.soundPool = new SoundPool(channels, AudioManager.STREAM_MUSIC, 0);
		
		SoundManager.bubblePop1ID = loadSound("pop1.ogg");
		SoundManager.bubblePop2ID = loadSound("pop2.ogg");
		SoundManager.bubblePop3ID = loadSound("pop3.ogg");
		SoundManager.bubblePop4ID = loadSound("pop4.ogg");
		SoundManager.gamesID = loadSound("games.ogg");
		SoundManager.powerUpID = loadSound("powerUp.ogg");
		SoundManager.powerDownID = loadSound("powerDown.ogg");
		SoundManager.powerHitID = loadSound("powerHit.ogg");
		SoundManager.levelUpID = loadSound("levelUp.ogg");
		SoundManager.gameOverID = loadSound("gameOver.ogg");
		SoundManager.metalID = loadSound("metal.ogg");
		SoundManager.menu1ID = loadSound("mainmenu1.ogg");
		SoundManager.menu2ID = loadSound("mainmenu2.ogg");
		SoundManager.resetID = loadSound("reset.ogg");
	}
	
	public int loadSound(String fileName) {
		try {
			AssetManager assetManager = context.getAssets();
			AssetFileDescriptor descriptor = assetManager.openFd(fileName);
			return soundPool.load(descriptor, 1);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public void play(int sound) {
		if(sound == BUBBLE_POP1) {
			if(bubblePop1ID != -1)
				soundPool.play(bubblePop1ID, 1, 1, 0, 0, 1);
		}
		else if(sound == BUBBLE_POP2) {
			if(bubblePop2ID != -1)
				soundPool.play(bubblePop2ID, 1, 1, 0, 0, 1);
		}
		if(sound == BUBBLE_POP3) {
			if(bubblePop3ID != -1)
				soundPool.play(bubblePop3ID, 1, 1, 0, 0, 1);
		}
		else if(sound == BUBBLE_POP4) {
			if(bubblePop4ID != -1)
				soundPool.play(bubblePop4ID, 1, 1, 0, 0, 1);
		}
		else if(sound == GAMES) {
			if(gamesID != -1)
				soundPool.play(gamesID, 1, 1, 0, 0, 1);
		}
		else if(sound == POWERUP) {
			if(powerUpID != -1)
				soundPool.play(powerUpID, 1, 1, 0, 0, 1);
		}
		else if(sound == POWERDOWN) {
			if(powerDownID != -1)
				soundPool.play(powerDownID, 1, 1, 0, 0, 1);
		}
		else if(sound == POWERHIT) {
			if(powerHitID != -1)
				soundPool.play(powerHitID, 1, 1, 0, 0, 1);
		}
		else if(sound == LEVELUP) {
			if(levelUpID != -1)
				soundPool.play(levelUpID, 1, 1, 0, 0, 1);
		}
		else if(sound == GAMEOVER) {
			if(gameOverID != -1)
				soundPool.play(gameOverID, 1, 1, 0, 0, 1);
		}
		else if(sound == METAL) {
			if(metalID != -1)
				soundPool.play(metalID, 1, 1, 0, 0, 1);
		}
		else if(sound == MENU1) {
			if(menu1ID != -1)
				soundPool.play(menu1ID, 1, 1, 0, 0, 1);
		}
		else if(sound == MENU2) {
			if(menu2ID != -1)
				soundPool.play(menu2ID, 1, 1, 0, 0, 1);
		}
		else if(sound == RESET) {
			if(resetID != -1)
				soundPool.play(resetID, 1, 1, 0, 0, 1);
		}
	}
	
	public void unloadSound(int sound) {
		if(sound == BUBBLE_POP1) {
			if(bubblePop1ID != -1) {
				soundPool.unload(bubblePop1ID);
				bubblePop1ID = -1;
			}
		}
		else if(sound == BUBBLE_POP2) {
			if(bubblePop2ID != -1) {
				soundPool.unload(bubblePop2ID);
				bubblePop2ID = -1;
			}
		}
		else if(sound == GAMES) {
			if(gamesID != -1) {
				soundPool.unload(gamesID);
				gamesID = -1;
			}
		}
		else if(sound == POWERUP) {
			if(powerUpID != -1) {
				soundPool.unload(powerUpID);
				powerUpID = -1;
			}
		}
		else if(sound == POWERDOWN) {
			if(powerDownID != -1) {
				soundPool.unload(powerDownID);
				powerDownID = -1;
			}
		}
		else if(sound == POWERHIT) {
			if(powerHitID != -1) {
				soundPool.unload(powerHitID);
				powerHitID = -1;
			}
		}
		else if(sound == LEVELUP) {
			if(levelUpID != -1) {
				soundPool.unload(levelUpID);
				levelUpID = -1;
			}
		}
		else if(sound == GAMEOVER) {
			if(gameOverID != -1) {
				soundPool.unload(gameOverID);
				gameOverID = -1;
			}
		}
		else if(sound == METAL) {
			if(metalID != -1) {
				soundPool.unload(metalID);
				metalID = -1;
			}
		}
		else if(sound == MENU1) {
			if(menu1ID != -1) {
				soundPool.unload(menu1ID);
				menu1ID = -1;
			}
		}
		else if(sound == MENU2) {
			if(menu2ID != -1) {
				soundPool.unload(menu2ID);
				menu2ID = -1;
			}
		}
		else if(sound == RESET) {
			if(resetID != -1) {
				soundPool.unload(resetID);
				resetID = -1;
			}
		}
	}
}
