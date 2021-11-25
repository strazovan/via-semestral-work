package cz.strazovan.cvut.viasharesomebackend.dao;

import cz.strazovan.cvut.viasharesomebackend.model.UserDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserDocumentDaoTest extends BaseDaoTestRunner {

    private final UserDocumentDao userDocumentDao;

    @Autowired
    public UserDocumentDaoTest(UserDocumentDao userDocumentDao) {
        this.userDocumentDao = userDocumentDao;
    }

    @Test
    public void returnsEmptyForNonExistingUser() {
        assertTrue(this.userDocumentDao.findByUsername("unknown").isEmpty());
    }

    @Test
    public void persists() {
        final UserDocument userDocument = getUserDocument();
        final var persisted = this.userDocumentDao.save(userDocument);
        assertNotNull(persisted.getId());
    }

    private UserDocument getUserDocument() {
        final UserDocument userDocument = new UserDocument();
        userDocument.setUsername("test user");
        return userDocument;
    }

    @Test
    public void updates() {
        final var document = getUserDocument();
        final var persisted = this.userDocumentDao.save(document);
        final var newUsername = "newUsername";
        persisted.setUsername(newUsername);
        this.userDocumentDao.save(persisted);
        final var byId = this.userDocumentDao.findById(persisted.getId());
        assertTrue(byId.isPresent());
        assertEquals(newUsername, byId.get().getUsername());
    }

    @AfterEach
    public void afterEach(@Autowired MongoTemplate mongoTemplate) {
        mongoTemplate.remove(new Query(), "users_data");
    }
}
