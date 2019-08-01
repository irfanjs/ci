package org.stc.ci;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import net.sourceforge.pmd.rules.strings.InefficientEmptyStringCheck;

public class CodeCollaboratorBugCount {
	
int bugCount;
int reviewid;
String line;
String finalbugCnt ="0";
String realtime;


public static void main(String args[])
{
	
	CodeCollaboratorBugCount cc = new CodeCollaboratorBugCount();
	cc.getBugCount(23362);
}
public int getBugCount(int reviewid)
{
	this.reviewid = reviewid;
	Logger.getLogger("").warning("review id is " + this.reviewid);
	System.out.println("review id is " + this.reviewid);
	
	String command1 = "//home//bvcontrolbuild//ccollab-cmdline//ccollab login https://tools-review.engba.symantec.com irfan_sayed Anna_12345";
	
	//String command1 = "C:\\collab\\ccollab.exe login https://tools-review.engba.symantec.com irfan_sayed Anna_12345";
	//String command1 = "hostname";
	
//	String command = "ccollab admin review-xml " + this.reviewid + " --xpath count(//reviews/review/conversations/conversation)";
	String command = "//home//bvcontrolbuild//ccollab-cmdline//ccollab admin review-xml " + this.reviewid + " --xpath " + '"' + "count(//reviews/review/conversations/conversation)" + '"';
	
	Process p;
	try {
		Logger.getLogger("").warning("in try block for " + command1);
		p = Runtime.getRuntime().exec(command1);
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		Logger.getLogger("").warning("in try block for second " + command1);
		System.out.println("in try block for " + command1);
                    			
		/*if(true)
			throw new Exception("This is my test exception");*/
		
		while ((line = reader.readLine())!= null) {
			//System.out.println(line + "\n");
			Logger.getLogger("").warning(line + "\n");
			System.out.println("line is " + line + "\n");
		}
		p.destroy();
	} catch (Exception e) {
		Logger.getLogger("").warning("Exception : " + e.getMessage() +  " -  stacktrace - "   + ExceptionUtil.stringStackTrace(e));
	//	com.symantec.dcsc.umc.exceptionhandler.BaseExceptionHandler.handleLog(e, log);
		
	}
	
try {
		
		p = Runtime.getRuntime().exec(command);
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		Logger.getLogger("").warning("in try block for " + command);
		System.out.println("in try block for " + command);
                    			
		while ((line = reader.readLine())!= null) {
			//System.out.println(line + "\n");
			Logger.getLogger("").warning("line is " + line + "\n");
			System.out.println("line is " + line + "\n");
			finalbugCnt = line;
			System.out.println("final bug count" + finalbugCnt);
			
		}
		
	} catch (Exception e) {
		Logger.getLogger("").warning("Exception : " + e.getMessage() +  " -  stacktrace - "   + ExceptionUtil.stringStackTrace(e));	
	}

	System.out.println("convert to integer " + finalbugCnt);
	bugCount = Integer.parseInt(finalbugCnt);
	return bugCount;
	
}

public int getTotaltimeSpentperReviewId(int reviewid)
{
	
	int hourtime = 0;
	int mintime = 0;
	float sectime;
	int time = 0;

	this.reviewid = reviewid;
	
	String commandtime = "//home//bvcontrolbuild//ccollab-cmdline//ccollab admin review-xml " + this.reviewid + " --xpath " + '"'+ "//reviews/review/metrics/total-person-time/text()" + '"';
	
	Process p;
	try {
		
		Logger.getLogger("").warning("in try block for " + commandtime );
		p = Runtime.getRuntime().exec(commandtime);
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		while ((line = reader.readLine())!= null) {
			//System.out.println(line + "\n");
			Logger.getLogger("").warning(line + "\n");
			System.out.println("line is " + line + "\n");
			realtime = line;
		}
		p.destroy();
	} catch (Exception e) {
		Logger.getLogger("").warning("Exception : " + e.getMessage() +  " -  stacktrace - "   + ExceptionUtil.stringStackTrace(e));
	//	com.symantec.dcsc.umc.exceptionhandler.BaseExceptionHandler.handleLog(e, log);
		
	}
	Logger.getLogger("").warning("actual time is " + realtime);
	String[] timestring = realtime.split(":");
	Logger.getLogger("").warning("first position " + timestring[0] );
	
	if(timestring[0].equals("00"))
	{
		Logger.getLogger("").warning(" hour is 0");
	}
	
	else
	{
		hourtime = Integer.parseInt(timestring[0]);
		hourtime = hourtime * 60;
		time=hourtime;
		return time;
	}
	
	if (timestring[1].equals("00"))
	{
		Logger.getLogger("").warning("min is 0");
	}
	
	else
	 {
		if (hourtime > 0)
		{
			time=hourtime;
			return time;
		}
		else
		{
			mintime = Integer.parseInt(timestring[1]);
			time = mintime;
			return time;
			
		}
		
	 }
	
	if (timestring[2].equals("00"))
	{
		Logger.getLogger("").warning("sec is 0");
	}
	 
	 else
	 {
		 if (hourtime > 0)
		 {
				time = hourtime;
				return time;		 
		 }
		 else if (mintime > 0)
		 {
			 time = mintime;
				return time;
		 }
		 else
		 {
			 sectime = Integer.parseInt(timestring[2]);
			 sectime = sectime / 60;
			 time = (int) sectime; 
			 return time;
		 }
		 
	 }	
	return time;
}

}
