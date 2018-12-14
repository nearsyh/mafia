curl -i -XPUT 'localhost:8080/games' \
	-H 'Content-Type: application/json' -H 'Accept: application/json' \
	-d '{
	"NORMAL_VILLAGER": 4,
	"WEREWOLF": 1,
	"SEER": 1,
	"WITCH": 1,
	"GUARDIAN": 1}'
