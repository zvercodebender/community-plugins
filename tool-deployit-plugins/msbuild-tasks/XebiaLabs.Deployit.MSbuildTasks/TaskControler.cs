using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using XebiaLabs.Deployit.Client;
using XebiaLabs.Deployit.Client.Manifest;
using XebiaLabs.Deployit.Client.Package;
using XebiaLabs.Deployit.Client.UDM;

namespace XebiaLabs.Deployit.MSbuildTasks
{
    public class TaskControler : DeployitConnectedTask
    {
        [Required]
        public string TaskId { get; set; }

        [Required]
        public string Action { get; set; }

        protected override bool ExecuteCore(DeployitServer deployitServer)
        {
            var taskService = deployitServer.TaskService;

            switch (Action.ToLowerInvariant())
            {
                case "start":
                    taskService.Start(TaskId);
                    break;
                case "stop":
                    taskService.Stop(TaskId);
                    break;
                case "abort":
                    taskService.Stop(TaskId);
                    break;
                case "cancel":
                    taskService.Cancel(TaskId);
                    break;
                case "archive":
                    taskService.Archive(TaskId);
                    break;
                default:
                    Log.LogError("unknown task action: {0}", Action);
                    return false;

            }
            return true;
        }

#if false
               [RequiredArgument]
            public InArgument<DeployitContext> DeployitContext { get; set; }

            [RequiredArgument]
            public InArgument<string> TaskId { get; set; }

            [RequiredArgument]
            public InArgument<TaskAction> Action { get; set; }

            protected override void Execute(CodeActivityContext context)
            {
                var deployitContext = context.GetValue<DeployitContext>(DeployitContext);
                var server = deployitContext.Server;


                var taskId = context.GetValue<string>(TaskId);
                var action = context.GetValue<TaskAction>(Action);

                var taskService = server.TaskService;

                switch (action)
                {
                    case TaskAction.Start:
                        taskService.Start(taskId);
                        break;
                    case TaskAction.Stop:
                        taskService.Stop(taskId);
                        break;
                    case TaskAction.Abort:
                        taskService.Stop(taskId);
                        break;
                    case TaskAction.Cancel:
                        taskService.Cancel(taskId);
                        break;
                    case TaskAction.Archive:
                        taskService.Archive(taskId);
                        break;
                    default:
                        throw new InvalidOperationException(String.Format("unknown task action: {0}", action));

                }

            }

        }

#endif

    }
}
