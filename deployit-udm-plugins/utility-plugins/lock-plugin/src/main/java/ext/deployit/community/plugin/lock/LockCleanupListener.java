package ext.deployit.community.plugin.lock;

import java.io.Serializable;
import java.util.Set;

import com.xebialabs.deployit.engine.spi.execution.ExecutionStateListener;
import com.xebialabs.deployit.engine.spi.execution.StepExecutionStateEvent;
import com.xebialabs.deployit.engine.spi.execution.TaskExecutionStateEvent;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;

public class LockCleanupListener implements ExecutionStateListener, Serializable {

	private final LockHelper lockHelper;
	private final Set<ConfigurationItem> cisToBeUnlocked;

	public LockCleanupListener(LockHelper lockHelper, Set<ConfigurationItem> cisToBeUnlocked) {
		this.lockHelper = lockHelper;
		this.cisToBeUnlocked = cisToBeUnlocked;
	}
	
	@Override
	public void stepStateChanged(StepExecutionStateEvent event) {
		System.out.println("step event: " + event);
	}

	@Override
	public void taskStateChanged(TaskExecutionStateEvent event) {
		System.out.println("task event: " + event);

		switch(event.currentState()) {
		case CANCELLED:
		case DONE:
			lockHelper.unlock(cisToBeUnlocked);
			break;
		default:
			break;
		}
	}

}
