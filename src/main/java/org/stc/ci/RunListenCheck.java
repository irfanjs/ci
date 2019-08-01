package org.stc.ci;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.sonyericsson.jenkins.plugins.bfa.model.FoundFailureCause;
import com.sonyericsson.jenkins.plugins.bfa.model.FailureCauseBuildAction;

@SuppressWarnings("rawtypes")
@Extension(ordinal = RunListenCheck.ORDINAL)
public class RunListenCheck extends RunListener<AbstractBuild> {
	int sizecse = 0;
	int cse = 0;
	String failurestr = null;
	int maxid = 0;
	int bldno;
	String mdlname;

	public static final int ORDINAL = 11002;
	private static final Logger logger = Logger.getLogger(RunListenCheck.class.getName());
	@Override
	public void onCompleted(AbstractBuild build, TaskListener listener) {
		
		bldno = build.getNumber();
		mdlname = build.getProject().getName();

		listener.getLogger().println("in new RunListener ");
		for (Action buildAction : build.getActions()){
			if (buildAction instanceof FailureCauseBuildAction) {
				FailureCauseBuildAction ta = (FailureCauseBuildAction)buildAction;

				listener.getLogger().println("in cause loop");
				listener.getLogger().println("in cause loop2");
				List<FoundFailureCause> failurecause = ta.getFoundFailureCauses();
				sizecse = failurecause.size();
				listener.getLogger().println("size is " + sizecse );
				if (sizecse > 0)
				{
					Iterator<FoundFailureCause> itrcse = failurecause.iterator();
					Iterator<FoundFailureCause> itrcse1 = failurecause.iterator();
					
					
					while (itrcse.hasNext()) {
						listener.getLogger().println("the " + cse++ + " position value is: " + itrcse.next().getName());
					//	failurestr = itrcse.next().getName();
					}

					while (itrcse1.hasNext()) {
						//listener.getLogger().println("the " + cse++ + " position value is: " + itrcse.next().getName());
						failurestr = itrcse1.next().getName();
					}
					listener.getLogger().println("updating database for reason");
					// get the last record from buildinfo table
					BuildInfoNightly bi = new BuildInfoNightly();
					try {
						listener.getLogger().println("in try block");
						maxid = bi.getLastRecordFromBuildinfo(bldno,mdlname);
						listener.getLogger().println("maxid is " + maxid);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// update reason for failure in buildinfo	

					try {
						listener.getLogger().println("updating database " + failurestr + maxid );
						bi.UpdateReasonForLastRecord(failurestr, maxid);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		

				}
			}
		}
	}
}