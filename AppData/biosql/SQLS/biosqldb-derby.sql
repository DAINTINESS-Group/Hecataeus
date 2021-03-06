CREATE TABLE biodatabase ( 
	 biodatabase_id INTEGER, 
	 name VARCHAR ( 128 ) NOT NULL, 
	 authority VARCHAR ( 128 ) , 
	 description VARCHAR (30000),
	 PRIMARY KEY (biodatabase_id)
) ; 

CREATE TABLE taxon ( 
	 taxon_id INTEGER, 
	 ncbi_taxon_id INTEGER NOT NULL, 
	 parent_taxon_id INTEGER , 
	 node_rank VARCHAR ( 32 ) , 
	 genetic_code SMALLINT , 
	 mito_genetic_code SMALLINT , 
	 left_value INTEGER NOT NULL , 
	 right_value INTEGER NOT NULL ,
	 PRIMARY KEY (taxon_id )
 );	 

CREATE TABLE taxon_name ( 
	 taxon_id INTEGER NOT NULL , 
	 name VARCHAR ( 255 ) NOT NULL , 
	 name_class VARCHAR ( 32 ) NOT NULL
      
) ; 


CREATE TABLE ontology ( 
	 ontology_id INTEGER, 
	 name VARCHAR ( 32 ) NOT NULL, 
	 definition VARCHAR (30000) , 
	 PRIMARY KEY (ontology_id )
	 
) ; 


CREATE TABLE term ( 
	 term_id INTEGER   PRIMARY KEY , 
	 name VARCHAR ( 255 ) NOT NULL , 
	 definition VARCHAR(30000) , 
	 identifier VARCHAR ( 40 ) NOT NULL, 
	 is_obsolete CHAR ( 1 ) NOT NULL,
	 ontology_id INTEGER NOT NULL
) ;



-- ontology terms have synonyms, here is how to store them.
-- Synonym is a reserved word in many RDBMSs, so the column synonym
-- may eventually be renamed to name.
CREATE TABLE term_synonym (
	 synonym VARCHAR(255) NOT NULL,
	 term_id INTEGER NOT NULL,
       	 PRIMARY KEY ( term_id , synonym ) ) ;

-- ontology terms to dbxref association: ontology terms have dbxrefs 
CREATE TABLE term_dbxref ( 
	 term_id INTEGER NOT NULL , 
	 dbxref_id INTEGER NOT NULL , 
	 rank INTEGER , 
	 PRIMARY KEY ( term_id , dbxref_id ) ) ; 



-- relationship between controlled vocabulary / ontology term 
-- we use subject/predicate/object but this could also 
-- be thought of as child/relationship-type/parent. 
-- the subject/predicate/object naming is better as we 
-- can think of the graph as composed of statements. 
-- 
-- we also treat the relationshiptypes / predicates as 
-- controlled terms in themselves; this is quite useful 
-- as a lot of systems (eg GO) will soon require 
-- ontologies of relationship types (eg subtle differences 
-- in the partOf relationship) 
-- 
-- this table probably won't be filled for a while, the core 
-- will just treat ontologies as flat lists of terms 
CREATE TABLE term_relationship ( 
	 term_relationship_id INTEGER   PRIMARY KEY , 
	 subject_term_id INTEGER NOT NULL , 
	 predicate_term_id INTEGER NOT NULL , 
	 object_term_id INTEGER NOT NULL , 
	 ontology_id INTEGER NOT NULL 
	  ) ; 


CREATE TABLE term_relationship_term (
        term_relationship_id INTEGER NOT NULL PRIMARY KEY ,
        term_id              INTEGER  NOT NULL 
);

-- the infamous transitive closure table on ontology term relationships 
-- this is a warehouse approach - you will need to update this regularly 
-- 
-- the triple of (subject, predicate, object) is the same as for ontology 
-- relationships, with the exception of predicate being the greatest common 
-- denominator of the relationships types visited in the path (i.e., if 
-- relationship type A is-a relationship type B, the greatest common 
-- denominator for path containing both types A and B is B) 
-- 
-- See the GO database or Chado schema for other (and possibly better 
-- documented) implementations of the transitive closure table approach. 
CREATE TABLE term_path ( 
         term_path_id INTEGER   PRIMARY KEY  ,
	 subject_term_id INTEGER NOT NULL , 
	 predicate_term_id INTEGER NOT NULL , 
	 object_term_id INTEGER NOT NULL , 
	 ontology_id INTEGER NOT NULL , 
	 distance INTEGER NOT NULL
	
	 ) ; 

