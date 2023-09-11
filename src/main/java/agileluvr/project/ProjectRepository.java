package agileluvr.project;

import agileluvr.common.documents.ProjectDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ProjectRepository extends MongoRepository<ProjectDocument, String> {
}
