import sys

# taskid provided as command line argument
taskid = sys.argv[1]
print 'taskid=' + taskid

tasks = repository.getArchivedTasks()
totsize = tasks.size()
# print 'total size= ' + str(totsize)
for i in range(totsize):
  # find taskid
  if tasks.getTasks().get(i).id == taskid:
    numSteps = tasks.getTasks().get(i).nrOfSteps
    print 'Number of Steps for Task= ' + str(numSteps)
    print
    for j in range(numSteps):
        print 'Step = ' + str(j+1) + ': ' + tasks.getTasks().get(i).getSteps().get(j).description
        thisStep = tasks.getTasks().get(i).getSteps().get(j)
        stepLog = thisStep.log
        print stepLog
