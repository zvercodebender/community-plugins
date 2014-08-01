email = specification.deployedOrPreviousApplication.email

if(specification.operation != "NOOP" and email is not None):
    context.addStep(steps.noop(description = "Sending an email to " + email + " that deployment is finished", order = 99))

