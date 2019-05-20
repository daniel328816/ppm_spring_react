package com.colin.ppm.services;

import com.colin.ppm.domain.Project;
import com.colin.ppm.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project saveOrUpdateProject(Project project){

        // logic

        return projectRepository.save(project);
    }
}
