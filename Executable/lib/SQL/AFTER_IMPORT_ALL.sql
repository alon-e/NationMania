
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;
##########################################################
# NATION WEIGHTS
##########################################################

#UPDATE nations weight by number of facts


UPDATE nations SET nation_weight = (SELECT FLOOR(SQRT(COUNT(nation_id))) FROM facts
WHERE nations.nation_id = facts.nation_id GROUP BY nation_id);

#DELETE facts of nations with less then 50 facts
DELETE facts FROM facts INNER JOIN nations
ON facts.nation_id = nations.nation_id
AND nations.nation_weight <= 5;

#DELETE nations with less then 50 facts
DELETE FROM nations
WHERE nations.nation_weight <= 5 OR nations.nation_weight IS NULL;








##########################################################
# FACT WEIGHTS
##########################################################

#Give all unweighted facts a value.
UPDATE facts INNER JOIN ( 
(SELECT nation_id,max(fact_weight)as max FROM facts GROUP BY nation_id) as max_nations CROSS JOIN relation_types)
ON facts.nation_id = max_nations.nation_id AND
facts.relation_type_id = relation_types.relation_type_id
SET fact_weight=1+FLOOR( max_nations.max/4 + RAND()*max_nations.max/2) # distribute in the middle half
WHERE fact_weight=-1;


##########################################################
# US STATES reweighting
##########################################################

### Get most US states 
CREATE VIEW USstates AS (SELECT fact_caption, fact_id as fact_state
FROM facts , nations , fact_types , relation_types
WHERE facts.nation_id = nations.nation_id AND
facts.fact_type_id = fact_types.fact_type_id AND
facts.relation_type_id = relation_types.relation_type_id
AND fact_caption NOT LIKE "%,%" AND nation_name = '<United_States>' AND fact_type_name = '<wordnet_administrative_district_108491826>'
ORDER BY fact_weight DESC LIMIT 200);

### Update 'real' weight of US states
UPDATE facts
INNER JOIN 
(SELECT fact_state, USstates.fact_caption , sum(facts.fact_weight) as sum
FROM facts , USstates , fact_types
WHERE fact_types.fact_type_name = '<wordnet_administrative_district_108491826>' AND facts.fact_caption LIKE CONCAT("%",USstates.fact_caption,"%") AND facts.fact_caption <> USstates.fact_caption
GROUP BY USstates.fact_caption) as states
ON states.fact_state = facts.fact_id
SET fact_weight = states.sum
WHERE states.fact_state = facts.fact_id;


### Remove facts that contain US states
DELETE facts FROM facts INNER JOIN (nations CROSS JOIN fact_types CROSS JOIN relation_types)
ON facts.nation_id = nations.nation_id AND
facts.fact_type_id = fact_types.fact_type_id AND
facts.relation_type_id = relation_types.relation_type_id
WHERE fact_caption LIKE "%,%" AND nation_name = '<United_States>' AND fact_type_name = '<wordnet_administrative_district_108491826>';

DROP VIEW USstates;


##########################################################
# WEIGHT DIST.
##########################################################


#Update Facts weight for better distribusion 
UPDATE facts INNER JOIN ( (SELECT nation_id,ROUND(MAX(fact_weight)) as max FROM facts
GROUP BY nation_id) as max_nations CROSS JOIN relation_types)
ON facts.nation_id = max_nations.nation_id AND
facts.relation_type_id = relation_types.relation_type_id
SET fact_weight=1+ROUND(SQRT(facts.fact_weight+max_nations.max/6));





#Remove duplicate facts
DELETE facts
FROM facts, (SELECT fact_id FROM facts 
GROUP BY fact_caption , nation_id HAVING COUNT(fact_caption) > 1) as dups
WHERE facts.fact_id=dups.fact_id ; 



##########################################################
#RANKING NUMERIC FACTS
##########################################################

#get all numeric facts - for ranking
CREATE VIEW numeric_relations AS
SELECT facts.relation_type_id, relation_type_caption
FROM facts INNER JOIN (nations CROSS JOIN fact_types CROSS JOIN relation_types)
ON facts.nation_id = nations.nation_id AND
facts.fact_type_id = fact_types.fact_type_id AND
facts.relation_type_id = relation_types.relation_type_id
WHERE CONCAT("",fact_caption*1) =fact_caption AND relation_types.relation_type_name <> "<isLocatedIn>"
GROUP BY facts.relation_type_id
ORDER BY fact_weight DESC;

