SELECT * FROM variable WHERE value <> 's';
SELECT MAX(nid) FROM node;
SELECT MAX(cid) FROM comments;
SELECT MAX(tid) FROM term_data;
SELECT MAX(cid) FROM comments;
SELECT MAX(vid) FROM vocabulary;
SELECT n.nid, t.tid FROM node n, term_node t WHERE n.nid = t.nid AND type = 'forum';
SELECT MAX(bid) FROM bundle;
SELECT MAX(fid) FROM feed;
SELECT nid FROM comments WHERE thread = '';
SELECT cid, pid FROM comments where nid = 0 ORDER BY timestamp;
SELECT MAX(uid) FROM users;
-- column not found -- SELECT nid, path FROM node WHERE path != '';
-- column not found -- SELECT nid, path FROM node WHERE path != '';
SELECT MAX(aid) FROM access;