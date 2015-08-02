package com.inflectionPoint.interpop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Settings {

	public static boolean mute = false;
	public static boolean firstRun = true;
	public static int[] highscores1M = new int[] {100, 80, 60, 40, 20};
	public static int[] highscores2M = new int[] {100, 80, 60, 40, 20};
	public static int[] highscores3M = new int[] {100, 80, 60, 40, 20};
	public static float[] highscores500S = new float[] {210, 240, 270, 300, 330};
	public static float[] highscores1000S = new float[] {210, 240, 270, 300, 330};
	public static float[] highscores2000S = new float[] {270, 300, 330, 360, 390};
	public static float[] highscoresScorePos = new float[] {100, 80, 60, 40, 20};
	public static boolean isBoundaryUnlocked = false;
	public static boolean isPowerUpUnlocked = false;
	public static boolean isPowerDownUnlocked = false;
	public static boolean isSPNormalUnlocked = false;
	public static boolean isSPBlackUnlocked = false;
	public static boolean isSPBlackNegUnlocked = false;
	public static boolean isInvertUnlocked = false;
	public static boolean isConvertNormalUnlocked = false;
	public static boolean isConvertBlackUnlocked = false;
	
	public static int[] highscores1MD = new int[] {100, 80, 60, 40, 20};
	public static int[] highscores2MD = new int[] {100, 80, 60, 40, 20};
	public static int[] highscores3MD = new int[] {100, 80, 60, 40, 20};
	public static float[] highscores500SD = new float[] {210, 240, 270, 300, 330};
	public static float[] highscores1000SD = new float[] {210, 240, 270, 300, 330};
	public static float[] highscores2000SD = new float[] {270, 300, 330, 360, 390};
	public static float[] highscoresScorePosD = new float[] {100, 80, 60, 40, 20};
	public static boolean isBoundaryUnlockedD = false;
	public static boolean isPowerUpUnlockedD = false;
	public static boolean isPowerDownUnlockedD = false;
	public static boolean isSPNormalUnlockedD = false;
	public static boolean isSPBlackUnlockedD = false;
	public static boolean isSPBlackNegUnlockedD = false;
	public static boolean isInvertUnlockedD = false;
	public static boolean isConvertNormalUnlockedD = false;
	public static boolean isConvertBlackUnlockedD = false;
	
	public static void saveSettings(FileManager manager) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(manager.writeFile("interpop")));
			writer.write(Boolean.toString(mute));
			writer.newLine();
			writer.write(Boolean.toString(firstRun));
			writer.newLine();
			for(int i = 0; i < 5; i++) {
				writer.write(Integer.toString(highscores1M[i]));
				writer.newLine();
			}
			for(int i = 0; i < 5; i++) {
				writer.write(Integer.toString(highscores2M[i]));
				writer.newLine();
			}
			for(int i = 0; i < 5; i++) {
				writer.write(Integer.toString(highscores3M[i]));
				writer.newLine();
			}
			for(int i = 0; i < 5; i++) {
				writer.write(Float.toString(highscores500S[i]));
				writer.newLine();
			}
			for(int i = 0; i < 5; i++) {
				writer.write(Float.toString(highscores1000S[i]));
				writer.newLine();
			}
			for(int i = 0; i < 5; i++) {
				writer.write(Float.toString(highscores2000S[i]));
				writer.newLine();
			}
			for(int i = 0; i < 5; i++) {
				writer.write(Float.toString(highscoresScorePos[i]));
				writer.newLine();
			}
			writer.write(Boolean.toString(isBoundaryUnlocked));
			writer.newLine();
			writer.write(Boolean.toString(isPowerUpUnlocked));
			writer.newLine();
			writer.write(Boolean.toString(isPowerDownUnlocked));
			writer.newLine();
			writer.write(Boolean.toString(isSPNormalUnlocked));
			writer.newLine();
			writer.write(Boolean.toString(isSPBlackUnlocked));
			writer.newLine();
			writer.write(Boolean.toString(isSPBlackNegUnlocked));
			writer.newLine();
			writer.write(Boolean.toString(isInvertUnlocked));
			writer.newLine();
			writer.write(Boolean.toString(isConvertNormalUnlocked));
			writer.newLine();
			writer.write(Boolean.toString(isConvertBlackUnlocked));
			writer.newLine();
		} catch (IOException e) {
		} finally {
			try {
				if(writer != null)
					writer.close();
			} catch (IOException e){
			}
		}
	}
	
	public static void saveDefSettings(FileManager manager) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(manager.writeFile("interpop")));
			writer.write(Boolean.toString(mute));
			writer.newLine();
			writer.write(Boolean.toString(firstRun));
			writer.newLine();
			for(int i = 0; i < 5; i++) {
				writer.write(Integer.toString(highscores1MD[i]));
				writer.newLine();
			}
			for(int i = 0; i < 5; i++) {
				writer.write(Integer.toString(highscores2MD[i]));
				writer.newLine();
			}
			for(int i = 0; i < 5; i++) {
				writer.write(Integer.toString(highscores3MD[i]));
				writer.newLine();
			}
			for(int i = 0; i < 5; i++) {
				writer.write(Float.toString(highscores500SD[i]));
				writer.newLine();
			}
			for(int i = 0; i < 5; i++) {
				writer.write(Float.toString(highscores1000SD[i]));
				writer.newLine();
			}
			for(int i = 0; i < 5; i++) {
				writer.write(Float.toString(highscores2000SD[i]));
				writer.newLine();
			}
			for(int i = 0; i < 5; i++) {
				writer.write(Float.toString(highscoresScorePosD[i]));
				writer.newLine();
			}
			writer.write(Boolean.toString(isBoundaryUnlockedD));
			writer.newLine();
			writer.write(Boolean.toString(isPowerUpUnlockedD));
			writer.newLine();
			writer.write(Boolean.toString(isPowerDownUnlockedD));
			writer.newLine();
			writer.write(Boolean.toString(isSPNormalUnlockedD));
			writer.newLine();
			writer.write(Boolean.toString(isSPBlackUnlockedD));
			writer.newLine();
			writer.write(Boolean.toString(isSPBlackNegUnlockedD));
			writer.newLine();
			writer.write(Boolean.toString(isInvertUnlockedD));
			writer.newLine();
			writer.write(Boolean.toString(isConvertNormalUnlockedD));
			writer.newLine();
			writer.write(Boolean.toString(isConvertBlackUnlockedD));
			writer.newLine();
		} catch (IOException e) {
		} finally {
			try {
				if(writer != null)
					writer.close();
			} catch (IOException e){
			}
		}
	}
	
	public static void loadSettings(FileManager manager) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(manager.openFile("interpop")));
			mute = Boolean.parseBoolean(reader.readLine());
			firstRun = Boolean.parseBoolean(reader.readLine());
			for(int i = 0; i < 5; i++) 
				highscores1M[i] = Integer.parseInt(reader.readLine());
			for(int i = 0; i < 5; i++) 
				highscores2M[i] = Integer.parseInt(reader.readLine());
			for(int i = 0; i < 5; i++) 
				highscores3M[i] = Integer.parseInt(reader.readLine());
			for(int i = 0; i < 5; i++) 
				highscores500S[i] = Float.parseFloat(reader.readLine());
			for(int i = 0; i < 5; i++) 
				highscores1000S[i] = Float.parseFloat(reader.readLine());
			for(int i = 0; i < 5; i++) 
				highscores2000S[i] = Float.parseFloat(reader.readLine());
			for(int i = 0; i < 5; i++) 
				highscoresScorePos[i] = Float.parseFloat(reader.readLine());
			isBoundaryUnlocked = Boolean.parseBoolean(reader.readLine());
			isPowerUpUnlocked = Boolean.parseBoolean(reader.readLine());
			isPowerDownUnlocked = Boolean.parseBoolean(reader.readLine());
			isSPNormalUnlocked = Boolean.parseBoolean(reader.readLine());
			isSPBlackUnlocked = Boolean.parseBoolean(reader.readLine());
			isSPBlackNegUnlocked = Boolean.parseBoolean(reader.readLine());
			isInvertUnlocked = Boolean.parseBoolean(reader.readLine());
			isConvertNormalUnlocked = Boolean.parseBoolean(reader.readLine());
			isConvertBlackUnlocked = Boolean.parseBoolean(reader.readLine());
		} catch (IOException e) {
		} catch (NumberFormatException e) {
		} finally {
			try {
				if(reader != null)
					reader.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static boolean appendScore(int mode, int score) {
		if(mode == GameRenderer.GAME_TIME_1M) {
			int i;
			int j;
			for(i = 0; i < 5; i++) {
				if(score > highscores1M[i])
					break;
			}
			if(i == 4) {
				highscores1M[i] = score;
				return true;
			}
			else if(i == 5)
				return false;
			else {
				for(j = 3; j >= i; j--)
					highscores1M[j + 1] = highscores1M[j];
				highscores1M[++j] = score;
				return true;
			}
		}
		else if(mode == GameRenderer.GAME_TIME_2M) {
			int i;
			int j;
			for(i = 0; i < 5; i++) {
				if(score > highscores2M[i])
					break;
			}
			if(i == 4) {
				highscores2M[i] = score;
				return true;
			}
			else if(i == 5)
				return false;
			else {
				for(j = 3; j >= i; j--)
					highscores2M[j + 1] = highscores2M[j];
				highscores2M[++j] = score;
				return true;
			}
		}
		else if(mode == GameRenderer.GAME_TIME_3M) {
			int i;
			int j;
			for(i = 0; i < 5; i++) {
				if(score > highscores3M[i])
					break;
			}
			if(i == 4) {
				highscores3M[i] = score;
				return true;
			}
			else if(i == 5)
				return false;
			else {
				for(j = 3; j >= i; j--)
					highscores3M[j + 1] = highscores3M[j];
				highscores3M[++j] = score;
				return true;
			}
		}
		else if(mode == GameRenderer.GAME_SCORE_POSITIVE) {
			int i;
			int j;
			for(i = 0; i < 5; i++) {
				if(score > highscoresScorePos[i])
					break;
			}
			if(i == 4) {
				highscoresScorePos[i] = score;
				return true;
			}
			else if(i == 5)
				return false;
			else {
				for(j = 3; j >= i; j--)
					highscoresScorePos[j + 1] = highscoresScorePos[j];
				highscoresScorePos[++j] = score;
				return true;
			}
		}
		return false;
	}
	
	public static boolean appendTime(int mode, float time) {
		if(mode == GameRenderer.GAME_SCORE_500) {
			int i;
			int j;
			for(i = 0; i < 5; i++) {
				if(time < highscores500S[i])
					break;
			}
			if(i == 4) {
				highscores500S[i] = time;
				return true;
			}
			else if(i == 5)
				return false;
			else {
				for(j = 3; j >= i; j--)
					highscores500S[j + 1] = highscores500S[j];
				highscores500S[++j] = time;
				return true;
			}
		}
		else if(mode == GameRenderer.GAME_SCORE_1000) {
			int i;
			int j;
			for(i = 0; i < 5; i++) {
				if(time < highscores1000S[i])
					break;
			}
			if(i == 4) {
				highscores1000S[i] = time;
				return true;
			}
			else if(i == 5)
				return false;
			else {
				for(j = 3; j >= i; j--)
					highscores1000S[j + 1] = highscores1000S[j];
				highscores1000S[++j] = time;
				return true;
			}
		}
		else if(mode == GameRenderer.GAME_SCORE_2000) {
			int i;
			int j;
			for(i = 0; i < 5; i++) {
				if(time < highscores2000S[i])
					break;
			}
			if(i == 4) {
				highscores2000S[i] = time;
				return true;
			}
			else if(i == 5)
				return false;
			else {
				for(j = 3; j >= i; j--)
					highscores2000S[j + 1] = highscores2000S[j];
				highscores2000S[++j] = time;
				return true;
			}
		}
		return false;
	}
}