CREATE VIEW numeric_facts AS
SELECT facts.*
FROM facts INNER JOIN (nations CROSS JOIN fact_types CROSS JOIN relation_types)
ON facts.nation_id = nations.nation_id AND
facts.fact_type_id = fact_types.fact_type_id AND
facts.relation_type_id = relation_types.relation_type_id
WHERE facts.relation_type_id IN (SELECT relation_type_id FROM numeric_relations)
ORDER BY fact_weight DESC;

#rank numeric facts
CREATE VIEW ranked_numeric_facts AS
SELECT f1.fact_id, FLOOR((COUNT(f1.fact_id) / 1)) AS rank
  FROM numeric_facts   AS f1
  JOIN numeric_facts   AS f2
    ON (f2.fact_caption, f2.fact_id) >= (f1.fact_caption, f1.fact_id)
   AND f1.relation_type_id = f2.relation_type_id
 GROUP BY f1.fact_id
        , f1.relation_type_id
        , f1.fact_caption
 ORDER BY f1.fact_caption, rank ;

#Update Ranks
UPDATE IGNORE facts INNER JOIN ranked_numeric_facts ON facts.fact_id = ranked_numeric_facts.fact_id
SET facts.fact_caption = ranked_numeric_facts.rank;

DROP VIEW numeric_relations;
DROP VIEW numeric_facts;
DROP VIEW ranked_numeric_facts;






##########################################################
#CONTINENTS
##########################################################
INSERT INTO continents (continent_name)
SELECT fact_caption
FROM facts INNER JOIN (nations CROSS JOIN fact_types CROSS JOIN relation_types)
ON facts.nation_id = nations.nation_id AND
facts.fact_type_id = fact_types.fact_type_id AND
facts.relation_type_id = relation_types.relation_type_id
WHERE fact_types.fact_type_name = '<wordnet_continent_109254614>'
GROUP BY facts.fact_caption;

UPDATE nations INNER JOIN (SELECT MIN(fact_caption) as name, facts.nation_id
FROM facts INNER JOIN (nations CROSS JOIN fact_types CROSS JOIN relation_types)
ON facts.nation_id = nations.nation_id AND
facts.fact_type_id = fact_types.fact_type_id AND
facts.relation_type_id = relation_types.relation_type_id
WHERE fact_types.fact_type_name = '<wordnet_continent_109254614>' GROUP BY facts.nation_id) as continentsnations 
ON continentsnations.nation_id = nations.nation_id
SET nations.continent_id = (SELECT continent_id FROM continents WHERE continents.continent_name = continentsnations.name);


##########################################################
#Remove redundant Language/phrasebook 
##########################################################
UPDATE facts INNER JOIN ( SELECT fact_id FROM facts, relation_types 
WHERE facts.relation_type_id =  relation_types.relation_type_id 
AND relation_types.relation_type_name = '<hasOfficialLanguage>'
AND fact_caption LIKE "%language") as lang
ON facts.fact_id = lang.fact_id 
SET fact_caption = REPLACE(fact_caption, 'language', '');

UPDATE facts INNER JOIN ( SELECT fact_id FROM facts, relation_types 
WHERE facts.relation_type_id =  relation_types.relation_type_id 
AND relation_types.relation_type_name = '<hasOfficialLanguage>'
AND fact_caption LIKE "%phrasebook") as lang
ON facts.fact_id = lang.fact_id 
SET fact_caption = REPLACE(fact_caption, 'phrasebook', '');


##########################################################
#Remove unknown days/months from wasCreatedOnDate
##########################################################

UPDATE facts INNER JOIN ( SELECT fact_id FROM facts, relation_types 
WHERE facts.relation_type_id =  relation_types.relation_type_id 
AND relation_types.relation_type_name = '<wasCreatedOnDate>'
AND ((fact_caption LIKE "%-##-##"))) as createdate
ON facts.fact_id = createdate.fact_id 
SET fact_caption = REPLACE(fact_caption, '-##-##', '');


SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;
