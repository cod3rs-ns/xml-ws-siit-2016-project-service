package rs.ac.uns.sw.xml.repository;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.semantics.SPARQLMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.sw.xml.config.MarkLogicConstants;
import rs.ac.uns.sw.xml.domain.Amendments;
import rs.ac.uns.sw.xml.domain.wrapper.SearchResult;
import rs.ac.uns.sw.xml.util.RDFExtractorUtil;
import rs.ac.uns.sw.xml.util.RepositoryUtil;
import rs.ac.uns.sw.xml.util.ResultHandler;

import java.util.List;

import static rs.ac.uns.sw.xml.util.PredicatesConstants.SUGGESTED;
import static rs.ac.uns.sw.xml.util.RDFExtractorUtil.PARLIAMENT_NAMED_GRAPH_URI;
import static rs.ac.uns.sw.xml.util.RDFExtractorUtil.handleResults;

@Component
public class AmendmentsRepositoryXML {

    @Autowired
    XMLDocumentManager documentManager;

    @Autowired
    QueryManager queryManager;

    @Autowired
    DatabaseClient databaseClient;

    public Amendments save(Amendments amendments) {
        RepositoryUtil.ResultDocumentHandler handler = RepositoryUtil.documentHandle(MarkLogicConstants.Collections.AMENDMENTS, Amendments.class);

        handler.getContentHandle().set(amendments);
        String data = handler.getContentHandle().toString();

        RDFExtractorUtil.writeMetadata(data, databaseClient, PARLIAMENT_NAMED_GRAPH_URI);

        documentManager.write(getDocumentId(amendments.getName()), handler.getMetadata(), handler.getContentHandle());

        return amendments;
    }

    public Amendments findAmendmentByName(String name) {
        JAXBHandle contentHandle = RepositoryUtil.getObjectHandle(Amendments.class);
        JAXBHandle result = documentManager.read(getDocumentId(name), contentHandle);

        return (Amendments) result.get(Amendments.class);
    }

    public SearchResult findAll() {
        ResultHandler handler = new ResultHandler(Amendments.class, documentManager);

        StructuredQueryBuilder builder = queryManager.newStructuredQueryBuilder();
        StructuredQueryDefinition criteria = builder.collection(MarkLogicConstants.Collections.AMENDMENTS);

        SearchHandle result = new SearchHandle();
        queryManager.search(criteria, result);

        return handler.toSearchResult(result);
    }

    private String getDocumentId(String value) {
        return String.format("/amendments/%s.xml", value);
    }


    public List<String> findAmendmentsByProposer(String proposerName){
        SPARQLQueryManager sparqlQueryManager = databaseClient.newSPARQLQueryManager();

        SPARQLQueryDefinition query = sparqlQueryManager
                .newQueryDefinition("SELECT * FROM <" + PARLIAMENT_NAMED_GRAPH_URI + "> WHERE { ?s ?p ?o }")
                .withBinding("o", proposerName)
                .withBinding("p", SUGGESTED);

        JacksonHandle resultsHandle = new JacksonHandle();
        resultsHandle.setMimetype(SPARQLMimeTypes.SPARQL_JSON);

        resultsHandle = sparqlQueryManager.executeSelect(query, resultsHandle);
        return handleResults(resultsHandle);
    }
}
