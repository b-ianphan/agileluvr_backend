package agileluvr.project;

import agileluvr.common.documents.ProjectListingDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ProjectListingRepository extends MongoRepository<ProjectListingDocument, String> {
}
