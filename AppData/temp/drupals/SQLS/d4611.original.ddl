CREATE TABLE access (
  aid integer,
  mask varchar(255) NOT NULL ,
  type varchar(255) NOT NULL ,
  status smallint NOT NULL ,
  PRIMARY KEY (aid)
);
CREATE TABLE accesslog (
  aid integer,
  mask varchar(255) NOT NULL ,
  title varchar(255) ,
  path varchar(255) ,
  url varchar(255) ,
  hostname varchar(128) ,
  uid integer ,
  timestamp integer NOT NULL ,
  PRIMARY KEY (aid)
);
CREATE INDEX accesslog_timestamp_idx ON accesslog (timestamp);
CREATE TABLE aggregator_category (
  cid integer,
  title varchar(255) NOT NULL ,
  description varchar(1024),
  block smallint NOT NULL ,
  PRIMARY KEY  (cid),
  UNIQUE (title)
);
CREATE TABLE aggregator_category_feed (
  fid integer NOT NULL ,
  cid integer NOT NULL ,
  PRIMARY KEY  (fid,cid)
);
CREATE TABLE aggregator_category_item (
  iid integer NOT NULL ,
  cid integer NOT NULL ,
  PRIMARY KEY  (iid,cid)
);
CREATE TABLE aggregator_feed (
  fid integer,
  title varchar(255) NOT NULL ,
  url varchar(255) NOT NULL ,
  refresh integer NOT NULL ,
  checked integer NOT NULL ,
  link varchar(255) NOT NULL ,
  description varchar(1024),
  image varchar(1024),
  etag varchar(255) NOT NULL ,
  modified integer NOT NULL ,
  block smallint NOT NULL ,
  PRIMARY KEY  (fid),
  UNIQUE (url),
  UNIQUE (title)
);
CREATE TABLE aggregator_item (
  iid integer,
  fid integer NOT NULL ,
  title varchar(255) NOT NULL ,
  link varchar(255) NOT NULL ,
  author varchar(255) NOT NULL ,
  description varchar(1024),
  timestamp integer ,
  PRIMARY KEY  (iid)
);
CREATE TABLE authmap (
  aid integer,
  uid integer NOT NULL ,
  authname varchar(128) NOT NULL ,
  module varchar(128) NOT NULL ,
  PRIMARY KEY (aid),
  UNIQUE (authname)
);
CREATE TABLE blocks (
  module varchar(64) NOT NULL ,
  delta varchar(32) NOT NULL ,
  status smallint NOT NULL ,
  weight smallint NOT NULL ,
  region smallint NOT NULL ,
  custom smallint NOT NULL ,
  throttle smallint NOT NULL ,
  visibility smallint NOT NULL ,
  pages varchar(1024) NOT NULL ,
  types varchar(1024) NOT NULL 
);
CREATE TABLE book (
  nid integer NOT NULL ,
  parent integer NOT NULL ,
  weight smallint NOT NULL ,
  log varchar(1024) ,
  PRIMARY KEY (nid)
);
CREATE INDEX book_nid_idx ON book(nid);
CREATE INDEX book_parent ON book(parent);
CREATE TABLE boxes (
  bid integer,
  title varchar(64) NOT NULL ,
  body varchar(1024) ,
  info varchar(128) NOT NULL ,
  format smallint NOT NULL ,
  PRIMARY KEY  (bid),
  UNIQUE (info),
  UNIQUE (title)
);
CREATE TABLE cache (
  cid varchar(255) NOT NULL ,
  data varchar(1024) ,
  expire integer NOT NULL ,
  created integer NOT NULL ,
  headers varchar(1024) ,
  PRIMARY KEY  (cid)
);
CREATE INDEX cache_expire_idx ON cache(expire);
CREATE TABLE comments (
  cid integer,
  pid integer NOT NULL ,
  nid integer NOT NULL ,
  uid integer NOT NULL ,
  subject varchar(64) NOT NULL ,
  comment varchar(1024) NOT NULL ,
  hostname varchar(128) NOT NULL ,
  timestamp integer NOT NULL ,
  score integer NOT NULL ,
  status smallint  NOT NULL ,
  format smallint NOT NULL ,
  thread varchar(255) ,
  users varchar(1024) ,
  name varchar(60) ,
  mail varchar(64) ,
  homepage varchar(255) ,
  PRIMARY KEY  (cid)
);
CREATE INDEX comments_nid_idx ON comments(nid);
CREATE TABLE node_comment_statistics (
  nid integer NOT NULL,
  last_comment_timestamp integer NOT NULL ,
  last_comment_name varchar(60)  ,
  last_comment_uid integer NOT NULL ,
  comment_count integer NOT NULL ,
  PRIMARY KEY (nid)
);
CREATE INDEX node_comment_statistics_timestamp_idx ON node_comment_statistics(last_comment_timestamp);
CREATE TABLE directory (
  link varchar(255) NOT NULL ,
  name varchar(128) NOT NULL ,
  mail varchar(128) NOT NULL ,
  slogan varchar(1024) NOT NULL ,
  mission varchar(1024) NOT NULL ,
  timestamp integer NOT NULL ,
  PRIMARY KEY  (link)
);
CREATE TABLE files (
  fid integer,
  nid integer NOT NULL ,
  filename varchar(255) NOT NULL ,
  filepath varchar(255) NOT NULL ,
  filemime varchar(255) NOT NULL ,
  filesize integer NOT NULL ,
  list smallint NOT NULL ,
  PRIMARY KEY  (fid)
);
CREATE TABLE filter_formats (
  format integer,
  name varchar(255) NOT NULL ,
  roles varchar(255) NOT NULL ,
  cache smallint NOT NULL ,
  PRIMARY KEY (format)
);
CREATE TABLE filters (
  format integer NOT NULL ,
  module varchar(64) NOT NULL ,
  delta smallint NOT NULL ,
  weight smallint  NOT NULL
);
CREATE INDEX filters_module_idx ON filters(module);
CREATE INDEX filters_weight_idx ON filters(weight);
CREATE TABLE flood (
  event varchar(64) NOT NULL ,
  hostname varchar(128) NOT NULL ,
  timestamp integer NOT NULL 
);
CREATE TABLE forum (
  nid integer NOT NULL ,
  tid integer NOT NULL ,
  shadow integer NOT NULL ,
  PRIMARY KEY  (nid)
);
CREATE INDEX forum_tid_idx ON forum(tid);
CREATE TABLE history (
  uid integer NOT NULL ,
  nid integer NOT NULL ,
  timestamp integer NOT NULL ,
  PRIMARY KEY  (uid,nid)
);
CREATE TABLE locales_meta (
  locale varchar(12) NOT NULL ,
  name varchar(64) NOT NULL ,
  enabled integer NOT NULL ,
  isdefault integer NOT NULL ,
  plurals integer NOT NULL ,
  formula varchar(128) NOT NULL ,
  PRIMARY KEY  (locale)
);
CREATE TABLE locales_source (
  lid integer,
  location varchar(255) NOT NULL ,
  source varchar(1024) NOT NULL,
  PRIMARY KEY  (lid)
);
CREATE TABLE locales_target (
  lid integer NOT NULL ,
  translation varchar(1024)  NOT NULL,
  locale varchar(12) NOT NULL ,
  plid integer NOT NULL ,
  plural integer NOT NULL ,
  UNIQUE  (lid)
);
CREATE INDEX locales_target_lid_idx ON locales_target(lid);
CREATE INDEX locales_target_lang_idx ON locales_target(locale);
CREATE INDEX locales_target_plid_idx ON locales_target(plid);
CREATE INDEX locales_target_plural_idx ON locales_target(plural);
CREATE TABLE menu (
  mid integer,
  pid integer NOT NULL ,
  path varchar(255) NOT NULL ,
  title varchar(255) NOT NULL ,
  description varchar(255) NOT NULL ,
  weight smallint NOT NULL ,
  type smallint NOT NULL ,
  PRIMARY KEY  (mid)
);
CREATE TABLE moderation_filters (
  fid integer,
  filter varchar(255) NOT NULL ,
  minimum smallint NOT NULL ,
  PRIMARY KEY  (fid)
);
CREATE TABLE moderation_roles (
  rid integer NOT NULL ,
  mid integer NOT NULL ,
  value smallint NOT NULL 
);
CREATE INDEX moderation_roles_rid_idx ON moderation_roles(rid);
CREATE INDEX moderation_roles_mid_idx ON moderation_roles(mid);
CREATE TABLE moderation_votes (
  mid integer,
  vote varchar(255) ,
  weight smallint NOT NULL ,
  PRIMARY KEY  (mid)
);
CREATE TABLE users_roles (
  uid integer NOT NULL ,
  rid integer NOT NULL ,
  PRIMARY KEY (uid, rid)
);
CREATE TABLE variable (
  name varchar(48) NOT NULL ,
  value varchar(1024) NOT NULL ,
  PRIMARY KEY  (name)
);
CREATE TABLE vocabulary (
  vid integer,
  name varchar(255) NOT NULL ,
  description varchar(1024) ,
  help varchar(255) NOT NULL ,
  relations smallint NOT NULL ,
  hierarchy smallint NOT NULL ,
  multiple smallint NOT NULL ,
  required smallint NOT NULL ,
  module varchar(255) NOT NULL ,
  weight smallint NOT NULL ,
  PRIMARY KEY  (vid)
);
CREATE TABLE vocabulary_node_types (
  vid integer NOT NULL ,
  type varchar(16) NOT NULL ,
  PRIMARY KEY (vid, type)
);
CREATE TABLE watchdog (
  wid integer,
  uid integer NOT NULL ,
  type varchar(16) NOT NULL ,
  message varchar(1024) NOT NULL ,
  severity smallint NOT NULL ,
  link varchar(255) NOT NULL ,
  location varchar(128) NOT NULL ,
  hostname varchar(128) NOT NULL ,
  timestamp integer NOT NULL ,
  PRIMARY KEY  (wid)
);
CREATE TABLE node_access (
  nid integer,
  gid integer NOT NULL ,
  realm varchar(255) NOT NULL ,
  grant_view smallint NOT NULL ,
  grant_update smallint NOT NULL ,
  grant_delete smallint NOT NULL ,
  PRIMARY KEY  (nid,gid,realm)
);
CREATE TABLE node_counter (
  nid integer NOT NULL ,
  totalcount integer NOT NULL ,
  daycount integer NOT NULL ,
  timestamp integer NOT NULL ,
  PRIMARY KEY  (nid)
);
CREATE INDEX node_counter_totalcount_idx ON node_counter(totalcount);
CREATE INDEX node_counter_daycount_idx ON node_counter(daycount);
CREATE INDEX node_counter_timestamp_idx ON node_counter(timestamp);
CREATE TABLE profile_fields (
  fid integer,
  title varchar(255) ,
  name varchar(128) ,
  explanation varchar(1024) ,
  category varchar(255) ,
  page varchar(255) ,
  type varchar(128) ,
  weight smallint  NOT NULL,
  required smallint  NOT NULL,
  register smallint  NOT NULL,
  visibility smallint  NOT NULL,
  options varchar(1024),
  UNIQUE (name),
  PRIMARY KEY (fid)
);
CREATE INDEX profile_fields_category ON profile_fields (category);
CREATE TABLE profile_values (
  fid integer ,
  uid integer ,
  value varchar(1024)
);
CREATE INDEX profile_values_uid ON profile_values (uid);
CREATE INDEX profile_values_fid ON profile_values (fid);
CREATE TABLE url_alias (
  pid integer,
  src varchar(128) NOT NULL ,
  dst varchar(128) NOT NULL ,
  PRIMARY KEY  (pid)
);
CREATE INDEX url_alias_dst_idx ON url_alias(dst);
CREATE TABLE permission (
  rid integer NOT NULL ,
  perm varchar(1024) ,
  tid integer NOT NULL 
);
CREATE INDEX permission_rid_idx ON permission(rid);
CREATE TABLE poll (
  nid integer NOT NULL ,
  runtime integer NOT NULL ,
  polled varchar(1024) NOT NULL ,
  active integer NOT NULL ,
  PRIMARY KEY  (nid)
);
CREATE TABLE poll_choices (
  chid integer,
  nid integer NOT NULL ,
  chtext varchar(128) NOT NULL ,
  chvotes integer NOT NULL ,
  chorder integer NOT NULL ,
  PRIMARY KEY  (chid)
);
CREATE INDEX poll_choices_nid_idx ON poll_choices(nid);
CREATE TABLE queue (
  nid integer NOT NULL ,
  uid integer NOT NULL ,
  vote integer NOT NULL ,
  PRIMARY KEY (nid, uid)
);
CREATE INDEX queue_nid_idx ON queue(nid);
CREATE INDEX queue_uid_idx ON queue(uid);
CREATE TABLE role (
  rid integer,
  name varchar(32) NOT NULL ,
  PRIMARY KEY  (rid),
  UNIQUE (name)
);
CREATE TABLE search_index (
  word varchar(50) NOT NULL ,
  sid integer NOT NULL ,
  type varchar(16) ,
  fromsid integer NOT NULL ,
  fromtype varchar(16) ,
  score integer  NULL
);
CREATE INDEX search_index_sid_idx ON search_index(sid);
CREATE INDEX search_index_fromsid_idx ON search_index(fromsid);
CREATE INDEX search_index_word_idx ON search_index(word);
CREATE TABLE search_total (
  word varchar(50) NOT NULL ,
  count float NULL
);
CREATE INDEX search_total_word_idx ON search_total(word);
CREATE TABLE sessions (
  uid integer not null,
  sid varchar(32) NOT NULL ,
  hostname varchar(128) NOT NULL ,
  timestamp integer NOT NULL ,
  session varchar(1024),
  PRIMARY KEY (sid)
);
CREATE TABLE sequences (
  name varchar(255) NOT NULL,
  id integer NOT NULL,
  PRIMARY KEY (name)
);
CREATE TABLE system (
  filename varchar(255) NOT NULL ,
  name varchar(255) NOT NULL ,
  type varchar(255) NOT NULL ,
  description varchar(255) NOT NULL ,
  status integer NOT NULL ,
  throttle smallint NOT NULL ,
  bootstrap integer NOT NULL ,
  PRIMARY KEY  (filename)
);
CREATE TABLE term_data (
  tid integer,
  vid integer NOT NULL ,
  name varchar(255) NOT NULL ,
  description varchar(1024) ,
  weight smallint NOT NULL ,
  PRIMARY KEY  (tid)
);
CREATE INDEX term_data_vid_idx ON term_data(vid);
CREATE TABLE term_hierarchy (
  tid integer NOT NULL ,
  parent integer NOT NULL 
);
CREATE INDEX term_hierarchy_tid_idx ON term_hierarchy(tid);
CREATE INDEX term_hierarchy_parent_idx ON term_hierarchy(parent);
CREATE TABLE term_node (
  nid integer NOT NULL ,
  tid integer NOT NULL ,
  PRIMARY KEY (tid,nid)
);
CREATE INDEX term_node_nid_idx ON term_node(nid);
CREATE INDEX term_node_tid_idx ON term_node(tid);
CREATE TABLE term_relation (
  tid1 integer NOT NULL ,
  tid2 integer NOT NULL 
);
CREATE INDEX term_relation_tid1_idx ON term_relation(tid1);
CREATE INDEX term_relation_tid2_idx ON term_relation(tid2);
CREATE TABLE term_synonym (
  tid integer NOT NULL ,
  name varchar(255) NOT NULL 
);
CREATE INDEX term_synonym_tid_idx ON term_synonym(tid);
CREATE INDEX term_synonym_name_idx ON term_synonym(name);
CREATE TABLE node (
  nid integer,
  type varchar(16) NOT NULL ,
  title varchar(128) NOT NULL ,
  uid integer NOT NULL ,
  status integer NOT NULL ,
  created integer NOT NULL ,
  changed integer NOT NULL ,
  comment integer NOT NULL ,
  promote integer NOT NULL ,
  moderate integer NOT NULL ,
  teaser varchar(1024) NOT NULL ,
  body varchar(1024) NOT NULL ,
  revisions varchar(1024) NOT NULL ,
  sticky integer NOT NULL ,
  format smallint NOT NULL ,
  PRIMARY KEY  (nid)
);
CREATE INDEX node_type_idx ON node(type);
CREATE INDEX node_title_idx ON node(title,type);
CREATE INDEX node_status_idx ON node(status);
CREATE INDEX node_uid_idx ON node(uid);
CREATE INDEX node_moderate_idx ON node (moderate);
CREATE INDEX node_promote_status_idx ON node (promote, status);
CREATE INDEX node_created ON node(created);
CREATE INDEX node_changed ON node(changed);
CREATE TABLE users (
  uid integer NOT NULL ,
  name varchar(60) NOT NULL ,
  pass varchar(32) NOT NULL ,
  mail varchar(64) ,
  mode smallint NOT NULL ,
  sort smallint ,
  threshold smallint ,
  theme varchar(255) NOT NULL ,
  signature varchar(255) NOT NULL ,
  created integer NOT NULL ,
  changed integer NOT NULL ,
  status smallint NOT NULL ,
  timezone varchar(8) ,
  language varchar(12) NOT NULL ,
  picture varchar(255) NOT NULL ,
  init varchar(64) ,
  data varchar(1024) ,
  PRIMARY KEY  (uid),
  UNIQUE (name)
);
CREATE INDEX users_changed_idx ON users(changed);
CREATE SEQUENCE users_uid_seq INCREMENT 1 START 1;