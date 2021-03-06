SELECT nid,type FROM node WHERE type = 'story' OR type = 'page' OR type = 'blog' OR type = 'forum' OR type = 'book';
-- SELECT * FROM $node->type WHERE nid = 0;
SELECT n.nid, n.title FROM node n WHERE n.type = 'book' AND n.status = 0;
-- column not found -- SELECT rid, perm FROM role;
SELECT rid, name FROM role;
SELECT MAX(vid) AS high FROM vocabulary;
-- table not found -- SELECT * FROM collection;
-- table not found -- SELECT * FROM tag;
SELECT MAX(tid) AS high FROM term_data;
SELECT nid,attrs FROM node WHERE attrs != '';
-- table not found -- SELECT name FROM tag WHERE attrs = '$node->attrs';
SELECT * FROM variable WHERE value <> 's';
SELECT MAX(nid) FROM node;
SELECT MAX(cid) FROM comments;
SELECT MAX(tid) FROM term_data;
SELECT MAX(cid) FROM comments;
SELECT MAX(vid) FROM vocabulary;
SELECT n.nid, t.tid FROM node n, term_node t WHERE n.nid = t.nid AND type = 'forum';
SELECT MAX(bid) FROM bundle;
SELECT MAX(fid) FROM feed;
SELECT n.nid, c.cid, c.subject FROM node n LEFT JOIN comments c ON n.nid = c.nid WHERE c.comment = '%s';
SELECT nid, title FROM node WHERE teaser = '%s' OR body = '%s';
-- column not found -- SELECT rid, perm FROM role;
