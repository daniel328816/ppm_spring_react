package com.colin.ppm.services;

import com.colin.ppm.domain.Backlog;
import com.colin.ppm.domain.Project;
import com.colin.ppm.domain.ProjectTask;
import com.colin.ppm.exceptions.ProjectNotFoundException;
import com.colin.ppm.repositories.BacklogRepository;
import com.colin.ppm.repositories.ProjectRepository;
import com.colin.ppm.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static sun.misc.Version.print;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username){


           //Project Tasks to be added to a specific project, project !=null, backlog exists
           Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier ,username).getBacklog();
           //Set the backlog to project task
           projectTask.setBacklog(backlog);
           //we want our project sequence to be like this: IDPRO-1 IDPRO-2 ... 100 101
           Integer backlogSequence = backlog.getPTSequence();
           //update the backlog sequence
           backlogSequence++;

           backlog.setPTSequence(backlogSequence);
           // add sequence to project task
           projectTask.setProjectSequence(projectIdentifier+"-"+backlogSequence);
           projectTask.setProjectIdentifier(projectIdentifier);


           //Initial status when status is null
           if(projectTask.getStatus()=="" || projectTask.getStatus() == null){
               projectTask.setStatus("TO_DO");
           }

           //Fix bug with priority in Spring Boot Server, needs to check null first
           if(projectTask.getPriority() == null || projectTask.getPriority() == 0 ){ // In the future we need projectTask.getPriority() == 0 to handle the form
               projectTask.setPriority(3);
           }
           return projectTaskRepository.save(projectTask);


    }

    public Iterable<ProjectTask> findBacklogById(String id, String username){

        projectService.findProjectByIdentifier(id, username);

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id,String pt_id){
        // make sure we are searching on an existing backlog
        Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
        if(backlog == null){
            throw new ProjectNotFoundException("Project with ID: '"+backlog_id+"' does not exist");
        }

        // make sure that our task exists
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);

        if(projectTask == null){
            throw new ProjectNotFoundException("Project Task: '"+pt_id+"' not found ");
        }

        // make sure that the backlog/project id in the path corresponds to the right project
        if(!projectTask.getProjectIdentifier().equals(backlog_id)){
            throw new ProjectNotFoundException("Project Task: '"+pt_id+"' does not exist in project: '"+backlog_id);
        }
        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);

        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlog_id, String pt_id){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);

        projectTaskRepository.delete(projectTask);
    }
    //update project task

    //find existing project task

    // replace it with updated task

    //save update

}
