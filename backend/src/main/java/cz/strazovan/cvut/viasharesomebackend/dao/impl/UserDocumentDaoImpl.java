package cz.strazovan.cvut.viasharesomebackend.dao.impl;

import cz.strazovan.cvut.viasharesomebackend.dao.UserDocumentDao;
import cz.strazovan.cvut.viasharesomebackend.model.UserDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDocumentDaoImpl implements UserDocumentDao {

    private MongoOperations operations;


    @Autowired
    public void setOperations(MongoOperations operations) {
        this.operations = operations;
    }

    @Override
    public UserDocument save(UserDocument userDocument) {
        return this.operations.save(userDocument);
    }

    @Override
    public Optional<UserDocument> findByUsername(String username) {
        final var query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        final var result = this.operations.find(query, UserDocument.class);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        if (result.size() > 1) {
            throw new RuntimeException("Too many results.");
        }
        return Optional.of(result.get(0));
    }

    @Override
    public Optional<UserDocument> findById(String id) {
        return Optional.ofNullable(this.operations.findById(id, UserDocument.class));
    }
}
