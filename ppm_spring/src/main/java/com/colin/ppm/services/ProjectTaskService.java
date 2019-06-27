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

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask){
       try{

           //Project Tasks to be added to a specific project, project !=null, backlog exists
           Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
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

           if(projectTask.getPriority() == null){ // In the future we need projectTask.getPriority() == 0 to handle the form
               projectTask.setPriority(3);
           }
           return projectTaskRepository.save(projectTask);

       } catch (Exception e){
            throw new ProjectNotFoundException("Project not found");
       }
    }

    public Iterable<ProjectTask> findBacklogById(String id){

        Project project = projectRepository.findByProjectIdentifier(id);
        if(project==null){
            throw new ProjectNotFoundException("Project with ID: '"+ id+"' does not exist");
        }
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

}
