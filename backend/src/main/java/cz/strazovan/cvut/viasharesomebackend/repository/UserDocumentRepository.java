package cz.strazovan.cvut.viasharesomebackend.repository;

import cz.strazovan.cvut.viasharesomebackend.model.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDocumentRepository extends MongoRepository<UserDocument, String> {
}
