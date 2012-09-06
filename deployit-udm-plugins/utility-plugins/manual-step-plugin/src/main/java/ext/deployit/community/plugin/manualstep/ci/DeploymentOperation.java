package ext.deployit.community.plugin.manualstep.ci;

import com.xebialabs.deployit.plugin.api.deployment.specification.Operation;

public enum DeploymentOperation {
    ANY(null),
    CREATE(Operation.CREATE),
    DESTROY(Operation.DESTROY),
    MODIFY(Operation.MODIFY),
    NOOP(Operation.NOOP);

    private Operation operation;

    DeploymentOperation(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }
}
