package rs.ac.uns.sw.xml.config;

public final class MarkLogicConstants {

    public final class Collections {
        public static final String LAWS  = "/laws.xml";
        public static final String USERS = "/api_users.xml";
        public static final String AMENDMENTS  = "/amendments.xml";
    }

    public final class Namespaces {
        public static final String PREDICATE    = "http://www.parlament.gov.rs/rdf_schema/skupstina#";
        public static final String AMENDMENTS   = "http://www.parlament.gov.rs/schema/amandman";
        public static final String LAWS         = "http://www.parlament.gov.rs/schema/propis";
        public static final String ELEMENTS     = "http://www.parlament.gov.rs/schema/elementi";
        public static final String USERS        = "http://www.parlament.gov.rs/schema/korisnici";
    }

    public final class Prefixes {
        public static final String PREDICATE_PREF   = "pred";
        public static final String AMENDMENTS_PREF  = "aman";
        public static final String LAWS_PREF        = "propis";
        public static final String ELEMENTS_PREF    = "elem";
        public static final String USERS_PREF       = "kor";
    }

}