CREATE TABLE bioentry ( 
	 bioentry_id INTEGER   PRIMARY KEY , 
	 biodatabase_id INTEGER NOT NULL , 
	 taxon_id INTEGER , 
	 name VARCHAR ( 40 ) NOT NULL , 
	 accession VARCHAR ( 128 ) NOT NULL , 
	 identifier VARCHAR ( 40 ) NOT NULL, 
	 division VARCHAR ( 6 ) , 
	 description VARCHAR( 4096 ) , 
	 version INTEGER NOT NULL  
	
) ; 



CREATE TABLE bioentry_relationship ( 
	 bioentry_relationship_id INTEGER   PRIMARY KEY , 
	 object_bioentry_id INTEGER NOT NULL , 
	 subject_bioentry_id INTEGER NOT NULL , 
	 term_id INTEGER NOT NULL , 
	 rank INTEGER
) ; 

CREATE TABLE bioentry_path ( 
	 object_bioentry_id INTEGER NOT NULL , 
	 subject_bioentry_id INTEGER NOT NULL , 
	 term_id INTEGER NOT NULL , 
	 distance INTEGER NOT NULL
	 );

CREATE TABLE biosequence ( 
	 bioentry_id INTEGER   PRIMARY KEY , 
	 version INTEGER , 
	 length INTEGER , 
	 alphabet VARCHAR ( 10 ) , 
	 seq VARCHAR(255) 
) ; 

-- database cross-references (e.g., GenBank:AC123456.1) 
--
-- Version may be unknown, may be undefined, or may not exist for a certain
-- accession or database (namespace). We require it here to avoid RDBMS-
-- dependend enforcement variants (version is in a compound alternative key),
-- and to simplify query construction for UK look-ups. If there is no version
-- the convention is to put 0 (zero) here. Likewise, a record with a version
-- of zero means the version is to be interpreted as NULL.
--
CREATE TABLE dbxref ( 
	 dbxref_id INTEGER   PRIMARY KEY , 
	 dbname VARCHAR ( 40 ) NOT NULL , 
	 accession VARCHAR ( 128 ) NOT NULL , 
	 version INTEGER NOT NULL) ; 


CREATE TABLE dbxref_qualifier_value ( 
	 dbxref_id INTEGER NOT NULL , 
	 term_id INTEGER NOT NULL , 
	 rank INTEGER NOT NULL, 
	 value VARCHAR (30000) , 
	 PRIMARY KEY ( dbxref_id , term_id , rank ) ) ; 

CREATE TABLE bioentry_dbxref ( 
	 bioentry_id INTEGER NOT NULL , 
	 dbxref_id INTEGER NOT NULL , 
	 rank INTEGER , 
	 PRIMARY KEY ( bioentry_id , dbxref_id ) ) ; 

CREATE TABLE reference ( 
	 reference_id INTEGER   PRIMARY KEY , 
	 dbxref_id INTEGER NOT NULL  ,
	 location VARCHAR (30000) NOT NULL , 
	 title VARCHAR (30000) , 
	 authors VARCHAR (30000) , 
	 crc VARCHAR ( 32 ) NOT NULL
) ; 

-- bioentry to reference associations 
CREATE TABLE bioentry_reference ( 
	 bioentry_id INTEGER NOT NULL , 
	 reference_id INTEGER NOT NULL , 
	 start_pos INTEGER , 
	 end_pos INTEGER , 
	 rank INTEGER NOT NULL , 
	 PRIMARY KEY ( bioentry_id , reference_id , rank ) ) ; 

CREATE TABLE comment ( 
	 comment_id INTEGER   PRIMARY KEY, 
	 bioentry_id INTEGER NOT NULL , 
	 comment_text VARCHAR (30000) NOT NULL , 
	 rank INTEGER NOT NULL) ; 

-- tag/value and ontology term annotation for bioentries goes here
CREATE TABLE bioentry_qualifier_value ( 
	 bioentry_id INTEGER NOT NULL , 
	 term_id INTEGER NOT NULL , 
	 value VARCHAR (30000) , 
	 rank INTEGER NOT NULL ) ; 

CREATE TABLE seqfeature ( 
	 seqfeature_id INTEGER   PRIMARY KEY , 
	 bioentry_id INTEGER NOT NULL , 
	 type_term_id INTEGER NOT NULL , 
	 source_term_id INTEGER NOT NULL , 
	 display_name VARCHAR ( 64 ) , 
	 rank INTEGER NOT NULL) ; 

