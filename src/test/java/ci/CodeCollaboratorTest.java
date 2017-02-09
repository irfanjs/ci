package ci;

import org.junit.Test;
import org.symantec.ci.CodeCollaboratorBugCount;


public class CodeCollaboratorTest {
	@Test
	public void test_getReviewIdCount(){
		CodeCollaboratorBugCount cc = new CodeCollaboratorBugCount();
		cc.getBugCount(20987);
	}

}
