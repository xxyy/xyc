package io.github.xxyy.common.util;

import java.util.regex.Pattern;

import org.bukkit.ChatColor;

/**
 * Class that provides some static methods for chat formatting
 * 
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 */
public abstract class ChatHelper
{
    /**
     * If global mute is enabled. This is just storage and chat listeners have to implement this themselves.
     */
    public static boolean isGlobalMute = false;
    /**
     * Reason for golbal mute. This is just storage and chat listeners have to implement this themselves.
     */
    public static String gloMuReason = "";
    /**
     * The lavel of Ad detection. Values go from 0 (off) to 3 (most aggressive). To minimize failure with an acceptable rate
     * of messages tricking the filter, use 1.
     */
    public static short adDetectionLevel = 1;
    /**
     * Whether to replace point-like characters in ad detection. this includes '_', '-', etc.
     */
    public static boolean adDetectionReplacePointLikeChars = false;
    /**
     * How much of a message must be CAPITALIZED in order for it to be marked as Caps.
     */
    public static float percentForCaps = 50;
    /**
     * These characters will cause '&' to become {@link ChatColor#COLOR_CHAR} if followed by one of them.
     */
    public static String allowedChatColors = "012356789AaBbDdEeFfRr";

    /**
     * Converts a string for colorizing, using '&' as color char and {@link ChatHelper#allowedChatColors} for allowed colors.
     * @param message Message to colorize
     * @return Colorized message
     * @author <a href="http://xxyy.github.io/">xxyy</a>
     */
    public static String convertDefaultChatColors(String message)
    {
    	char[] b = message.toCharArray();
    	for (int i = 0; i < b.length - 1; i++) {
    	    if (b[i] == '&' && ChatHelper.allowedChatColors.indexOf(b[i+1]) > -1) {
    	        b[i] = ChatColor.COLOR_CHAR;
    	        b[i+1] = Character.toLowerCase(b[i+1]);
    	    }
    	}
    	return new String(b);
    }

    /**
     * Converts a string for colorizing, using '&' as color char and "012356789AaBbCcDdEeFfKkRr" for allowed colors.
     * @param message Message to colorize
     * @return Colorized message
     * @author <a href="http://xxyy.github.io/">xxyy</a>
     */
    public static String convertStandardColors(String message)
    {
    	char[] b = message.toCharArray();
    	for (int i = 0; i < b.length - 1; i++) {
    	    if (b[i] == '&' && "012356789AaBbCcDdEeFfKkRr".indexOf(b[i+1]) > -1) {
    	        b[i] = ChatColor.COLOR_CHAR;
    	        b[i+1] = Character.toLowerCase(b[i+1]);
    	    }
    	}
    	return new String(b);
    }

    /**
     * Detects if a message is an advertisement (aka a server or internet address) based on {@link ChatHelper#adDetectionLevel}.
     * @param msg Message to check
     * @return If the message is probably an ad.
     * @author <a href="http://xxyy.github.io/">xxyy</a>
     */
    public static boolean isAdvertisement(String msg)
    {
    	if(ChatHelper.adDetectionLevel >= 2) {
            msg = msg.replace(" ", "");
        }
    	msg = msg.toLowerCase();
    	if(msg.contains("minotopia") || msg.contains("minecrime") || msg.contains("pvpwar") || msg.contains("living-bots")) return false;
    	msg = msg.replaceAll("(\\(punkt\\)|\\(dot\\)|\\s\\.\\s)", ".");
    	if(ChatHelper.adDetectionReplacePointLikeChars) {
            msg = msg.replaceAll("[-_\\|~]", ".");
        }
    	if(msg.matches("(.*)(\\d{1,3}\\.\\d{1,3}.\\d{1,3}.\\d{1,3})+(.*)")) return true;
    	if(ChatHelper.adDetectionLevel >= 3) {if(msg.matches("(.*)\\.([^-\\./]{1}[A-Za-z]{1,4})(.*)")) return true;}
    	if(ChatHelper.adDetectionLevel < 1) return false;
    	if(Pattern.compile("\\.(me|de|at|to|tk|eu|com|net|org|ly)\\b", Pattern.CASE_INSENSITIVE)
    	        .matcher(msg).find()) return true;
    	return false;
    }

    /**
     * Checks if a message is CAPTIALIZED, based on {@link ChatHelper#percentForCaps}.
     * @param msg Message to check
     * @return Whether {@link ChatHelper#percentForCaps}% of this message are CAPITALIZED. 
     * @author <a href="http://xxyy.github.io/">xxyy</a>
     */
    public static boolean isCaps(String msg)
    {
    	if(ChatHelper.percentForCaps <= 0) return false;
    	if(msg.length() <= 5) return false;
    	int reqCaps = (int) (msg.length() * (ChatHelper.percentForCaps/100F));
    	int capsCaught = 0;
    	for(int i = 0; i < msg.length();i++){
    		if(Character.isUpperCase(msg.charAt(i))){
    			capsCaught++;
    			if(capsCaught >= reqCaps) return true;
    		}
    	}
    	return false;
    }

    /**
     * replaces some common insults with nice replacements (german!) and some other things (for example '<3').
     * Remember to ship your plugin in UTF-8 if you use this method!
     * @param msg Message to use
     * @return Censored and prettified message
     * @author <a href="http://xxyy.github.io/">xxyy</a>
     */
    public static String replaceSpecialWords(String msg)
    {
    	msg = msg.replaceAll("[nN][oO0u\\(\\)]+[bB]","nette Person");//noob
    	msg = msg.replaceAll("[sS][pP]+[aA4]+[sS]+[tT]","sehr nette Person");//spast
    	msg = msg.replaceAll("[hH]+[uU]+[rR]*[Ee]*[Nn]*[Ss]+[Oo]+[Hh]*[Nn]*","Sohn einer lieben Person");//huso
    	msg = msg.replaceAll("(?i)[w]+[i]+[chsx]+[e]+[r]+", "belebte Person");
    	msg = msg.replaceAll("(?i)[a]+[r]+[s]+[c]+[h]+[l]+[o]+[c]+[h]+", "elegante Person");//alo
    	msg = msg.replaceAll("[?!]{2,}", ".");
    	
    	msg = msg.replaceAll("<3", "❤");
    	msg = msg.replaceAll(";\\)", " ツ");
    	msg = msg.replaceAll("\\!", "❢");
    	msg = msg.replaceFirst("%", "%%");
    
    	return msg;
    }
    
}