CREATE TABLE seqfeature_relationship ( 
	 seqfeature_relationship_id INTEGER   PRIMARY KEY , 
	 object_seqfeature_id INTEGER NOT NULL , 
	 subject_seqfeature_id INTEGER NOT NULL , 
	 term_id INTEGER NOT NULL , 
	 rank INTEGER  ) ; 

CREATE TABLE seqfeature_path ( 
	 object_seqfeature_id INTEGER NOT NULL , 
	 subject_seqfeature_id INTEGER NOT NULL , 
	 term_id INTEGER NOT NULL , 
	 distance INTEGER NOT NULL ) ; 

CREATE TABLE seqfeature_qualifier_value ( 
	 seqfeature_id INTEGER NOT NULL , 
	 term_id INTEGER NOT NULL , 
	 rank INTEGER NOT NULL , 
	 value VARCHAR (1024)  NOT NULL ) ; 

CREATE TABLE seqfeature_dbxref ( 
	 seqfeature_id INTEGER NOT NULL , 
	 dbxref_id INTEGER NOT NULL , 
	 rank INTEGER ) ; 


CREATE TABLE location ( 
	 location_id INTEGER   PRIMARY KEY , 
	 seqfeature_id INTEGER NOT NULL , 
	 dbxref_id INTEGER , 
	 term_id INTEGER , 
	 start_pos INTEGER , 
	 end_pos INTEGER , 
	 strand INTEGER NOT NULL  , 
	 rank INTEGER NOT NULL  ) ; 


CREATE TABLE location_qualifier_value ( 
	 location_id INTEGER NOT NULL , 
	 term_id INTEGER NOT NULL , 
	 value VARCHAR ( 255 ) NOT NULL , 
	 int_value INTEGER , 
	 PRIMARY KEY ( location_id , term_id ) ) ; 
--~ 
--~ ALTER TABLE term ADD CONSTRAINT FKont_term
      --~ FOREIGN KEY ( ontology_id ) REFERENCES ontology ( ontology_id ) 
      --~ ON DELETE CASCADE ;
