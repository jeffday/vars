package vars.knowledgebase.rules;

import vars.IPersistenceRule;
import vars.VARSPersistenceException;
import vars.knowledgebase.Concept;

/**
 * Throws an exception if the concept is missing a primary name
 */
public class MissingPrimaryNameRule implements IPersistenceRule<Concept> {

    public Concept apply(Concept object) {
        if (object.getPrimaryConceptName() == null) {
            throw new VARSPersistenceException("You are not allowed to persist a concept without a conceptname");
        }
        return object;
    }
}
