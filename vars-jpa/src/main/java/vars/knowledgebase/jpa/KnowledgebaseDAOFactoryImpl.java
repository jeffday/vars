package vars.knowledgebase.jpa;

import vars.DAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadataDAO;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.HistoryDAO;
import vars.knowledgebase.LinkRealizationDAO;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.MediaDAO;
import vars.knowledgebase.UsageDAO;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import vars.jpa.EntityManagerFactoryAspect;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:37:10 AM
 * To change this template use File | Settings | File Templates.
 */
    public class KnowledgebaseDAOFactoryImpl implements KnowledgebaseDAOFactory, EntityManagerFactoryAspect {

    private final EntityManagerFactory entityManagerFactory;

    @Inject
    public KnowledgebaseDAOFactoryImpl(@Named("knowledgebasePersistenceUnit") String persistenceUnit) {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    public ConceptDAO newConceptDAO() {
        return new ConceptDAOImpl(entityManagerFactory.createEntityManager());
    }

    public ConceptMetadataDAO newConceptMetadataDAO() {
        return new ConceptMetadataDAOImpl(entityManagerFactory.createEntityManager());
    }

    public ConceptNameDAO newConceptNameDAO() {
        return new ConceptNameDAOImpl(entityManagerFactory.createEntityManager());
    }

    public HistoryDAO newHistoryDAO() {
        return new HistoryDAOImpl(entityManagerFactory.createEntityManager());
    }

    public LinkRealizationDAO newLinkRealizationDAO() {
        return new LinkRealizationDAOImpl(entityManagerFactory.createEntityManager());
    }

    public LinkTemplateDAO newLinkTemplateDAO() {
        return new LinkTemplateDAOImpl(entityManagerFactory.createEntityManager());
    }

    public MediaDAO newMediaDAO() {
        return new MediaDAOImpl(entityManagerFactory.createEntityManager());
    }

    public UsageDAO newUsageDAO() {
        return new UsageDAOImpl(entityManagerFactory.createEntityManager());
    }

    public DAO newDAO() {
        return new vars.jpa.DAO(entityManagerFactory.createEntityManager());
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
}