--~ 
--~ -- term synonyms
--~ ALTER TABLE term_synonym ADD CONSTRAINT FKterm_syn
      --~ FOREIGN KEY ( term_id ) REFERENCES term ( term_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- term_dbxref 
--~ ALTER TABLE term_dbxref ADD CONSTRAINT FKdbxref_trmdbxref
      --~ FOREIGN KEY ( dbxref_id ) REFERENCES dbxref ( dbxref_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE term_dbxref ADD CONSTRAINT FKterm_trmdbxref
      --~ FOREIGN KEY ( term_id ) REFERENCES term ( term_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- term_relationship 
--~ ALTER TABLE term_relationship ADD CONSTRAINT FKtrmsubject_trmrel
      --~ FOREIGN KEY ( subject_term_id ) REFERENCES term ( term_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE term_relationship ADD CONSTRAINT FKtrmpredicate_trmrel
      --~ FOREIGN KEY ( predicate_term_id ) REFERENCES term ( term_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE term_relationship ADD CONSTRAINT FKtrmobject_trmrel
      --~ FOREIGN KEY ( object_term_id ) REFERENCES term ( term_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE term_relationship ADD CONSTRAINT FKontology_trmrel
      --~ FOREIGN KEY ( ontology_id ) REFERENCES ontology ( ontology_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- term_relationship_term
--~ ALTER TABLE term_relationship_term ADD CONSTRAINT FKtrmrel_trmreltrm
      --~ FOREIGN KEY (term_relationship_id) REFERENCES term_relationship(term_relationship_id)
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE term_relationship_term ADD CONSTRAINT FKtrm_trmreltrm
      --~ FOREIGN KEY (term_id) REFERENCES term(term_id)
      --~ ON DELETE CASCADE ;
--~ 
--~ -- term_path 
--~ ALTER TABLE term_path ADD CONSTRAINT FKtrmsubject_trmpath
      --~ FOREIGN KEY ( subject_term_id ) REFERENCES term ( term_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE term_path ADD CONSTRAINT FKtrmpredicate_trmpath
      --~ FOREIGN KEY ( predicate_term_id ) REFERENCES term ( term_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE term_path ADD CONSTRAINT FKtrmobject_trmpath
      --~ FOREIGN KEY ( object_term_id ) REFERENCES term ( term_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE term_path ADD CONSTRAINT FKontology_trmpath
      --~ FOREIGN KEY ( ontology_id ) REFERENCES ontology ( ontology_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- taxon, taxon_name 
--~ -- unfortunately, we can't constrain parent_taxon_id as it is violated
--~ -- occasionally by the downloads available from NCBI
--~ -- ALTER TABLE taxon ADD CONSTRAINT FKtaxon_taxon
--~ --       FOREIGN KEY ( parent_taxon_id ) REFERENCES taxon ( taxon_id )
--~ --       DEFERRABLE;
--~ ALTER TABLE taxon_name ADD CONSTRAINT FKtaxon_taxonname
      --~ FOREIGN KEY ( taxon_id ) REFERENCES taxon ( taxon_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- bioentry 
--~ ALTER TABLE bioentry ADD CONSTRAINT FKtaxon_bioentry
      --~ FOREIGN KEY ( taxon_id ) REFERENCES taxon ( taxon_id ) ; 
--~ ALTER TABLE bioentry ADD CONSTRAINT FKbiodatabase_bioentry
      --~ FOREIGN KEY ( biodatabase_id ) REFERENCES biodatabase ( biodatabase_id ) ; 
--~ -- bioentry_relationship 
--~ ALTER TABLE bioentry_relationship ADD CONSTRAINT FKterm_bioentryrel
      --~ FOREIGN KEY ( term_id ) REFERENCES term ( term_id ) ; 
--~ ALTER TABLE bioentry_relationship ADD CONSTRAINT FKparentent_bioentryrel
      --~ FOREIGN KEY ( object_bioentry_id ) REFERENCES bioentry ( bioentry_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE bioentry_relationship ADD CONSTRAINT FKchildent_bioentryrel
      --~ FOREIGN KEY ( subject_bioentry_id ) REFERENCES bioentry ( bioentry_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- bioentry_path 
--~ ALTER TABLE bioentry_path ADD CONSTRAINT FKterm_bioentrypath
      --~ FOREIGN KEY ( term_id ) REFERENCES term ( term_id ) ; 
--~ ALTER TABLE bioentry_path ADD CONSTRAINT FKparentent_bioentrypath
      --~ FOREIGN KEY ( object_bioentry_id ) REFERENCES bioentry ( bioentry_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE bioentry_path ADD CONSTRAINT FKchildent_bioentrypath
      --~ FOREIGN KEY ( subject_bioentry_id ) REFERENCES bioentry ( bioentry_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- biosequence 
--~ ALTER TABLE biosequence ADD CONSTRAINT FKbioentry_bioseq
      --~ FOREIGN KEY ( bioentry_id ) REFERENCES bioentry ( bioentry_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- comment 
--~ ALTER TABLE comment ADD CONSTRAINT FKbioentry_comment
      --~ FOREIGN KEY ( bioentry_id ) REFERENCES bioentry ( bioentry_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- bioentry_dbxref 
--~ ALTER TABLE bioentry_dbxref ADD CONSTRAINT FKbioentry_dblink
      --~ FOREIGN KEY ( bioentry_id ) REFERENCES bioentry ( bioentry_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE bioentry_dbxref ADD CONSTRAINT FKdbxref_dblink
      --~ FOREIGN KEY ( dbxref_id ) REFERENCES dbxref ( dbxref_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- dbxref_qualifier_value 
--~ ALTER TABLE dbxref_qualifier_value ADD CONSTRAINT FKtrm_dbxrefqual
      --~ FOREIGN KEY ( term_id ) REFERENCES term ( term_id ) ;
--~ ALTER TABLE dbxref_qualifier_value ADD CONSTRAINT FKdbxref_dbxrefqual
      --~ FOREIGN KEY ( dbxref_id ) REFERENCES dbxref ( dbxref_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- bioentry_reference 
--~ ALTER TABLE bioentry_reference ADD CONSTRAINT FKbioentry_entryref
      --~ FOREIGN KEY ( bioentry_id ) REFERENCES bioentry ( bioentry_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE bioentry_reference ADD CONSTRAINT FKreference_entryref
      --~ FOREIGN KEY ( reference_id ) REFERENCES reference ( reference_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- reference 
--~ ALTER TABLE reference ADD CONSTRAINT FKdbxref_reference
      --~ FOREIGN KEY ( dbxref_id ) REFERENCES dbxref ( dbxref_id ) ;
--~ 
--~ -- bioentry_qualifier_value 
--~ ALTER TABLE bioentry_qualifier_value ADD CONSTRAINT FKbioentry_entqual
      --~ FOREIGN KEY ( bioentry_id ) REFERENCES bioentry ( bioentry_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE bioentry_qualifier_value ADD CONSTRAINT FKterm_entqual
      --~ FOREIGN KEY ( term_id ) REFERENCES term ( term_id ) ;
--~ 
--~ -- seqfeature 
--~ ALTER TABLE seqfeature ADD CONSTRAINT FKterm_seqfeature
      --~ FOREIGN KEY ( type_term_id ) REFERENCES term ( term_id ) ; 
--~ ALTER TABLE seqfeature ADD CONSTRAINT FKsourceterm_seqfeature
      --~ FOREIGN KEY ( source_term_id ) REFERENCES term ( term_id ) ; 
--~ ALTER TABLE seqfeature ADD CONSTRAINT FKbioentry_seqfeature
      --~ FOREIGN KEY ( bioentry_id ) REFERENCES bioentry ( bioentry_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- seqfeature_relationship 
--~ ALTER TABLE seqfeature_relationship ADD CONSTRAINT FKterm_seqfeatrel
      --~ FOREIGN KEY ( term_id ) REFERENCES term ( term_id ) ;
--~ ALTER TABLE seqfeature_relationship ADD CONSTRAINT FKparentfeat_seqfeatrel
      --~ FOREIGN KEY ( object_seqfeature_id ) REFERENCES seqfeature ( seqfeature_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE seqfeature_relationship ADD CONSTRAINT FKchildfeat_seqfeatrel
      --~ FOREIGN KEY ( subject_seqfeature_id ) REFERENCES seqfeature ( seqfeature_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- seqfeature_path 
--~ ALTER TABLE seqfeature_path ADD CONSTRAINT FKterm_seqfeatpath
      --~ FOREIGN KEY ( term_id ) REFERENCES term ( term_id ) ;
--~ ALTER TABLE seqfeature_path ADD CONSTRAINT FKparentfeat_seqfeatpath
      --~ FOREIGN KEY ( object_seqfeature_id ) REFERENCES seqfeature ( seqfeature_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE seqfeature_path ADD CONSTRAINT FKchildfeat_seqfeatpath
      --~ FOREIGN KEY ( subject_seqfeature_id ) REFERENCES seqfeature ( seqfeature_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- seqfeature_qualifier_value 
--~ ALTER TABLE seqfeature_qualifier_value ADD CONSTRAINT FKterm_featqual
      --~ FOREIGN KEY ( term_id ) REFERENCES term ( term_id ) ;
--~ ALTER TABLE seqfeature_qualifier_value ADD CONSTRAINT FKseqfeature_featqual
      --~ FOREIGN KEY ( seqfeature_id ) REFERENCES seqfeature ( seqfeature_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- seqfeature_dbxref 
--~ ALTER TABLE seqfeature_dbxref ADD CONSTRAINT FKseqfeature_feadblink
      --~ FOREIGN KEY ( seqfeature_id ) REFERENCES seqfeature ( seqfeature_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE seqfeature_dbxref ADD CONSTRAINT FKdbxref_feadblink
      --~ FOREIGN KEY ( dbxref_id ) REFERENCES dbxref ( dbxref_id )
      --~ ON DELETE CASCADE ;
--~ 
--~ -- location 
--~ ALTER TABLE location ADD CONSTRAINT FKseqfeature_location
      --~ FOREIGN KEY ( seqfeature_id ) REFERENCES seqfeature ( seqfeature_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE location ADD CONSTRAINT FKdbxref_location
      --~ FOREIGN KEY ( dbxref_id ) REFERENCES dbxref ( dbxref_id ) ;
--~ ALTER TABLE location ADD CONSTRAINT FKterm_featloc
      --~ FOREIGN KEY ( term_id ) REFERENCES term ( term_id ) ;
--~ 
--~ -- location_qualifier_value 
--~ ALTER TABLE location_qualifier_value ADD CONSTRAINT FKfeatloc_locqual
      --~ FOREIGN KEY ( location_id ) REFERENCES location ( location_id )
      --~ ON DELETE CASCADE ;
--~ ALTER TABLE location_qualifier_value ADD CONSTRAINT FKterm_locqual
      --~ FOREIGN KEY ( term_id ) REFERENCES term ( term_id ) ;
--~ 
--~ 
 --~ 
