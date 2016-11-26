package rs.ac.uns.sw.xml.repository;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.sw.xml.domain.Glava;
import rs.ac.uns.sw.xml.domain.wrapper.GlavaSearchResult;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GlavaRepositoryXML {

    public static final String COLLECTION_REF = "/glave.xml";

    @Autowired
    XMLDocumentManager documentManager;

    @Autowired
    QueryManager queryManager;


    // BASIC REPOSITORY FUNCTIONS
    public Glava save(Glava document) {
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();

        // Add Collection
        metadata.getCollections().add(COLLECTION_REF);

        JAXBHandle contentHandle = getProductHandle();
        contentHandle.set(document);

        documentManager.write(getDocumentId(document.getId()), metadata, contentHandle);

        return findById(document.getId());
    }

    public Glava findById(String id) {
        JAXBHandle contentHandle = getProductHandle();
        JAXBHandle result = documentManager.read(getDocumentId(id), contentHandle);
        return (Glava) result.get(Glava.class);
    }

    public GlavaSearchResult findAll() {
        StructuredQueryBuilder builder = queryManager.newStructuredQueryBuilder();
        StructuredQueryDefinition criteria = builder.collection(COLLECTION_REF);

        SearchHandle result = new SearchHandle();
        queryManager.search(criteria, result);

        return toSearchResult(result);
    }


    // ADVANCED REPOSITORY FUNCTIONS
    public GlavaSearchResult findByOdjeljakContains(String query) {
        StructuredQueryBuilder builder = queryManager.newStructuredQueryBuilder();

        // The following example defines a query that searches for the QUERY value within an element range index on 'odjeljak'.
        StructuredQueryDefinition criteria = builder.and(
          builder.wordConstraint("odjeljak", query)
        );

        SearchHandle result = new SearchHandle();
        queryManager.search(criteria, result);

        return toSearchResult(result);
    }


    // PRIVATE HELPER FUNCTIONS
    private GlavaSearchResult toSearchResult(SearchHandle resultHandler) {
        List<Glava> products = new ArrayList<>();
        for (MatchDocumentSummary summary : resultHandler.getMatchResults()) {
            JAXBHandle contentHandle = getProductHandle();
            documentManager.read(summary.getUri(), contentHandle);
            products.add((Glava) contentHandle.get(Glava.class));
        }
        return new GlavaSearchResult(products);
    }

    private JAXBHandle getProductHandle() {
        try {
            JAXBContext context = JAXBContext.newInstance(Glava.class);
            return new JAXBHandle(context);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to create Product Handle...", e);
        }
    }

    private String getDocumentId(String id) {
        return String.format("/glave/%s.xml", id);
    }
}